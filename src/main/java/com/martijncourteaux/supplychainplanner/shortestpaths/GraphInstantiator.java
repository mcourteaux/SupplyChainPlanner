/* 
 * Copyright (C) 2017 Martijn Courteaux.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.shortestpaths;

import com.martijncourteaux.supplychainplanner.ConsignmentDetails;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.VariableGraph;
import edu.asu.emit.algorithm.graph.Vertex;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class GraphInstantiator {

    public GraphInstantiator() {
    }

    private Vertex createVertex(AttributedGraph<VertexAttribute> gr, int loc) {
        Graph g = gr.getGraph();
        Vertex v = new Vertex();
        g.addVertex(v);
        gr.setVertexForLocation(loc, v);

        VertexAttribute att = new VertexAttribute();
        att.is_location_vertex = true;
        att.location_code = String.format("LOC_%05d", loc);
        att.location_id = loc;
        gr.setAttribute(v.getId(), att);

        return v;
    }

    public AttributedGraph instantiateGraph(Connection conn, ConsignmentDetails cm) throws SQLException {
        String cost_formula = String.format(
                "%f * offer_cost_base + "
                + "%f * offer_cost_per_kg + "
                + "%f * offer_cost_per_m3 + "
                + "%f * offer_cost_per_pallet + "
                + "%f * offer_duration_hours",
                cm.basic_cost_weight,
                cm.cost_per_kg_weight,
                cm.cost_per_m3_weight,
                cm.cost_per_pallet_weight,
                cm.duration_hours_weight);

        String conditions = String.format(
                "offer_min_weight <= %f AND"
                + " (offer_max_weight IS NULL OR %f <= offer_max_weight) AND "
                + "offer_min_volume <= %f AND"
                + " (offer_max_volume IS NULL OR %f <= offer_max_volume) AND "
                + "offer_min_pallets <= %d AND"
                + " (offer_max_pallets IS NULL OR %d <= offer_max_pallets) ",
                cm.weight_kg, cm.weight_kg,
                cm.volume_m3, cm.volume_m3,
                cm.pallets, cm.pallets);

        if (!cm.allow_ferry) {
            conditions += "AND line_modality != 'ferry' ";
        }
        if (!cm.allow_roads) {
            conditions += "AND line_modality != 'road' ";
        }

        for (int agent_id : cm.disallowed_agents) {
            conditions += "AND offer_agent != " + agent_id;
        }

        String query
                = "SELECT (" + cost_formula + ") AS cost, "
                + "offer_duration_hours, offer_agent, "
                + "line_from, line_to, line_code, line_modality "
                + "FROM transport_offers "
                + "INNER JOIN transport_lines ON (line_id = offer_line) "
                + "WHERE " + conditions;

        Statement s = conn.createStatement();
        AttributedGraph<VertexAttribute> attribs;
        try (ResultSet rs = s.executeQuery(query)) {
            int col_cost = rs.findColumn("cost");
            int col_duration = rs.findColumn("offer_duration_hours");
            int col_agent = rs.findColumn("offer_agent");
            int col_from = rs.findColumn("line_from");
            int col_to = rs.findColumn("line_to");
            int col_line_code = rs.findColumn("line_code");
            int col_line_modality = rs.findColumn("line_modality");

            Map<Integer, Vertex> vertices = new HashMap<>();
            Graph g = new VariableGraph();
            attribs = new AttributedGraph<>(g);
            while (rs.next()) {
                double cost = rs.getDouble(col_cost);
                int from = rs.getInt(col_from);
                int to = rs.getInt(col_to);

                Vertex vFrom = vertices.get(from);
                Vertex vTo = vertices.get(to);

                if (vFrom == null) {
                    vFrom = createVertex(attribs, from);
                    vertices.put(from, vFrom);
                }
                if (vTo == null) {
                    vTo = createVertex(attribs, to);
                    vertices.put(from, vTo);
                }

                Vertex vAux = new Vertex();
                g.addVertex(vAux);

                g.addEdge(vFrom.getId(), vAux.getId(), 0.0);
                g.addEdge(vAux.getId(), vTo.getId(), cost);

                VertexAttribute att = new VertexAttribute();
                att.cost = cost;
                att.is_auxiliary_offer_out_vertex = true;
                att.agent_id = rs.getInt(col_agent);
                att.line_code = rs.getString(col_line_code);
                att.line_from = from;
                att.line_to = to;
                att.line_modality = rs.getString(col_line_modality);
                att.duration = rs.getInt(col_duration);
                attribs.setAttribute(vAux.getId(), att);
            }
        }

        return attribs;
    }

}

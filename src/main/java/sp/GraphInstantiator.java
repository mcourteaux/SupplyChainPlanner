package sp;

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

    public AttributedGraph instantiateGraph(Connection conn, CostModel cm) throws SQLException {
        String cost_formula = String.format(
                "%f * offer_cost_base + "
                + "%f * offer_cost_per_kg + "
                + "%f * offer_cost_per_m3 + "
                + "%f * offer_duration_hours",
                cm.basic_cost_weight,
                cm.cost_per_kg_weight,
                cm.cost_per_m3_weight,
                cm.duration_hours_weight);

        String query
                = "SELECT (" + cost_formula + ") AS cost, "
                + "offer_duration_hours, offer_agent, line_from, line_to "
                + "FROM transport_offers "
                + "INNER JOIN transport_lines ON (line_id = offer_line)";

        Statement s = conn.createStatement();
        AttributedGraph<VertexAttribute> attribs;
        try (ResultSet rs = s.executeQuery(query)) {
            int col_cost = rs.findColumn("cost");
            int col_duration = rs.findColumn("offer_duration_hours");
            int col_agent = rs.findColumn("offer_agent");
            int col_from = rs.findColumn("line_from");
            int col_to = rs.findColumn("line_to");
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
                    vFrom = new Vertex();
                    g.addVertex(vFrom);
                    vertices.put(from, vFrom);
                    attribs.setVertexForLocation(from, vFrom);
                }
                if (vTo == null) {
                    vTo = new Vertex();
                    g.addVertex(vTo);
                    vertices.put(from, vTo);
                    attribs.setVertexForLocation(to, vTo);
                }
                
                Vertex vAux = new Vertex();
                g.addVertex(vAux);
                
                
                g.addEdge(vFrom.getId(), vAux.getId(), 0.0);
                g.addEdge(vAux.getId(), vTo.getId(), cost);
                
                VertexAttribute att = new VertexAttribute();
                att.is_auxiliary_offer_out_vertex = true;
                att.agent_id = rs.getInt(col_agent);
                att.line_from = from;
                att.line_to = to;
                att.duration = rs.getInt(col_duration);
                attribs.setAttribute(vAux.getId(), att);
            }
        }

        return attribs;
    }

}

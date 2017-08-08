
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ddg.LineGenerator;
import ddg.LocationGenerator;
import ddg.OffersGenerator;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.Vertex;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;
import edu.asu.emit.algorithm.graph.shortestpaths.YenTopKShortestPathsAlg;
import java.sql.ResultSet;
import sp.AttributedGraph;
import sp.CostModel;
import sp.GraphInstantiator;

import sp.VertexAttribute;

public class Main {

    public static void main(String args[]) throws Exception {
        Class.forName("org.postgresql.Driver");
        String db_uri = "jdbc:postgresql://localhost:5432/poc";
        String db_user = "postgres";
        String db_pass = "";
        try (Connection connection = DriverManager.getConnection(db_uri,
                db_user, db_pass)) {
            System.out.println("Connection made!");

            //drop_dummies(connection);
            //generate_dummies(connection);
            print_graph_stats(connection);

            GraphInstantiator gi = new GraphInstantiator();
            CostModel cm = new CostModel();
            cm.basic_cost_weight = 1.0;
            cm.cost_per_kg_weight = 140.0;
            cm.cost_per_m3_weight = 20.0;
            cm.duration_hours_weight = 3.0;

            System.out.println("Loading graph from database...");
            AttributedGraph<VertexAttribute> g = gi.instantiateGraph(connection, cm);
            Graph gr = g.getGraph();

            BaseVertex vFrom = g.getVertexForLocation(6);
            BaseVertex vTo = g.getVertexForLocation(26);

            System.out.println("Searching shortest paths...");
            YenTopKShortestPathsAlg alg = new YenTopKShortestPathsAlg(gr, vFrom, vTo);

            int count = 0;
            while (alg.hasNext() && count++ < 30) {
                Path p = alg.next();
                System.out.printf("Path %03d: %s%n", count, path_to_string(p, g));
            }
        }
    }

    public static String path_to_string(Path p, AttributedGraph<VertexAttribute> gr) {
        StringBuilder sb = new StringBuilder();
        sb.append("Path (cost=");
        sb.append(p.getWeight());
        sb.append("; hops=");
        sb.append((p.getVertexList().size() - 1) / 2);
        sb.append(")\n");
        for (int i = 0; i < p.getVertexList().size(); ++i) {
            BaseVertex vert = p.getVertexList().get(i);
            VertexAttribute att = gr.getAttribute(vert.getId());
            if (i % 2 == 0) {
                sb.append(att.location_code);
                sb.append("\n");
            } else {
                sb.append(" ---{");
                sb.append(String.format("%8s: agent=%2d, cost=%5.2f",
                        att.line_code, att.agent_id, att.cost));
                sb.append("}---> ");
            }
        }
        return sb.toString();
    }

    public static void print_graph_stats(Connection conn) throws SQLException {
        long num_loc;
        long num_lines;
        long num_offers;
        long num_agents;
        try (Statement stat = conn.createStatement()) {
            ResultSet rs;

            rs = stat.executeQuery("SELECT COUNT(*) FROM locations");
            rs.next();
            num_loc = rs.getLong(1);

            rs = stat.executeQuery("SELECT COUNT(*) FROM transport_lines");
            rs.next();
            num_lines = rs.getLong(1);

            rs = stat.executeQuery("SELECT COUNT(*) FROM transport_offers");
            rs.next();
            num_offers = rs.getLong(1);

            rs = stat.executeQuery("SELECT COUNT(*) FROM agents");
            rs.next();
            num_agents = rs.getLong(1);
        }

        System.out.printf("Locations     : %6d%n", num_loc);
        System.out.printf("Lines         : %6d%n", num_lines);
        System.out.printf("Offers        : %6d%n", num_offers);
        System.out.printf("Agents        : %6d%n", num_agents);

    }

    private static void drop_dummies(Connection conn) throws SQLException {
        try (Statement stat = conn.createStatement()) {
            stat.executeUpdate("DELETE FROM transport_lines");
            stat.executeUpdate("DELETE FROM transport_offers");
            stat.executeUpdate("DELETE FROM locations");
            stat.execute("ALTER SEQUENCE locations_location_id_seq RESTART");
            stat.execute("ALTER SEQUENCE transport_lines_line_id_seq RESTART");
            stat.execute("ALTER SEQUENCE transport_offers_offer_id_seq RESTART");
        }
    }

    private static void generate_dummies(Connection conn) throws SQLException {

        int locations = 100;
        int lines = 50 * locations;
        int offers = 4 * lines;
        LocationGenerator loc_gen = new LocationGenerator();
        loc_gen.generate(conn, locations);

        LineGenerator lg = new LineGenerator();
        lg.generate(conn, lines, locations);

        OffersGenerator og = new OffersGenerator();
        og.generate(conn, offers, 30, lines);
    }
}

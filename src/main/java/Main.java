
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
import edu.asu.emit.algorithm.graph.shortestpaths.YenTopKShortestPathsAlg;
import sp.AttributedGraph;
import sp.CostModel;
import sp.GraphInstantiator;

import sp.VertexAttribute;

public class Main {

    public static void main(String args[]) throws Exception {
        Class.forName("org.postgresql.Driver");
        String db_uri = "jdbc:postgresql://localhost:5432/poc";
        String db_user = "martijn";
        String db_pass = "";
        try (Connection connection = DriverManager.getConnection(db_uri,
                db_user, db_pass)) {
            System.out.println("Connection made!");
            //ShortestPath sp = new ShortestPath();
            //sp.calculate(connection);
            GraphInstantiator gi = new GraphInstantiator();
            CostModel cm = new CostModel();
            cm.basic_cost_weight = 1.0;
            cm.cost_per_kg_weight = 140.0;
            cm.cost_per_m3_weight = 20.0;
            cm.duration_hours_weight = 3.0;

            System.out.println("Loading graph from database...");
            AttributedGraph<VertexAttribute> g = gi.instantiateGraph(connection, cm);
            Graph gr = g.getGraph();

            Vertex vFrom = g.getVertexForLocation(6);
            Vertex vTo = g.getVertexForLocation(51);

            System.out.println("Searching shortest paths...");
            YenTopKShortestPathsAlg alg = new YenTopKShortestPathsAlg(gr, vFrom, vTo);

            int count = 0;
            while (alg.hasNext() && count++ < 30) {
                Path p = alg.next();
                System.out.printf("Path %03d: %s%n", count, p.toString());
            }
        }
    }

    private static void drop_dummies(Connection conn) throws SQLException {
        try (Statement stat = conn.createStatement()) {
            stat.executeUpdate("DELETE FROM transport_lines");
            stat.executeUpdate("DELETE FROM transport_offers");
            stat.executeUpdate("DELETE FROM locations");
        }
    }

    private static void generate_dummies(Connection conn) throws SQLException {
        LocationGenerator loc_gen = new LocationGenerator();
        loc_gen.generate(conn, 100);

        LineGenerator lg = new LineGenerator();
        lg.generate(conn, 500, 100);

        OffersGenerator og = new OffersGenerator();
        og.generate(conn, 3000, 30, 500);
    }
}

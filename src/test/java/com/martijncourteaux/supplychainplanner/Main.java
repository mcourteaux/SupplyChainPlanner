package com.martijncourteaux.supplychainplanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.martijncourteaux.supplychainplanner.dummydatagenerator.LineGenerator;
import com.martijncourteaux.supplychainplanner.dummydatagenerator.LocationGenerator;
import com.martijncourteaux.supplychainplanner.dummydatagenerator.OffersGenerator;
import java.sql.ResultSet;
import com.martijncourteaux.supplychainplanner.shortestpaths.ShortestPathsSolver;
import com.martijncourteaux.supplychainplanner.shortestpaths.TransportPath;

public class Main {

    public static void main(String args[]) throws Exception {
        Class.forName("org.postgresql.Driver");
        String db_uri = "jdbc:postgresql://localhost:5432/poc";
        String db_user = "postgres";
        String db_pass = "";
        try (Connection connection = DriverManager.getConnection(db_uri,
                db_user, db_pass)) {
            System.out.println("Connection made!");

            //regenerate_dummies(connection);
            print_graph_stats(connection);

            /* ==== Create a consignment ==== */
            ConsignmentDetails cm = new ConsignmentDetails();
            cm.location_from = 1;
            cm.location_to = 55;

            /* Specify the size of the consignment, such that the offers can be
             * filtered. */
            cm.pallets = 5;
            cm.volume_m3 = 20;
            cm.weight_kg = 800;

            /* Cost weights */
            cm.basic_cost_weight = 1.0;
            cm.cost_per_kg_weight = cm.weight_kg;
            cm.cost_per_m3_weight = cm.volume_m3;
            cm.cost_per_pallet_weight = cm.pallets;
            cm.duration_hours_weight = 3.0;

            /* Some parameters for extra filtering. */
            cm.allow_ferry = true;

            /* Some agents that the client had trouble with and doesn't want to
             * work with again. */
            cm.disallowed_agents.add(7);

            /* ==== Solve it ==== */
            ShortestPathsSolver sps = new ShortestPathsSolver(cm);
            System.out.println("Loading graph from database...");
            sps.buildGraph(connection);
            System.out.println("Searching shortest paths...");
            int found = sps.searchPaths(5);

            System.out.println("Found " + found + " paths.");

            for (TransportPath tp : sps.getTopKShortestPaths()) {
                System.out.println(tp);
                System.out.println();
            }
        }
    }

    /**
     * Prints some stats of the graph size.
     *
     * @param conn a valid SQL connection.
     * @throws SQLException
     */
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

    private static void regenerate_dummies(Connection conn) throws SQLException {
        drop_dummies(conn);
        generate_dummies(conn);
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

package com.martijncourteaux.supplychainplanner.shortestpaths;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class ShortestPath {
    private static final String TRANSPORT_SOLUTIONS_ALL_FIELDS =
        ""
        + "solution_has_road, solution_has_ferry, "
        + "solution_next_stop, solution_from, solution_to, "
        + "solution_next_offer, solution_num_offers, "
        + "solution_duration_hours, "
        + "solution_cost_base, solution_cost_per_kg, "
        + "solution_cost_per_m3, solution_cost_per_pallet, "
        + "solution_min_weight, solution_max_weight, "
        + "solution_min_volume, solution_max_volume, "
        + "solution_min_pallets, solution_max_pallets, "
        + "solution_required_categories, solution_rejected_categories";

    private static final String INSERT_INTO_TRANSPORT_SOLUTIONS_PREFIX =
        "INSERT INTO transport_solutions (" + TRANSPORT_SOLUTIONS_ALL_FIELDS +
        ") ";

    private static final String COST = "solution_cost_base"
                                       + " + 500 * solution_cost_per_kg"
                                       + " + 40 * solution_cost_per_m3";

    public void calculate(Connection conn) throws SQLException {
        //        clear_solutions(conn);
        //        copy_level_1_offers(conn);
        //        compute_next_level_offers(conn, 1);
        //        delete_bad_solutions_alt(conn);
        //        compute_next_level_offers(conn, 2);
        //        delete_bad_solutions_alt(conn);
        //      compute_next_level_offers(conn, 3);
        //      delete_bad_solutions_alt(conn);
        compute_next_level_offers(conn, 4);
        delete_bad_solutions_alt(conn);
    }

    private void clear_solutions(Connection conn) throws SQLException {
        Statement stat = conn.createStatement();
        int c = stat.executeUpdate("TRUNCATE TABLE transport_solutions;");
        System.out.println("Deleted " + c + " existing solutions.");
    }

    private void copy_level_1_offers(Connection conn) throws SQLException {
        String query =
            INSERT_INTO_TRANSPORT_SOLUTIONS_PREFIX + "SELECT "
            + "line_modality = 'road' AS has_road, "
            + "line_modality = 'ferry' AS has_ferry, "
            + "line_to AS next_stop, "
            + "line_from, "
            + "line_to, "
            + "offer_id, "  // next offer
            + "1, "         // num offers
            + "offer_duration_hours, "
            + "offer_cost_base, offer_cost_per_kg, "
            + "offer_cost_per_m3, offer_cost_per_pallet, "
            + "offer_min_weight, offer_max_weight, "
            + "offer_min_volume, offer_max_volume, "
            + "offer_min_pallets, offer_max_pallets, "
            + "offer_required_categories, offer_rejected_categories"
            + " FROM transport_offers"
            + " INNER JOIN transport_lines"
            + " ON transport_offers.offer_line = transport_lines.line_id;";

        Statement stat = conn.createStatement();
        int c = stat.executeUpdate(query);
        System.out.println("Copied " + c + " base offers.");
    }

    private void compute_next_level_offers(Connection conn, int lastLevel)
        throws SQLException {
        String query =
            INSERT_INTO_TRANSPORT_SOLUTIONS_PREFIX + "SELECT "
            + "p1.solution_has_road OR p2.solution_has_road, "
            + "p1.solution_has_ferry OR p2.solution_has_ferry, "
            + "p1.solution_to, p1.solution_from, p2.solution_to, "
            + "p1.solution_next_offer, "
            + "p1.solution_num_offers + p2.solution_num_offers, "
            + "p1.solution_duration_hours + p2.solution_duration_hours, "
            + "p1.solution_cost_base + p2.solution_cost_base, "
            + "p1.solution_cost_per_kg + p2.solution_cost_per_kg, "
            + "p1.solution_cost_per_m3 + p2.solution_cost_per_m3, "
            + "p1.solution_cost_per_pallet + p2.solution_cost_per_pallet, "
            + "GREATEST(p1.solution_min_weight, p2.solution_min_weight), "
            + "LEAST   (p1.solution_max_weight, p2.solution_max_weight), "
            + "GREATEST(p1.solution_min_volume, p2.solution_min_volume), "
            + "LEAST   (p1.solution_max_volume, p2.solution_max_volume), "
            + "GREATEST(p1.solution_min_pallets, p2.solution_min_pallets), "
            + "LEAST   (p1.solution_max_pallets, p2.solution_max_pallets), "
            + "NULL, NULL"  // TODO
            + " FROM transport_solutions AS p1"
            + " INNER JOIN transport_solutions AS p2"
            + " ON p1.solution_to = p2.solution_from"
            + " WHERE p1.solution_num_offers = " + lastLevel + ";";

        Statement stat = conn.createStatement();
        int c = stat.executeUpdate(query);
        System.out.println("Calculated " + c + " new offer-combinations.");
    }

    private void delete_bad_solutions_direct(Connection conn)
        throws SQLException {
        String query = "DELETE FROM transport_solutions "
                       + "WHERE solution_id NOT IN"
                       + "(SELECT solution_id FROM"
                       + " ("
                       + "  SELECT solution_id, "
                       + "  ROW_NUMBER() OVER ("
                       + "     PARTITION BY solution_from, solution_to"
                       + "     ORDER BY (" + COST + ") ASC) AS CostRowNo"
                       + "  FROM transport_solutions"
                       + " ) AS RANKED_SOLS"
                       + " WHERE CostRowNo <= 5);";

        Statement stat = conn.createStatement();
        int c = stat.executeUpdate(query);
        System.out.println("Deleted " + c + " bad offer-combinations.");
    }

    private void delete_bad_solutions_alt(Connection conn) throws SQLException {
        String query =
            "INSERT INTO transport_solutions_alt SELECT solution_id, " +
            TRANSPORT_SOLUTIONS_ALL_FIELDS + " FROM"
            + "("
            + " SELECT *, "
            + "  ROW_NUMBER() OVER ("
            + "   PARTITION BY solution_from, solution_to"
            + "   ORDER BY (" + COST + ") ASC) AS CostRowNo"
            + " FROM transport_solutions"
            + ") AS RANKED_SOLS "
            + "WHERE CostRowNo <= 3;";
        System.out.println(query);
        System.out.printf("%721s%n", "^");

        Statement stat = conn.createStatement();
        int c = stat.executeUpdate(query);
        System.out.println("Copied " + c +
                           " good offer-combinations to alt table.");

        clear_solutions(conn);
        move_from_alt_to_main(conn);
    }

    private void move_from_alt_to_main(Connection conn) throws SQLException {
        Statement stat = conn.createStatement();
        stat.executeUpdate("ALTER TABLE transport_solutions RENAME TO ts_temp");
        stat.executeUpdate(
            "ALTER TABLE transport_solutions_alt RENAME TO transport_solutions");
        stat.executeUpdate(
            "ALTER TABLE ts_temp RENAME TO transport_solutions_alt");
        System.out.println("Swapped alt/main table.");
    }
}

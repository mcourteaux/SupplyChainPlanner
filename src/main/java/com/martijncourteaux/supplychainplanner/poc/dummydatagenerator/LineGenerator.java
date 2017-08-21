/* 
 * Copyright (C) 2017 Martijn Courteaux.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.poc.dummydatagenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class LineGenerator {

    public void generate(Connection conn, int numLines, int numLocations) throws SQLException {
        String query
                = "INSERT INTO transport_lines (line_from, line_to, line_distance, line_code, line_modality) VALUES(?, ?, ?, ?, ?);";
        PreparedStatement stat = conn.prepareStatement(query);

        Random random = new Random();

        for (int i = 0; i < numLines; ++i) {
            int loc_from = random.nextInt(numLocations);
            int loc_to = (loc_from + random.nextInt(20) + numLocations - 10) % numLocations;
            stat.setInt(1, loc_from + 1);
            stat.setInt(2, loc_to + 1);
            stat.setDouble(3, random.nextDouble() * 1000);
            stat.setString(4, String.format("LINE_%05d", i + 1));
            String modality;
            switch (random.nextInt(4)) {
                case 0:
                case 1:
                case 2:
                    modality = "road";
                    break;

                case 3:
                    modality = "ferry";
                    break;
                default:
                    modality = null;
            }
            stat.setString(5, modality);

            stat.addBatch();
            if (i % 100 == 0) {
                stat.executeBatch();
                System.out.println("Inserted lines " + i);
                stat.clearBatch();
            }
        }
        stat.executeBatch();
        stat.clearBatch();
        System.out.println("Done");
    }
}

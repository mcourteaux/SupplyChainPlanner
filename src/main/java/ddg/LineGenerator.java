package ddg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class LineGenerator {
    public void generate(Connection conn, int numLines, int numLocations) throws SQLException {
        String query =
            "INSERT INTO transport_lines (line_from, line_to, line_distance, line_code, line_modality) VALUES(?, ?, ?, ?, ?);";
        PreparedStatement stat = conn.prepareStatement(query);

        Random random = new Random();

        for (int i = 0; i < numLines; ++i) {
            stat.setInt(1, random.nextInt(numLocations) + 1);
            stat.setInt(2, random.nextInt(numLocations) + 1);
            stat.setDouble(3, random.nextDouble() * 1000);
            stat.setString(4, String.format("LINE_%05d", i + 1));
            String modality;
            switch (random.nextInt(4)) {
                case 0:
                case 1:
                case 2: modality = "road"; break;

                case 3: modality = "ferry"; break;
                default: modality = null;
            }
            stat.setString(5, modality);

            stat.executeUpdate();
            System.out.println("Inserted line " + i);
        }
    }
}

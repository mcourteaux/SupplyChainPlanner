package ddg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class LocationGenerator {
    public void generate(Connection conn, int numLocations) throws SQLException {
        String query =
            "INSERT INTO locations (location_lat, location_lon, location_desc) VALUES(?, ?, ?);";
        PreparedStatement stat = conn.prepareStatement(query);

        Random random = new Random();

        for (int i = 0; i < numLocations; ++i) {
            stat.setDouble(1, random.nextDouble() * 90 - 45);
            stat.setDouble(2, random.nextDouble() * 45);
            stat.setString(3, String.format("LOCATION_%04d", i + 1));

            stat.executeUpdate();
            System.out.println("Inserted row.");
        }
    }
}

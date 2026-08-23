package com.codeit.monew;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ClearDbTest {
    @Test
    public void clearDb() throws Exception {
        String url = "jdbc:postgresql://ep-late-frog-azt9pgds.c-3.ap-southeast-1.aws.neon.tech/neondb?sslmode=require";
        String user = "neondb_owner";
        String password = "npg_CxrYG4PSOe9T";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE article CASCADE;");
            System.out.println("Articles cleared successfully!");
        }
    }
}

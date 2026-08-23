package com.codeit.monew;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckDbTest {
    @Test
    public void checkDb() throws Exception {
        String url = "jdbc:postgresql://ep-late-frog-azt9pgds.c-3.ap-southeast-1.aws.neon.tech/neondb?sslmode=require";
        String user = "neondb_owner";
        String password = "npg_CxrYG4PSOe9T";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM article;");
            rs.next();
            int articles = rs.getInt(1);
            
            rs = stmt.executeQuery("SELECT count(*) FROM interest;");
            rs.next();
            int interests = rs.getInt(1);

            throw new RuntimeException("Articles: " + articles + ", Interests: " + interests);
        }
    }
}

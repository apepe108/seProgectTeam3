package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.dbinteract.SiteDecorator;
import it.unisa.diem.se.team3.models.Site;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SiteDecoratorTest {
    private SiteDecorator db;

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new SiteDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "INSERT INTO factory_site (id, name) VALUES (1, 'Factory Site 1'); " +
                "INSERT INTO area (id, name, factory_site) VALUES (1, 'Area 1', 1), (2, 'Area 2', 1); " +
                "INSERT INTO site (id, factory_site, area) VALUES (1, 1, 1), (2, 1, 2);";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException ignored) {
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM site CASCADE; " +
                "DELETE FROM area CASCADE;" +
                "DELETE FROM factory_site CASCADE; ";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void getSite() {
        // Actual
        List<Site> actual = db.getSite();

        // Expected
        ArrayList<Site> expected = new ArrayList<>();
        expected.add(new Site(1, "Factory Site 1-Area 1"));
        expected.add(new Site(2, "Factory Site 1-Area 2"));

        // Match
        assertEquals(expected, actual);
    }
}
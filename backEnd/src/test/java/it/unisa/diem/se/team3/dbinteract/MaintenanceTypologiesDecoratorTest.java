package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.dbinteract.MaintenanceTypologiesDecorator;
import it.unisa.diem.se.team3.models.MaintenanceTypologies;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceTypologiesDecoratorTest {
    private MaintenanceTypologiesDecorator db;

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new MaintenanceTypologiesDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "INSERT INTO maintenance_typologies (id, name, description)  " +
                "VALUES (1, 'Typologies 1', 'Description Typologies 1'); " +
                "INSERT INTO maintenance_typologies (id, name, description) " +
                "VALUES (2, 'Typologies 2', 'Description Typologies 2'); " +
                "INSERT INTO maintenance_typologies (id, name, description) " +
                "VALUES (3, 'Typologies 3', 'Description Typologies 3');";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM maintenance_typologies CASCADE; ";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void getMaintenanceTypologies() {
        // Actual
        List<MaintenanceTypologies> actual = db.getMaintenanceTypologies();

        // Expected
        ArrayList<MaintenanceTypologies> expected = new ArrayList<>();
        expected.add(new MaintenanceTypologies(1, "Typologies 1", "Description Typologies 1"));
        expected.add(new MaintenanceTypologies(2, "Typologies 2", "Description Typologies 2"));
        expected.add(new MaintenanceTypologies(3, "Typologies 3", "Description Typologies 3"));

        // Match
        assertEquals(expected, actual);
    }
}
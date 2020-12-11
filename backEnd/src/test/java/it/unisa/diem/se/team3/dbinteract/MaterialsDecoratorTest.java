package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.dbinteract.MaterialsDecorator;
import it.unisa.diem.se.team3.models.Materials;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaterialsDecoratorTest {
    private MaterialsDecorator db;

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new MaterialsDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "INSERT INTO materials (id, name, description)  " +
                "VALUES (1, 'Material 1', 'Description material 1'); " +
                "INSERT INTO materials (id, name, description)  " +
                "VALUES (2, 'Material 2', 'Description material 2'); " +
                "INSERT INTO materials (id, name, description)  " +
                "VALUES (3, 'Material 3', 'Description material 3'); ";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException ignored) {
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM materials CASCADE; ";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void getMaterials() {
        // Actual
        List<Materials> actual = db.getMaterials();

        // Expected
        ArrayList<Materials> expected = new ArrayList<>();
        expected.add(new Materials(1, "Material 1", "Description material 1"));
        expected.add(new Materials(2, "Material 2", "Description material 2"));
        expected.add(new Materials(3, "Material 3", "Description material 3"));

        // Match
        assertEquals(expected, actual);
    }
}
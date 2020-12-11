package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.dbinteract.MaintenanceProcedureDecorator;
import it.unisa.diem.se.team3.models.MaintenanceProcedure;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MaintenanceProcedureDecoratorTest {
    private MaintenanceProcedureDecorator db;

    @BeforeEach
    void setUp() throws IOException {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new MaintenanceProcedureDecorator(ServletUtil.connectDb());
        db.connect();

        byte[] array = Files.readAllBytes(Paths.get("./src/test/resources/dzonerc99-securityinjavaeeapplications.pdf"));

        String populateQuery = "ALTER SEQUENCE maintenance_procedures_id RESTART WITH 4; " +
                "INSERT INTO smp (id, pdf_file) VALUES (nextval('smp_id'), ?); " +
                "INSERT INTO maintenance_procedures (id, name, smp) VALUES (1, 'Procedure 1', 1), (2, 'Procedure 2', null), (3, 'Procedure 3', null);";

        try (PreparedStatement stmt = db.getConn().prepareStatement(populateQuery)) {
            stmt.setBytes(1, array);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM maintenance_procedures CASCADE; " +
                "ALTER SEQUENCE maintenance_procedures_id RESTART WITH 1;" +
                "DELETE FROM smp CASCADE;" +
                "ALTER SEQUENCE smp_id RESTART WITH 1;";
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void getMaintenanceProcedures() {
        // Actual
        List<MaintenanceProcedure> actual = db.getMaintenanceProcedures();

        // Expected
        ArrayList<MaintenanceProcedure> expected = new ArrayList<>();
        expected.add(new MaintenanceProcedure(1, "Procedure 1", 1));
        expected.add(new MaintenanceProcedure(2, "Procedure 2", 0));
        expected.add(new MaintenanceProcedure(3, "Procedure 3", 0));

        // Match
        assertEquals(expected, actual);
    }

    @Test
    void getSmp() throws IOException {
        byte[] actual = db.getSmp(1);

        byte[] expected = Files.readAllBytes(Paths.get("./src/test/resources/dzonerc99-securityinjavaeeapplications.pdf"));

        for (int i = 0; i < actual.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void getSmpNoExisting() {
        byte[] actual = db.getSmp(7);

        assertNull(actual);
    }

    @Test
    void associateSmp() throws IOException {
        InputStream is = new FileInputStream("./src/test/resources/WordTeXPaper.pdf");
        db.associateSmp(2, is);

        // Expected
        ArrayList<MaintenanceProcedure> expected = new ArrayList<>();
        expected.add(new MaintenanceProcedure(1, "Procedure 1", 1));
        expected.add(new MaintenanceProcedure(2, "Procedure 2", 2));
        expected.add(new MaintenanceProcedure(3, "Procedure 3", 0));

        // Actual
        List<MaintenanceProcedure> actual = db.getMaintenanceProcedures();

        // Match
        assertEquals(expected, actual);

        byte[] act = db.getSmp(2);

        byte[] exp = Files.readAllBytes(Paths.get("./src/test/resources/WordTeXPaper.pdf"));

        for (int i = 0; i < act.length; i++) {
            assertEquals(exp[i], act[i]);
        }
    }
}
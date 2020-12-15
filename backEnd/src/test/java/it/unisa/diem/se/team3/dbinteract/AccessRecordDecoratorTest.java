package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.AccessRecord;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccessRecordDecoratorTest {
    private AccessRecordDecorator db;

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new AccessRecordDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "INSERT INTO access_record (id, email, name, role, login_date, logout_date)" +
                "VALUES (1, 'tizio@email.com', 'tizio', 'SysAdmin', '2021-12-13 12:45:05', '2021-12-13 15:40:44'); ";

        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM access_record CASCADE;" +
                "ALTER SEQUENCE access_record_id RESTART WITH 1;";
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void getAccessRecord() {
        // Actual
        List<AccessRecord> actual = db.getAccessRecord();

        // Expected
        ArrayList<AccessRecord> expected = new ArrayList<>();
        expected.add(new AccessRecord("tizio@email.com", "tizio", "SysAdmin",
                LocalDateTime.of(2021,12,13,12,45,5),
                LocalDateTime.of(2021, 12, 13, 15, 40, 44)));

        // Match
        assertEquals(expected, actual);
    }

    @Test
    void createAccess() {
        long id = db.createAccess("mail", "name", "role");

        assertTrue(db.endAccess(id));
    }

}
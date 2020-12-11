package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.MaintenanceProcedureDecorator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceProcedureServletTest {
    private MaintenanceProcedureDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(MaintenanceProcedureServlet.class, new String[]{"/procedure", "/smp", "/view-smp"});
        tester.startServer();
    }

    @AfterAll
    static void afterAll() throws Exception {
        tester.shutdownServer();
    }

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
    void doGetProcedure() throws IOException {
        // Test GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/procedure").openConnection();
        http.connect();
        assertEquals(HttpStatus.OK_200, http.getResponseCode());
        assertEquals("[{\"id\":\"1\",\"name\":\"Procedure 1\",\"smp\":\"1\"},{\"id\":\"2\",\"name\":\"Procedure 2\",\"smp\":\"0\"},{\"id\":\"3\",\"name\":\"Procedure 3\",\"smp\":\"0\"}]", tester.readPage(http));
    }

    // @Test
    // void doGetSmp() throws IOException {
    //     // Test GET
    //     HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/view-smp?id=1").openConnection();
    //     http.connect();
    //     assertEquals(HttpStatus.OK_200, http.getResponseCode());
    //
    //     byte[] expected = Files.readAllBytes(Paths.get("./src/test/resources/dzonerc99-securityinjavaeeapplications.pdf"));
    //
    //     byte[] actual = tester.readPage(http).getBytes();
    //
    //     for (int i = 0; i < expected.length; i++) {
    //         assertEquals(expected[i], actual[i]);
    //     }
    // }
}
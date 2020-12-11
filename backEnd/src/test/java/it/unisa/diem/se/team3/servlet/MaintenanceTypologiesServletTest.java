package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.MaintenanceTypologiesDecorator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceTypologiesServletTest {
    private MaintenanceTypologiesDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(MaintenanceTypologiesServlet.class, new String[]{"/typology"});
        tester.startServer();
    }

    @AfterAll
    static void afterAll() throws Exception {
        tester.shutdownServer();
    }

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
    void doGet() throws IOException {
        // Test GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/typology").openConnection();
        http.connect();
        assertEquals(HttpStatus.OK_200, http.getResponseCode());
        assertEquals("[{\"id\":\"1\",\"name\":\"Typologies 1\",\"description\":\"Description Typologies 1\"},{\"id\":\"2\",\"name\":\"Typologies 2\",\"description\":\"Description Typologies 2\"},{\"id\":\"3\",\"name\":\"Typologies 3\",\"description\":\"Description Typologies 3\"}]", tester.readPage(http));
    }
}
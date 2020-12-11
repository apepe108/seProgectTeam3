package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.SiteDecorator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SiteServletTest {
    private SiteDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(SiteServlet.class, new String[]{"/site"});
        tester.startServer();
    }

    @AfterAll
    static void afterAll() throws Exception {
        tester.shutdownServer();
    }

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
    void doGet() throws IOException {
        // Test GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/site").openConnection();
        http.connect();
        assertEquals(HttpStatus.OK_200, http.getResponseCode());
        assertEquals("[{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"},{\"id\":\"2\",\"name\":\"Factory Site 1-Area 2\"}]", tester.readPage(http));
    }
}
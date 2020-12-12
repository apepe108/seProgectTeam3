package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.MaterialsDecorator;
import it.unisa.diem.se.team3.models.JsonUtil;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaterialsServletTest {
    private MaterialsDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(MaterialsServlet.class, new String[]{"/material", "/edit-material", "/delete-material", "/create-material"});
        tester.startServer();
    }

    @AfterAll
    static void afterAll() throws Exception {
        tester.shutdownServer();
    }

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new MaterialsDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "DELETE FROM materials CASCADE;" +
                "ALTER SEQUENCE materials_id RESTART WITH 4; " +
                "INSERT INTO materials (id, name, description)  " +
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
        String deleteQuery = "DELETE FROM materials CASCADE; " +
                "ALTER SEQUENCE materials_id RESTART WITH 1;";
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
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/material").openConnection();
        http.connect();
        assertEquals(HttpStatus.OK_200, http.getResponseCode());
        assertEquals("[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description material 1\"},{\"id\":\"2\",\"name\":\"Material 2\",\"description\":\"Description material 2\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description material 3\"}]", tester.readPage(http));
    }
}
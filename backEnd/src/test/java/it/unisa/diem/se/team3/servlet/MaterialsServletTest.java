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
    void doPostCreate() throws IOException {
        // TRY POST
        String urlParameters = "name=New Material&description=Description new material.";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/create-material";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }
        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        // Verify correct insert
        String expected = "[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description material 1\"},{\"id\":\"2\",\"name\":\"Material 2\",\"description\":\"Description material 2\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description material 3\"},{\"id\":\"4\",\"name\":\"New Material\",\"description\":\"Description new material.\"}]";
        assertEquals(expected, JsonUtil.toJson(db.getMaterials()));
    }


    @Test
    void doPostCreateMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "name=New Material";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/create-material";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }
        assertEquals(HttpStatus.BAD_REQUEST_400, http.getResponseCode());
    }

    @Test
    void doGetAll() throws IOException, InterruptedException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/material").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description material 1\"},{\"id\":\"2\",\"name\":\"Material 2\",\"description\":\"Description material 2\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description material 3\"}]";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetOne() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/material?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description material 1\"}";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetOneNoExisting() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/material?id=6").openConnection();
        http.connect();

        assertEquals(HttpStatus.EXPECTATION_FAILED_417, http.getResponseCode());
    }

    @Test
    void doGetDelete() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/delete-material?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        // Verify correct delete
        String expected = "[{\"id\":\"2\",\"name\":\"Material 2\",\"description\":\"Description material 2\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description material 3\"}]";
        assertEquals(expected, JsonUtil.toJson(db.getMaterials()));
    }

    @Test
    void doGetDeleteNoId() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/delete-material").openConnection();
        http.connect();

        assertEquals(HttpStatus.BAD_REQUEST_400, http.getResponseCode());
    }

    @Test
    void doPostEdit() throws IOException {
        // TRY POST
        String urlParameters = "id=1&name=New Material&description=New description of material";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-material";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }
        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        // Verify correct insert
        String expected = "[{\"id\":\"1\",\"name\":\"New Material\",\"description\":\"New description of material\"},{\"id\":\"2\",\"name\":\"Material 2\",\"description\":\"Description material 2\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description material 3\"}]";
        assertEquals(expected, JsonUtil.toJson(db.getMaterials()));
    }

    @Test
    void doPostEditMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "name=New Material&description=New description of material";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-material";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }
        assertEquals(HttpStatus.BAD_REQUEST_400, http.getResponseCode());
    }
}
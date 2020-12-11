package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.models.JsonUtil;
import it.unisa.diem.se.team3.dbinteract.MaintainerRoleDecorator;

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

class MaintainerRoleServletTest {
    private MaintainerRoleDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(MaintainerRoleServlet.class, new String[]{"/maintainer-role", "/edit-maintainer-role",
                "/create-maintainer-role", "/delete-maintainer-role"});
        tester.startServer();
    }

    @AfterAll
    static void afterAll() throws Exception {
        tester.shutdownServer();
    }

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new MaintainerRoleDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "DELETE FROM maintainer_role CASCADE;" +
                "ALTER SEQUENCE maintainer_role_id RESTART WITH 4; " +
                "INSERT INTO maintainer_role (id, name, description) " +
                "VALUES (1, 'Role 1', 'Description role 1.'), (2, 'Role 2', 'Description role 2.'), (3, 'Role 3', 'Description role 3.');";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM maintainer_role CASCADE; " +
                "ALTER SEQUENCE maintainer_role_id RESTART WITH 1;";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void doGetAll() throws IOException, InterruptedException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/maintainer-role").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "[{\"id\":\"1\",\"name\":\"Role 1\",\"description\":\"Description role 1.\"},{\"id\":\"2\",\"name\":\"Role 2\",\"description\":\"Description role 2.\"},{\"id\":\"3\",\"name\":\"Role 3\",\"description\":\"Description role 3.\"}]";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetOne() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/maintainer-role?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "{\"id\":\"1\",\"name\":\"Role 1\",\"description\":\"Description role 1.\"}";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetOneNoExisting() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/maintainer-role?id=6").openConnection();
        http.connect();

        assertEquals(HttpStatus.EXPECTATION_FAILED_417, http.getResponseCode());
    }

    @Test
    void doGetDelete() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/delete-maintainer-role?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        // Verify correct delete
        String expected = "[{\"id\":\"2\",\"name\":\"Role 2\",\"description\":\"Description role 2.\"},{\"id\":\"3\",\"name\":\"Role 3\",\"description\":\"Description role 3.\"}]";
        assertEquals(expected, JsonUtil.toJson(db.getMaintainerRoles()));
    }

    @Test
    void doGetDeleteNoId() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/delete-maintainer-role").openConnection();
        http.connect();

        assertEquals(HttpStatus.BAD_REQUEST_400, http.getResponseCode());
    }

    @Test
    void doPostCreate() throws IOException {
        // TRY POST
        String urlParameters = "name=New Role&description=Description new role.";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/create-maintainer-role";
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
        String expected = "[{\"id\":\"1\",\"name\":\"Role 1\",\"description\":\"Description role 1.\"},{\"id\":\"2\",\"name\":\"Role 2\",\"description\":\"Description role 2.\"},{\"id\":\"3\",\"name\":\"Role 3\",\"description\":\"Description role 3.\"},{\"id\":\"4\",\"name\":\"New Role\",\"description\":\"Description new role.\"}]";
        assertEquals(expected, JsonUtil.toJson(db.getMaintainerRoles()));
    }

    @Test
    void doPostCreateMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "name=New Role";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/create-maintainer-role";
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
    void doPostEdit() throws IOException {
        // TRY POST
        String urlParameters = "id=1&name=New Role&description=Description new role.";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-maintainer-role";
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
        String expected = "[{\"id\":\"1\",\"name\":\"New Role\",\"description\":\"Description new role.\"},{\"id\":\"2\",\"name\":\"Role 2\",\"description\":\"Description role 2.\"},{\"id\":\"3\",\"name\":\"Role 3\",\"description\":\"Description role 3.\"}]";
        assertEquals(expected, JsonUtil.toJson(db.getMaintainerRoles()));
    }

    @Test
    void doPostEditMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "name=New Role&description=Description new role.";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-maintainer-role";
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
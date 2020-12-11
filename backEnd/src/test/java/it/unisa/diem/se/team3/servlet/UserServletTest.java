package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.UserDecorator;
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

public class UserServletTest {
    private UserDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(UserServlet.class, new String[]{"/user", "/edit-user", "/create-user", "/delete-user"});
        tester.startServer();
    }

    @AfterAll
    static void afterAll() throws Exception {
        tester.shutdownServer();
    }

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new UserDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "ALTER SEQUENCE user_id RESTART WITH 3;" +
                "INSERT INTO users (internal_id, email, password) VALUES (1, 'user1@email.com', 'password1'); " +
                "INSERT INTO users (internal_id, email, password) VALUES (2, 'user2@email.com', 'password2'); " +
                "INSERT INTO planner (internal_id, name, email) VALUES (1, 'UserName1', 'user1@email.com'); " +
                "INSERT INTO maintainer (internal_id, name, email) VALUES (2, 'UserName2', 'user2@email.com');";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "ALTER SEQUENCE user_id RESTART WITH 1;" +
                "DELETE FROM users CASCADE; " +
                "DELETE FROM planner CASCADE; " +
                "DELETE FROM maintainer CASCADE;";
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
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/user").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "[{\"id\":\"1\",\"name\":\"UserName1\",\"email\":\"user1@email.com\",\"password\":\"password1\",\"role\":\"Planner\"}," +
                "{\"id\":\"2\",\"name\":\"UserName2\",\"email\":\"user2@email.com\",\"password\":\"password2\",\"role\":\"Maintainer\"}]";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetOne() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/user?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "{\"id\":\"1\",\"name\":\"UserName1\",\"email\":\"user1@email.com\",\"password\":\"password1\",\"role\":\"Planner\"}";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetOneNoExisting() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/user?id=6").openConnection();
        http.connect();

        assertEquals(HttpStatus.EXPECTATION_FAILED_417, http.getResponseCode());
    }

    @Test
    void doGetDelete() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/delete-user?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        // Verify correct delete
        String expected = "[{\"id\":\"2\",\"name\":\"UserName2\",\"email\":\"user2@email.com\",\"password\":\"password2\",\"role\":\"Maintainer\"}]";
        assertEquals(expected, JsonUtil.toJson(db.getUsers()));
    }

    @Test
    void doGetDeleteNoId() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/delete-user").openConnection();
        http.connect();

        assertEquals(HttpStatus.BAD_REQUEST_400, http.getResponseCode());
    }

    @Test
    void doPostCreate() throws IOException {
        // TRY POST
        String urlParameters = "name=New user&email=newuser@email.com&password=new password&role=planner";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/create-user";
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
        String expected = "[{\"id\":\"1\",\"name\":\"UserName1\",\"email\":\"user1@email.com\",\"password\":\"password1\",\"role\":\"Planner\"}," +
                "{\"id\":\"2\",\"name\":\"UserName2\",\"email\":\"user2@email.com\",\"password\":\"password2\",\"role\":\"Maintainer\"}," +
                "{\"id\":\"3\",\"name\":\"New user\",\"email\":\"newuser@email.com\",\"password\":\"new password\",\"role\":\"Planner\"}]";
        assertEquals(expected, JsonUtil.toJson(db.getUsers()));
    }

    @Test
    void doPostCreateMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "name=New user";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/create-user";
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
        String urlParameters = "id=1&name=New user1&email=newemail1@email.com&password=new password1&role=planner";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-user";
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
        String expected = "[{\"id\":\"1\",\"name\":\"New user1\",\"email\":\"newemail1@email.com\",\"password\":\"new password1\",\"role\":\"Planner\"}," +
                "{\"id\":\"2\",\"name\":\"UserName2\",\"email\":\"user2@email.com\",\"password\":\"password2\",\"role\":\"Maintainer\"}]";
        assertEquals(expected, JsonUtil.toJson(db.getUsers()));
    }

    @Test
    void doPostEditMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "name=New user1&email=newemail1@email.com&password=new password1&role=Maintainer";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-user";
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

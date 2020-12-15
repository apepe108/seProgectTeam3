package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.AccessRecordDecorator;
import it.unisa.diem.se.team3.dbinteract.UserDecorator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class LoginServletTest {
    private UserDecorator db;
    private AccessRecordDecorator db1;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(LoginServlet.class, new String[]{"/login", "/logout", "/access-record"});
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
        db1 = new AccessRecordDecorator(db);
        db.connect();

        String populateQuery = "ALTER SEQUENCE user_id RESTART WITH 3; " +
                "INSERT INTO users (internal_id, email, password) VALUES (1, 'user1@mail.com', 'pass1'), " +
                "(2, 'user2@mail.com', 'pass2'); " +
                "INSERT INTO sysadmin (internal_id, email, name) VALUES (1, 'user1@mail.com', 'Sig. SysAdmin 1'); " +
                "INSERT INTO planner (internal_id, email, name) VALUES (2, 'user2@mail.com', 'Sig. planner 2'); " +
                "INSERT  INTO access_record (id, email, name, role, login_date, logout_date) " +
                "VALUES (1, 'tizio@email.com', 'tizio', 'SysAdmin', '2021-12-13 12:45:05' , '2021-12-13 15:40:44')";
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "ALTER SEQUENCE user_id RESTART WITH 3; " +
                "DELETE FROM users CASCADE;" +
                "DELETE FROM sysadmin CASCADE;" +
                "DELETE FROM planner CASCADE;" +
                "ALTER SEQUENCE access_record_id RESTART WITH 3;" +
                "DELETE FROM access_record CASCADE";
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void doLoginCorrect() throws IOException {
        // TRY POST
        String urlParameters = "email=user1@mail.com&password=pass1";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/login";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }

        assertEquals("JSESSIONID=", http.getHeaderField("Set-Cookie").substring(0, 11));
        assertEquals(ServletUtil.getProperty("httpserver.sysadmin"), http.getHeaderField("Location"));
        assertEquals(HttpStatus.FOUND_302, http.getResponseCode());
    }

    @Test
    void doLoginFail() throws IOException {
        // TRY POST
        String urlParameters = "email=user1@mail.com&password=passWrong";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/login";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }

        assertNull(http.getHeaderField("Set Cookie"));
        assertEquals(ServletUtil.getProperty("httpserver.login") + "?err=true", http.getHeaderField("Location"));
        assertEquals(HttpStatus.FOUND_302, http.getResponseCode());
    }

    @Test
    void doLoginNotExistingMail() throws IOException {
        // TRY POST
        String urlParameters = "email=user4@mail.com&password=passWrong";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/login";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }

        assertNull(http.getHeaderField("Set Cookie"));
        assertEquals(ServletUtil.getProperty("httpserver.login") + "?err=true", http.getHeaderField("Location"));
        assertEquals(HttpStatus.FOUND_302, http.getResponseCode());
    }

    @Test
    void doLoginMissingParameter() throws IOException {
        // TRY POST
        String urlParameters = "email=user4@mail.com";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/login";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }

        assertNull(http.getHeaderField("Set Cookie"));
        assertEquals(ServletUtil.getProperty("httpserver.login") + "?err=true", http.getHeaderField("Location"));
        assertEquals(HttpStatus.FOUND_302, http.getResponseCode());
    }

    @Test
    void doLogout() throws IOException {
        // TRY POST
        String urlParameters = "";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/logout";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }

        assertNull(http.getHeaderField("Set-Cookie"));
        assertEquals(ServletUtil.getProperty("httpserver.login"), http.getHeaderField("Location"));
        assertEquals(HttpStatus.FOUND_302, http.getResponseCode());
    }

    @Test
    void doGet() throws IOException {
        // Test GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/access-record").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());
        assertEquals("[{\"email\":\"tizio@email.com\",\"name\":\"tizio\",\"role\":\"SysAdmin\"," +
                "\"login_date\":\"13-12-2021 12:45:05\",\"logout_date\":\"13-12-2021 15:40:44\"}]", tester.readPage(http));
    }
}
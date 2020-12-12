package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.ActivityDecorator;
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

import static org.junit.jupiter.api.Assertions.*;

class AssignServletTest {
    private ActivityDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(AssignServlet.class, new String[]{"/assign"});
        tester.startServer();
    }

    @AfterAll
    static void afterAll() throws Exception {
        tester.shutdownServer();
    }

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new ActivityDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "INSERT INTO materials (id, name, description)  " +
                "VALUES (nextval('materials_id'), 'Material 1', 'Description Material 1.'),  " +
                "(nextval('materials_id'), 'Material 2', 'Description Material 2.'),  " +
                "(nextval('materials_id'), 'Material 3', 'Description Material 3.'); " +
                "INSERT INTO maintenance_typologies (id, name, description)  " +
                "VALUES (1, 'Typologies 1', 'Description typologies 1'), " +
                "(2, 'Typologies 2', 'Description typologies 2'); " +
                "INSERT INTO maintenance_procedures (id, name)  " +
                "VALUES (1, 'Procedure 1'), (2, 'Procedure 2'); " +
                "INSERT INTO workspace_notes (id, description) VALUES (1, 'Description workspace notes 1'), (2, 'Description workspace notes 2'); " +
                "INSERT INTO factory_site (id, name) VALUES (1, 'Factory Site 1'); " +
                "INSERT INTO area (id, name, factory_site, workspace_notes) VALUES (1, 'Area 1', 1, 1), (2, 'Area 2', 1, null); " +
                "INSERT INTO site (id, factory_site, area) VALUES (1, 1, 1), (2, 1, 2); " +
                "ALTER SEQUENCE workspace_notes_id RESTART WITH 3;" +
                "INSERT INTO activity (id, year, week, day, type, interruptibility, estimated_intervention_time, description, " +
                "maintenance_typologies, maintenance_procedures, site)  " +
                "VALUES (nextval('activity_id'), 2020, 21, null, 'p', true, 30, 'Activity 1 description.', 1, 1, 1), " +
                "(nextval('activity_id'), 2020, 21, null, 'e', true, 90, 'Activity 2 description.', 2, 1, 2), " +
                "(nextval('activity_id'), 2020, 21, null, 'p', true, 20, 'Activity 3 description.', 2, 2, 1), " +
                "(nextval('activity_id'), 2020, 21, null, 'p', true, 40, 'Activity 4 description.', 1, 2, 2); " +
                "INSERT INTO daily_time_slot (id, hour_start, duration)  " +
                "VALUES (1, '06:00:00', 60), (2, '07:00:00', 60), (3, '08:00:00', 60); " +
                "INSERT INTO users (internal_id, email, password) VALUES (1, 'pepealessio.ap@gmail.com', 'password1'), " +
                "(2, 'user2@email.com', 'password2'); " +
                "INSERT INTO maintainer (internal_id, name, email) VALUES (1, 'UserName1', 'pepealessio.ap@gmail.com'), " +
                "(2, 'UserName2', 'user2@email.com'); " +
                "INSERT INTO need (activity, materials) " +
                "VALUES (1, 1), (1, 2), (1, 3), (3, 1), (4, 1), (4, 3);" +
                "ALTER SEQUENCE activity_id RESTART WITH 5;";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM assigned_slot CASCADE; " +
                "DELETE FROM daily_time_slot CASCADE; " +
                "DELETE FROM assigned_activity CASCADE; " +
                "DELETE FROM need CASCADE; " +
                "DELETE FROM activity CASCADE; ALTER SEQUENCE activity_id RESTART WITH 1; " +
                "DELETE FROM materials CASCADE; ALTER SEQUENCE materials_id RESTART WITH 1; " +
                "DELETE FROM users CASCADE; " +
                "DELETE FROM maintainer CASCADE; " +
                "DELETE FROM maintenance_typologies CASCADE; " +
                "DELETE FROM maintenance_procedures CASCADE; " +
                "DELETE FROM site CASCADE; " +
                "DELETE FROM area CASCADE;" +
                "DELETE FROM factory_site CASCADE; " +
                "ALTER SEQUENCE workspace_notes_id RESTART WITH 1;" +
                "DELETE FROM workspace_notes CASCADE;" +
                "ALTER SEQUENCE activity_id RESTART WITH 1;";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void assignActivity() throws IOException{
        // TRY POST
        String urlParameters = "activity-id=2&maintainer-id=1&day=1&slot-id=1&slot-id=2&minutes=60&minutes=30";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/assign";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }

        assertEquals(HttpStatus.CREATED_201, http.getResponseCode());
    }

    @Test
    void assignActivityMissingParameter() throws IOException{
        // TRY POST
        String urlParameters = "activity-id=2&day=1&slot-id=1&slot-id=2&minutes=60&minutes=30";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/assign";
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
    void assignActivityFailCase() throws IOException{
        // TRY POST
        String urlParameters = "activity-id=2&maintainer-id=1&day=1&slot-id=1&slot-id=2&minutes=60&minutes=10";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/assign";
        URL url = new URL(request);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("charset", "utf-8");
        http.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.write(postData);
        }

        assertEquals(HttpStatus.EXPECTATION_FAILED_417, http.getResponseCode());
    }
}
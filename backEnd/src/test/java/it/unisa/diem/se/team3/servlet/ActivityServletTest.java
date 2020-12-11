package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.ActivityDecorator;
import it.unisa.diem.se.team3.models.Activity;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActivityServletTest {
    private ActivityDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(ActivityServlet.class, new String[]{"/activity", "/create-activity", "/edit-activity", "/delete-activity"});
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
                "INSERT INTO users (internal_id, email, password) VALUES (1, 'user1@email.com', 'password1'), " +
                "(2, 'user2@email.com', 'password2'); " +
                "INSERT INTO maintainer (internal_id, name, email) VALUES (1, 'UserName1', 'user1@email.com'), " +
                "(2, 'UserName2', 'user2@email.com'); " +
                "INSERT INTO need (activity, materials) " +
                "VALUES (1, 1), (1, 2), (1, 3), (3, 1), (4, 1), (4, 3);" +
                "ALTER SEQUENCE activity_id RESTART WITH 5;" +
                "INSERT INTO competences (id, name, description) " +
                "VALUES (1, 'Skill 1', 'Description skill 1.')," +
                "(2, 'Skill 2', 'Description skill 2.'), " +
                "(3, 'Skill 3', 'Description skill 3.')," +
                "(4, 'Skill 4', 'Description skill 4.'), " +
                "(5, 'Skill 5', 'Description skill 5.'), " +
                "(6, 'Skill 6', 'Description skill 6.');" +
                "INSERT INTO require (maintenance_procedures, competences) VALUES (1, 1), (1, 3), (1, 4); " +
                "INSERT INTO require_ewo (activity, competences) VALUES (2, 3), (2, 4), (2, 5);";
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM require_ewo CASCADE; " +
                "DELETE FROM require CASCADE; " +
                "DELETE FROM competences CASCADE; " +
                "DELETE FROM assigned_slot CASCADE; " +
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
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void doGetAllPlanned() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/activity?type=planned").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "[{\"id\":\"1\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"},\"typology\":{\"id\":\"1\",\"name\":\"Typologies 1\",\"description\":\"Description typologies 1\"},\"description\":\"Activity 1 description.\",\"estimatedInterventionTime\":\"30\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"},{\"id\":\"2\",\"name\":\"Material 2\",\"description\":\"Description Material 2.\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description Material 3.\"}],\"maintenanceProcedures\":{\"id\":\"1\",\"name\":\"Procedure 1\",\"smp\":\"0\"},\"workspace\":{\"id\":\"1\",\"description\":\"Description workspace notes 1\",\"site\":[]},\"skill\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"},{\"id\":\"4\",\"name\":\"Skill 4\",\"description\":\"Description skill 4.\"}]},{\"id\":\"3\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"},\"typology\":{\"id\":\"2\",\"name\":\"Typologies 2\",\"description\":\"Description typologies 2\"},\"description\":\"Activity 3 description.\",\"estimatedInterventionTime\":\"20\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"}],\"maintenanceProcedures\":{\"id\":\"2\",\"name\":\"Procedure 2\",\"smp\":\"0\"},\"workspace\":{\"id\":\"1\",\"description\":\"Description workspace notes 1\",\"site\":[]},\"skill\":[]},{\"id\":\"4\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"2\",\"name\":\"Factory Site 1-Area 2\"},\"typology\":{\"id\":\"1\",\"name\":\"Typologies 1\",\"description\":\"Description typologies 1\"},\"description\":\"Activity 4 description.\",\"estimatedInterventionTime\":\"40\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description Material 3.\"}],\"maintenanceProcedures\":{\"id\":\"2\",\"name\":\"Procedure 2\",\"smp\":\"0\"},\"workspace\":{\"id\":\"0\",\"description\":\"null\",\"site\":[]},\"skill\":[]}]";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetAllEwo() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/activity?type=ewo").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "[{\"id\":\"2\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"2\",\"name\":\"Factory Site 1-Area 2\"},\"typology\":{\"id\":\"2\",\"name\":\"Typologies 2\",\"description\":\"Description typologies 2\"},\"description\":\"Activity 2 description.\",\"estimatedInterventionTime\":\"90\",\"interruptibility\":\"true\",\"materials\":[],\"maintenanceProcedures\":{\"id\":\"1\",\"name\":\"Procedure 1\",\"smp\":\"0\"},\"workspace\":{\"id\":\"0\",\"description\":\"null\",\"site\":[]},\"skill\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"},{\"id\":\"4\",\"name\":\"Skill 4\",\"description\":\"Description skill 4.\"},{\"id\":\"5\",\"name\":\"Skill 5\",\"description\":\"Description skill 5.\"}]}]";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetOne() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/activity?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "{\"id\":\"1\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"},\"typology\":{\"id\":\"1\",\"name\":\"Typologies 1\",\"description\":\"Description typologies 1\"},\"description\":\"Activity 1 description.\",\"estimatedInterventionTime\":\"30\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"},{\"id\":\"2\",\"name\":\"Material 2\",\"description\":\"Description Material 2.\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description Material 3.\"}],\"maintenanceProcedures\":{\"id\":\"1\",\"name\":\"Procedure 1\",\"smp\":\"0\"},\"workspace\":{\"id\":\"1\",\"description\":\"Description workspace notes 1\",\"site\":[]},\"skill\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"},{\"id\":\"4\",\"name\":\"Skill 4\",\"description\":\"Description skill 4.\"}]}";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetByWeek() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/activity?year=2020&week=21&type=planned").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "[{\"id\":\"1\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"},\"typology\":{\"id\":\"1\",\"name\":\"Typologies 1\",\"description\":\"Description typologies 1\"},\"description\":\"Activity 1 description.\",\"estimatedInterventionTime\":\"30\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"},{\"id\":\"2\",\"name\":\"Material 2\",\"description\":\"Description Material 2.\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description Material 3.\"}],\"maintenanceProcedures\":{\"id\":\"1\",\"name\":\"Procedure 1\",\"smp\":\"0\"},\"workspace\":{\"id\":\"1\",\"description\":\"Description workspace notes 1\",\"site\":[]},\"skill\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"},{\"id\":\"4\",\"name\":\"Skill 4\",\"description\":\"Description skill 4.\"}]},{\"id\":\"3\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"},\"typology\":{\"id\":\"2\",\"name\":\"Typologies 2\",\"description\":\"Description typologies 2\"},\"description\":\"Activity 3 description.\",\"estimatedInterventionTime\":\"20\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"}],\"maintenanceProcedures\":{\"id\":\"2\",\"name\":\"Procedure 2\",\"smp\":\"0\"},\"workspace\":{\"id\":\"1\",\"description\":\"Description workspace notes 1\",\"site\":[]},\"skill\":[]},{\"id\":\"4\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"2\",\"name\":\"Factory Site 1-Area 2\"},\"typology\":{\"id\":\"1\",\"name\":\"Typologies 1\",\"description\":\"Description typologies 1\"},\"description\":\"Activity 4 description.\",\"estimatedInterventionTime\":\"40\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description Material 3.\"}],\"maintenanceProcedures\":{\"id\":\"2\",\"name\":\"Procedure 2\",\"smp\":\"0\"},\"workspace\":{\"id\":\"0\",\"description\":\"null\",\"site\":[]},\"skill\":[]}]";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetByWeekEmpty() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/activity?year=2020&week=24&type=ewo").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "[]";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetOneNotExists() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/activity?id=8").openConnection();
        http.connect();

        assertEquals(HttpStatus.EXPECTATION_FAILED_417, http.getResponseCode());
    }

    @Test
    void doGetNoParameter() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/activity").openConnection();
        http.connect();

        assertEquals(HttpStatus.BAD_REQUEST_400, http.getResponseCode());
    }

    @Test
    void doGetDelete() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/delete-activity?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        // Verify correct delete
        String expected = "[{\"id\":\"3\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"},\"typology\":{\"id\":\"2\",\"name\":\"Typologies 2\",\"description\":\"Description typologies 2\"},\"description\":\"Activity 3 description.\",\"estimatedInterventionTime\":\"20\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"}],\"maintenanceProcedures\":{\"id\":\"2\",\"name\":\"Procedure 2\",\"smp\":\"0\"},\"workspace\":{\"id\":\"1\",\"description\":\"Description workspace notes 1\",\"site\":[]},\"skill\":[]},{\"id\":\"4\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"2\",\"name\":\"Factory Site 1-Area 2\"},\"typology\":{\"id\":\"1\",\"name\":\"Typologies 1\",\"description\":\"Description typologies 1\"},\"description\":\"Activity 4 description.\",\"estimatedInterventionTime\":\"40\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"},{\"id\":\"3\",\"name\":\"Material 3\",\"description\":\"Description Material 3.\"}],\"maintenanceProcedures\":{\"id\":\"2\",\"name\":\"Procedure 2\",\"smp\":\"0\"},\"workspace\":{\"id\":\"0\",\"description\":\"null\",\"site\":[]},\"skill\":[]}]";
        assertEquals(expected, JsonUtil.toJson(db.getActivity('p')));
    }

    @Test
    void doGetDeleteNoParam() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/delete-activity").openConnection();
        http.connect();

        assertEquals(HttpStatus.BAD_REQUEST_400, http.getResponseCode());
    }

    @Test
    void doPostCreate() throws IOException {
        // TRY POST
        String urlParameters = "year=2020&week=40&type=ewo&day=3&interruptibility=true&typologyId=1&siteId=2&materials=1&materials=2&workspace=Hello Test&skill-id=1&skill-id=3&skill-id=5";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/create-activity";
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
        String expected = "[{\"id\":\"2\",\"year\":\"2020\",\"week\":\"21\",\"day\":\"0\",\"site\":{\"id\":\"2\",\"name\":\"Factory Site 1-Area 2\"},\"typology\":{\"id\":\"2\",\"name\":\"Typologies 2\",\"description\":\"Description typologies 2\"},\"description\":\"Activity 2 description.\",\"estimatedInterventionTime\":\"90\",\"interruptibility\":\"true\",\"materials\":[],\"maintenanceProcedures\":{\"id\":\"1\",\"name\":\"Procedure 1\",\"smp\":\"0\"},\"workspace\":{\"id\":\"3\",\"description\":\"Hello Test\",\"site\":[]},\"skill\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"},{\"id\":\"4\",\"name\":\"Skill 4\",\"description\":\"Description skill 4.\"},{\"id\":\"5\",\"name\":\"Skill 5\",\"description\":\"Description skill 5.\"}]}" +
                ",{\"id\":\"5\",\"year\":\"2020\",\"week\":\"40\",\"day\":\"3\",\"site\":{\"id\":\"2\",\"name\":\"Factory Site 1-Area 2\"},\"typology\":{\"id\":\"1\",\"name\":\"Typologies 1\",\"description\":\"Description typologies 1\"},\"description\":\"null\",\"estimatedInterventionTime\":\"0\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"},{\"id\":\"2\",\"name\":\"Material 2\",\"description\":\"Description Material 2.\"}],\"maintenanceProcedures\":{\"id\":\"0\",\"name\":\"null\",\"smp\":\"0\"},\"workspace\":{\"id\":\"3\",\"description\":\"Hello Test\",\"site\":[]},\"skill\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"},{\"id\":\"5\",\"name\":\"Skill 5\",\"description\":\"Description skill 5.\"}]}]";
        assertEquals(expected, JsonUtil.toJson(db.getActivity('e')));
    }

    @Test
    void doPostCreateMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "year=2020&type=ewo&interruptibility=true&typologyId=1&procedureId=1&siteId=1&materials=1&materials=2";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/create-activity";
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
        String urlParameters = "id=1&year=2020&week=23&day=4&type=ewo&interruptibility=true&time=&typologyId=1&procedureId=1&siteId=1&materials=1&materials=2&workspace=New description.";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-activity";
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
        String expected = "{\"id\":\"1\",\"year\":\"2020\",\"week\":\"23\",\"day\":\"4\",\"site\":{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"},\"typology\":{\"id\":\"1\",\"name\":\"Typologies 1\",\"description\":\"Description typologies 1\"},\"description\":\"null\",\"estimatedInterventionTime\":\"0\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description Material 1.\"},{\"id\":\"2\",\"name\":\"Material 2\",\"description\":\"Description Material 2.\"}],\"maintenanceProcedures\":{\"id\":\"1\",\"name\":\"Procedure 1\",\"smp\":\"0\"},\"workspace\":{\"id\":\"1\",\"description\":\"New description.\",\"site\":[]},\"skill\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"},{\"id\":\"4\",\"name\":\"Skill 4\",\"description\":\"Description skill 4.\"}]}";
        assertEquals(expected, db.getActivity(1).toJSON());
    }

    @Test
    void doPostEditMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "id=1&year=2020&type=ewo&interruptibility=true&time=&procedureId=1&siteId=1&materials=1&materials=2";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-activity";
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
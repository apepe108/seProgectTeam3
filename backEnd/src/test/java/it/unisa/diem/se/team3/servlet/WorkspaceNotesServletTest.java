package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.WorkspaceNotesDecorator;
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

class WorkspaceNotesServletTest {
    private WorkspaceNotesDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(WorkspaceNotesServlet.class, new String[]{"/workspaces", "/edit-workspaces",
                "/create-workspaces", "/delete-workspaces"});
        tester.startServer();
    }

    @AfterAll
    static void afterAll() throws Exception {
        tester.shutdownServer();
    }

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new WorkspaceNotesDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery =
                "INSERT INTO workspace_notes (id, description) VALUES (1, 'Description workspace notes 1'), (2, 'Description workspace notes 2'); " +
                        "INSERT INTO factory_site (id, name) VALUES (1, 'Factory Site 1'); " +
                        "INSERT INTO area (id, name, factory_site, workspace_notes) VALUES (1, 'Area 1', 1, 1), (2, 'Area 2', 1, null); " +
                        "INSERT INTO site (id, factory_site, area) VALUES (1, 1, 1), (2, 1, 2); " +
                        "ALTER SEQUENCE workspace_notes_id RESTART WITH 3;";
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery =
                "DELETE FROM site CASCADE; " +
                        "DELETE FROM area CASCADE;" +
                        "DELETE FROM factory_site CASCADE; " +
                        "ALTER SEQUENCE workspace_notes_id RESTART WITH 1;" +
                        "DELETE FROM workspace_notes CASCADE;";
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.disconnect();
    }

    @Test
    void doGetAll() throws IOException, InterruptedException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/workspaces").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "[{\"id\":\"1\",\"description\":\"Description workspace notes 1\",\"site\":[{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"}]},{\"id\":\"2\",\"description\":\"Description workspace notes 2\",\"site\":[]}]";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetOne() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/workspaces?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "{\"id\":\"1\",\"description\":\"Description workspace notes 1\",\"site\":[{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"}]}";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGetOneNoExisting() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/workspaces?id=6").openConnection();
        http.connect();

        assertEquals(HttpStatus.EXPECTATION_FAILED_417, http.getResponseCode());
    }

    @Test
    void doGetDelete() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/delete-workspaces?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        // Verify correct delete
        String expected = "[{\"id\":\"2\",\"description\":\"Description workspace notes 2\",\"site\":[]}]";
        assertEquals(expected, JsonUtil.toJson(db.getWorkspaceNotes()));
    }

    @Test
    void doGetDeleteNoId() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/delete-workspaces").openConnection();
        http.connect();

        assertEquals(HttpStatus.BAD_REQUEST_400, http.getResponseCode());
    }

    @Test
    void doPostCreate() throws IOException, InterruptedException {
        // TRY POST
        String urlParameters = "description=Description new workspace&id-site=1&id-site=2";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/create-workspaces";
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
        String expected = "[{\"id\":\"1\",\"description\":\"Description workspace notes 1\",\"site\":[]},{\"id\":\"2\",\"description\":\"Description workspace notes 2\",\"site\":[]},{\"id\":\"3\",\"description\":\"Description new workspace\",\"site\":[{\"id\":\"1\",\"name\":\"Factory Site 1-Area 1\"},{\"id\":\"2\",\"name\":\"Factory Site 1-Area 2\"}]}]";
        assertEquals(expected, JsonUtil.toJson(db.getWorkspaceNotes()));
    }

    @Test
    void doPostCreateMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "id-site=3";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/create-workspaces";
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
        String urlParameters = "id=1&description=Description new workspace notes.";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-workspaces";
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
        String expected = "[{\"id\":\"1\",\"description\":\"Description new workspace notes.\",\"site\":[]},{\"id\":\"2\",\"description\":\"Description workspace notes 2\",\"site\":[]}]";
        assertEquals(expected, JsonUtil.toJson(db.getWorkspaceNotes()));
    }

    @Test
    void doPostEditMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "description=Description new workspace notes.";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-workspaces";
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

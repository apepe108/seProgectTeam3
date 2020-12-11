package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.CompetenciesRoleDecorator;
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

class CompetenciesRoleServletTest {
    private CompetenciesRoleDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(CompetenciesRoleServlet.class, new String[]{"/role-competencies", "/edit-role-competencies"});
        tester.startServer();
    }

    @AfterAll
    static void afterAll() throws Exception {
        tester.shutdownServer();
    }

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new CompetenciesRoleDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery =
                "INSERT INTO competences (id, name, description) " +
                        "VALUES (1, 'Skill 1', 'Description skill 1.'), (2, 'Skill 2', 'Description skill 2.'), (3, 'Skill 3', 'Description skill 3.'); " +
                        "INSERT INTO maintainer_role (id, name, description) " +
                        "VALUES (1, 'Role 1', 'Description role 1.'), (2, 'Role 2', 'Description role 2.'), (3, 'Role 3', 'Description role 3.'); " +
                        "INSERT INTO has_skill (maintainer_role, competences) VALUES (1, 1), (1, 2), (1, 3), (2, 1), (2, 3);" +
                        "ALTER SEQUENCE competences_id RESTART WITH 4;";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM has_skill CASCADE; " +
                "DELETE FROM competences CASCADE; " +
                "DELETE FROM maintainer_role CASCADE;" +
                "ALTER SEQUENCE competences_id RESTART WITH 1;";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void doGetAll() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/role-competencies").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "[{\"role\":{\"id\":\"1\",\"name\":\"Role 1\",\"description\":\"Description role 1.\"},\"competences\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"2\",\"name\":\"Skill 2\",\"description\":\"Description skill 2.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"}]},{\"role\":{\"id\":\"2\",\"name\":\"Role 2\",\"description\":\"Description role 2.\"},\"competences\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"}]},{\"role\":{\"id\":\"3\",\"name\":\"Role 3\",\"description\":\"Description role 3.\"},\"competences\":[]}]";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doGeOne() throws IOException {
        // TRY GET
        HttpURLConnection http = (HttpURLConnection) new URL("http://localhost:8080/role-competencies?id=1").openConnection();
        http.connect();

        assertEquals(HttpStatus.OK_200, http.getResponseCode());

        String expected = "{\"role\":{\"id\":\"1\",\"name\":\"Role 1\",\"description\":\"Description role 1.\"},\"competences\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"2\",\"name\":\"Skill 2\",\"description\":\"Description skill 2.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"}]}";
        assertEquals(expected, tester.readPage(http));
    }

    @Test
    void doPostEdit() throws IOException {
        // TRY POST
        String urlParameters = "id-role=1";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-role-competencies";
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
        String expected = "{\"role\":{\"id\":\"1\",\"name\":\"Role 1\",\"description\":\"Description role 1.\"},\"competences\":[]}";
        assertEquals(expected, db.getCompetenciesRole(1).toJSON());
    }

    @Test
    void doPostEditMissingParameters() throws IOException {
        // TRY POST
        String urlParameters = "id-competence=1&id-competence=3";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://localhost:8080/edit-role-competencies";
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
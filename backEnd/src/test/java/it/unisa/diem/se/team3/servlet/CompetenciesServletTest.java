package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.CompetenciesDecorator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetenciesServletTest {
    private CompetenciesDecorator db;
    private static ServletTester tester;

    @BeforeAll
    static void beforeAll() throws Exception {
        tester = new ServletTester(CompetenciesServlet.class, new String[]{"/competencies"});
        tester.startServer();
    }

    @AfterAll
    static void afterAll() throws Exception {
        tester.shutdownServer();
    }

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new CompetenciesDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "INSERT INTO competences (id, name, description) " +
                "VALUES (1, 'Skill 1', 'Description skill 1.')," +
                "(2, 'Skill 2', 'Description skill 2.'), " +
                "(3, 'Skill 3', 'Description skill 3.');";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM competences CASCADE;";
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
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/competencies").openConnection();
        http.connect();
        assertEquals(HttpStatus.OK_200, http.getResponseCode());
        assertEquals("[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"2\",\"name\":\"Skill 2\",\"description\":\"Description skill 2.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"}]", tester.readPage(http));
    }
}
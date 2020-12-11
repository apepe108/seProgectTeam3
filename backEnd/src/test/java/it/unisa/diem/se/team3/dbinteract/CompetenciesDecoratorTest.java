package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.dbinteract.CompetenciesDecorator;
import it.unisa.diem.se.team3.models.Competencies;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetenciesDecoratorTest {
    private CompetenciesDecorator db;

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
    void getCompetencies() {
        List<Competencies> actual = db.getCompetencies();

        // Make expected
        List<Competencies> expected = new ArrayList<>();
        expected.add(new Competencies(1, "Skill 1", "Description skill 1."));
        expected.add(new Competencies(2, "Skill 2", "Description skill 2."));
        expected.add(new Competencies(3, "Skill 3", "Description skill 3."));

        // Match
        assertEquals(expected, actual);
    }
}
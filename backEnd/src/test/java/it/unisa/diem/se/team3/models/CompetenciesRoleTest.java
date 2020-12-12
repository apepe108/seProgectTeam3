package it.unisa.diem.se.team3.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CompetenciesRoleTest {

    @Test
    void testEquals() {
        CompetenciesRole c1 = new CompetenciesRole(1, "Role 1", "Description role 1.");
        c1.addCompetencies(1, "Competence 1", "Description competence 1.");

        CompetenciesRole c2 = new CompetenciesRole(1, "Role 1", "Description role 1.");
        c2.addCompetencies(1, "Competence 1", "Description competence 1.");

        assertEquals(c1, c1);
        assertNotEquals(c1, null);
        assertEquals(c1, c2);
    }

    @Test
    void toJSON() {
        // Make actual
        CompetenciesRole cr = new CompetenciesRole(1, "Role 1", "Description role 1.");
        String actual = cr.toJSON();

        // Make expected
        String expected = "{\"role\":{\"id\":\"1\",\"name\":\"Role 1\",\"description\":\"Description role 1.\"},\"competences\":[]}";

        // Match
        assertEquals(expected, actual);

        // Make actual
        cr.addCompetencies(1, "Skill 1", "Description skill 1.");
        cr.addCompetencies(2, "Skill 2", "Description skill 2.");
        cr.addCompetencies(3, "Skill 3", "Description skill 3.");
        actual = cr.toJSON();

        // Make expected
        expected = "{\"role\":{\"id\":\"1\",\"name\":\"Role 1\",\"description\":\"Description role 1.\"},\"competences\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"},{\"id\":\"2\",\"name\":\"Skill 2\",\"description\":\"Description skill 2.\"},{\"id\":\"3\",\"name\":\"Skill 3\",\"description\":\"Description skill 3.\"}]}";

        // Match
        assertEquals(expected, actual);
    }
}
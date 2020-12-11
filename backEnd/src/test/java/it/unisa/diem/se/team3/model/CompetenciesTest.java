package it.unisa.diem.se.team3.model;

import it.unisa.diem.se.team3.models.Competencies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CompetenciesTest {

    @Test
    void testEquals() {
        Competencies c1 = new Competencies(1, "Competence Name 1", "Competence description 1");
        Competencies c2 = new Competencies(1, "Competence Name 1", "Competence description 1");

        assertEquals(c1, c1);
        assertNotEquals(c1, null);
        assertEquals(c1, c2);
    }

    @Test
    void toJSON() {
        // Make expected
        String expected = "{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Description skill 1.\"}";

        // Make actual
        Competencies c = new Competencies(1, "Skill 1", "Description skill 1.");
        String actual = c.toJSON();

        // Test
        assertEquals(expected, actual);
    }
}
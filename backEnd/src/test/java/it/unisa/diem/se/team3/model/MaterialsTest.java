package it.unisa.diem.se.team3.model;

import it.unisa.diem.se.team3.models.Materials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MaterialsTest {

    @Test
    void testEquals() {
        Materials m1 = new Materials(1,"Material 1", "Description material 1");
        Materials m2 = new Materials(1,"Material 1", "Description material 1");

        assertEquals(m1, m1);
        assertNotEquals(m1, null);
        assertEquals(m1, m2);
    }


    @Test
    void toJSON() {
        // Make expected
        String expected = "{\"id\":\"1\",\"name\":\"Material 1\",\"description\":\"Description material 1.\"}";

        // Make actual
        Materials c = new Materials(1, "Material 1", "Description material 1.");
        String actual = c.toJSON();

        // Test
        assertEquals(expected, actual);
    }
}
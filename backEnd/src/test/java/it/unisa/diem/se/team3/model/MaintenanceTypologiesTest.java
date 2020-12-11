package it.unisa.diem.se.team3.model;

import it.unisa.diem.se.team3.models.MaintenanceTypologies;
import it.unisa.diem.se.team3.models.Materials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MaintenanceTypologiesTest {

    @Test
    void testEquals() {
        MaintenanceTypologies t1 = new MaintenanceTypologies(1,"Typologies 1", "Description typologies 1");
        MaintenanceTypologies t2 = new MaintenanceTypologies(1,"Typologies 1", "Description typologies 1");

        assertEquals(t1, t1);
        assertNotEquals(t1, null);
        assertEquals(t1, t2);
    }

    @Test
    void toJSON() {
        // Make expected
        String expected = "{\"id\":\"1\",\"name\":\"Typologies 1\",\"description\":\"Description typologies 1.\"}";

        // Make actual
        Materials c = new Materials(1, "Typologies 1", "Description typologies 1.");
        String actual = c.toJSON();

        // Test
        assertEquals(expected, actual);
    }
}
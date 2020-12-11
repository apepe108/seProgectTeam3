package it.unisa.diem.se.team3.model;

import it.unisa.diem.se.team3.models.MaintainerRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MaintainerRoleTest {

    @Test
    void testEquals() {
        MaintainerRole m1 = new MaintainerRole(1, "Role Name 1", "Role description 1");
        MaintainerRole m2 = new MaintainerRole(1, "Role Name 1", "Role description 1");

        assertEquals(m1, m1);
        assertNotEquals(m1, null);
        assertEquals(m1, m2);
    }

    @Test
    void toJSONTest() {
        // Make actual
        MaintainerRole mr = new MaintainerRole(1, "Plumber", "test_plumber description.");
        String actual = mr.toJSON();

        // Make expected
        String expected = "{\"id\":\"1\",\"name\":\"Plumber\",\"description\":\"test_plumber description.\"}";

        // Check condition
        assertEquals(expected, actual);
    }
}
package it.unisa.diem.se.team3.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SiteTest {

    @Test
    void testEquals() {
        Site s = new Site(1, "Factory Site 1-Area 1");
        Site s1 = new Site(1, "Factory Site 1-Area 1");

        assertEquals(s, s);
        assertNotEquals(s1, null);
        assertEquals(s, s1);
    }

    @Test
    void toJSON() {
        // Make actual
        Site s = new Site(1, "Fisciano - Molding");
        String actual = s.toJSON();

        // Make expected
        String expected = "{\"id\":\"1\",\"name\":\"Fisciano - Molding\"}";

        // Make match
        assertEquals(expected, actual);
    }
}
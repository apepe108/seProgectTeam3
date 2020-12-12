package it.unisa.diem.se.team3.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityWeeklyTest {

    @Test
    void testEquals() {
        AvailabilityWeekly aw1 = new AvailabilityWeekly(1, "Jeff", "3/4");
        aw1.addDay(1, 90);
        aw1.addDay(2, 100);
        aw1.addDay(3, 80);
        aw1.addDay(4, 73);
        aw1.addDay(5, 67);
        aw1.addDay(6, 43);
        aw1.addDay(7, 23);

        AvailabilityWeekly aw2 = new AvailabilityWeekly(1, "Jeff", "3/4");
        aw2.addDay(1, 90);
        aw2.addDay(2, 100);
        aw2.addDay(3, 80);
        aw2.addDay(4, 73);
        aw2.addDay(5, 67);
        aw2.addDay(6, 43);
        aw2.addDay(7, 23);

        assertEquals(aw1, aw2);
        assertEquals(aw1, aw1);
        assertNotEquals(aw1, null);

        AvailabilityWeekly.Day d1 = new AvailabilityWeekly.Day(1, 90);
        AvailabilityWeekly.Day d2 = new AvailabilityWeekly.Day(1, 90);

        assertEquals(d1, d2);
        assertEquals(d1, d1);
        assertNotEquals(d1, null);
    }

    @Test
    void toJSON() {
        AvailabilityWeekly aw1 = new AvailabilityWeekly(1, "Jeff", "3/4");
        aw1.addDay(1, 90);
        aw1.addDay(2, 100);
        aw1.addDay(3, 80);
        aw1.addDay(4, 73);
        aw1.addDay(5, 67);
        aw1.addDay(6, 43);
        aw1.addDay(7, 23);
        String actual = aw1.toJSON();

        String expected = "{\"id\":\"1\",\"name\":\"Jeff\",\"competence_compliance\":\"3/4\",\"availability\":[{\"day\":\"1\",\"percentage\":\"90\"},{\"day\":\"2\",\"percentage\":\"100\"},{\"day\":\"3\",\"percentage\":\"80\"},{\"day\":\"4\",\"percentage\":\"73\"},{\"day\":\"5\",\"percentage\":\"67\"},{\"day\":\"6\",\"percentage\":\"43\"},{\"day\":\"7\",\"percentage\":\"23\"}]}";

        assertEquals(expected, actual);
    }
}
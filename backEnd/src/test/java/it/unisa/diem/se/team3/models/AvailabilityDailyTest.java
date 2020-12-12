package it.unisa.diem.se.team3.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AvailabilityDailyTest {

    @Test
    void testEquals() {
        AvailabilityDaily a1 = new AvailabilityDaily(1, "Pippo", "3/4");
        a1.addSlot(1, "08:00-09:00", 60);
        a1.addSlot(2, "09:00-10:00", 35);
        a1.addSlot(3, "10:00-11:00", 0);

        AvailabilityDaily a2 = new AvailabilityDaily(1, "Pippo", "3/4");
        a2.addSlot(1, "08:00-09:00", 60);
        a2.addSlot(2, "09:00-10:00", 35);
        a2.addSlot(3, "10:00-11:00", 0);

        assertEquals(a1, a2);
        assertEquals(a1, a1);
        assertNotEquals(a1, null);

        AvailabilityDaily.Slot s1 = new AvailabilityDaily.Slot(1, "08:00-09:00", 60);
        AvailabilityDaily.Slot s2 = new AvailabilityDaily.Slot(1, "08:00-09:00", 60);

        assertEquals(s1, s2);
        assertEquals(s1, s1);
        assertNotEquals(s1, null);
    }

    @Test
    void toJSON() {
        AvailabilityDaily a1 = new AvailabilityDaily(1, "Pippo", "3/4");
        a1.addSlot(1, "08:00-09:00", 60);
        a1.addSlot(2, "09:00-10:00", 35);
        a1.addSlot(3, "10:00-11:00", 0);

        String actual = a1.toJSON();

        String expected = "{\"id\":\"1\",\"name\":\"Pippo\",\"competence_compliance\":\"3/4\",\"availability\":[{\"id\":\"1\",\"description\":\"08:00-09:00\",\"minutes\":\"60\"},{\"id\":\"2\",\"description\":\"09:00-10:00\",\"minutes\":\"35\"},{\"id\":\"3\",\"description\":\"10:00-11:00\",\"minutes\":\"0\"}]}";

        assertEquals(expected, actual);
    }
}
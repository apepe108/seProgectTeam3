package it.unisa.diem.se.team3.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MaintenanceProcedureTest {

    @Test
    void testEquals() {
        MaintenanceProcedure m1 = new MaintenanceProcedure(1, "Role Name 1", 0);
        MaintenanceProcedure m2 = new MaintenanceProcedure(1, "Role Name 1", 0);

        assertEquals(m1, m1);
        assertNotEquals(m1, null);
        assertEquals(m1, m2);
    }

    @Test
    void toJSON() {
        // Make actual
        MaintenanceProcedure mp = new MaintenanceProcedure(1, "Procedure 1", 1);
        String actual = mp.toJSON();

        // Make expected
        String expected = "{\"id\":\"1\",\"name\":\"Procedure 1\",\"smp\":\"1\"}";

        // Make check
        assertEquals(expected, actual);
    }
}
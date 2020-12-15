package it.unisa.diem.se.team3.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AccessRecordTest {

    @Test
    void testEquals() {
        AccessRecord ar1 = new AccessRecord("tizio@email.com", "tizio", "SysAdmin", LocalDateTime.of(2021,12,13,12,45,05), LocalDateTime.of(2021, 12, 13, 15, 40, 44));
        AccessRecord ar2 = new AccessRecord("tizio@email.com", "tizio", "SysAdmin", LocalDateTime.of(2021,12,13,12,45,05), LocalDateTime.of(2021, 12, 13, 15, 40, 44));

        assertEquals(ar1, ar1);
        assertNotEquals(ar1, null);
        assertEquals(ar1, ar2);
    }

    @Test
    void toJSON() {
        // Make expected
        String expected = "{\"email\":\"tizio@email.com\",\"name\":\"tizio\",\"role\":\"SysAdmin\",\"login_date\":" +
                "\"13-12-2021 12:45:05\",\"logout_date\":\"13-12-2021 15:40:44\"}";

        // Make actual
        AccessRecord c = new AccessRecord("tizio@email.com", "tizio", "SysAdmin",
                LocalDateTime.of(2021, 12, 13, 12, 45, 5),
                LocalDateTime.of(2021, 12, 13, 15, 40, 44));
        String actual = c.toJSON();

        // Test
        assertEquals(expected, actual);
    }

}

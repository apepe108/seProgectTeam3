package it.unisa.diem.se.team3.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    @Test
    void testEquals() {
        User u1 = new User(1, "name", "user1@email.com", "mySecretPassword1", "planner");
        User u2 = new User(1, "name", "user1@email.com", "mySecretPassword1", "planner");

        assertEquals(u1, u1);
        assertNotEquals(u1, null);
        assertEquals(u1, u2);
    }

    @Test
    void toJSON() {
        // Make expected
        String expected = "{\"id\":\"1\",\"name\":\"name\",\"email\":\"user1@email.com\",\"password\":\"mySecretPassword1\",\"role\":\"planner\"}";

        // Make actual
        User u = new User(1, "name", "user1@email.com", "mySecretPassword1", "planner");
        String actual = u.toJSON();

        // Test
        assertEquals(expected, actual);
    }

    @Test
    void getEmail() {
        User u1 = new User(1, "name", "user1@email.com", "mySecretPassword1", "planner");
        assertEquals("user1@email.com", u1.getEmail());
    }

    @Test
    void getPassword() {
        User u1 = new User(1, "name", "user1@email.com", "mySecretPassword1", "planner");
        assertEquals("mySecretPassword1", u1.getPassword());
    }

    @Test
    void getRole() {
        User u1 = new User(1, "name", "user1@email.com", "mySecretPassword1", "planner");
        assertEquals("planner", u1.getRole());
    }

    @Test
    void getName() {
        User u1 = new User(1, "name", "user1@email.com", "mySecretPassword1", "planner");
        assertEquals("name", u1.getName());
    }
}
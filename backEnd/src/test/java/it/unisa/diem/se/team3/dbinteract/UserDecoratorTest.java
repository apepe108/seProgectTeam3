package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.dbinteract.UserDecorator;
import it.unisa.diem.se.team3.models.User;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDecoratorTest {
    private UserDecorator db;

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new UserDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "ALTER SEQUENCE user_id RESTART WITH 3;" +
                "INSERT INTO users (internal_id, email, password) VALUES (1, 'user1@email.com', 'password1'); " +
                "INSERT INTO users (internal_id, email, password) VALUES (2, 'user2@email.com', 'password2'); " +
                "INSERT INTO planner (internal_id, name, email) VALUES (1, 'UserName1', 'user1@email.com'); " +
                "INSERT INTO maintainer (internal_id, name, email) VALUES (2, 'UserName2', 'user2@email.com');";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "ALTER SEQUENCE user_id RESTART WITH 1;" +
                "DELETE FROM users CASCADE; " +
                "DELETE FROM planner CASCADE; " +
                "DELETE FROM maintainer CASCADE;";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.disconnect();
    }

    @Test
    void getUsers() {
        List<User> actual = db.getUsers();

        ArrayList<User> expected = new ArrayList<>();
        expected.add(new User(1, "UserName1", "user1@email.com", "password1", "Planner"));
        expected.add(new User(2, "UserName2", "user2@email.com", "password2", "Maintainer"));

        assertEquals(expected, actual);
    }

    @Test
    void getUsersById() {
        User actual = db.getUsers(1);

        User expected = new User(1, "UserName1", "user1@email.com", "password1", "Planner");

        assertEquals(expected, actual);
    }

    @Test
    void getUsersByIdNoExisting() {
        User actual = db.getUsers(9);

        assertNull(actual);
    }

    @Test
    void getUsersByEmail() {
        User actual = db.getUsers("user1@email.com");

        User expected = new User(1, "UserName1", "user1@email.com", "password1", "Planner");

        assertEquals(expected, actual);
    }

    @Test
    void getUsersByEmailNoExisting() {
        User actual = db.getUsers("user32@email.com");

        assertNull(actual);
    }
    
    @Test
    void deleteUsers() {
        assertTrue(db.deleteUsers(1));
        List<User> actual = db.getUsers();

        ArrayList<User> expected = new ArrayList<>();
        expected.add(new User(2, "UserName2", "user2@email.com", "password2", "Maintainer"));

        assertEquals(expected, actual);
    }

    @Test
    void addUsers() {
        assertTrue(db.addUsers("Username3", "user3@email.com", "password3", "maintainer"));
        assertTrue(db.addUsers("Username4", "user4@email.com", "password4", "planner"));
        List<User> actual = db.getUsers();

        ArrayList<User> expected = new ArrayList<>();
        expected.add(new User(1, "UserName1", "user1@email.com", "password1", "Planner"));
        expected.add(new User(2, "UserName2", "user2@email.com", "password2", "Maintainer"));
        expected.add(new User(3, "UserName3", "user3@email.com", "password3", "Maintainer"));
        expected.add(new User(4, "Username4", "user4@email.com", "password4", "Planner"));

        assertEquals(expected, actual);
    }

    @Test
    void editUsers() {
        assertTrue(db.editUser(2, "New Username2", "new2@email.com", "newpassword2", "maintainer"));
        assertTrue(db.editUser(1, "New Username1", "new1@email.com", "newpassword1", "maintainer"));
        List<User> actual = db.getUsers();

        ArrayList<User> expected = new ArrayList<>();
        expected.add(new User(1, "New Username1", "new1@email.com", "newpassword1", "Maintainer"));
        expected.add(new User(2, "New Username2", "new2@email.com", "newpassword2", "Maintainer"));

        assertEquals(expected, actual);
    }
}
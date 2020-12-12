package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.AvailabilityDaily;
import it.unisa.diem.se.team3.models.AvailabilityWeekly;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityDecoratorTest {
    private AvailabilityDecorator db;

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new AvailabilityDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "INSERT INTO materials (id, name, description)  " +
                "VALUES (nextval('materials_id'), 'Material 1', 'Description Material 1.'),  " +
                "(nextval('materials_id'), 'Material 2', 'Description Material 2.'),  " +
                "(nextval('materials_id'), 'Material 3', 'Description Material 3.'); " +
                "INSERT INTO maintenance_typologies (id, name, description)  " +
                "VALUES (1, 'Typologies 1', 'Description typologies 1'), " +
                "(2, 'Typologies 2', 'Description typologies 2'); " +
                "INSERT INTO maintenance_procedures (id, name)  " +
                "VALUES (1, 'Procedure 1'), (2, 'Procedure 2'); " +
                "INSERT INTO workspace_notes (id, description) VALUES (1, 'Description workspace notes 1'), (2, 'Description workspace notes 2'); " +
                "INSERT INTO factory_site (id, name) VALUES (1, 'Factory Site 1'); " +
                "INSERT INTO area (id, name, factory_site, workspace_notes) VALUES (1, 'Area 1', 1, 1), (2, 'Area 2', 1, null); " +
                "INSERT INTO site (id, factory_site, area) VALUES (1, 1, 1), (2, 1, 2); " +
                "ALTER SEQUENCE workspace_notes_id RESTART WITH 3;" +
                "INSERT INTO activity (id, year, week, day, type, interruptibility, estimated_intervention_time, description, " +
                "maintenance_typologies, maintenance_procedures, site)  " +
                "VALUES (nextval('activity_id'), 2020, 21, 1, 'p', true, 30, 'Activity 1 description.', 1, 1, 1), " +
                "(nextval('activity_id'), 2020, 21, 1, 'e', true, 90, 'Activity 2 description.', 2, 1, 2), " +
                "(nextval('activity_id'), 2020, 21, 1, 'p', true, 20, 'Activity 3 description.', 2, 2, 1), " +
                "(nextval('activity_id'), 2020, 21, 1, 'p', true, 40, 'Activity 4 description.', 1, 2, 2); " +
                "INSERT INTO need (activity, materials) " +
                "VALUES (1, 1), (1, 2), (1, 3), (3, 1), (4, 1), (4, 3);" +
                "ALTER SEQUENCE activity_id RESTART WITH 5; " +
                "INSERT INTO users (internal_id, email, password) VALUES (1, 'user1@email.com', 'password1'), " +
                "(2, 'user2@email.com', 'password2'); " +
                "INSERT INTO maintainer (internal_id, name, email) VALUES (1, 'UserName1', 'user1@email.com'), " +
                "(2, 'UserName2', 'user2@email.com'); " +
                "INSERT INTO daily_time_slot (id, hour_start, duration)  " +
                "VALUES (1, '06:00:00', 60), (2, '07:00:00', 60), (3, '08:00:00', 60);" +
                "INSERT INTO assigned_activity (activity, maintainer) VALUES (1, 1), (2, 2), (3, 1), (4, 2); " +
                "INSERT INTO assigned_slot (assigned_activity, daily_time_slot, minutes) VALUES (1, 2, 30), (2, 2, 60), (2, 3, 30), (3, 2, 20), (4, 1, 40); " +
                "INSERT INTO maintainer_role (id, name, description) " +
                "VALUES (1, 'Role 1', 'Description role 1.'), (2, 'Role 2', 'Description role 2.'), (3, 'Role 3', 'Description role 3.'); " +
                "INSERT INTO competences (id, name, description) " +
                "VALUES (1, 'Skill 1', 'Description skill 1.')," +
                "(2, 'Skill 2', 'Description skill 2.'), " +
                "(3, 'Skill 3', 'Description skill 3.')," +
                "(4, 'Skill 4', 'Description skill 4.'), " +
                "(5, 'Skill 5', 'Description skill 5.'), " +
                "(6, 'Skill 6', 'Description skill 6.');" +
                "INSERT INTO has_skill (maintainer_role, competences) VALUES (1, 1), (1, 2), (1, 3), (2, 4), (2, 5), (2, 6); " +
                "INSERT INTO is_a (maintainer, maintainer_role) VALUES (1, 1), (2, 2); " +
                "INSERT INTO require (maintenance_procedures, competences) VALUES (1, 1), (1, 3), (1, 4), (1, 5), (2, 4), (2, 5), (2, 6); ";
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM need CASCADE; " +
                "DELETE FROM materials CASCADE; ALTER SEQUENCE materials_id RESTART WITH 1; " +
                "DELETE FROM users CASCADE; " +
                "DELETE FROM assigned_slot CASCADE; " +
                "DELETE FROM assigned_activity CASCADE; " +
                "DELETE FROM is_a CASCADE; " +
                "DELETE FROM maintainer CASCADE; " +
                "DELETE FROM daily_time_slot CASCADE; " +
                "DELETE FROM activity CASCADE; ALTER SEQUENCE activity_id RESTART WITH 1; " +
                "DELETE FROM maintenance_typologies CASCADE; " +
                "DELETE FROM require CASCADE; " +
                "DELETE FROM maintenance_procedures CASCADE; " +
                "DELETE FROM has_skill CASCADE; " +
                "DELETE FROM maintainer_role CASCADE; " +
                "DELETE FROM competences CASCADE; " +
                "DELETE FROM site CASCADE; " +
                "DELETE FROM area CASCADE;" +
                "DELETE FROM factory_site CASCADE; " +
                "ALTER SEQUENCE workspace_notes_id RESTART WITH 1;" +
                "DELETE FROM workspace_notes CASCADE;" +
                "ALTER SEQUENCE activity_id RESTART WITH 1;";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void getAvailabilityDaily() {
        List<AvailabilityDaily> actual = db.getAvailabilityDaily(1, 1);

        List<AvailabilityDaily> expected = new ArrayList<>();
        expected.add(new AvailabilityDaily(1, "UserName1", "2/4"));
        expected.get(0).addSlot(1, "06:00-07:00", 60);
        expected.get(0).addSlot(2, "07:00-08:00", 10);
        expected.get(0).addSlot(3, "08:00-09:00", 60);
        expected.add(new AvailabilityDaily(2, "UserName2", "2/4"));
        expected.get(1).addSlot(1, "06:00-07:00", 20);
        expected.get(1).addSlot(2, "07:00-08:00", 0);
        expected.get(1).addSlot(3, "08:00-09:00", 30);

        assertEquals(expected, actual);
    }

    @Test
    void testGetAvailabilityDaily() {
        AvailabilityDaily actual = db.getAvailabilityDaily(1, 2, 1);

        AvailabilityDaily expected = new AvailabilityDaily(2, "UserName2", "2/4");
        expected.addSlot(1, "06:00-07:00", 20);
        expected.addSlot(2, "07:00-08:00", 0);
        expected.addSlot(3, "08:00-09:00", 30);

        assertEquals(expected, actual);
    }

    @Test
    void testGetAvailabilityDaily2() {
        AvailabilityDaily actual = db.getAvailabilityDaily(1, 2, 2);

        AvailabilityDaily expected = new AvailabilityDaily(2, "UserName2", "2/4");
        expected.addSlot(1, "06:00-07:00", 60);
        expected.addSlot(2, "07:00-08:00", 60);
        expected.addSlot(3, "08:00-09:00", 60);

        assertEquals(expected, actual);
    }

    @Test
    void getAvailabilityWeekly() {
        List<AvailabilityWeekly> actual = db.getAvailabilityWeekly(1);

        List<AvailabilityWeekly> expected = new ArrayList<>();
        expected.add(new AvailabilityWeekly(1, "UserName1", "2/4"));
        expected.get(0).addDay(1, 72);
        expected.get(0).addDay(2, 100);
        expected.get(0).addDay(3, 100);
        expected.get(0).addDay(4, 100);
        expected.get(0).addDay(5, 100);
        expected.get(0).addDay(6, 100);
        expected.get(0).addDay(7, 100);
        expected.add(new AvailabilityWeekly(2, "UserName2", "2/4"));
        expected.get(1).addDay(1, 27);
        expected.get(1).addDay(2, 100);
        expected.get(1).addDay(3, 100);
        expected.get(1).addDay(4, 100);
        expected.get(1).addDay(5, 100);
        expected.get(1).addDay(6, 100);
        expected.get(1).addDay(7, 100);

        assertEquals(expected, actual);
    }

    @Test
    void testGetAvailabilityWeekly() {
        AvailabilityWeekly actual = db.getAvailabilityWeekly(1, 1);

        AvailabilityWeekly expected = new AvailabilityWeekly(1, "UserName1", "2/4");
        expected.addDay(1, 72);
        expected.addDay(2, 100);
        expected.addDay(3, 100);
        expected.addDay(4, 100);
        expected.addDay(5, 100);
        expected.addDay(6, 100);
        expected.addDay(7, 100);

        assertEquals(expected, actual);
    }
}
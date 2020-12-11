package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.dbinteract.ActivityDecorator;
import it.unisa.diem.se.team3.models.Activity;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDecoratorTest {
    private ActivityDecorator db;

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new ActivityDecorator(ServletUtil.connectDb());
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
                "VALUES (nextval('activity_id'), 2020, 21, null, 'p', true, 30, 'Activity 1 description.', 1, 1, 1), " +
                "(nextval('activity_id'), 2020, 21, null, 'e', true, 90, 'Activity 2 description.', 2, 1, 2), " +
                "(nextval('activity_id'), 2020, 21, null, 'p', true, 20, 'Activity 3 description.', 2, 2, 1), " +
                "(nextval('activity_id'), 2020, 21, null, 'p', true, 40, 'Activity 4 description.', 1, 2, 2); " +
                "INSERT INTO daily_time_slot (id, hour_start, duration)  " +
                "VALUES (1, '06:00:00', 60), (2, '07:00:00', 60), (3, '08:00:00', 60); " +
                "INSERT INTO users (internal_id, email, password) VALUES (1, 'user1@email.com', 'password1'), " +
                "(2, 'user2@email.com', 'password2'); " +
                "INSERT INTO maintainer (internal_id, name, email) VALUES (1, 'UserName1', 'user1@email.com'), " +
                "(2, 'UserName2', 'user2@email.com'); " +
                "INSERT INTO need (activity, materials) " +
                "VALUES (1, 1), (1, 2), (1, 3), (3, 1), (4, 1), (4, 3);" +
                "ALTER SEQUENCE activity_id RESTART WITH 5;" +
                "INSERT INTO competences (id, name, description) " +
                "VALUES (1, 'Skill 1', 'Description skill 1.')," +
                "(2, 'Skill 2', 'Description skill 2.'), " +
                "(3, 'Skill 3', 'Description skill 3.')," +
                "(4, 'Skill 4', 'Description skill 4.'), " +
                "(5, 'Skill 5', 'Description skill 5.'), " +
                "(6, 'Skill 6', 'Description skill 6.');" +
                "INSERT INTO require (maintenance_procedures, competences) VALUES (1, 1), (1, 3), (1, 4); " +
                "INSERT INTO require_ewo (activity, competences) VALUES (2, 3), (2, 4), (2, 5);";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM require_ewo CASCADE; " +
                "DELETE FROM require CASCADE; " +
                "DELETE FROM competences CASCADE; " +
                "DELETE FROM assigned_slot CASCADE; " +
                "DELETE FROM daily_time_slot CASCADE; " +
                "DELETE FROM assigned_activity CASCADE; " +
                "DELETE FROM need CASCADE; " +
                "DELETE FROM activity CASCADE; ALTER SEQUENCE activity_id RESTART WITH 1; " +
                "DELETE FROM materials CASCADE; ALTER SEQUENCE materials_id RESTART WITH 1; " +
                "DELETE FROM users CASCADE; " +
                "DELETE FROM maintainer CASCADE; " +
                "DELETE FROM maintenance_typologies CASCADE; " +
                "DELETE FROM maintenance_procedures CASCADE; " +
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
    void getActivityByType() {
        // Actual
        List<Activity> actual = db.getActivity('p');

        // Expected
        ArrayList<Activity> expected = new ArrayList<>();
        Activity a = new Activity(1, 2020, 21, 0, true, 30,
                "Activity 1 description.", 1, "Typologies 1",
                "Description typologies 1", 1, "Factory Site 1-Area 1", 1,
                "Procedure 1", 0, 1, "Description workspace notes 1");
        a.addMaterial(1, "Material 1", "Description Material 1.");
        a.addMaterial(2, "Material 2", "Description Material 2.");
        a.addMaterial(3, "Material 3", "Description Material 3.");
        a.addCompetence(1, "Skill 1", "Description skill 1.");
        a.addCompetence(3, "Skill 3", "Description skill 3.");
        a.addCompetence(4, "Skill 4", "Description skill 4.");
        expected.add(a);
        Activity a3 = new Activity(3, 2020, 21, 0, true, 20,
                "Activity 3 description.", 2, "Typologies 2",
                "Description typologies 2", 1, "Factory Site 1-Area 1", 2,
                "Procedure 2", 0, 1, "Description workspace notes 1");
        a3.addMaterial(1, "Material 1", "Description Material 1.");
        expected.add(a3);
        Activity a4 = new Activity(4, 2020, 21, 0, true, 40,
                "Activity 4 description.", 1, "Typologies 1",
                "Description typologies 1", 2, "Factory Site 1-Area 2", 2,
                "Procedure 2", 0, 0, null);
        a4.addMaterial(1, "Material 1", "Description Material 1.");
        a4.addMaterial(3, "Material 3", "Description Material 3.");
        expected.add(a4);

        // Match
        assertEquals(expected, actual);
    }

    @Test
    void getActivityById() {
        // Actual
        Activity actual = db.getActivity(3);

        // Expected
        Activity a3 = new Activity(3, 2020, 21, 0, true, 20,
                "Activity 3 description.", 2, "Typologies 2",
                "Description typologies 2", 1, "Factory Site 1-Area 1", 2,
                "Procedure 2", 0, 1, "Description workspace notes 1");
        a3.addMaterial(1, "Material 1", "Description Material 1.");

        // Match
        assertEquals(a3, actual);
    }

    @Test
    void getActivityByWeek() {
        // Actual
        List<Activity> actual = db.getActivity(2020, 21, 'p');

        // Expected
        ArrayList<Activity> expected = new ArrayList<>();
        Activity a = new Activity(1, 2020, 21, 0, true, 30,
                "Activity 1 description.", 1, "Typologies 1",
                "Description typologies 1", 1, "Factory Site 1-Area 1", 1,
                "Procedure 1", 0, 1, "Description workspace notes 1");
        a.addMaterial(1, "Material 1", "Description Material 1.");
        a.addMaterial(2, "Material 2", "Description Material 2.");
        a.addMaterial(3, "Material 3", "Description Material 3.");
        a.addCompetence(1, "Skill 1", "Description skill 1.");
        a.addCompetence(3, "Skill 3", "Description skill 3.");
        a.addCompetence(4, "Skill 4", "Description skill 4.");
        expected.add(a);
        Activity a3 = new Activity(3, 2020, 21, 0, true, 20,
                "Activity 3 description.", 2, "Typologies 2",
                "Description typologies 2", 1, "Factory Site 1-Area 1", 2,
                "Procedure 2", 0, 1, "Description workspace notes 1");
        a3.addMaterial(1, "Material 1", "Description Material 1.");
        expected.add(a3);
        Activity a4 = new Activity(4, 2020, 21, 0, true, 40,
                "Activity 4 description.", 1, "Typologies 1",
                "Description typologies 1", 2, "Factory Site 1-Area 2", 2,
                "Procedure 2", 0, 0, null);
        a4.addMaterial(1, "Material 1", "Description Material 1.");
        a4.addMaterial(3, "Material 3", "Description Material 3.");
        expected.add(a4);

        // Match
        assertEquals(expected, actual);
    }

    @Test
    void getActivityByWeekNoExisting() {
        // Actual
        List<Activity> actual = db.getActivity(2020, 25, 'e');

        // Expected
        ArrayList<Activity> expected = new ArrayList<>();

        // Match
        assertEquals(expected, actual);
    }

    @Test
    void getActivityByNotExistingId() {
        // Actual
        Activity actual = db.getActivity(7);

        // Check
        assertNull(actual);
    }

    @Test
    void addActivity() {
        assertTrue(db.addActivity(2020, 23, 3, 'e', false, 0, null, 1, 1, 1, new long[]{1, 2}, "New description 1", new long[]{}));
        assertTrue(db.addActivity(2020, 23, 0, 'p', false, 30, "New activity description", 1, 0, 2, new long[]{}, "New description 2", new long[]{1, 5}));

        // Expected
        Activity a5 = new Activity(5, 2020, 23, 3, false, 0,
                null, 1, "Typologies 1",
                "Description typologies 1", 1, "Factory Site 1-Area 1", 1,
                "Procedure 1", 0, 1, "New description 1");
        a5.addMaterial(1, "Material 1", "Description Material 1.");
        a5.addMaterial(2, "Material 2", "Description Material 2.");
        a5.addCompetence(1, "Skill 1", "Description skill 1.");
        a5.addCompetence(3, "Skill 3", "Description skill 3.");
        a5.addCompetence(4, "Skill 4", "Description skill 4.");

        Activity a6 = new Activity(6, 2020, 23, 0, false, 30,
                "New activity description", 1, "Typologies 1",
                "Description typologies 1", 2, "Factory Site 1-Area 2", 0,
                null, 0, 3, "New description 2");
        a6.addCompetence(1, "Skill 1", "Description skill 1.");
        a6.addCompetence(5, "Skill 5", "Description skill 5.");

        assertEquals(a5, db.getActivity(5));
        assertEquals(a6, db.getActivity(6));
    }

    @Test
    void editActivity() {
        assertTrue(db.editActivity(2, 2020, 22, 0, 'e', false, 0, null, 1, 1, 2, new long[]{1, 2}, "", new long[]{}));
        assertTrue(db.editActivity(3, 2020, 22, 1, 'e', false, 12, null, 1, 0, 1, new long[]{1, 2}, "New description 1",  new long[]{1, 5}));

        Activity a2 = new Activity(2, 2020, 22, 0, false, 0,
                null, 1, "Typologies 1",
                "Description typologies 1", 2, "Factory Site 1-Area 2", 1,
                "Procedure 1", 0, 3, "");
        a2.addMaterial(1, "Material 1", "Description Material 1.");
        a2.addMaterial(2, "Material 2", "Description Material 2.");
        a2.addCompetence(1, "Skill 1", "Description skill 1.");
        a2.addCompetence(3, "Skill 3", "Description skill 3.");
        a2.addCompetence(4, "Skill 4", "Description skill 4.");

        assertEquals(a2, db.getActivity(2));

        Activity a3 = new Activity(3, 2020, 22, 1, false, 12,
                null, 1, "Typologies 1",
                "Description typologies 1", 1, "Factory Site 1-Area 1", 0,
                null, 0, 1, "New description 1");
        a3.addMaterial(1, "Material 1", "Description Material 1.");
        a3.addMaterial(2, "Material 2", "Description Material 2.");
        a3.addCompetence(1, "Skill 1", "Description skill 1.");
        a3.addCompetence(5, "Skill 5", "Description skill 5.");

        assertEquals(a3, db.getActivity(3));
    }

    @Test
    void deleteActivity() {
        assertTrue(db.deleteActivity(1));

        // Actual
        List<Activity> actual = db.getActivity('p');

        // Expected
        ArrayList<Activity> expected = new ArrayList<>();
        Activity a3 = new Activity(3, 2020, 21, 0, true, 20,
                "Activity 3 description.", 2, "Typologies 2",
                "Description typologies 2", 1, "Factory Site 1-Area 1", 2,
                "Procedure 2", 0, 1, "Description workspace notes 1");
        a3.addMaterial(1, "Material 1", "Description Material 1.");
        expected.add(a3);
        Activity a4 = new Activity(4, 2020, 21, 0, true, 40,
                "Activity 4 description.", 1, "Typologies 1",
                "Description typologies 1", 2, "Factory Site 1-Area 2", 2,
                "Procedure 2", 0, 0, null);
        a4.addMaterial(1, "Material 1", "Description Material 1.");
        a4.addMaterial(3, "Material 3", "Description Material 3.");
        expected.add(a4);

        assertEquals(expected, actual);
    }
}
package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.dbinteract.WorkspaceNotesDecorator;
import it.unisa.diem.se.team3.models.WorkspaceNotes;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkspaceNotesDecoratorTest {
    private WorkspaceNotesDecorator db;

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new WorkspaceNotesDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery =
                "INSERT INTO workspace_notes (id, description) VALUES (1, 'Description workspace notes 1'), (2, 'Description workspace notes 2'); " +
                        "INSERT INTO factory_site (id, name) VALUES (1, 'Factory Site 1'); " +
                        "INSERT INTO area (id, name, factory_site, workspace_notes) VALUES (1, 'Area 1', 1, 1), (2, 'Area 2', 1, null); " +
                        "INSERT INTO site (id, factory_site, area) VALUES (1, 1, 1), (2, 1, 2); " +
                        "ALTER SEQUENCE workspace_notes_id RESTART WITH 3;";
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery =
                "DELETE FROM site CASCADE; " +
                        "DELETE FROM area CASCADE;" +
                        "DELETE FROM factory_site CASCADE; " +
                        "ALTER SEQUENCE workspace_notes_id RESTART WITH 1;" +
                        "DELETE FROM workspace_notes CASCADE;";
        try (Statement stmt = db.getConn().createStatement()) {
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.disconnect();
    }

    @Test
    void addWorkspaceNotes() {
        assertTrue(db.addWorkspaceNotes("Description workspace notes 3", new long[]{1}));
        assertTrue(db.addWorkspaceNotes("Description workspace notes 4", new long[]{2}));

        List<WorkspaceNotes> actual = db.getWorkspaceNotes();

        ArrayList<WorkspaceNotes> expected = new ArrayList<>();
        expected.add(new WorkspaceNotes(1, "Description workspace notes 1"));
        expected.add(new WorkspaceNotes(2, "Description workspace notes 2"));
        expected.add(new WorkspaceNotes(3, "Description workspace notes 3"));
        expected.get(2).addSite(1, "Factory Site 1-Area 1");
        expected.add(new WorkspaceNotes(4, "Description workspace notes 4"));
        expected.get(3).addSite(2, "Factory Site 1-Area 2");

        assertEquals(expected, actual);
    }

    @Test
    void addWorkspaceNotesDbDisconnected() {
        db.disconnect();

        assertFalse(db.addWorkspaceNotes("Description workspace notes 1", new long[]{}));

        db.connect();
    }

    @Test
    void getWorkspaceNotes() {
        List<WorkspaceNotes> actual = db.getWorkspaceNotes();

        ArrayList<WorkspaceNotes> expected = new ArrayList<>();
        expected.add(new WorkspaceNotes(1, "Description workspace notes 1"));
        expected.get(0).addSite(1, "Factory Site 1-Area 1");
        expected.add(new WorkspaceNotes(2, "Description workspace notes 2"));

        assertEquals(expected, actual);
    }

    @Test
    void getWorkspaceNotesById() {
        WorkspaceNotes actual = db.getWorkspaceNotes(2);

        WorkspaceNotes expected = new WorkspaceNotes(2, "Description workspace notes 2");

        assertEquals(expected, actual);
    }

    @Test
    void deleteWorkspaceNotes() {
        assertTrue(db.deleteWorkspaceNotes(1));
        List<WorkspaceNotes> actual = db.getWorkspaceNotes();

        ArrayList<WorkspaceNotes> expected = new ArrayList<>();
        expected.add(new WorkspaceNotes(2, "Description workspace notes 2"));

        assertEquals(expected, actual);

        try (Statement stmt = db.getConn().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT workspace_notes FROM area WHERE id = 1;"
            )) {
                rs.next();
                assertEquals(0, rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void editWorkspaceNotes() {
        assertTrue(db.editWorkspaceNotes(1, "NEW Description workspace notes", new long[]{}));
        assertTrue(db.editWorkspaceNotes(2, "NEW Description workspace notes", new long[]{1, 2}));
        List<WorkspaceNotes> actual = db.getWorkspaceNotes();

        ArrayList<WorkspaceNotes> expected = new ArrayList<>();
        expected.add(new WorkspaceNotes(1, "NEW Description workspace notes"));
        expected.add(new WorkspaceNotes(2, "NEW Description workspace notes"));
        expected.get(1).addSite(1, "Factory Site 1-Area 1");
        expected.get(1).addSite(2, "Factory Site 1-Area 2");

        assertEquals(expected, actual);
    }
}
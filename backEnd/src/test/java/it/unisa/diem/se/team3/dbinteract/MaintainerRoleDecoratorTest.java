package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.dbinteract.MaintainerRoleDecorator;
import it.unisa.diem.se.team3.models.MaintainerRole;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MaintainerRoleDecoratorTest {
    private MaintainerRoleDecorator db;

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = new MaintainerRoleDecorator(ServletUtil.connectDb());
        db.connect();

        String populateQuery = "DELETE FROM maintainer_role CASCADE;" +
                "ALTER SEQUENCE maintainer_role_id RESTART WITH 4; " +
                "INSERT INTO maintainer_role (id, name, description) " +
                "VALUES (1, 'Role 1', 'Description role 1.'), (2, 'Role 2', 'Description role 2.'), (3, 'Role 3', 'Description role 3.');";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(populateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        String deleteQuery = "DELETE FROM maintainer_role CASCADE; " +
                "ALTER SEQUENCE maintainer_role_id RESTART WITH 1;";
        try (Statement stmt = db.getConn().createStatement()){
            stmt.execute(deleteQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }

    @Test
    void getMaintainerRolesAll() {
        // Actual
        List<MaintainerRole> actual = db.getMaintainerRoles();

        // Make expected
        ArrayList<MaintainerRole> expected = new ArrayList<>();
        expected.add(new MaintainerRole(1, "Role 1", "Description role 1."));
        expected.add(new MaintainerRole(2, "Role 2", "Description role 2."));
        expected.add(new MaintainerRole(3, "Role 3", "Description role 3."));

        // Make test
        assertEquals(expected, actual);
    }

    @Test
    void getMaintainersRoleById() {
        // Actual
        MaintainerRole actual = db.getMaintainerRoles(2);

        // Make expected
        MaintainerRole expected = new MaintainerRole(2, "Role 2", "Description role 2.");

        // Make test
        assertEquals(expected, actual);
    }

    @Test
    void getMaintainersRoleByIdNotExisting() {
        // Actual
        MaintainerRole actual = db.getMaintainerRoles(5);

        // Make
        assertNull(actual);
    }

    @Test
    void addMaintainerRoleTest() {
        // Made add
        assertTrue(db.addMaintainerRole("Plumber", "Description of plumber."));

        // Actual
        List<MaintainerRole> actual = db.getMaintainerRoles();

        // Make expected
        ArrayList<MaintainerRole> expected = new ArrayList<>();
        expected.add(new MaintainerRole(1, "Role 1", "Description role 1."));
        expected.add(new MaintainerRole(2, "Role 2", "Description role 2."));
        expected.add(new MaintainerRole(3, "Role 3", "Description role 3."));
        expected.add(new MaintainerRole(4, "Plumber", "Description of plumber."));

        // Match
        assertEquals(expected, actual);
    }

    @Test
    void deleteMaintainerRole() {
        // Actual
        assertTrue(db.deleteMaintainerRole(1));

        // Expected
        ArrayList<MaintainerRole> expected = new ArrayList<>();
        expected.add(new MaintainerRole(2, "Role 2", "Description role 2."));
        expected.add(new MaintainerRole(3, "Role 3", "Description role 3."));

        // Actual
        List<MaintainerRole> actual = db.getMaintainerRoles();

        // Mach
        assertEquals(expected, actual);
    }

    @Test
    void editMaintainerRole() {
        // Made edit
        assertTrue(db.editMaintainerRole(1, "Plumber", "Description of plumber."));

        // Actual
        List<MaintainerRole> actual = db.getMaintainerRoles();

        // Make expected
        ArrayList<MaintainerRole> expected = new ArrayList<>();
        expected.add(new MaintainerRole(1, "Plumber", "Description of plumber."));
        expected.add(new MaintainerRole(2, "Role 2", "Description role 2."));
        expected.add(new MaintainerRole(3, "Role 3", "Description role 3."));

        // Match
        assertEquals(expected, actual);
    }
}
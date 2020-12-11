package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.WorkspaceNotes;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Decorator class to query the workspace_notes table.
 */
public class WorkspaceNotesDecorator extends DbDecorator {

    /**
     * Class used to decorate a Db Interface object and use the queries provided on the workspace_notes table.
     *
     * @param db: an object implementing DbInterface.
     */
    public WorkspaceNotesDecorator(DbInterface db) {
        super(db);
    }

    /**
     * This method allows add a tuple to the workspace_notes table in the db.
     *
     * @param description: the workspace description
     * @param sites:       a list of sites id to add.
     * @return true if no error occur, else false.
     */
    public boolean addWorkspaceNotes(@NotNull String description, long[] sites) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "INSERT INTO workspace_notes (id, description) VALUES (nextval('workspace_notes_id'), ?);")) {
            stmt.setString(1, description);
            stmt.execute();
        } catch (SQLException e) {
            return false;
        }
        for (long site : sites) {
            try (PreparedStatement stmt = getConn().prepareStatement(
                    "UPDATE area SET workspace_notes = currval('workspace_notes_id') WHERE id IN (SELECT area FROM site WHERE id = ?);")) {
                stmt.setLong(1, site);
                stmt.execute();
            } catch (SQLException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method displays a list of WorkspaceNotes elements, which represent data from the activity and related
     * tables.
     *
     * @return a list of WorkspaceNotes Model or null if an error occur.
     */
    public ArrayList<WorkspaceNotes> getWorkspaceNotes() {
        ArrayList<WorkspaceNotes> result = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT w.id AS workspace_id, w.description AS workspace_description, s.id AS site_id, " +
                             "fs.name AS factory_name, a.name AS area_name " +
                             "FROM ((workspace_notes AS w LEFT JOIN " +
                             "(area AS a JOIN factory_site fs ON a.factory_site = fs.id) ON a.workspace_notes = w.id ) " +
                             "LEFT JOIN site AS s ON s.area = a.id) ORDER BY w.id, s.id;")) {
            long prevId = 0;
            while (rs.next()) {
                long id = rs.getLong("workspace_id");
                if (id != prevId) {
                    result.add(new WorkspaceNotes(id, rs.getString("workspace_description")));
                }
                long siteId = rs.getLong("site_id");
                if (siteId != 0) {
                    result.get(result.size() - 1).addSite(siteId, rs.getString("factory_name") + "-" +
                            rs.getString("area_name"));
                }
                prevId = id;
            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }

    /**
     * This method displays a WorkspaceNotes elements selected by his id.
     *
     * @param id: the workspace notes id;
     * @return a WorkspaceNotes object or null if an error occur.
     */
    public WorkspaceNotes getWorkspaceNotes(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT w.id AS workspace_id, w.description AS workspace_description, s.id AS site_id, " +
                        "fs.name AS factory_name, a.name AS area_name " +
                        "FROM ((workspace_notes AS w LEFT JOIN " +
                        "(area AS a JOIN factory_site fs ON a.factory_site = fs.id) ON a.workspace_notes = w.id ) " +
                        "LEFT JOIN site AS s ON s.area = a.id) WHERE w.id = ?;")) {
            stmt.setLong(1, id);
            WorkspaceNotes result;
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                result = new WorkspaceNotes(rs.getLong("workspace_id"), rs.getString("workspace_description"));
                do {
                    long siteId = rs.getLong("site_id");
                    if (siteId != 0) {
                        String siteName = rs.getString("factory_name") + "-" + rs.getString("area_name");
                        result.addSite(siteId, siteName);
                    }
                } while (rs.next());
            }
            return result;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * This method  delete a tuple from the workspace_notes table based on its id.
     *
     * @param id: id of the tuple to delete
     * @return true if no error occur, else false.
     */
    public boolean deleteWorkspaceNotes(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "UPDATE area SET workspace_notes = null WHERE workspace_notes = ?; " +
                        "DELETE FROM workspace_notes WHERE id = ?;")) {
            stmt.setLong(1, id);
            stmt.setLong(2, id);
            stmt.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * This method edit a tuple from the workspace_notes table based on its id.
     *
     * @param id:          the id of the tuple to edit.
     * @param description: the new description to insert
     * @param sites:       a list of sites id to add.
     * @return true if no error occur, else false.
     */
    public boolean editWorkspaceNotes(long id, String description, long[] sites) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "UPDATE workspace_notes SET description = ? WHERE id = ?; " +
                        "UPDATE area SET workspace_notes = null WHERE workspace_notes = ?;")) {
            stmt.setString(1, description);
            stmt.setLong(2, id);
            stmt.setLong(3, id);
            stmt.execute();
            for (long site : sites) {
                try (PreparedStatement stm = getConn().prepareStatement(
                        "UPDATE area SET workspace_notes = ? WHERE id IN (SELECT area FROM site WHERE id = ?)")) {
                    stm.setLong(1, id);
                    stm.setLong(2, site);
                    stm.execute();
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}

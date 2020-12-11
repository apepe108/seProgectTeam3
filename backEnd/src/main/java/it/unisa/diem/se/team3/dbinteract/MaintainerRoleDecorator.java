package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.MaintainerRole;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Decorator class to query the maintainer_role table and related.
 */
public class MaintainerRoleDecorator extends DbDecorator {

    /**
     * Class used to decorate a Db Interface object and use the queries provided on the maintainer_role table.
     *
     * @param db: an object implementing DbInterface.
     */
    public MaintainerRoleDecorator(DbInterface db) {
        super(db);
    }

    /**
     * This method allows add a tuple to the maintainer_role table in the db. It takes the fields of the tuple as a parameter.
     *
     * @param name:        the name of the role.
     * @param description: the description of the role.
     * @return true if no error occur, else false.
     */
    public boolean addMaintainerRole(@NotNull String name, @NotNull String description) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "INSERT INTO maintainer_role (id, name, description) VALUES (nextval('maintainer_role_id'), ?, ?);"
        )){
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * This method allows you to view the contents of the maintainer_role table, provided as a Model list.
     *
     * @return a list of MaintainerRole Model or null if an error occur.
     */
    public ArrayList<MaintainerRole> getMaintainerRoles() {
        ArrayList<MaintainerRole> result = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM maintainer_role ORDER BY id;")){
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                result.add(new MaintainerRole(id, name, description));
            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }

    /**
     * This method displays an MaintainerRole elements with the selected id, which represent data from the
     * maintainer_role and related tables.
     *
     * @param id: the role id;
     * @return a MaintainerRole object or null if an error occur.
     */
    public MaintainerRole getMaintainerRoles(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement("SELECT * FROM maintainer_role WHERE id = ?;")){
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (!rs.next()) {
                    return null;
                }
                return new MaintainerRole(rs.getLong("id"), rs.getString("name"),
                        rs.getString("description"));
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * This method delete a tuple from the maintainer_role table based on its id.
     *
     * @param id: id of the tuple to delete.
     * @return true if no error occur, else false.
     */
    public boolean deleteMaintainerRole(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "DELETE FROM maintainer_role WHERE id = ?;"
        )){
            stmt.setLong(1, id);
            stmt.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * This method edit a tuple from the maintainer_role table based on its id. Need the parameter to edit.
     *
     * @param id:          the id of the tuple to edit.
     * @param name:        the new name to assign.
     * @param description: the new description to assign.
     * @return true if no error occur, else false.
     */
    public boolean editMaintainerRole(long id, @NotNull String name, @NotNull String description) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "UPDATE maintainer_role SET name = ?, description = ? WHERE id = ?"
        )){
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setLong(3, id);
            stmt.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.Materials;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Decorator class to query the materials table.
 */
public class MaterialsDecorator extends DbDecorator{

    /**
     * Class used to decorate a Db Interface object and use the queries provided on the materials table.
     *
     * @param db: an object implementing DbInterface.
     */
    public MaterialsDecorator(DbInterface db) {
        super(db);
    }

    /**
     * This method allows add a tuple to the materials table in the db. It takes the fields of the tuple as a parameter.
     *
     * @param name:        the name of the materials.
     * @param description: the description of the materials.
     * @return true if no error occur, else false.
     */
    public boolean addMaterials(@NotNull String name, @NotNull String description) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "INSERT INTO materials(id, name, description) VALUES (nextval('materials_id'), ?, ?);"
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
     * This method displays a list of Materials elements, which represent data from the materials and related tables.
     *
     * @return a list of Materials Model or null if an error occur.
     */
    public ArrayList<Materials> getMaterials() {
        ArrayList<Materials> result = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM materials ORDER BY id;")){
            while (rs.next()) {
                result.add(new Materials(rs.getLong("id"), rs.getString("name"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }

    /**
     * This method displays an Material elements with the selected id, which represent data from the
     * materials tables.
     *
     * @param id: the material id;
     * @return a Material object or null if an error occur.
     */
    public Materials getMaterial(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement("SELECT * FROM materials WHERE id = ?;")){
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (!rs.next()) {
                    return null;
                }
                return new Materials(rs.getLong("id"), rs.getString("name"),
                        rs.getString("description"));
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * This method delete a tuple from the materials table based on its id.
     *
     * @param id: id of the tuple to delete.
     * @return true if no error occur, else false.
     */
    public boolean deleteMaterials(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "DELETE FROM materials WHERE id = ?;"
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
     * @param name:        the new materials name.
     * @param description: the new materials description.
     * @return true if no error occur, else false.
     */
    public boolean editMaterials(long id, @NotNull String name, @NotNull String description) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "UPDATE materials SET name = ?, description = ? WHERE id = ?"
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

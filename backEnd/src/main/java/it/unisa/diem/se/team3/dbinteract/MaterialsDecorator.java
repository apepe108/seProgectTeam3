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
}

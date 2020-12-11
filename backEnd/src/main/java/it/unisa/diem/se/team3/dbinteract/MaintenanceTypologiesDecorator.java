package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.MaintenanceTypologies;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Decorator class to query the maintenance_typologies table.
 */
public class MaintenanceTypologiesDecorator extends DbDecorator {

    /**
     * Class used to decorate a Db Interface object and use the queries provided on the maintenance_typologies table.
     *
     * @param db: an object implementing DbInterface.
     */
    public MaintenanceTypologiesDecorator(DbInterface db) {
        super(db);
    }

    /**
     * This method displays a list of MaintenanceTypologies elements, which represent data from the
     * maintenance_typologies and related tables.
     *
     * @return a list of Typologies Model or null if an error occur.
     */
    public ArrayList<MaintenanceTypologies> getMaintenanceTypologies() {
        ArrayList<MaintenanceTypologies> result = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM maintenance_typologies;")) {
            while (rs.next()) {
                result.add(new MaintenanceTypologies(rs.getLong("id"), rs.getString("name"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }
}

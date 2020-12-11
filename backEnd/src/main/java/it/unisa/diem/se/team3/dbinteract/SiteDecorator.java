package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.Site;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Decorator class to query the factory_site, site and area table.
 */
public class SiteDecorator extends DbDecorator {

    /**
     * Base constructor, used to get an instance of the DbInterface interface used by the decorator classes.
     *
     * @param db : a concrete implementation of DbInterface.
     */
    public SiteDecorator(DbInterface db) {
        super(db);
    }

    /**
     * This method displays a list of Site elements, which represent data from the site and related tables.
     *
     * @return a list of Site Model or null if an error occur.
     */
    public ArrayList<Site> getSite() {
        ArrayList<Site> result = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM site_view;")){
            while (rs.next()) {
                result.add(new Site(rs.getLong("site_id"), rs.getString("factory_name")
                        + "-" + rs.getString("area_name")));
            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }
}

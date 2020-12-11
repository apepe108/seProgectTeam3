package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.Competencies;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Decorator class to query the competencies table.
 */
public class CompetenciesDecorator extends DbDecorator {

    /**
     * Class used to decorate a Db Interface object and use the queries provided on the competences table.
     *
     * @param db: an object implementing DbInterface.
     */
    public CompetenciesDecorator(DbInterface db) {
        super(db);
    }

    /**
     * This method displays a list of Competencies elements, which represent data from the competencies and related
     * tables, based on the type of activity chosen.
     *
     * @return a list of Competencies Model or null if an error occur.
     */
    public ArrayList<Competencies> getCompetencies() {

        ArrayList<Competencies> result = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM competences;")) {
            while (rs.next()) {
                result.add(new Competencies(rs.getLong("id"), rs.getString("name"),
                        rs.getString("description")));
            }
            return result;
        } catch (SQLException e) {
            return null;
        }
    }
}


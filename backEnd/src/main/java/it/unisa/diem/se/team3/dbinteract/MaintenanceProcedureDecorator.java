package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.MaintenanceProcedure;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Decorator class to add query for maintenance_procedures and other related to it.
 */
public class MaintenanceProcedureDecorator extends DbDecorator {

    /**
     * Base constructor, used to get an instance of the DbInterface interface used by the decorator classes.
     *
     * @param db : a concrete implementation of DbInterface.
     */
    public MaintenanceProcedureDecorator(DbInterface db) {
        super(db);
    }

    /**
     * This method displays a list of MaintenanceProcedure elements, which represent data from the
     * maintenance_procedures and related tables
     *
     * @return a list of MaintenanceProcedure Model or null if an error occur.
     */
    public ArrayList<MaintenanceProcedure> getMaintenanceProcedures() {
        ArrayList<MaintenanceProcedure> result = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM maintenance_procedures ORDER BY id;")) {
            while (rs.next()) {
                result.add(new MaintenanceProcedure(rs.getLong("id"), rs.getString("name"),
                        rs.getLong("smp")));
            }
            return result;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * This method return a pdf file by his id in the form of a byte array.
     *
     * @param id: the file id.
     * @return a byte[] representing the pdf file.
     */
    public byte[] getSmp(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement("SELECT pdf_file FROM smp WHERE id = ?;")){
            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rs.getBytes("pdf_file");
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * This method associate a pdf file to a procedure, storing it like a byte array.
     *
     * @param procedureId: the procedure to associate dte pdf file;
     * @param file: the InputStream representing the pdf file.
     * @return true if the association has success, else false.
     */
    public boolean associateSmp(long procedureId, InputStream file) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                        "DELETE FROM smp WHERE id IN (SELECT smp FROM maintenance_procedures AS m WHERE m.id = ?); " +
                        "INSERT INTO smp (id, pdf_file) VALUES (nextval('smp_id'), ?); " +
                        "UPDATE maintenance_procedures SET smp = currval('smp_id') WHERE id = ?;"
        )) {
            stmt.setLong(1, procedureId);
            stmt.setBinaryStream(2, file);
            stmt.setLong(3, procedureId);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}

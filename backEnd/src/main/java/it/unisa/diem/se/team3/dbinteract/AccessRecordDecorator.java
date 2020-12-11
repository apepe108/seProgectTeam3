package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.AccessRecord;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Decorator class to query the access_record table.
 */
public class AccessRecordDecorator extends DbDecorator {

    /**
     * Base constructor, used to get an instance of the DbInterface interface used by the decorator classes.
     *
     * @param db : a concrete implementation of DbInterface.
     */
    public AccessRecordDecorator(DbInterface db) {
        super(db);
    }

    /**
     * Create a new row access.
     *
     * @param email: mail of the user who access.
     * @param name:  name of the user who access.
     * @param role:  mail of the user who access.
     * @return the id of the row, 0 if a error occur.
     */
    public long createAccess(String email, String name, String role) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "INSERT INTO access_record (id, email, name, role, login_date)" +
                        "VALUES (nextval('access_record_id'), ?, ?, ?, ?);"
        )) {
            stmt.setString(1, email);
            stmt.setString(2, name);
            stmt.setString(3, role);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.execute();

            try (Statement stm = getConn().createStatement();
                 ResultSet rs = stm.executeQuery("SELECT currval('access_record_id');")
            ) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Update row of an access with the logout timestamp.
     *
     * @param id: the access id.
     * @return true if the ending is correct, else false.
     */
    public boolean endAccess(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "UPDATE access_record SET logout_date = ? WHERE id = ?;"
        )) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, id);
            stmt.execute();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * This method displays a list of access_record elements, which represent data from the access_record tables.
     *
     * @return a list of AccessRecord Model or null if an error occur.
     */
    public ArrayList<AccessRecord> getAccessRecord() {
        ArrayList<AccessRecord> result = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM access_record ORDER BY id;")){
            while (rs.next()) {
                Timestamp logout = rs.getTimestamp("logout_date");
                result.add(new AccessRecord(rs.getString("email"), rs.getString("name"),
                        rs.getString("role"), rs.getTimestamp("login_date").toLocalDateTime(),
                        (logout != null ? logout.toLocalDateTime() : null)));
            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }
}

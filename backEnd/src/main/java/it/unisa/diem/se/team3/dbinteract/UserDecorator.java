package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.User;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Class used to decorate a Db Interface object and use the queries provided on the users table.
 */
public class UserDecorator extends DbDecorator {

    /**
     * Base constructor, used to get an instance of the DbInterface interface used by the decorator classes.
     *
     * @param db : a concrete implementation of DbInterface.
     */
    public UserDecorator(DbInterface db) {
        super(db);
    }

    /**
     * This method add a tuple to the users table in the db.
     *
     * @param name:     the user real name;
     * @param email:    the user email;
     * @param password: the user password;
     * @param role:     the user role;
     * @return true if no error occur, else false.
     */
    public boolean addUsers(@NotNull String name, @NotNull String email, @NotNull String password, @NotNull String role) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "INSERT INTO users (internal_id, email, password) VALUES (nextval('user_id'), ?, ?);"
        )) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.execute();

            String roleQuery;
            if ("planner".equals(role)) {
                roleQuery = "INSERT INTO planner (internal_id, name, email) VALUES (currval('user_id'), ?, ?);";
            } else if ("maintainer".equals(role)) {
                roleQuery = "INSERT INTO maintainer (internal_id, name, email) VALUES (currval('user_id'), ?, ?);";
            } else {
                roleQuery = "INSERT INTO sysadmin (internal_id, name, email) VALUES (currval('user_id'), ?, ?);";
            }
            try (PreparedStatement stm = getConn().prepareStatement(roleQuery)) {
                stm.setString(1, name);
                stm.setString(2, email);
                stm.execute();
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * This method displays a list of User elements, which represent data from the user and related tables.
     *
     * @return a list of Users Model or null if an error occur.
     */
    public ArrayList<User> getUsers() {
        ArrayList<User> result = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT u.internal_id, pm.name, pm.email, u.password, pm.role " +
                     "FROM users AS u JOIN  " +
                     "(SELECT p.internal_id, p.name, p.email, 'Planner' AS role " +
                     "FROM planner AS p " +
                     "UNION " +
                     "SELECT m.internal_id, m.name, m.email, 'Maintainer' " +
                     "FROM maintainer AS m " +
                     "UNION " +
                     "SELECT s.internal_id, s.name, s.email, 'SysAdmin' AS role " +
                     "FROM sysadmin AS s) AS pm ON u.internal_id = pm.internal_id " +
                     "ORDER BY u.internal_id;")) {
            while (rs.next()) {
                result.add(new User(rs.getLong("internal_id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("password"), rs.getString("role")));
            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }

    /**
     * This method displays a User elements, which represent data from the user searched by his id.
     *
     * @param id: the id of the user to view.
     * @return a User Model, or null if an error occur.
     */
    public User getUsers(long id) {
        String query = "SELECT u.internal_id, pm.name, pm.email, u.password, pm.role " +
                "FROM users AS u JOIN  " +
                "(SELECT p.internal_id, p.name, p.email, 'Planner' AS role " +
                "FROM planner AS p " +
                "UNION " +
                "SELECT m.internal_id, m.name, m.email, 'Maintainer' " +
                "FROM maintainer AS m " +
                "UNION " +
                "SELECT s.internal_id, s.name, s.email, 'SysAdmin' AS role " +
                "FROM sysadmin AS s) AS pm ON u.internal_id = pm.internal_id " +
                "WHERE u.internal_id = ?;";
        try {
            PreparedStatement stmt = getConn().prepareStatement(query);
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return new User(rs.getLong("internal_id"), rs.getString("name"), rs.getString("email"),
                    rs.getString("password"), rs.getString("role"));
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * This method displays a User elements, which represent data from the user searched by his email.
     *
     * @param email: the id of the user to view.
     * @return a User Model, or null if an error occur.
     */
    public User getUsers(String email) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT u.internal_id, pm.name, pm.email, u.password, pm.role " +
                        "FROM users AS u JOIN  " +
                        "(SELECT p.internal_id, p.name, p.email, 'Planner' AS role " +
                        "FROM planner AS p " +
                        "UNION " +
                        "SELECT m.internal_id, m.name, m.email, 'Maintainer' " +
                        "FROM maintainer AS m " +
                        "UNION " +
                        "SELECT s.internal_id, s.name, s.email, 'SysAdmin' AS role " +
                        "FROM sysadmin AS s) AS pm ON u.internal_id = pm.internal_id " +
                        "WHERE u.email = ?;"
        )) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new User(rs.getLong("internal_id"), rs.getString("name"), rs.getString("email"),
                        rs.getString("password"), rs.getString("role"));
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * This method delete an user by his id.
     *
     * @param id: the id of the user to delete.
     * @return true if the delete success, otherwise false.
     */
    public boolean deleteUsers(long id) {
        String query = "DELETE FROM users WHERE internal_id = ?; DELETE FROM planner WHERE internal_id = ?; DELETE FROM maintainer WHERE internal_id = ?; DELETE FROM sysadmin WHERE internal_id = ?;";
        try (PreparedStatement stmt = getConn().prepareStatement(query)) {
            stmt.setLong(1, id);
            stmt.setLong(2, id);
            stmt.setLong(3, id);
            stmt.setLong(4, id);
            stmt.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * This method edit a user in the db.
     *
     * @param id:       the id of the user to edit.
     * @param name:     the user real name;
     * @param email:    the user email;
     * @param password: the user password;
     * @param role:     the user role;
     * @return true if no error occur, else false.
     */
    public boolean editUser(long id, @NotNull String name, @NotNull String email, @NotNull String password, @NotNull String role) {
        int res;
        try (PreparedStatement stmt = getConn().prepareStatement(
                "DELETE FROM maintainer WHERE internal_id = ?;" +
                        "DELETE FROM planner WHERE internal_id = ?;" +
                        "DELETE FROM sysadmin WHERE internal_id = ?;" +
                        "UPDATE users SET email = ? , password = ? WHERE internal_id = ?;"
        )) {
            stmt.setLong(1, id);
            stmt.setLong(2, id);
            stmt.setLong(3, id);
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.setLong(6, id);
            stmt.execute();


            String roleQuery;
            if ("planner".equals(role)) {
                roleQuery = "INSERT INTO planner (internal_id, name, email) VALUES (?, ?, ?);";
            } else if ("maintainer".equals(role)) {
                roleQuery = "INSERT INTO maintainer (internal_id, name, email) VALUES (?, ?, ?);";
            } else {
                roleQuery = "INSERT INTO sysadmin (internal_id, name, email) VALUES (?, ?, ?);";
            }
            try (PreparedStatement stm = getConn().prepareStatement(roleQuery)) {
                stm.setLong(1, id);
                stm.setString(2, name);
                stm.setString(3, email);
                stm.execute();
            }

        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}

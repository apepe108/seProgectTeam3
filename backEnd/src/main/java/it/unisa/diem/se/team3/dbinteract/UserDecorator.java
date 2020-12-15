package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.MaintainerRole;
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
    public boolean addUsers(@NotNull String name, @NotNull String email, @NotNull String password, @NotNull String role, long[] roleIds) {
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

            if ("maintainer".equals(role)) {
                for (long roleId : roleIds) {
                    try (PreparedStatement stm = getConn().prepareStatement(
                            "INSERT INTO is_a (maintainer, maintainer_role)  VALUES (currval('user_id'), ?);")) {
                        stm.setLong(1, roleId);
                        stm.execute();
                    }
                }
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
                long id = rs.getLong("internal_id");
                result.add(new User(id, rs.getString("name"),
                        rs.getString("email"), rs.getString("password"), rs.getString("role")));
                getMaintainerRoles(id, result.get(result.size() - 1));
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
            User u = new User(rs.getLong("internal_id"), rs.getString("name"), rs.getString("email"),
                    rs.getString("password"), rs.getString("role"));
            getMaintainerRoles(id, u);
            return u;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * For a selected user, add maintainerRoles data.
     */
    private void getMaintainerRoles(long id, User u) throws SQLException {
        PreparedStatement stmt = getConn().prepareStatement(
                "SELECT MR.* " +
                        "FROM maintainer m JOIN is_a ON m.internal_id = is_a.maintainer " +
                        "JOIN maintainer_role mr on is_a.maintainer_role = mr.id " +
                        "WHERE m.internal_id = ? " +
                        "ORDER BY mr.id;"
        );
        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            u.addRoles(rs.getLong("id"), rs.getString("name"), rs.getString("description"));
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
        String query = "DELETE FROM users WHERE internal_id = ?; DELETE FROM planner WHERE internal_id = ?; DELETE FROM is_a WHERE maintainer = ?; DELETE FROM maintainer WHERE internal_id = ?; DELETE FROM sysadmin WHERE internal_id = ?;";
        try (PreparedStatement stmt = getConn().prepareStatement(query)) {
            stmt.setLong(1, id);
            stmt.setLong(2, id);
            stmt.setLong(3, id);
            stmt.setLong(4, id);
            stmt.setLong(5, id);
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
     * @return true if no error occur, else false.
     */
    public boolean editUser(long id, @NotNull String name, @NotNull String email, @NotNull String password, long[] roleIds) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "DELETE FROM is_a WHERE maintainer = ?;" +
                        "UPDATE " + getUserRole(id)  + " SET name = ?, email = ? WHERE internal_id = ?;" +
                        "UPDATE users SET email = ? , password = ? WHERE internal_id = ?;"
        )) {
            stmt.setLong(1, id);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setLong(4, id);
            stmt.setString(5, email);
            stmt.setString(6, password);
            stmt.setLong(7, id);
            stmt.execute();

            if ("maintainer".equals(getUserRole(id))) {
                addMaintainerRole(id, roleIds);
            }

        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * Add role to a Maintainer
     */
    private void addMaintainerRole (long id, long[] roleIds) throws SQLException {
        for (long roleId : roleIds) {
            try (PreparedStatement stm = getConn().prepareStatement(
                    "INSERT INTO is_a (maintainer, maintainer_role)  VALUES (?, ?);")) {
                stm.setLong(1, id);
                stm.setLong(2, roleId);
                stm.execute();
            }
        }
    }

    /**
     * Get the role of the user by his id.
     */
    private String getUserRole(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT 'sysadmin' FROM sysadmin WHERE internal_id = ? " +
                        "UNION SELECT 'maintainer' FROM maintainer WHERE internal_id = ? " +
                        "UNION SELECT 'planner' FROM planner WHERE internal_id = ?")) {
            stmt.setLong(1, id);
            stmt.setLong(2, id);
            stmt.setLong(3, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return rs.getString(1);
                }
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }
}

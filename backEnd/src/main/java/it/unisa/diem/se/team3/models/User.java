package it.unisa.diem.se.team3.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the UserDecorator class.
 */
public class User implements Model {
    private final long id;
    private final String name;
    private final String email;
    private final String password;
    private final String role;
    private List<MaintainerRole> roles;

    /**
     * @param id:       the id of the user.
     * @param name:     real name of the user.
     * @param email:    the email of the user.
     * @param password: the password of the user.
     * @param role:     the specific user role (planner or maintainer).
     */
    public User(long id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        roles = new ArrayList<>();
    }

    /**
     * Add maintainer role at the user.
     *
     * @param id:          the id of the role.
     * @param name:        the name of the role.
     * @param description: the description of the role.
     */
    public void addRoles(long id, String name, String description) {
        roles.add(new MaintainerRole(id, name, description));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(role, user.role) &&
                Objects.equals(roles, user.roles);
    }

    /**
     * Method that derives, given the current object, its representation in JSON string.
     *
     * @return a String representing the object in JSON.
     */
    @Override
    public String toJSON() {
        return "{" + "\"id\":\"" + id + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"," +
                "\"role\":\"" + role + "\"," +
                "\"roles\":" + JsonUtil.toJson(roles) + "}";
    }

    /**
     * @return the user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the user's role.
     */
    public String getRole() {
        return role;
    }

    /**
     * @return the user's name.
     */
    public String getName() {
        return name;
    }
}

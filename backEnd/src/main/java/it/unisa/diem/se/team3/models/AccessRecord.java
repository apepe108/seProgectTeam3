package it.unisa.diem.se.team3.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the AccessRecordDecorator class.
 */
public class AccessRecord implements Model {
    private final String email;
    private final String name;
    private final String role;
    private final LocalDateTime login_date;
    private final LocalDateTime logout_date;

    /**
     * @param email:      the email of the user who access
     * @param name:       the name of the user who access
     * @param role:       the role of the user who access
     * @param login_date: the login date
     * @param logout_date the logout date
     */
    public AccessRecord(String email, String name, String role, LocalDateTime login_date, LocalDateTime logout_date) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.login_date = login_date;
        this.logout_date = logout_date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessRecord that = (AccessRecord) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(name, that.name) &&
                Objects.equals(role, that.role) &&
                Objects.equals(login_date, that.login_date) &&
                Objects.equals(logout_date, that.logout_date);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * Method that derives, given the current object, its representation in JSON string.
     *
     * @return a String representing the object in JSON.
     */
    @Override
    public String toJSON() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return "{" + "\"email\":\"" + email + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"role\":\"" + role + "\"," +
                "\"login_date\":\"" + login_date.format(formatter) + "\"," +
                "\"logout_date\":\"" + (logout_date != null ? logout_date.format(formatter) : "null" ) + "\"" + "}";
    }

    @Override
    public String toString(){
        String s= toJSON();
        return s;
    }


}

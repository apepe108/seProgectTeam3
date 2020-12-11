package it.unisa.diem.se.team3.models;

/**
 * Class used to create the rows that represent the queries performed on a database by the MaintainerRoleDecorator
 * class.
 */
public class MaintainerRole implements Model {
    private final long id;
    private final String name;
    private final String description;

    /**
     * It instantiates an object of the class MaintainerRole, which represents a tuple of the corresponding table.
     *
     * @param id:          the id of the role.
     * @param name:        the name of the role.
     * @param description: the description of te role.
     */
    public MaintainerRole(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MaintainerRole that = (MaintainerRole) o;
        return id == that.id &&
                name.equals(that.name) &&
                description.equals(that.description);
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
                "\"description\":\"" + description + "\"}";
    }
}

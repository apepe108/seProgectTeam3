package it.unisa.diem.se.team3.models;

import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the TypologiesDecorator class.
 */
public class MaintenanceTypologies implements Model {
    private final long id;
    private final String name;
    private final String description;

    /**
     * @param id          the id of the maintenance typologies
     * @param name        the name of the maintenance typologies
     * @param description the description of the maintenance typologies
     */
    public MaintenanceTypologies(long id, String name, String description) {
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
        MaintenanceTypologies that = (MaintenanceTypologies) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
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
                "\"description\":\"" + description + "\"" + "}";
    }
}

package it.unisa.diem.se.team3.models;

import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the MaterialsDecorator class.
 */
public class Materials implements Model {
    private final long id;
    private final String name;
    private final String description;

    /**
     * @param id          the id of the material
     * @param name        the name of the material
     * @param description the description of the material
     */
    public Materials(long id, String name, String description) {
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
        Materials materials = (Materials) o;
        return id == materials.id &&
                Objects.equals(name, materials.name) &&
                Objects.equals(description, materials.description);
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

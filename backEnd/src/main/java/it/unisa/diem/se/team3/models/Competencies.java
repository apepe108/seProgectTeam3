package it.unisa.diem.se.team3.models;

import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the CompetenciesDecorator class.
 */
public class Competencies implements Model {
    private final long id;
    private final String name;
    private final String description;

    /**
     * It instantiates an object of the class Competencies, which represents a tuple of the corresponding table.
     *
     * @param id:          the competence id.
     * @param name:        the competence name.
     * @param description: the competence description.
     */
    public Competencies(long id, String name, String description) {
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
        Competencies that = (Competencies) o;
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
                "\"description\":\"" + description + "\"}";
    }
}

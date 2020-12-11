package it.unisa.diem.se.team3.models;

/**
 * Interface used to build classes that represent queries performed on a database.
 */
public interface Model {

    /**
     * Method that derives, given the current object, its representation in JSON string.
     *
     * @return a String representing the object in JSON.
     */
    String toJSON();

    @Override
    boolean equals(Object o);
}

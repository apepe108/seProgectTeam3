package it.unisa.diem.se.team3.models;

import java.util.Objects;

/**
 *
 */
public class MaintenanceProcedure implements Model {
    private final long id;
    private final String name;
    private final long smp;

    /**
     * It instantiates an object of the class MaintenanceProcedures, which represents a tuple of the corresponding table.
     * @param id: the procedure id;
     * @param name: the procedure id;
     * @param smp: the id od the smp related to the procedure.
     */
    public MaintenanceProcedure(long id, String name, long smp) {
        this.id = id;
        this.name = name;
        this.smp = smp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MaintenanceProcedure that = (MaintenanceProcedure) o;
        return id == that.id &&
                smp == that.smp &&
                Objects.equals(name, that.name);
    }

    /**
     * Method that derives, given the current object, its representation in JSON string.
     *
     * @return a String representing the object in JSON.
     */
    @Override
    public String toJSON() {
        return "{" + "\"id\":\"" + id + "\","
                + "\"name\":\"" + name + "\","
                + "\"smp\":\"" + smp + "\"}";
    }
}

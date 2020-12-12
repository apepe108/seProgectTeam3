package it.unisa.diem.se.team3.models;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the AvailabilityDecorator class.
 */
public class AvailabilityDaily implements Model {

    /**
     * Class representing a slot.
     */
    protected static class Slot implements Model {
        private final long idSlot;
        private final String description;
        private final int minutes;

        /**
         * Create a Day object, which have information about the day's availability.
         *
         * @param idSlot:      daily slot's id.
         * @param description: time description of teh slot (e.g., 09:00-10:00).
         * @param minutes:     the minutes remaining for this slot.
         */
        public Slot(long idSlot, String description, int minutes) {
            this.idSlot = idSlot;
            this.description = description;
            this.minutes = minutes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Slot slot = (Slot) o;
            return idSlot == slot.idSlot &&
                    minutes == slot.minutes &&
                    Objects.equals(description, slot.description);
        }

        /**
         * Method that derives, given the current object, its representation in JSON string.
         *
         * @return a String representing the object in JSON.
         */
        @Override
        public String toJSON() {
            return "{" + "\"id\":\"" + idSlot + "\","
                    + "\"description\":\"" + description + "\","
                    + "\"minutes\":\"" + minutes + "\"}";
        }

        @Override
        public String toString() {
            return toJSON();
        }
    }

    private final long id;
    private final String name;
    private final String competenceCompliance;
    private final ArrayList<Slot> slots;

    /**
     * Create a Availability daily object, which have information about the maintainer's availability.
     *
     * @param id:                    the maintainer's id;
     * @param name:                  the maintainer's name;
     * @param competence_compliance: the maintainer competence compliance.
     */
    public AvailabilityDaily(long id, String name, String competence_compliance) {
        this.id = id;
        this.name = name;
        this.competenceCompliance = competence_compliance;
        slots = new ArrayList<>();
    }

    /**
     * Add a slot's information.
     *
     * @param idSlot:      daily slot's id.
     * @param description: time description of teh slot (e.g., 09:00-10:00).
     * @param minutes:     the minutes remaining for this slot.
     */
    public void addSlot(long idSlot, String description, int minutes) {
        slots.add(new Slot(idSlot, description, minutes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AvailabilityDaily that = (AvailabilityDaily) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(competenceCompliance, that.competenceCompliance) &&
                Objects.equals(slots, that.slots);
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
                + "\"competence_compliance\":\"" + competenceCompliance + "\","
                + "\"availability\":" + JsonUtil.toJson(slots) + "}";
    }

    @Override
    public String toString() {
        return toJSON();
    }
}

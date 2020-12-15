package it.unisa.diem.se.team3.models;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the AvailabilityDecorator class.
 */
public class AvailabilityWeekly implements Model {

    /**
     * Class representing a weekly day.
     */
    protected static class Day implements Model {
        private final int day;
        private final int percentage;

        /**
         * Create a Day object, which have information about the day's availability.
         *
         * @param day:        number to 1 from 7 representing the weekly day.
         * @param percentage: number from 1 to 100 representing the availability's percentage.
         */
        public Day(int day, int percentage) {
            this.day = day;
            this.percentage = percentage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Day day1 = (Day) o;
            return day == day1.day &&
                    percentage == day1.percentage;
        }

        /**
         * Method that derives, given the current object, its representation in JSON string.
         *
         * @return a String representing the object in JSON.
         */
        @Override
        public String toJSON() {
            return "{" + "\"day\":\"" + day + "\","
                    + "\"percentage\":\"" + percentage + "\"}";
        }
    }

    private final long id;
    private final String name;
    private final String competenceCompliance;
    private final ArrayList<Day> days;

    /**
     * Create a Availability Weekly object, which have information about the maintainer's availability.
     *
     * @param id:                    the maintainer's id;
     * @param name:                  the maintainer's name;
     * @param competence_compliance: the maintainer competence compliance.
     */
    public AvailabilityWeekly(long id, String name, String competence_compliance) {
        this.id = id;
        this.name = name;
        this.competenceCompliance = competence_compliance;
        days = new ArrayList<>();
    }

    /**
     * Add a day's information.
     *
     * @param id:         number to 1 from 7 representing the weekly day.
     * @param percentage: number from 1 to 100 representing the availability's percentage.
     */
    public void addDay(int id, int percentage) {
        days.add(new Day(id, percentage));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AvailabilityWeekly that = (AvailabilityWeekly) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(competenceCompliance, that.competenceCompliance) &&
                Objects.equals(days, that.days);
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
                + "\"availability\":" + JsonUtil.toJson(days) + "}";
    }
}

package it.unisa.diem.se.team3.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the WorkspaceNotes class.
 */
public class WorkspaceNotes implements Model {
    private final long id;
    private final String description;
    private final List<Site> sites;

    /**
     * It instantiates an object of the class WorkspaceNotes, which represents a tuple of the corresponding table.
     *
     * @param id:          the id of the workspace notes
     * @param description: the description of the workspace notes
     */
    public WorkspaceNotes(long id, String description) {
        this.id = id;
        this.description = description;
        sites = new ArrayList<>();
    }

    public void addSite(long id, String name) {
        sites.add(new Site(id, name));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WorkspaceNotes that = (WorkspaceNotes) o;
        return id == that.id &&
                Objects.equals(description, that.description) &&
                Objects.equals(sites, that.sites);
    }

    /**
     * Method that derives, given the current object, its representation in JSON string.
     *
     * @return a String representing the object in JSON.
     */
    @Override
    public String toJSON() {
        return "{" + "\"id\":\"" + id + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"site\":" + JsonUtil.toJson(sites) + "}";
    }
}

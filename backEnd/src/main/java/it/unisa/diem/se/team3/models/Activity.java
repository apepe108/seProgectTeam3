package it.unisa.diem.se.team3.models;


import java.util.ArrayList;
import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the ActivityDecorator class.
 */
public class Activity implements Model {
    private final long id;
    private final int year;
    private final int day;
    private final int week;
    private final boolean interruptibility;
    private final int estimatedInterventionTime;
    private final String description;
    private final MaintenanceTypologies typology;
    private final Site site;
    private final ArrayList<Materials> materials;
    private final MaintenanceProcedure procedure;
    private final WorkspaceNotes workspace;
    private final ArrayList<Competencies> competencies;

    /**
     * Create an Activity object, which have all information about that, excluding materials.
     *
     * @param id:                        the activity's id
     * @param year:                      the year for which the activity is planned.
     * @param week:                      the week for which the activity is planned.
     * @param day:                       the day of thr week (1-7) which the activity is planned.
     * @param interruptibility:          indicates whether the activity is interruptible.
     * @param estimatedInterventionTime: activity's estimated time in minutes.
     * @param description:               the activity's description.
     * @param typology_id:               the id of the type of maintenance linked to the activity.
     * @param typology_name:             the name of the type of maintenance linked to the activity.
     * @param typology_description:      the id of the type of maintenance linked to the activity.
     * @param site_id:                   the id of the site linked to the activity.
     * @param site_name:                 the name of the site linked to the activity.
     * @param procedure_id:              the id of the procedure linked to the activity.
     * @param procedure_name:            the name of the procedure linked to the activity.
     * @param procedure_smp              the SMP id of the procedure linked to the activity.
     * @param workspaceId:               the workspaces id of the area of activity.
     * @param workspaceDescription:      the workspaces description of the area of activity.
     */
    public Activity(long id, int year, int week, int day, boolean interruptibility, int estimatedInterventionTime,
                    String description, long typology_id, String typology_name, String typology_description,
                    long site_id, String site_name, long procedure_id, String procedure_name, long procedure_smp,
                    long workspaceId, String workspaceDescription) {
        this.id = id;
        this.year = year;
        this.week = week;
        this.day = day;
        this.interruptibility = interruptibility;
        this.estimatedInterventionTime = estimatedInterventionTime;
        this.description = description;
        this.typology = new MaintenanceTypologies(typology_id, typology_name, typology_description);
        this.site = new Site(site_id, site_name);
        this.procedure = new MaintenanceProcedure(procedure_id, procedure_name, procedure_smp);
        this.materials = new ArrayList<>();
        this.workspace = new WorkspaceNotes(workspaceId, workspaceDescription);
        this.competencies = new ArrayList<>();
    }

    /**
     * Add a material needed for the activity.
     *
     * @param id:          the material's id.
     * @param name:        the material's name.
     * @param description: the material's description.
     */
    public void addMaterial(long id, String name, String description) {
        materials.add(new Materials(id, name, description));
    }

    /**
     * Add a skill needed for the activity.
     *
     * @param id:          the skill's id.
     * @param name:        the skill's name.
     * @param description: the skill's description.
     */
    public void addCompetence(long id, String name, String description) {
        competencies.add(new Competencies(id, name, description));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Activity activity = (Activity) o;
        return id == activity.id &&
                year == activity.year &&
                day == activity.day &&
                week == activity.week &&
                interruptibility == activity.interruptibility &&
                estimatedInterventionTime == activity.estimatedInterventionTime &&
                Objects.equals(description, activity.description) &&
                Objects.equals(typology, activity.typology) &&
                Objects.equals(site, activity.site) &&
                Objects.equals(materials, activity.materials) &&
                Objects.equals(procedure, activity.procedure) &&
                Objects.equals(workspace, activity.workspace) &&
                Objects.equals(competencies, activity.competencies);
    }

    /**
     * Method that derives, given the current object, its representation in JSON string.
     *
     * @return a String representing the object in JSON.
     */
    @Override
    public String toJSON() {
        return "{" + "\"id\":\"" + id + "\","
                + "\"year\":\"" + year + "\","
                + "\"week\":\"" + week + "\","
                + "\"day\":\"" + day + "\","
                + "\"site\":" + site.toJSON() + ","
                + "\"typology\":" + typology.toJSON() + ","
                + "\"description\":\"" + description + "\","
                + "\"estimatedInterventionTime\":\"" + estimatedInterventionTime + "\","
                + "\"interruptibility\":\"" + interruptibility + "\","
                + "\"materials\":" + JsonUtil.toJson(materials) + ","
                + "\"maintenanceProcedures\":" + procedure.toJSON() + ","
                + "\"workspace\":" + workspace.toJSON() + ","
                + "\"skill\":" + JsonUtil.toJson(competencies) + "}";
    }
}

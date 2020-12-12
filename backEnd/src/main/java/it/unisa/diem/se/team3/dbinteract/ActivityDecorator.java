package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.Activity;
import it.unisa.diem.se.team3.models.Materials;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Decorator class to add query for activity and other related to it.
 */
public class ActivityDecorator extends DbDecorator {

    /**
     * Base constructor, used to get an instance of the DbInterface interface used by the decorator classes.
     *
     * @param db: a concrete implementation of DbInterface.
     */
    public ActivityDecorator(DbInterface db) {
        super(db);
    }

    /**
     * This method displays a list of Activity elements, which represent data from the activity and related tables,
     * based on the type of activity chosen.
     *
     * @param type: 'p' to view the scheduled tasks, 'e' to view the ewo tasks.
     * @return a list of Activity or null if an error occur.
     */
    public ArrayList<Activity> getActivity(char type) {
        ArrayList<Activity> result = new ArrayList<>();
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT a.id, a.year, a.week, a.day, a.interruptibility, a.estimated_intervention_time, a.description, mp.id AS procedure_id, mp.name AS procedure_name, mp.smp AS procedure_smp, mt.id AS typology_id, mt.name AS typology_name, mt.description AS typology_description, s.site_id, s.factory_name, s.area_name, s.workspace_id, s.workspace_description \n" +
                        "FROM activity AS a LEFT JOIN maintenance_typologies AS mt ON a.maintenance_typologies = mt.id LEFT JOIN maintenance_procedures AS mp ON a.maintenance_procedures = mp.id LEFT JOIN site_view AS s ON a.site = s.site_id \n" +
                        "WHERE a.type = ? AND a.id NOT IN (SELECT activity FROM assigned_activity) ORDER BY a.id;"
        )) {
            stmt.setString(1, String.valueOf(type));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long activityId = rs.getLong("id");
                    result.add(new Activity(activityId, rs.getInt("year"), rs.getShort("week"),
                            rs.getShort("day"), rs.getBoolean("interruptibility"),
                            rs.getInt("estimated_intervention_time"), rs.getString("description"),
                            rs.getLong("typology_id"), rs.getString("typology_name"),
                            rs.getString("typology_description"), rs.getLong("site_id"),
                            rs.getString("factory_name") + "-" + rs.getString("area_name"),
                            rs.getLong("procedure_id"), rs.getString("procedure_name"),
                            rs.getLong("procedure_smp"), rs.getLong("workspace_id"),
                            rs.getString("workspace_description")));
                    getMaterialNeeded(activityId, result.get(result.size() - 1));
                    getSkillNeeded(activityId, result.get(result.size() - 1));
                }

            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }

    /**
     * This method displays an Activity elements with the selected id, which represent data from the activity and
     * related tables.
     *
     * @param id: the id for the activity to view.
     * @return an Activity object, null if there are no activities with that id or an error occur.
     */
    public Activity getActivity(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT a.id, a.year, a.week, a.day, a.interruptibility, a.estimated_intervention_time, a.description, mp.id AS procedure_id, mp.name AS procedure_name, mp.smp AS procedure_smp, mt.id AS typology_id, mt.name AS typology_name, mt.description AS typology_description, s.site_id, s.factory_name, s.area_name, s.workspace_id, s.workspace_description \n" +
                        "FROM activity AS a LEFT JOIN maintenance_typologies AS mt ON a.maintenance_typologies = mt.id LEFT JOIN maintenance_procedures AS mp ON a.maintenance_procedures = mp.id LEFT JOIN site_view AS s ON a.site = s.site_id \n" +
                        "WHERE a.id = ?;"
        )) {
            stmt.setLong(1, id);
            Activity result;
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {   // Not existing id
                    return null;
                }
                result = new Activity(rs.getLong("id"), rs.getInt("year"),
                        rs.getShort("week"), rs.getShort("day"),
                        rs.getBoolean("interruptibility"), rs.getInt("estimated_intervention_time"),
                        rs.getString("description"), rs.getLong("typology_id"),
                        rs.getString("typology_name"), rs.getString("typology_description"),
                        rs.getLong("site_id"), rs.getString("factory_name") + "-" +
                        rs.getString("area_name"), rs.getLong("procedure_id"),
                        rs.getString("procedure_name"), rs.getLong("procedure_smp"),
                        rs.getLong("workspace_id"), rs.getString("workspace_description"));
                getMaterialNeeded(id, result);
                getSkillNeeded(id, result);
            }
            return result;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * This method displays a list of Activity elements, which represent data from the activity and related tables,
     * based on the week and year of activity chosen.
     *
     * @param year: the year for which the activity is planned.
     * @param week: the week for which the activity is planned.
     * @param type: 'p' to view the scheduled tasks, 'e' to view the ewo tasks.
     * @return a list of Activity or null if an error occur.
     */
    public ArrayList<Activity> getActivity(long year, long week, char type) {
        ArrayList<Activity> result = new ArrayList<>();
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT a.id, a.year, a.week, a.day, a.interruptibility, a.estimated_intervention_time, a.description, mp.id AS procedure_id, mp.name AS procedure_name, mp.smp AS procedure_smp, mt.id AS typology_id, mt.name AS typology_name, mt.description AS typology_description, s.site_id, s.factory_name, s.area_name, s.workspace_id, s.workspace_description \n" +
                        "FROM activity AS a LEFT JOIN maintenance_typologies AS mt ON a.maintenance_typologies = mt.id LEFT JOIN maintenance_procedures AS mp ON a.maintenance_procedures = mp.id LEFT JOIN site_view AS s ON a.site = s.site_id \n" +
                        "WHERE a.year = ? AND a.week = ? AND a.type = ? AND a.id NOT IN (SELECT activity FROM assigned_activity) ORDER BY a.id;"
        )) {
            stmt.setLong(1, year);
            stmt.setLong(2, week);
            stmt.setString(3, String.valueOf(type));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long activityId = rs.getLong("id");
                    result.add(new Activity(activityId, rs.getInt("year"), rs.getShort("week"),
                            rs.getShort("day"), rs.getBoolean("interruptibility"),
                            rs.getInt("estimated_intervention_time"), rs.getString("description"),
                            rs.getLong("typology_id"), rs.getString("typology_name"),
                            rs.getString("typology_description"), rs.getLong("site_id"),
                            rs.getString("factory_name") + "-" + rs.getString("area_name"),
                            rs.getLong("procedure_id"), rs.getString("procedure_name"),
                            rs.getLong("procedure_smp"), rs.getLong("workspace_id"),
                            rs.getString("workspace_description")));
                    getMaterialNeeded(activityId, result.get(result.size() - 1));
                    getSkillNeeded(activityId, result.get(result.size() - 1));
                }
            } catch (SQLException e) {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }

    /**
     * This method displays a list of Activity elements, which represent data from the activity and related tables,
     * based on the week and year of activity chosen.
     *
     * @param week: the week for which the activity is planned.
     * @param day:  the day of the week which the activity is planned.
     * @param year: the year for which the activity is planned.
     * @param type: 'p' to view the scheduled tasks, 'e' to view the ewo tasks.
     * @return a list of Activity or null if an error occur.
     */
    public ArrayList<Activity> getActivity(long year, long week, long day, char type) {
        ArrayList<Activity> result = new ArrayList<>();
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT a.id, a.year, a.week, a.day, a.interruptibility, a.estimated_intervention_time, a.description, mp.id AS procedure_id, mp.name AS procedure_name, mp.smp AS procedure_smp, mt.id AS typology_id, mt.name AS typology_name, mt.description AS typology_description, s.site_id, s.factory_name, s.area_name, s.workspace_id, s.workspace_description \n" +
                        "FROM activity AS a LEFT JOIN maintenance_typologies AS mt ON a.maintenance_typologies = mt.id LEFT JOIN maintenance_procedures AS mp ON a.maintenance_procedures = mp.id LEFT JOIN site_view AS s ON a.site = s.site_id \n" +
                        "WHERE a.year = ? AND a.week = ? AND a.day = ? AND a.type = ? AND a.id NOT IN (SELECT activity FROM assigned_activity) ORDER BY a.id;"
        )) {
            stmt.setLong(1, year);
            stmt.setLong(2, week);
            stmt.setLong(3, day);
            stmt.setString(4, String.valueOf(type));
            try (ResultSet rs = stmt.executeQuery()) {
                long prevActivityId = 0;
                while (rs.next()) {
                    long activityId = rs.getLong("id");
                    result.add(new Activity(activityId, rs.getInt("year"), rs.getShort("week"),
                            rs.getShort("day"), rs.getBoolean("interruptibility"),
                            rs.getInt("estimated_intervention_time"), rs.getString("description"),
                            rs.getLong("typology_id"), rs.getString("typology_name"),
                            rs.getString("typology_description"), rs.getLong("site_id"),
                            rs.getString("factory_name") + "-" + rs.getString("area_name"),
                            rs.getLong("procedure_id"), rs.getString("procedure_name"),
                            rs.getLong("procedure_smp"), rs.getLong("workspace_id"),
                            rs.getString("workspace_description")));
                    getMaterialNeeded(activityId, result.get(result.size() - 1));
                    getSkillNeeded(activityId, result.get(result.size() - 1));
                }
            } catch (SQLException e) {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }

    /**
     * Get material needed for a selected activity, by his id.
     */
    private void getMaterialNeeded(long activityId, Activity result) throws SQLException {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT m.id, m.name, m.description\n" +
                        "FROM activity a LEFT JOIN need n ON a.id = n.activity\n" +
                        "LEFT JOIN materials m ON n.materials = m.id\n" +
                        "WHERE a.id = ?\n" +
                        "ORDER BY m.id;"
        )) {
            stmt.setLong(1, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long materialId = rs.getLong("id");
                    if (materialId != 0) {
                        result.addMaterial(materialId,
                                rs.getString("name"), rs.getString("description"));
                    }
                }
            }
        }
    }

    /**
     * Get skill needed for a selected activity, by his id.
     */
    private void getSkillNeeded(long activityId, Activity result) throws SQLException {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT c.id, c.name, c.description\n" +
                        "FROM activity a LEFT JOIN require_ewo re ON a.id = re.activity\n" +
                        "LEFT JOIN competences c ON re.competences = c.id\n" +
                        "WHERE a.id = ?\n" +
                        "UNION\n" +
                        "SELECT c.id, c.name, c.description\n" +
                        "FROM activity a LEFT JOIN maintenance_procedures mp ON a.maintenance_procedures = mp.id\n" +
                        "LEFT JOIN require r ON mp.id = r.maintenance_procedures\n" +
                        "LEFT JOIN competences c ON r.competences = c.id\n" +
                        "WHERE a.id = ?\n" +
                        "ORDER BY id;"
        )) {
            stmt.setLong(1, activityId);
            stmt.setLong(2, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long competenceId = rs.getLong("id");
                    if (competenceId != 0) {
                        result.addCompetence(competenceId, rs.getString("name"), rs.getString("description"));
                    }
                }
            }
        }
    }

    /**
     * This method create a bew activity and store it into the database.
     *
     * @param year:                 the activity year;
     * @param week:                 the activity week;
     * @param day:                  the activity day;
     * @param type:                 the type of activity (planned p, ewo e);
     * @param interruptibility:     true if the activity is interruptible, otherwise false;
     * @param time:                 the estimated intervention time;
     * @param description:          the activity description;
     * @param typologyId:           the id of the maintenance typologies related to activity;
     * @param procedureId:          the id of the maintenance procedures related to activity;
     * @param siteId:               the id of the maintenance site related to activity;
     * @param materials:            the ids of the material needed for the activity;
     * @param workspaceDescription: the workspace notes description of the site related to the activity.
     * @param competencies:         the skills related to the activity.
     * @return true if is correctly inserted, false if an error occur.
     */
    public boolean addActivity(int year, int week, int day, char type, boolean interruptibility, int time, String description,
                               long typologyId, long procedureId, long siteId, long[] materials, String workspaceDescription,
                               long[] competencies) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "INSERT INTO activity (id, year, week, day, type, interruptibility, estimated_intervention_time, description, maintenance_typologies, maintenance_procedures, site)" +
                        "VALUES (nextval('activity_id'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
        )) {
            stmt.setLong(1, year);
            stmt.setInt(2, week);
            if (day > 0) {
                stmt.setInt(3, day);
            } else {
                stmt.setNull(3, java.sql.Types.NULL);
            }
            stmt.setString(4, String.valueOf(type));
            stmt.setBoolean(5, interruptibility);
            if (time > 0) {
                stmt.setInt(6, time);
            } else {
                stmt.setNull(6, java.sql.Types.NULL);
            }
            stmt.setString(7, description);
            stmt.setLong(8, typologyId);
            if (procedureId > 0) {
                stmt.setLong(9, procedureId);
            } else {
                stmt.setNull(9, java.sql.Types.NULL);
            }
            stmt.setLong(10, siteId);
            stmt.execute();

            long id;
            try (ResultSet rs = getConn().createStatement().executeQuery("SELECT currval('activity_id');")) {
                rs.next();
                id = rs.getLong(1);
            }
            addMaterialToActivity(id, materials);
            if (workspaceDescription != null) {
                editWorkspace(siteId, workspaceDescription);
            }
            addCompetenciesToActivity(id, competencies);

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * This method edit an activity, identified by his id.
     *
     * @param id:                   the activity id to edit;
     * @param year:                 the new activity year;
     * @param week:                 the new activity week;
     * @param day:                  the new activity day;
     * @param type:                 the new type of activity (planned p, ewo e);
     * @param interruptibility:     true if the activity is interruptible, otherwise false;
     * @param time:                 the new estimated intervention time;
     * @param description:          the new activity description;
     * @param typologyId:           the new id of the maintenance typologies related to activity;
     * @param procedureId:          the new id of the maintenance procedures related to activity;
     * @param siteId:               the new id of the maintenance site related to activity;
     * @param materials:            the new ids of the material needed for the activity;
     * @param workspaceDescription: the new workspace notes description of the site related to the activity.
     * @param competencies:         the new skills related to the activity.
     * @return true if is correctly inserted, false if an error occur.
     */
    public boolean editActivity(long id, int year, int week, int day, char type, boolean interruptibility, int time, String description,
                                long typologyId, long procedureId, long siteId, long[] materials, String workspaceDescription,
                                long[] competencies) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "UPDATE activity SET year = ?, week = ?, day = ?, type = ?, interruptibility = ?, estimated_intervention_time = ?, " +
                        "description = ? , maintenance_typologies = ?, maintenance_procedures = ?, site = ? WHERE id = ?;"
        )) {
            stmt.setLong(1, year);
            stmt.setInt(2, week);
            if (day > 0) {
                stmt.setInt(3, day);
            } else {
                stmt.setNull(3, java.sql.Types.NULL);
            }
            stmt.setString(4, String.valueOf(type));
            stmt.setBoolean(5, interruptibility);
            if (time > 0) {
                stmt.setInt(6, time);
            } else {
                stmt.setNull(6, java.sql.Types.NULL);
            }
            stmt.setString(7, description);
            stmt.setLong(8, typologyId);
            if (procedureId > 0) {
                stmt.setLong(9, procedureId);
            } else {
                stmt.setNull(9, java.sql.Types.NULL);
            }
            stmt.setLong(10, siteId);
            stmt.setLong(11, id);
            stmt.execute();

            addMaterialToActivity(id, materials);
            editWorkspace(siteId, workspaceDescription);
            addCompetenciesToActivity(id, competencies);

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Delete an activity by his id.
     *
     * @param id: the id of the activity to delete.
     * @return true if is correctly deleted, false if an error occur.
     */
    public boolean deleteActivity(long id) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "DELETE FROM need WHERE activity = ?; DELETE FROM activity WHERE id = ? ;"
        )) {
            stmt.setLong(1, id);
            stmt.setLong(2, id);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * On edit and add, edit the material related to an activity.
     *
     * @param id:        the id of the activity;
     * @param materials: the id of the materials to associate to the activity.
     * @throws SQLException if an error occur.
     */
    private void addMaterialToActivity(long id, long[] materials) throws SQLException {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "DELETE FROM need WHERE activity = ?;"
        )) {
            stmt.setLong(1, id);
            stmt.execute();
            for (long material : materials) {
                try (PreparedStatement stm = getConn().prepareStatement(
                        "INSERT INTO need (activity, materials) VALUES (?, ?);"
                )) {
                    stm.setLong(1, id);
                    stm.setLong(2, material);
                    stm.execute();
                }
            }
        }
    }

    /**
     * On edit and add, edit the competencies related to an activity.
     *
     * @param id:          the id of the activity;
     * @param competences: the id of the competences to associate to the activity.
     * @throws SQLException if an error occur.
     */
    private void addCompetenciesToActivity(long id, long[] competences) throws SQLException {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "DELETE FROM require_ewo WHERE activity = ?;"
        )) {
            stmt.setLong(1, id);
            stmt.execute();
            for (long competence : competences) {
                try (PreparedStatement stm = getConn().prepareStatement(
                        "INSERT INTO require_ewo (activity, competences) VALUES (?, ?);"
                )) {
                    stm.setLong(1, id);
                    stm.setLong(2, competence);
                    stm.execute();
                }
            }
        }
    }

    /**
     * On edit and add, if the activity site has been associated a workspace notes, edit that. Else create new and
     * associate at the activity site.
     *
     * @param siteId:                  the id site linked to the workspace;
     * @param newWorkspaceDescription: the new workspace description.
     * @throws SQLException if an error occur.
     */
    private void editWorkspace(long siteId, String newWorkspaceDescription) throws SQLException {
        int res;
        try (PreparedStatement stmt = getConn().prepareStatement(
                "UPDATE workspace_notes SET description = ? WHERE " +
                        "id IN (SELECT a.workspace_notes FROM area AS a JOIN site s on a.id = s.area WHERE s.id = ?) AND " +
                        "(SELECT a.workspace_notes FROM area AS a JOIN site s on a.id = s.area WHERE s.id = ?) IS NOT NULL;"
        )) {
            stmt.setString(1, newWorkspaceDescription);
            stmt.setLong(2, siteId);
            stmt.setLong(3, siteId);
            res = stmt.executeUpdate();
            if (res == 0) {
                try (PreparedStatement stm = getConn().prepareStatement(
                        "INSERT INTO workspace_notes (id, description) VALUES (nextval('workspace_notes_id'), ?);" +
                                "UPDATE area SET workspace_notes = currval('workspace_notes_id') WHERE id IN (SELECT area FROM site WHERE id = ?);"
                )) {
                    stm.setString(1, newWorkspaceDescription);
                    stm.setLong(2, siteId);
                    stm.execute();
                }
            }
        }
    }

    /**
     * Method for assign an activity, passing the needed info.
     *
     * @param activityId:   the activity to assign.
     * @param maintainerId: the maintainer to assign at activity.
     * @param day:          the day of the week assigned for the activity.
     * @param slotIds:      the slot ids for make the activity.
     * @param minutes:      the minutes scheduled for each slot ids.
     * @return true if the activity is assigned correctly, else false.
     */
    public boolean assignActivity(long activityId, long maintainerId, int day, long[] slotIds, int[] minutes) {
        int actualTime = IntStream.of(minutes).sum();
        int realTime = getActivityTime(activityId);

        if (actualTime != realTime || slotIds.length != minutes.length) {
            return false;
        }

        try (PreparedStatement stmt = getConn().prepareStatement(
                "UPDATE activity SET day = ? WHERE id = ?; " +
                        "INSERT INTO assigned_activity (activity, maintainer) VALUES (?, ?);"
        )) {
            stmt.setInt(1, day);
            stmt.setLong(2, activityId);
            stmt.setLong(3, activityId);
            stmt.setLong(4, maintainerId);
            stmt.execute();
            for (int i = 0; i < slotIds.length; i++) {
                assignSlot(slotIds[i], activityId, minutes[i]);
            }
        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    /**
     * @param activityId: the activity's id of which he wants to see the estimated time.
     * @return the estimated time.
     */
    private int getActivityTime(long activityId) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT estimated_intervention_time FROM activity WHERE id = ?;"
        )) {
            stmt.setLong(1, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return 0;
                }
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Assign slot
     */
    private void assignSlot(long slotId, long activityId, int minutes) throws SQLException {
        try (PreparedStatement stm = getConn().prepareStatement(
                "INSERT INTO assigned_slot (daily_time_slot, assigned_activity, minutes) VALUES (?, ?, ?);"
        )) {
            stm.setLong(1, slotId);
            stm.setLong(2, activityId);
            stm.setInt(3, minutes);
            stm.execute();
        }
    }
}

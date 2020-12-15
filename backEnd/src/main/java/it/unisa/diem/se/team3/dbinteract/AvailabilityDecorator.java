package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.AvailabilityDaily;
import it.unisa.diem.se.team3.models.AvailabilityWeekly;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Decorator class to query the assigned_activity, daily_slot_time and related table.
 */
public class AvailabilityDecorator extends DbDecorator {

    /**
     * Base constructor, used to get an instance of the DbInterface interface used by the decorator classes.
     *
     * @param db : a concrete implementation of DbInterface.
     */
    public AvailabilityDecorator(DbInterface db) {
        super(db);
    }

    /**
     * @param activityId: the referring activity to see daily availability.
     * @return a list of AvailabilityDaily, or null if an error occur.
     */
    public ArrayList<AvailabilityDaily> getAvailabilityDaily(long activityId, int day) {
        ArrayList<AvailabilityDaily> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT A.maintainer_id, A.maintainer_name, CC.skill_had, CC.required_skill, A.id_slot, A.hour_start, A.hour_end, A.remaining_time\n" +
                        "FROM\n" +
                        "(\n" +
                        "\t-- View, for each maintainer and for a selected day, every timeslot and the related remaining time \n" +
                        "\tSELECT M.internal_id AS maintainer_id, M.name AS maintainer_name, DTS.id AS id_slot, DTS.hour_start, \n" +
                        "\t\tDTS.hour_start + DTS.duration * INTERVAL '1 minute' AS hour_end, (DTS.duration - SUM(ASL.minutes)) AS remaining_time\n" +
                        "\tFROM assigned_slot AS ASL\n" +
                        "\t\tRIGHT JOIN daily_time_slot AS dts ON ASL.daily_time_slot = DTS.id\n" +
                        "\t\tRIGHT JOIN assigned_activity AS aa ON ASL.assigned_activity = AA.activity\n" +
                        "\t\tRIGHT JOIN activity AS a ON AA.activity = A.id\n" +
                        "\t\tRIGHT JOIN maintainer AS m ON AA.maintainer = M.internal_id\n" +
                        "\t\tWHERE A.year = (SELECT year FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\t\tAND A.week = (SELECT week FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\t\tAND A.day = ? -- day\n" +
                        "\tGROUP BY M.internal_id, DTS.id\n" +
                        "\tUNION \n" +
                        "\tSELECT M.internal_id, M.name, DTS.id, DTS.hour_start, DTS.hour_start + DTS.duration * INTERVAL '1 minute', DTS.duration\n" +
                        "\tFROM maintainer AS m, daily_time_slot AS dts\n" +
                        "\tWHERE NOT EXISTS (\n" +
                        "\t\tSELECT (DTS1.duration - SUM(A.estimated_intervention_time))\n" +
                        "\t\tFROM assigned_slot AS ASL\n" +
                        "\t\t\tRIGHT JOIN daily_time_slot AS dts1 ON ASL.daily_time_slot = DTS1.id\n" +
                        "\t\t\tRIGHT JOIN assigned_activity AS aa ON ASL.assigned_activity = AA.activity\n" +
                        "\t\t\tRIGHT JOIN activity AS a ON AA.activity = A.id\n" +
                        "\t\t\tRIGHT JOIN maintainer AS m1 ON AA.maintainer = M1.internal_id\n" +
                        "\t\tWHERE M1.internal_id = M.internal_id\n" +
                        "\t\t\tAND DTS1.id = DTS.id\t\n" +
                        "\t\t\tAND A.year = (SELECT year FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\t\tAND A.week = (SELECT week FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\t\tAND A.day = ? -- day\n" +
                        "\t\tGROUP BY M1.internal_id, DTS1.id\n" +
                        "\t)\n" +
                        ") AS a LEFT JOIN \n" +
                        "(\n" +
                        "\t-- View competencies compliance by activity, selected by id.\n" +
                        "\tSELECT M.internal_id as maintainer_id, COUNT(C1.id) AS skill_had, (\n" +
                        "\t\tSELECT COUNT(skills.id)\n" +
                        "\t\tFROM\n" +
                        "\t\t(\n" +
                        "\t\t\tSELECT C2.id\n" +
                        "\t\t\tFROM activity AS a1 \n" +
                        "\t\t\tLEFT JOIN maintenance_procedures AS mp1 ON A1.maintenance_procedures = MP1.id\n" +
                        "\t\t\tLEFT JOIN require AS r1 ON MP1.id = R1.maintenance_procedures\n" +
                        "\t\t\tLEFT JOIN competences AS c2 ON R1.competences = C2.id\n" +
                        "\t\t\tWHERE a1.id = ?  -- Id activity\n" +
                        "\t\t\tUNION\n" +
                        "\t\t\tSELECT C2.id\n" +
                        "\t\t\tFROM activity AS a1 \n" +
                        "\t\t\tLEFT JOIN require_ewo AS re1 ON A1.id = RE1.activity\n" +
                        "\t\t\tLEFT JOIN competences AS c2 ON RE1.competences = C2.id\n" +
                        "\t\t\tWHERE a1.id = ?  -- Id activity\n" +
                        "\t\t) AS skills\n" +
                        "\t) AS required_skill\n" +
                        "\tFROM maintainer AS m \n" +
                        "\t\tLEFT JOIN is_a AS ia ON M.internal_id = IA.maintainer\n" +
                        "\t\tLEFT JOIN maintainer_role AS mr ON IA.maintainer_role = MR.id\n" +
                        "\t\tLEFT JOIN has_skill AS hs ON MR.id = HS.maintainer_role\n" +
                        "\t\tLEFT JOIN competences AS c1 ON HS.competences = C1.id\n" +
                        "\tWHERE C1.id IN (\n" +
                        "\t\tSELECT C2.id \n" +
                        "\t\tFROM activity AS a1 \n" +
                        "\t\tLEFT JOIN maintenance_procedures AS mp1 ON A1.maintenance_procedures = MP1.id\n" +
                        "\t\tLEFT JOIN require AS r1 ON MP1.id = R1.maintenance_procedures\n" +
                        "\t\tLEFT JOIN competences AS c2 ON R1.competences = C2.id\n" +
                        "\t\tWHERE a1.id = ?  -- id activity\n" +
                        "\t\tUNION\n" +
                        "\t\tSELECT C2.id\n" +
                        "\t\tFROM activity AS a1 \n" +
                        "\t\tLEFT JOIN require_ewo AS re1 ON A1.id = RE1.activity\n" +
                        "\t\tLEFT JOIN competences AS c2 ON RE1.competences = C2.id\n" +
                        "\t\tWHERE a1.id = ?  -- Id activity\n" +
                        "\t) OR C1.id IS null\n" +
                        "\tGROUP BY M.internal_id\n" +
                        ") AS cc ON a.maintainer_id = cc.maintainer_id\n" +
                        "ORDER BY a.maintainer_id, id_slot;"
        )) {
            stmt.setLong(1, activityId);
            stmt.setLong(2, activityId);
            stmt.setLong(3, day);
            stmt.setLong(4, activityId);
            stmt.setLong(5, activityId);
            stmt.setLong(6, day);
            stmt.setLong(7, activityId);
            stmt.setLong(8, activityId);
            stmt.setLong(9, activityId);
            stmt.setLong(10, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                long prevMaintainerId = 0;
                while (rs.next()) {
                    long maintainerId = rs.getLong("maintainer_id");
                    if (maintainerId != prevMaintainerId) {
                        result.add(new AvailabilityDaily(maintainerId, rs.getString("maintainer_name"),
                                rs.getInt("skill_had") + "/" + rs.getInt("required_skill")));
                    }
                    long idSlot = rs.getLong("id_slot");
                    if (idSlot != 0) {
                        result.get(result.size() - 1).addSlot(idSlot, rs.getTimestamp("hour_start").toLocalDateTime().format(formatter)
                                + "-" + rs.getTimestamp("hour_end").toLocalDateTime().format(formatter), rs.getInt("remaining_time"));
                    }
                    prevMaintainerId = maintainerId;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param activityId:   the referring activity to see daily availability.
     * @param maintainerId: the maintainer which see the daily availability.
     * @param day: the day which see the availability.
     * @return an AvailabilityDaily object, or null if an error occur.
     */
    public AvailabilityDaily getAvailabilityDaily(long activityId, long maintainerId, int day) {
        AvailabilityDaily result;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT A.maintainer_id, A.maintainer_name, CC.skill_had, CC.required_skill, A.id_slot, A.hour_start, A.hour_end, A.remaining_time\n" +
                        "FROM\n" +
                        "(\n" +
                        "\t-- View, for each maintainer and for a selected day, every timeslot and the related remaining time \n" +
                        "\tSELECT M.internal_id AS maintainer_id, M.name AS maintainer_name, DTS.id AS id_slot, DTS.hour_start, \n" +
                        "\t\tDTS.hour_start + DTS.duration * INTERVAL '1 minute' AS hour_end, (DTS.duration - SUM(ASL.minutes)) AS remaining_time\n" +
                        "\tFROM assigned_slot AS ASL\n" +
                        "\t\tRIGHT JOIN daily_time_slot AS dts ON ASL.daily_time_slot = DTS.id\n" +
                        "\t\tRIGHT JOIN assigned_activity AS aa ON ASL.assigned_activity = AA.activity\n" +
                        "\t\tRIGHT JOIN activity AS a ON AA.activity = A.id\n" +
                        "\t\tRIGHT JOIN maintainer AS m ON AA.maintainer = M.internal_id\n" +
                        "\t\tWHERE A.year = (SELECT year FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\t\tAND A.week = (SELECT week FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\t\tAND A.day = ? -- day\n" +
                        "\tGROUP BY M.internal_id, DTS.id\n" +
                        "\tUNION \n" +
                        "\tSELECT M.internal_id, M.name, DTS.id, DTS.hour_start, DTS.hour_start + DTS.duration * INTERVAL '1 minute', DTS.duration\n" +
                        "\tFROM maintainer AS m, daily_time_slot AS dts\n" +
                        "\tWHERE NOT EXISTS (\n" +
                        "\t\tSELECT (DTS1.duration - SUM(A.estimated_intervention_time))\n" +
                        "\t\tFROM assigned_slot AS ASL\n" +
                        "\t\t\tRIGHT JOIN daily_time_slot AS dts1 ON ASL.daily_time_slot = DTS1.id\n" +
                        "\t\t\tRIGHT JOIN assigned_activity AS aa ON ASL.assigned_activity = AA.activity\n" +
                        "\t\t\tRIGHT JOIN activity AS a ON AA.activity = A.id\n" +
                        "\t\t\tRIGHT JOIN maintainer AS m1 ON AA.maintainer = M1.internal_id\n" +
                        "\t\tWHERE M1.internal_id = M.internal_id\n" +
                        "\t\t\tAND DTS1.id = DTS.id\t\n" +
                        "\t\t\tAND A.year = (SELECT year FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\t\tAND A.week = (SELECT week FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\t\tAND A.day = ? -- day\n" +
                        "\t\tGROUP BY M1.internal_id, DTS1.id\n" +
                        "\t)\n" +
                        ") AS a LEFT JOIN \n" +
                        "(\n" +
                        "\t-- View competencies compliance by activity, selected by id.\n" +
                        "\tSELECT M.internal_id as maintainer_id, COUNT(C1.id) AS skill_had, (\n" +
                        "\t\tSELECT COUNT(skills.id)\n" +
                        "\t\tFROM\n" +
                        "\t\t(\n" +
                        "\t\t\tSELECT C2.id\n" +
                        "\t\t\tFROM activity AS a1 \n" +
                        "\t\t\tLEFT JOIN maintenance_procedures AS mp1 ON A1.maintenance_procedures = MP1.id\n" +
                        "\t\t\tLEFT JOIN require AS r1 ON MP1.id = R1.maintenance_procedures\n" +
                        "\t\t\tLEFT JOIN competences AS c2 ON R1.competences = C2.id\n" +
                        "\t\t\tWHERE a1.id = ?  -- Id activity\n" +
                        "\t\t\tUNION\n" +
                        "\t\t\tSELECT C2.id\n" +
                        "\t\t\tFROM activity AS a1 \n" +
                        "\t\t\tLEFT JOIN require_ewo AS re1 ON A1.id = RE1.activity\n" +
                        "\t\t\tLEFT JOIN competences AS c2 ON RE1.competences = C2.id\n" +
                        "\t\t\tWHERE a1.id = ?  -- Id activity\n" +
                        "\t\t) AS skills\n" +
                        "\t) AS required_skill\n" +
                        "\tFROM maintainer AS m \n" +
                        "\t\tLEFT JOIN is_a AS ia ON M.internal_id = IA.maintainer\n" +
                        "\t\tLEFT JOIN maintainer_role AS mr ON IA.maintainer_role = MR.id\n" +
                        "\t\tLEFT JOIN has_skill AS hs ON MR.id = HS.maintainer_role\n" +
                        "\t\tLEFT JOIN competences AS c1 ON HS.competences = C1.id\n" +
                        "\tWHERE C1.id IN (\n" +
                        "\t\tSELECT C2.id \n" +
                        "\t\tFROM activity AS a1 \n" +
                        "\t\tLEFT JOIN maintenance_procedures AS mp1 ON A1.maintenance_procedures = MP1.id\n" +
                        "\t\tLEFT JOIN require AS r1 ON MP1.id = R1.maintenance_procedures\n" +
                        "\t\tLEFT JOIN competences AS c2 ON R1.competences = C2.id\n" +
                        "\t\tWHERE a1.id = ?  -- id activity\n" +
                        "\t\tUNION\n" +
                        "\t\tSELECT C2.id\n" +
                        "\t\tFROM activity AS a1 \n" +
                        "\t\tLEFT JOIN require_ewo AS re1 ON A1.id = RE1.activity\n" +
                        "\t\tLEFT JOIN competences AS c2 ON RE1.competences = C2.id\n" +
                        "\t\tWHERE a1.id = ?  -- Id activity\n" +
                        "\t) OR C1.id IS null\n" +
                        "\tGROUP BY M.internal_id\n" +
                        ") AS cc ON a.maintainer_id = cc.maintainer_id\n" +
                        "WHERE a.maintainer_id = ?\n" +
                        "ORDER BY a.maintainer_id, id_slot;"
        )) {
            stmt.setLong(1, activityId);
            stmt.setLong(2, activityId);
            stmt.setLong(3, day);
            stmt.setLong(4, activityId);
            stmt.setLong(5, activityId);
            stmt.setLong(6, day);
            stmt.setLong(7, activityId);
            stmt.setLong(8, activityId);
            stmt.setLong(9, activityId);
            stmt.setLong(10, activityId);
            stmt.setLong(11, maintainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                result = new AvailabilityDaily(rs.getLong("maintainer_id"), rs.getString("maintainer_name"),
                        rs.getInt("skill_had") + "/" + rs.getInt("required_skill"));
                do {
                    long idSlot = rs.getLong("id_slot");
                    if (idSlot != 0) {
                        result.addSlot(idSlot, rs.getTimestamp("hour_start").toLocalDateTime().format(formatter)
                                + "-" + rs.getTimestamp("hour_end").toLocalDateTime().format(formatter), rs.getInt("remaining_time"));
                    }
                } while (rs.next());
            }
            return result;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * @param activityId: the referring activity to see weekly availability.
     * @return a list of AvailabilityWeekly, or null if an error occur.
     */
    public List<AvailabilityWeekly> getAvailabilityWeekly(long activityId) {
        ArrayList<AvailabilityWeekly> result = new ArrayList<>();
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT A.maintainer_id, A.maintainer_name, CC.skill_had, CC.required_skill, A.day, A.remaining_percentage\n" +
                        "FROM (\n" +
                        "\t-- View, for each maintainer and for a selected week, the daily remaining time in percentage\n" +
                        "\tSELECT M.internal_id AS maintainer_id, M.name AS maintainer_name, week.day, 100 AS remaining_percentage\n" +
                        "\tFROM maintainer AS m, (SELECT 1 AS day UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7) AS week\n" +
                        "\tWHERE NOT EXISTS(\n" +
                        "\t\tSELECT M1.internal_id AS maintainer_id, m1.name AS maintainer_name, A1.day, \n" +
                        "\t\t\t((SELECT SUM(duration) FROM daily_time_slot) - SUM(ASL1.minutes)) * 100 / (SELECT SUM(duration) FROM daily_time_slot) AS remaining_percentage\n" +
                        "\t\tFROM assigned_slot AS ASL1\n" +
                        "\t\t\tRIGHT JOIN daily_time_slot AS dts1 ON ASL1.daily_time_slot = DTS1.id\n" +
                        "\t\t\tRIGHT JOIN assigned_activity AS aa1 ON ASL1.assigned_activity = AA1.activity\n" +
                        "\t\t\tRIGHT JOIN activity AS a1 ON AA1.activity = A1.id\n" +
                        "\t\t\tRIGHT JOIN maintainer AS m1 ON AA1.maintainer = M1.internal_id\n" +
                        "\t\tWHERE M1.internal_id = M.internal_id\n" +
                        "\t\t\tAND week.day = A1.day\n" +
                        "\t\t\tAND A1.year = (SELECT year FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\t\tAND A1.week = (SELECT week FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\tGROUP BY M1.internal_id, A1.day\n" +
                        "\t)\n" +
                        "\tUNION\n" +
                        "\tSELECT M.internal_id AS maintainer_id, m.name AS maintainer_name, A.day, \n" +
                        "\t\t((SELECT SUM(duration) FROM daily_time_slot) - SUM(ASL.minutes)) * 100 / (SELECT SUM(duration) FROM daily_time_slot) AS remaining_percentage\n" +
                        "\tFROM assigned_slot AS ASL\n" +
                        "\t\tRIGHT JOIN daily_time_slot AS dts ON ASL.daily_time_slot = DTS.id\n" +
                        "\t\tRIGHT JOIN assigned_activity AS aa ON ASL.assigned_activity = AA.activity\n" +
                        "\t\tRIGHT JOIN activity AS a ON AA.activity = A.id\n" +
                        "\t\tRIGHT JOIN maintainer AS m ON AA.maintainer = M.internal_id\n" +
                        "\tWHERE A.year = (SELECT year FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\tAND A.week = (SELECT week FROM activity WHERE id = ?) -- id activity\n" +
                        "\tGROUP BY M.internal_id, A.day\n" +
                        ") AS a LEFT JOIN \n" +
                        "(\n" +
                        "\t-- View competencies compliance by activity, selected by id.\n" +
                        "\tSELECT M.internal_id as maintainer_id, COUNT(C1.id) AS skill_had, (\n" +
                        "\t\tSELECT COUNT(C2.id)\n" +
                        "\t\tFROM activity AS a1 \n" +
                        "\t\tLEFT JOIN maintenance_procedures AS mp1 ON A1.maintenance_procedures = MP1.id\n" +
                        "\t\tLEFT JOIN require AS r1 ON MP1.id = R1.maintenance_procedures\n" +
                        "\t\tLEFT JOIN competences AS c2 ON R1.competences = C2.id\n" +
                        "\t\tWHERE a1.id = ?  -- Id activity\n" +
                        "\t\tGROUP BY A1.id\n" +
                        "\t) AS required_skill\n" +
                        "\tFROM maintainer AS m \n" +
                        "\t\tLEFT JOIN is_a AS ia ON M.internal_id = IA.maintainer\n" +
                        "\t\tLEFT JOIN maintainer_role AS mr ON IA.maintainer_role = MR.id\n" +
                        "\t\tLEFT JOIN has_skill AS hs ON MR.id = HS.maintainer_role\n" +
                        "\t\tLEFT JOIN competences AS c1 ON HS.competences = C1.id\n" +
                        "\tWHERE C1.id IN (\n" +
                        "\t\tSELECT C2.id \n" +
                        "\t\tFROM activity AS a1 \n" +
                        "\t\tLEFT JOIN maintenance_procedures AS mp1 ON A1.maintenance_procedures = MP1.id\n" +
                        "\t\tLEFT JOIN require AS r1 ON MP1.id = R1.maintenance_procedures\n" +
                        "\t\tLEFT JOIN competences AS c2 ON R1.competences = C2.id\n" +
                        "\t\tWHERE a1.id = ?  -- id activity\n" +
                        "\t) OR C1.id IS null\n" +
                        "\tGROUP BY M.internal_id\n" +
                        ") AS cc ON a.maintainer_id = cc.maintainer_id\n" +
                        "ORDER BY a.maintainer_id, a.day;"
        )) {
            stmt.setLong(1, activityId);
            stmt.setLong(2, activityId);
            stmt.setLong(3, activityId);
            stmt.setLong(4, activityId);
            stmt.setLong(5, activityId);
            stmt.setLong(6, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                long prevMaintainerId = 0;
                while (rs.next()) {
                    long maintainerId = rs.getLong("maintainer_id");
                    if (maintainerId != prevMaintainerId) {
                        result.add(new AvailabilityWeekly(maintainerId, rs.getString("maintainer_name"),
                                rs.getInt("skill_had") + "/" + rs.getInt("required_skill")));
                    }
                    long day = rs.getLong("day");
                    if (day != 0) {
                        result.get(result.size() - 1).addDay(rs.getInt("day"), rs.getInt("remaining_percentage"));
                    }
                    prevMaintainerId = maintainerId;
                }
            }
            return result;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * @param activityId:   the referring activity to see weekly availability.
     * @param maintainerId: the maintainer which see the weekly availability.
     * @return an AvailabilityWeekly object, or null if an error occur.
     */
    public AvailabilityWeekly getAvailabilityWeekly(long activityId, long maintainerId) {
        AvailabilityWeekly result;
        try (PreparedStatement stmt = getConn().prepareStatement(
                "SELECT A.maintainer_id, A.maintainer_name, CC.skill_had, CC.required_skill, A.day, A.remaining_percentage\n" +
                        "FROM (\n" +
                        "\t-- View, for each maintainer and for a selected week, the daily remaining time in percentage\n" +
                        "\tSELECT M.internal_id AS maintainer_id, M.name AS maintainer_name, week.day, 100 AS remaining_percentage\n" +
                        "\tFROM maintainer AS m, (SELECT 1 AS day UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7) AS week\n" +
                        "\tWHERE NOT EXISTS(\n" +
                        "\t\tSELECT M1.internal_id AS maintainer_id, m1.name AS maintainer_name, A1.day, \n" +
                        "\t\t\t((SELECT SUM(duration) FROM daily_time_slot) - SUM(ASL1.minutes)) * 100 / (SELECT SUM(duration) FROM daily_time_slot) AS remaining_percentage\n" +
                        "\t\tFROM assigned_slot AS ASL1\n" +
                        "\t\t\tRIGHT JOIN daily_time_slot AS dts1 ON ASL1.daily_time_slot = DTS1.id\n" +
                        "\t\t\tRIGHT JOIN assigned_activity AS aa1 ON ASL1.assigned_activity = AA1.activity\n" +
                        "\t\t\tRIGHT JOIN activity AS a1 ON AA1.activity = A1.id\n" +
                        "\t\t\tRIGHT JOIN maintainer AS m1 ON AA1.maintainer = M1.internal_id\n" +
                        "\t\tWHERE M1.internal_id = M.internal_id\n" +
                        "\t\t\tAND week.day = A1.day\n" +
                        "\t\t\tAND A1.year = (SELECT year FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\t\tAND A1.week = (SELECT week FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\tGROUP BY M1.internal_id, A1.day\n" +
                        "\t)\n" +
                        "\tUNION\n" +
                        "\tSELECT M.internal_id AS maintainer_id, m.name AS maintainer_name, A.day, \n" +
                        "\t\t((SELECT SUM(duration) FROM daily_time_slot) - SUM(ASL.minutes)) * 100 / (SELECT SUM(duration) FROM daily_time_slot) AS remaining_percentage\n" +
                        "\tFROM assigned_slot AS ASL\n" +
                        "\t\tRIGHT JOIN daily_time_slot AS dts ON ASL.daily_time_slot = DTS.id\n" +
                        "\t\tRIGHT JOIN assigned_activity AS aa ON ASL.assigned_activity = AA.activity\n" +
                        "\t\tRIGHT JOIN activity AS a ON AA.activity = A.id\n" +
                        "\t\tRIGHT JOIN maintainer AS m ON AA.maintainer = M.internal_id\n" +
                        "\tWHERE A.year = (SELECT year FROM activity WHERE id = ?) -- id activity\n" +
                        "\t\tAND A.week = (SELECT week FROM activity WHERE id = ?) -- id activity\n" +
                        "\tGROUP BY M.internal_id, A.day\n" +
                        ") AS a LEFT JOIN \n" +
                        "(\n" +
                        "\t-- View competencies compliance by activity, selected by id.\n" +
                        "\tSELECT M.internal_id as maintainer_id, COUNT(C1.id) AS skill_had, (\n" +
                        "\t\tSELECT COUNT(C2.id)\n" +
                        "\t\tFROM activity AS a1 \n" +
                        "\t\tLEFT JOIN maintenance_procedures AS mp1 ON A1.maintenance_procedures = MP1.id\n" +
                        "\t\tLEFT JOIN require AS r1 ON MP1.id = R1.maintenance_procedures\n" +
                        "\t\tLEFT JOIN competences AS c2 ON R1.competences = C2.id\n" +
                        "\t\tWHERE a1.id = ?  -- Id activity\n" +
                        "\t\tGROUP BY A1.id\n" +
                        "\t) AS required_skill\n" +
                        "\tFROM maintainer AS m \n" +
                        "\t\tLEFT JOIN is_a AS ia ON M.internal_id = IA.maintainer\n" +
                        "\t\tLEFT JOIN maintainer_role AS mr ON IA.maintainer_role = MR.id\n" +
                        "\t\tLEFT JOIN has_skill AS hs ON MR.id = HS.maintainer_role\n" +
                        "\t\tLEFT JOIN competences AS c1 ON HS.competences = C1.id\n" +
                        "\tWHERE C1.id IN (\n" +
                        "\t\tSELECT C2.id \n" +
                        "\t\tFROM activity AS a1 \n" +
                        "\t\tLEFT JOIN maintenance_procedures AS mp1 ON A1.maintenance_procedures = MP1.id\n" +
                        "\t\tLEFT JOIN require AS r1 ON MP1.id = R1.maintenance_procedures\n" +
                        "\t\tLEFT JOIN competences AS c2 ON R1.competences = C2.id\n" +
                        "\t\tWHERE a1.id = ?  -- id activity\n" +
                        "\t) OR C1.id IS null\n" +
                        "\tGROUP BY M.internal_id\n" +
                        ") AS cc ON a.maintainer_id = cc.maintainer_id\n" +
                        "WHERE a.maintainer_id = ?\n" +
                        "ORDER BY a.maintainer_id, a.day;"
        )) {
            stmt.setLong(1, activityId);
            stmt.setLong(2, activityId);
            stmt.setLong(3, activityId);
            stmt.setLong(4, activityId);
            stmt.setLong(5, activityId);
            stmt.setLong(6, activityId);
            stmt.setLong(7, maintainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                result = new AvailabilityWeekly(rs.getLong("maintainer_id"), rs.getString("maintainer_name"),
                        rs.getInt("skill_had") + "/" + rs.getInt("required_skill"));
                do {
                    long day = rs.getLong("day");
                    if (day != 0) {
                        result.addDay(rs.getInt("day"), rs.getInt("remaining_percentage"));
                    }
                } while (rs.next());
            }
            return result;
        } catch (SQLException e) {
            return null;
        }
    }
}

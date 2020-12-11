package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.models.CompetenciesRole;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Decorator class to query the competencies, maintainer_role and has_skill table.
 */
public class CompetenciesRoleDecorator extends DbDecorator {

    /**
     * Class used to decorate a Db Interface object and use the queries provided on the competencies, maintainer_role
     * and has_skill table.
     *
     * @param db: an object implementing DbInterface.
     */
    public CompetenciesRoleDecorator(DbInterface db) {
        super(db);
    }

    /**
     * This method displays a list of Competencies elements, related at each role.
     *
     * @return a list of CompetenciesRole Model or null if an error occur.
     */
    public ArrayList<CompetenciesRole> getCompetenciesRole() {
        ArrayList<CompetenciesRole> result = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT mr.id AS role_id, mr.name AS role_name, mr.description AS role_description, c.id AS competence_id, c.name AS competence_name, c.description AS competence_description " +
                             "FROM (has_skill AS h JOIN competences AS c ON h.competences = c.id) RIGHT JOIN maintainer_role AS mr ON h.maintainer_role = mr.id " +
                             "ORDER BY mr.id, c.id;"
             )) {
            long prevRoleId = 0;
            while (rs.next()) {
                long roleId = rs.getLong("role_id");
                if (roleId != prevRoleId) {
                    result.add(new CompetenciesRole(roleId, rs.getString("role_name"), rs.getString("role_description")));
                }
                long competenceId = rs.getLong("competence_id");
                if (competenceId != 0) {
                    result.get(result.size() - 1).addCompetencies(competenceId, rs.getString("competence_name"),
                            rs.getString("competence_description"));
                }
                prevRoleId = roleId;
            }
        } catch (SQLException e) {
            return null;
        }
        return result;
    }

    /**
     * This method displays a Competencies elements, related at the role searched by his id.
     *
     * @param idRole: the role to see the competencies.
     * @return a CompetenciesRole Model or null if an error occur.
     */
    public CompetenciesRole getCompetenciesRole(long idRole) {
        try (PreparedStatement stmt = getConn().prepareStatement("SELECT mr.id AS role_id, mr.name AS role_name, mr.description AS role_description, c.id AS competence_id, c.name AS competence_name, c.description AS competence_description " +
                "FROM (has_skill AS h JOIN competences AS c ON h.competences = c.id) RIGHT JOIN maintainer_role AS mr ON h.maintainer_role = mr.id " +
                "WHERE mr.id = ? " +
                "ORDER BY mr.id, c.id;")) {
            stmt.setLong(1, idRole);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                CompetenciesRole result = new CompetenciesRole(rs.getLong("role_id"),
                        rs.getString("role_name"), rs.getString("role_description"));
                do {
                    long competenceId = rs.getLong("competence_id");
                    if (competenceId != 0) {
                        result.addCompetencies(competenceId, rs.getString("competence_name"),
                                rs.getString("competence_description"));
                    }
                } while (rs.next());
                return result;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * This method edit the competencies related to a role with id idRole.
     *
     * @param idRole:         the role to whom edit related competencies;
     * @param idCompetencies: the new competence associated at the role.
     * @return true if no error occur, else false.
     */
    public boolean editCompetence(long idRole, @NotNull long[] idCompetencies) {
        try (PreparedStatement stmt = getConn().prepareStatement(
                "DELETE FROM has_skill WHERE maintainer_role = ?;"
        )) {
            stmt.setLong(1, idRole);
            stmt.execute();
            for (long idCompetency : idCompetencies) {
                try (PreparedStatement stm = getConn().prepareStatement(
                        "INSERT INTO has_skill (maintainer_role, competences) VALUES (?, ?);"
                )) {
                    stm.setLong(1, idRole);
                    stm.setLong(2, idCompetency);
                    stm.execute();
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}


package it.unisa.diem.se.team3.models;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class used to create the rows that represent the queries performed on a database by the CompetenciesRoleDecorator
 * class.
 */
public class CompetenciesRole implements Model {
    private final MaintainerRole role;
    private final ArrayList<Competencies> competencies;

    /**
     * It instantiates an object of the class MaintainerRole, which represents a tuple of the corresponding query, that
     * associates the competences attributed to a role. Initially a role has no associated skills.
     *
     * @param roleId:          the role id.
     * @param roleName:        the role name.
     * @param roleDescription: the role description.
     */
    public CompetenciesRole(long roleId, String roleName, String roleDescription) {
        role = new MaintainerRole(roleId, roleName, roleDescription);
        competencies = new ArrayList<>();
    }

    /**
     * Allows you to associate a skill with the role.
     *
     * @param id:          the competence id.
     * @param name:        the competence name.
     * @param description: the competence description.
     */
    public void addCompetencies(long id, String name, String description) {
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
        CompetenciesRole that = (CompetenciesRole) o;
        return Objects.equals(role, that.role) &&
                Objects.equals(competencies, that.competencies);
    }

    /**
     * Method that derives, given the current object, its representation in JSON string.
     *
     * @return a String representing the object in JSON.
     */
    @Override
    public String toJSON() {
        return "{" + "\"role\":" + role.toJSON() + ",\"competences\":" + JsonUtil.toJson(competencies) + "}";
    }
}


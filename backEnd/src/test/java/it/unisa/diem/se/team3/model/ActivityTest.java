package it.unisa.diem.se.team3.model;

import it.unisa.diem.se.team3.models.Activity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ActivityTest {

    @Test
    void testEquals() {
        Activity a = new Activity(1, 2020, 21, 2, true, 30,
                "Activity 1 description.", 1, "Typologies 1",
                "Description typologies 1", 1, "Factory Site 1-Area 1", 1,
                "Procedure 1", 0, 1, "Workspace 1.");
        a.addMaterial(1, "Material 1", "Description Material 1.");
        a.addMaterial(2, "Material 2", "Description Material 2.");
        a.addMaterial(3, "Material 3", "Description Material 3.");
        a.addCompetence(1, "Skill 1", "Desc skill 1");
        Activity a1 = new Activity(1, 2020, 21, 2, true, 30,
                "Activity 1 description.", 1, "Typologies 1",
                "Description typologies 1", 1, "Factory Site 1-Area 1", 1,
                "Procedure 1", 0, 1, "Workspace 1.");
        a1.addMaterial(1, "Material 1", "Description Material 1.");
        a1.addMaterial(2, "Material 2", "Description Material 2.");
        a1.addMaterial(3, "Material 3", "Description Material 3.");
        a1.addCompetence(1, "Skill 1", "Desc skill 1");

        assertEquals(a, a);
        assertNotEquals(a1, null);
        assertEquals(a, a1);
    }

    @Test
    void toJSON() {
        // Make actual
        Activity a = new Activity(1, 2020, 43, 3, true, 30,
                "Activity description", 1, "Electrical",
                "desc. electrical", 1, "Fisciano - Molding", 1,
                "Procedure 1", 1, 1, "Workspace 1.");
        a.addMaterial(1, "material_1", "desc_material 1");
        a.addMaterial(2, "material_2", "desc_material 2");
        a.addCompetence(1, "Skill 1", "Desc skill 1");
        a.addCompetence(2, "Skill 2", "Desc skill 2");

        String actual = a.toJSON();

        // Make expected
        String expected = "{\"id\":\"1\",\"year\":\"2020\",\"week\":\"43\",\"day\":\"3\",\"site\":{\"id\":\"1\",\"name\":\"Fisciano - Molding\"},\"typology\":{\"id\":\"1\",\"name\":\"Electrical\",\"description\":\"desc. electrical\"},\"description\":\"Activity description\",\"estimatedInterventionTime\":\"30\",\"interruptibility\":\"true\",\"materials\":[{\"id\":\"1\",\"name\":\"material_1\",\"description\":\"desc_material 1\"},{\"id\":\"2\",\"name\":\"material_2\",\"description\":\"desc_material 2\"}],\"maintenanceProcedures\":{\"id\":\"1\",\"name\":\"Procedure 1\",\"smp\":\"1\"},\"workspace\":{\"id\":\"1\",\"description\":\"Workspace 1.\",\"site\":[]},\"skill\":[{\"id\":\"1\",\"name\":\"Skill 1\",\"description\":\"Desc skill 1\"},{\"id\":\"2\",\"name\":\"Skill 2\",\"description\":\"Desc skill 2\"}]}";

        // Do match
        assertEquals(expected, actual);
    }


}
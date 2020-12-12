package it.unisa.diem.se.team3.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class WorkspaceNotesTest {

    @Test
    void testEquals() {
        WorkspaceNotes wn1 = new WorkspaceNotes(1,"Description workspace notes 1");
        wn1.addSite(1, "SITO");
        WorkspaceNotes wn2 = new WorkspaceNotes(1,"Description workspace notes 1");
        wn2.addSite(1, "SITO");

        assertEquals(wn1, wn1);
        assertNotEquals(wn1, null);
        assertEquals(wn1, wn2);
    }

    @Test
    void toJSONTest() {
        // Make actual
        WorkspaceNotes wn = new WorkspaceNotes(1, "work_space_1 description.");
        wn.addSite(1, "SITO");
        wn.addSite(2, "SITO 2");
        String actual = wn.toJSON();

        // Make expected
        String expected = "{\"id\":\"1\",\"description\":\"work_space_1 description.\",\"site\":[{\"id\":\"1\",\"name\":\"SITO\"},{\"id\":\"2\",\"name\":\"SITO 2\"}]}";

        // Check condition
        assertEquals(expected, actual);
    }
}
package it.unisa.diem.se.team3.models;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JsonUtil {

    /**
     * Function used to convert a List of objects that implement the Model interface into a JSON string.
     *
     * @param l: a list of object that implement the Model interface. It can not be 'null'.
     * @return a JSON string who represent the list.
     */
    static public String toJson(@NotNull List<? extends Model> l) {
        StringBuilder sb = new StringBuilder("[");
        for (Model m : l) {
            sb.append(m.toJSON()).append(",");
        }
        if(sb.length() != 1) {
            sb.deleteCharAt(sb.length() - 1);       // Delete last comma
        }
        sb.append("]");
        return sb.toString();
    }
}
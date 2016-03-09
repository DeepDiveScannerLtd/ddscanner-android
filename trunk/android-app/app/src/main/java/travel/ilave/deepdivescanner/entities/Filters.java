package travel.ilave.deepdivescanner.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lashket on 24.1.16.
 */
public class Filters implements Serializable {
    private Map<String, String> currents;
    private Map<String, String> level;
    private Map<String, String> object;
    private List<String> rating;
    private Map<String, String> visibility;

    public Map<String, String> getCurrents() {
        return currents;
    }

    public void setCurrents(Map<String, String> currents) {
        this.currents = currents;
    }

    public Map<String, String> getLevel() {
        return level;
    }

    public void setLevel(Map<String, String> level) {
        this.level = level;
    }

    public Map<String, String> getObject() {
        return object;
    }

    public void setObject(Map<String, String> object) {
        this.object = object;
    }

    public List<String> getRating() {
        return rating;
    }

    public void setRating(List<String> rating) {
        this.rating = rating;
    }

    public Map<String, String> getVisibility() {
        return visibility;
    }

    public void setVisibility(Map<String, String> visibility) {
        this.visibility = visibility;
    }
}

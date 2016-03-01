package travel.ilave.deepdivescanner.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lashket on 24.1.16.
 */
public class Filters implements Serializable {
    private List<String> currents;
    private List<String> level;
    private List<String> visibility;

    public List<String> getCurrents() {
        return currents;
    }

    public void setCurrents(List<String> currents) {
        this.currents = currents;
    }

    public List<String> getLevel() {
        return level;
    }

    public void setLevel(List<String> level) {
        this.level = level;
    }

    public List<String> getVisibility() {
        return visibility;
    }

    public void setVisibility(List<String> visibility) {
        this.visibility = visibility;
    }
}

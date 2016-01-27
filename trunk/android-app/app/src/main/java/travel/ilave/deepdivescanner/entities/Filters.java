package travel.ilave.deepdivescanner.entities;

import java.io.Serializable;

/**
 * Created by lashket on 24.1.16.
 */
public class Filters implements Serializable {
    private String currents;
    private String level;
    private String visibility;

    public String getCurrents() {
        return currents;
    }

    public void setCurrents(String currents) {
        this.currents = currents;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}

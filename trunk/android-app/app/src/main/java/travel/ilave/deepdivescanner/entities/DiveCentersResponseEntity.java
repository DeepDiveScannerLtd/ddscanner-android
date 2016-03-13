package travel.ilave.deepdivescanner.entities;

import java.util.List;

/**
 * Created by lashket on 5.2.16.
 */
public class DiveCentersResponseEntity {
    private List<DiveCenter> divecenters;
    private  String logoPath;

    public List<DiveCenter> getDivecenters() {
        return divecenters;
    }

    public void setDivecenters(List<DiveCenter> divecenters) {
        this.divecenters = divecenters;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}

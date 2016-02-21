package travel.ilave.deepdivescanner.entities;

import java.util.List;

/**
 * Created by lashket on 22.1.16.
 */
public class DivespotDetails {

    private String sealifePathSmall;
    private String sealifePathMedium;
    private String sealifePathOrigin;
    private DiveSpotFull divespot;
    private List<Sealife> sealifes;




    public DiveSpotFull getDivespot() { return divespot; }

    public void setDivespotFull(DiveSpotFull divespot) { this.divespot = divespot; }

    public List<Sealife> getSealifes() { return sealifes; }

    public void setSealifes(List<Sealife> sealifes) { this.sealifes = sealifes; }

}

package travel.ilave.deepdivescanner.entities;

import java.util.List;

/**
 * Created by lashket on 22.1.16.
 */
public class DivespotDetails {

    private String diveSpotPathSmall;
    private String diveSpotPathMedium;
    private String diveSpotPathOrigin;
    private String sealifePathSmall;
    private String sealifePathMedium;
    private String sealifePathOrigin;
    private DiveSpotFull divespot;
    private List<Sealife> sealifes;

    public String getDiveSpotPathSmall() { return diveSpotPathSmall; }

    public void setDiveSpotPathSmall(String diveSpotPathSmall) { this.diveSpotPathSmall = diveSpotPathSmall; }

    public String getDiveSpotPathMedium() { return diveSpotPathMedium; }

    public void setDiveSpotPathMedium(String diveSpotPathMedium) { this.diveSpotPathMedium = diveSpotPathMedium; }

    public String getDiveSpotPathOrigin() { return diveSpotPathOrigin; }

    public void setDiveSpotPathOrigin(String diveSpotPathOrigin) { this.diveSpotPathOrigin = diveSpotPathOrigin; }

    public String getSealifePathSmall() { return sealifePathSmall; }

    public void setSealifePathSmall(String sealifePathSmall) { this.sealifePathSmall = sealifePathSmall; }

    public String getSealifePathMedium() { return sealifePathMedium; }

    public void setSealifePathMedium(String sealifePathMedium) { this.diveSpotPathMedium = sealifePathMedium; }

    public String getSealifePathOrigin() { return sealifePathOrigin; }

    public void setSealifePathOrigin(String sealifePathOrigin) { this.sealifePathOrigin = sealifePathOrigin; }

    public DiveSpotFull getDivespot() { return divespot; }

    public void setDivespotFull(DiveSpotFull divespot) { this.divespot = divespot; }

    public List<Sealife> getSealifes() { return sealifes; }

    public void setSealifes(List<Sealife> sealifes) { this.sealifes = sealifes; }

}

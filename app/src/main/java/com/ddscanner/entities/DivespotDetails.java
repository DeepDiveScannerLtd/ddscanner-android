package com.ddscanner.entities;

import java.util.List;

/**
 * Created by lashket on 22.1.16.
 */
public class DivespotDetails {

    private List<Comment> comments;
    private DiveSpotFull divespot;
    private List<Sealife> sealifes;



    public List<Comment> getComments() {
        return comments;
    }
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public DiveSpotFull getDivespot() { return divespot; }

    public void setDivespotFull(DiveSpotFull divespot) { this.divespot = divespot; }

    public List<Sealife> getSealifes() { return sealifes; }

    public void setSealifes(List<Sealife> sealifes) { this.sealifes = sealifes; }

}

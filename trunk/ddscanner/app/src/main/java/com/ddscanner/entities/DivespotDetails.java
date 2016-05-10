package com.ddscanner.entities;

import java.util.List;

/**
 * Created by lashket on 22.1.16.
 */
public class DivespotDetails {

    private List<Comment> comments;
    private DiveSpotFull divespot;
    private List<Sealife> sealifes;
    private List<User> checkins;
    private List<User> editors;

    public void setDivespot(DiveSpotFull divespot) {
        this.divespot = divespot;
    }

    public List<User> getCheckins() {
        return checkins;
    }

    public void setCheckins(List<User> checkins) {
        this.checkins = checkins;
    }

    public List<User> getEditors() {
        return editors;
    }

    public void setEditors(List<User> editors) {
        this.editors = editors;
    }

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

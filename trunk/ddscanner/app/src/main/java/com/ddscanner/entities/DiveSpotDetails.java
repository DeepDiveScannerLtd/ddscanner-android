package com.ddscanner.entities;

import java.util.List;

public class DiveSpotDetails {

    private List<Comment> comments;
    private DiveSpotFull divespot;
    private List<Sealife> sealifes;
    private List<UserOld> checkins;
    private List<UserOld> editors;

    public void setDivespot(DiveSpotFull divespot) {
        this.divespot = divespot;
    }

    public List<UserOld> getCheckins() {
        return checkins;
    }

    public void setCheckins(List<UserOld> checkins) {
        this.checkins = checkins;
    }

    public List<UserOld> getEditors() {
        return editors;
    }

    public void setEditors(List<UserOld> editors) {
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

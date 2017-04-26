package com.ddscanner.entities;

import java.util.List;

public class DiveSpotDetails {

    private List<CommentOld> commentOlds;
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

    public List<CommentOld> getCommentOlds() {
        return commentOlds;
    }
    public void setCommentOlds(List<CommentOld> commentOlds) {
        this.commentOlds = commentOlds;
    }

    public DiveSpotFull getDivespot() { return divespot; }

    public void setDivespotFull(DiveSpotFull divespot) { this.divespot = divespot; }

    public List<Sealife> getSealifes() { return sealifes; }

    public void setSealifes(List<Sealife> sealifes) { this.sealifes = sealifes; }

}

package com.ddscanner.entities;

/**
 * Created by lashket on 19.7.16.
 */
public class ForeignUserLike {

    private int diveSpotId;
    private String diveSpotName;
    private String comment;
    private String date;
    private UserOld userOld;

    public int getDiveSpotId() {
        return diveSpotId;
    }

    public void setDiveSpotId(int diveSpotId) {
        this.diveSpotId = diveSpotId;
    }

    public String getDiveSpotName() {
        return diveSpotName;
    }

    public void setDiveSpotName(String diveSpotName) {
        this.diveSpotName = diveSpotName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserOld getUserOld() {
        return userOld;
    }

    public void setUserOld(UserOld userOld) {
        this.userOld = userOld;
    }
}

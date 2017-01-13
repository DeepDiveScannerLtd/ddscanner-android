package com.ddscanner.entities;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.google.gson.annotations.SerializedName;
import com.ddscanner.entities.ActivityTypes;

public class DiveCenterActivityNotification {

    private String id;
    private int type;
    @SerializedName("is_new")
    private boolean isNew;
    private String date;
    private User user;
    @SerializedName("dive_spot")
    private DiveSpotShort diveSpotShort;

    public DiveSpotShort getDiveSpotShort() {
        return diveSpotShort;
    }

    public void setDiveSpotShort(DiveSpotShort diveSpotShort) {
        this.diveSpotShort = diveSpotShort;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ActivityTypes getActivityType() {
        switch (type) {
            case 1:
                return ActivityTypes.DIVE_SPOT_ADDED;
            case 2:
                return ActivityTypes.DIVE_SPOT_PHOTOS_ADDED;
            case 3:
                return ActivityTypes.DIVE_SPOT_CHANGED;
            case 4:
                return ActivityTypes.DIVE_SPOT_CHECKIN;
            case 5:
                return ActivityTypes.DIVE_SPOT_REVIEW_ADDED;
            case 6:
                return ActivityTypes.ACHIEVEMENT_GETTED;
            case 7:
                return ActivityTypes.DIVE_SPOT_REVIEW_LIKE;
            case 8:
                return ActivityTypes.DIVE_SPOT_REVIEW_DISLIKE;
            case 9:
                return ActivityTypes.DIVE_SPOT_PHOTO_LIKE;
            case 10:
                return ActivityTypes.DIVE_CENTER_INSTRUCTOR_REMOVE;
            case 11:
                return ActivityTypes.DIVE_CENTER_INSTRUCTOR_ADD;
            default:
                return null;
        }
    }

    public String getText() {
        switch (getActivityType()) {
            case DIVE_CENTER_INSTRUCTOR_ADD:
                return DDScannerApplication.getInstance().getString(R.string.activity_type_instructor_added, user.getName());
            case DIVE_SPOT_PHOTO_LIKE:
                return DDScannerApplication.getInstance().getString(R.string.activity_type_photo_liked, user.getName(), diveSpotShort.getName());
            case DIVE_SPOT_ADDED:
                return DDScannerApplication.getInstance().getString(R.string.activity_type_dive_spot_added, diveSpotShort.getName());
            case DIVE_SPOT_REVIEW_ADDED:
                return DDScannerApplication.getInstance().getString(R.string.activity_type_review_added, diveSpotShort.getName());
            case DIVE_SPOT_CHANGED:
                return DDScannerApplication.getInstance().getString(R.string.activity_type_dive_spot_changed, diveSpotShort.getName());

            default:
                return "";
        }
    }



}

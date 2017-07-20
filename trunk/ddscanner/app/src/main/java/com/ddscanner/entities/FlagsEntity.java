package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class FlagsEntity {

    @SerializedName("is_checked_in")
    private Boolean isCheckedIn;
    @SerializedName("is_favorite")
    private Boolean isFavorite;
    @SerializedName("is_approved")
    private Boolean isApproved;
    @SerializedName("is_working_here")
    private Boolean isWorkingHere;
    @SerializedName("is_editable")
    private Boolean isEditable;
    @SerializedName("is_reviewed")
    private Boolean isReviewed;

    public Boolean isReviewed() {
        return isReviewed;
    }

    public void setReviewed(Boolean reviewed) {
        isReviewed = reviewed;
    }

    public Boolean isEditable() {
        return isEditable;
    }

    public void setEditable(Boolean editable) {
        isEditable = editable;
    }

    public Boolean isWorkingHere() {
        if (isWorkingHere == null) {
            return false;
        }
        return isWorkingHere;
    }

    public void setWorkingHere(Boolean workingHere) {
        isWorkingHere = workingHere;
    }

    public Boolean isCheckedIn() {
        if (isCheckedIn == null) {
            return false;
        }
        return isCheckedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        isCheckedIn = checkedIn;
    }

    public Boolean isFavorite() {
        if (isFavorite == null) {
            return false;
        }
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public Boolean isApproved() {
        if (isApproved == null) {
            return false;
        }
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }
}

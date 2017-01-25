package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class FlagsEntity {

    @SerializedName("is_checked_in")
    private boolean isCheckedIn;
    @SerializedName("is_favourite")
    private boolean isFavorite;
    @SerializedName("is_verified")
    private boolean isVerified;
    @SerializedName("is_working_here")
    private boolean isWorkingHere;
    @SerializedName("is_editable")
    private boolean isEditable;

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isWorkingHere() {
        return isWorkingHere;
    }

    public void setWorkingHere(boolean workingHere) {
        isWorkingHere = workingHere;
    }

    public boolean isCheckedIn() {
        return isCheckedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        isCheckedIn = checkedIn;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}

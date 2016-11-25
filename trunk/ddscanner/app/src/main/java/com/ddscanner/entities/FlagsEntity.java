package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class FlagsEntity {

    @SerializedName("is_checked_in")
    private boolean isCheckedIn;
    @SerializedName("is_favourite")
    private boolean isFavorite;
    @SerializedName("is_verified")
    private boolean isVerified;

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

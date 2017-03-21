package com.ddscanner.entities;

import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.utils.Helpers;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationEntity {

    private ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(ContextCompat.getColor(DDScannerApplication.getInstance(), R.color.primary));
    private ForegroundColorSpan timeColorSpan = new ForegroundColorSpan(ContextCompat.getColor(DDScannerApplication.getInstance(), R.color.notification_time_color));


    private String id;
    private int type;
    @SerializedName("is_new")
    private boolean isNew;
    private String date;
    private User user;
    @SerializedName("dive_spot")
    private DiveSpotShort diveSpot;
    private ReviewShort review;
    private ArrayList<DiveSpotPhoto> photos;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<DiveSpotPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<DiveSpotPhoto> photos) {
        this.photos = photos;
    }

    public ReviewShort getReview() {
        return review;
    }

    public void setReview(ReviewShort review) {
        this.review = review;
    }

    public DiveSpotShort getDiveSpot() {
        return diveSpot;
    }

    public void setDiveSpot(DiveSpotShort diveSpot) {
        this.diveSpot = diveSpot;
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

    public SpannableString getText() {
        SpannableString finalString;
        String returnedString;
        String timeAgo = Helpers.getDate(date);
        String reviewText;
        switch (getActivityType()) {
            case DIVE_SPOT_ADDED:
                returnedString = DDScannerApplication.getInstance().getString(R.string.activity_type_dive_spot_added, "bulbu", diveSpot.getName(), timeAgo);
                finalString = new SpannableString(returnedString);
                finalString.setSpan(blueColorSpan, 0, 5, 0);
                finalString.setSpan(blueColorSpan, returnedString.indexOf(diveSpot.getName()), returnedString.indexOf(diveSpot.getName()) + diveSpot.getName().length(), 0);
                finalString.setSpan(timeColorSpan, returnedString.indexOf(timeAgo), returnedString.indexOf(timeAgo) + timeAgo.length(), 0);
                return finalString;
            case DIVE_CENTER_INSTRUCTOR_ADD:
                returnedString = DDScannerApplication.getInstance().getString(R.string.activity_type_instructor_added, user.getName(), timeAgo);
                finalString = new SpannableString(returnedString);
                finalString.setSpan(blueColorSpan, 0, user.getName().length(), 0);
                finalString.setSpan(timeColorSpan, returnedString.indexOf(timeAgo), returnedString.indexOf(timeAgo) + timeAgo.length(), 0);
                return finalString;
            case DIVE_SPOT_PHOTO_LIKE:
                returnedString = DDScannerApplication.getInstance().getString(R.string.activity_type_photo_liked, user.getName(), diveSpot.getName(), timeAgo);
                finalString = new SpannableString(returnedString);
                finalString.setSpan(blueColorSpan, 0, user.getName().length(), 0);
                finalString.setSpan(blueColorSpan, returnedString.indexOf(diveSpot.getName()), returnedString.indexOf(diveSpot.getName()) + diveSpot.getName().length(), 0);
                finalString.setSpan(timeColorSpan, returnedString.indexOf(timeAgo), returnedString.indexOf(timeAgo) + timeAgo.length(), 0);
                return finalString;
            case DIVE_SPOT_REVIEW_ADDED:
                returnedString = DDScannerApplication.getInstance().getString(R.string.activity_type_review_added, user.getName(), diveSpot.getName(), cropString(review.getReview()), timeAgo);
                finalString = new SpannableString(returnedString);
                finalString.setSpan(blueColorSpan, 0, user.getName().length(), 0);
                finalString.setSpan(blueColorSpan, returnedString.indexOf(diveSpot.getName()), returnedString.indexOf(diveSpot.getName()) + diveSpot.getName().length(), 0);
                finalString.setSpan(timeColorSpan, returnedString.indexOf(timeAgo), returnedString.indexOf(timeAgo) + timeAgo.length(), 0);
                return finalString;
            case DIVE_SPOT_CHANGED:
                returnedString = DDScannerApplication.getInstance().getString(R.string.activity_type_dive_spot_changed, "bulbu", diveSpot.getName(), timeAgo);
                finalString = new SpannableString(returnedString);
                finalString.setSpan(blueColorSpan, 0, 5, 0);
                finalString.setSpan(blueColorSpan, returnedString.indexOf(diveSpot.getName()), returnedString.indexOf(diveSpot.getName()) + diveSpot.getName().length(), 0);
                finalString.setSpan(timeColorSpan, returnedString.indexOf(timeAgo), returnedString.indexOf(timeAgo) + timeAgo.length(), 0);
                return finalString;
            case DIVE_SPOT_PHOTOS_ADDED:
                returnedString = DDScannerApplication.getInstance().getString(R.string.activity_type_photo_added, user.getName(), String.valueOf(photos.size()), diveSpot.getName(), timeAgo);
                finalString  = new SpannableString(returnedString);
                finalString.setSpan(blueColorSpan, 0, user.getName().length(), 0);
                finalString.setSpan(blueColorSpan, returnedString.indexOf(diveSpot.getName()), returnedString.indexOf(diveSpot.getName()) + diveSpot.getName().length(), 0);
                finalString.setSpan(timeColorSpan, returnedString.indexOf(timeAgo), returnedString.indexOf(timeAgo) + timeAgo.length(), 0);
                return finalString;
            case DIVE_SPOT_CHECKIN:
                returnedString = DDScannerApplication.getInstance().getString(R.string.activity_type_checked_in, user.getName(), diveSpot.getName(), timeAgo);
                finalString  = new SpannableString(returnedString);
                finalString.setSpan(blueColorSpan, 0, user.getName().length(), 0);
                finalString.setSpan(blueColorSpan, returnedString.indexOf(diveSpot.getName()), returnedString.indexOf(diveSpot.getName()) + diveSpot.getName().length(), 0);
                finalString.setSpan(timeColorSpan, returnedString.indexOf(timeAgo), returnedString.indexOf(timeAgo) + timeAgo.length(), 0);
                return finalString;
            case DIVE_CENTER_INSTRUCTOR_REMOVE:
                returnedString = DDScannerApplication.getInstance().getString(R.string.activity_type_left_dive_center, user.getName(), timeAgo);
                finalString  = new SpannableString(returnedString);
                finalString.setSpan(blueColorSpan, 0, user.getName().length(), 0);
                finalString.setSpan(timeColorSpan, returnedString.indexOf(timeAgo), returnedString.indexOf(timeAgo) + timeAgo.length(), 0);
                return finalString;
            default:
                return new SpannableString("");
        }
    }


    private String cropString(String incomingString) {
        String outgoingString = "";
        if (incomingString.length() < 30) {
            return incomingString;
        }
        outgoingString = incomingString.substring(0,26) + "...";
        return outgoingString;
    }


}

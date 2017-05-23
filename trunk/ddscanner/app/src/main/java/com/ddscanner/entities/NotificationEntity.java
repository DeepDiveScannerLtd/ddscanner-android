package com.ddscanner.entities;

import android.support.v4.content.ContextCompat;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.OpenDiveSpotDetailsActivityEvent;
import com.ddscanner.events.OpenUserProfileActivityFromNotifications;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.annotations.SerializedName;
import com.klinker.android.link_builder.Link;

import java.util.ArrayList;

public class NotificationEntity {

    private String id;
    private Integer type;
    @SerializedName("is_new")
    private boolean isNew;
    private String date;
    private User user;
    @SerializedName("dive_spot")
    private DiveSpotShort diveSpot;
    private ReviewShort review;
    private ArrayList<DiveSpotPhoto> photos;
    private String message;
    private AchievmentProfile achievement;
    @SerializedName("photos_count")
    private int photosCount;
    private ArrayList<DiveSpotPhoto> maps;
    @SerializedName("maps_count")
    private int mapsCount;
    private ActivityTypes activityType;

    public ArrayList<DiveSpotPhoto> getMaps() {
        return maps;
    }

    public void setMaps(ArrayList<DiveSpotPhoto> maps) {
        this.maps = maps;
    }

    public int getMapsCount() {
        return mapsCount;
    }

    public void setMapsCount(int mapsCount) {
        this.mapsCount = mapsCount;
    }

    public int getPhotosCount() {
        return photosCount;
    }

    public void setPhotosCount(int photosCount) {
        this.photosCount = photosCount;
    }

    public AchievmentProfile getAchievement() {
        return achievement;
    }

    public void setAchievement(AchievmentProfile achievement) {
        this.achievement = achievement;
    }

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
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
                 activityType = ActivityTypes.DIVE_SPOT_ADDED;
                 break;
            case 2:
                activityType =  ActivityTypes.DIVE_SPOT_PHOTOS_ADDED;
                break;
            case 3:
                activityType =  ActivityTypes.DIVE_SPOT_CHANGED;
                break;
            case 4:
                activityType =  ActivityTypes.DIVE_SPOT_CHECKIN;
                break;
            case 5:
                activityType =  ActivityTypes.DIVE_SPOT_REVIEW_ADDED;
                break;
            case 6:
                activityType =  ActivityTypes.ACHIEVEMENT_GETTED;
                break;
            case 7:
                activityType =  ActivityTypes.DIVE_SPOT_REVIEW_LIKE;
                break;
            case 8:
                activityType =  ActivityTypes.DIVE_SPOT_REVIEW_DISLIKE;
                break;
            case 9:
                activityType =  ActivityTypes.DIVE_SPOT_PHOTO_LIKE;
                break;
            case 10:
                activityType =  ActivityTypes.DIVE_CENTER_INSTRUCTOR_REMOVE;
                break;
            case 11:
                activityType =  ActivityTypes.DIVE_CENTER_INSTRUCTOR_ADD;
                break;
            case 12:
                activityType =  ActivityTypes.INSTRUCTOR_LEFT_DIVE_CENTER;
                break;
            case 13:
                activityType =  ActivityTypes.DIVE_SPOT_MAPS_ADDED;
                break;
            default:
                activityType =  ActivityTypes.VALIDATING_ERROR;
                break;
        }
        return activityType;
    }

    public void setActivtyType(ActivityTypes activityType) {
        this.activityType = activityType;
    }

    public String getText(boolean isSelf, SharedPreferenceHelper.UserType userType) {
        String returnedString = "";
        if (isNew) {
            returnedString += "\u25CF ";
        }
        String timeAgo = Helpers.getDate(date);
        switch (getActivityType()) {
            case DIVE_SPOT_ADDED:
                switch (userType) {
                    case DIVECENTER:
                        returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_dive_spot_added, user.getName(), diveSpot.getName(), timeAgo);
                        return returnedString;
                    default:
                        returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_dive_spot_added_user, user.getName(), diveSpot.getName(), timeAgo);
                        return returnedString;
                }

            case DIVE_CENTER_INSTRUCTOR_ADD:
                returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_instructor_added, user.getName(), timeAgo);
                return returnedString;
            case DIVE_SPOT_PHOTO_LIKE:
                returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_photo_liked, user.getName(), timeAgo);
                return returnedString;
            case DIVE_SPOT_REVIEW_ADDED:
                returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_review_added, user.getName(), diveSpot.getName(), cropString(review.getReview()), timeAgo);
                return returnedString;
            case DIVE_SPOT_CHANGED:
                returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_dive_spot_changed, user.getName(), diveSpot.getName(), timeAgo);
                return returnedString;
            case DIVE_SPOT_PHOTOS_ADDED:
                if (photos.size() == 1) {
                    returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_single_photo_added, user.getName(), diveSpot.getName(), timeAgo);
                } else {
                    returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_photo_added, user.getName(), String.valueOf(photosCount), diveSpot.getName(), timeAgo);
                }
                return returnedString;
            case DIVE_SPOT_MAPS_ADDED:
                if (maps.size() == 1) {
                    returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_single_map_added, user.getName(), diveSpot.getName(), timeAgo);
                } else {
                    returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_maps_added, user.getName(), String.valueOf(mapsCount), diveSpot.getName(), timeAgo);
                }
                return returnedString;
            case DIVE_SPOT_CHECKIN:
                returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_checked_in, user.getName(), diveSpot.getName(), timeAgo);
                return returnedString;
            case DIVE_CENTER_INSTRUCTOR_REMOVE:
                returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_left_dive_center, user.getName(), timeAgo);
                return returnedString;
            case DIVE_SPOT_REVIEW_LIKE:
                returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_review_liked, user.getName(), cropString(review.getReview()), timeAgo);
                return returnedString;
            case DIVE_SPOT_REVIEW_DISLIKE:
                returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_review_disliked, user.getName(), cropString(review.getReview()), timeAgo);
                return returnedString;
            case ACHIEVEMENT_GETTED:
                if (isSelf) {
                    returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_self_achievement_achieved, achievement.getName(), timeAgo);
                    return returnedString;
                }
                returnedString += DDScannerApplication.getInstance().getString(R.string.activity_achievement_achieved, user.getName(), achievement.getName(), timeAgo);
                return  returnedString;
            case INSTRUCTOR_LEFT_DIVE_CENTER:
                returnedString += DDScannerApplication.getInstance().getString(R.string.activity_type_instructor_left_dive_center, user.getName(), timeAgo);
                return returnedString;
            default:
                return "";
        }
    }

    public ArrayList<Link> getLinks() {
        ArrayList<Link> links = new ArrayList<>();
        Link userLink = new Link("");
        Link diveSpotLink = new Link("");
        Link dotLink = new Link("\u25CF");
        dotLink.setTextColor(ContextCompat.getColor(DDScannerApplication.getInstance(),R.color.orange));
        dotLink.setUnderlined(false);
        String timeAgo = Helpers.getDate(date);
        Link timeLink = new Link(timeAgo);
        timeLink.setTextColor(ContextCompat.getColor(DDScannerApplication.getInstance(), R.color.notification_time_color));
        timeLink.setUnderlined(false);
        links.add(timeLink);
        links.add(dotLink);
        if (user != null) {
            userLink = new Link(user.getName());
            userLink.setTextColor(ContextCompat.getColor(DDScannerApplication.getInstance(),R.color.notification_clickable_text_color));
            userLink.setUnderlined(false);
            userLink.setHighlightAlpha(0);
            userLink.setOnClickListener(clickedText -> DDScannerApplication.bus.post(new OpenUserProfileActivityFromNotifications(user.getId(), user.getType())));
        }
        if (diveSpot != null) {
            diveSpotLink = new Link(diveSpot.getName());
            diveSpotLink.setTextColor(ContextCompat.getColor(DDScannerApplication.getInstance(),R.color.notification_clickable_text_color));
            diveSpotLink.setUnderlined(false);
            diveSpotLink.setHighlightAlpha(0);
            diveSpotLink.setOnClickListener(clickedText -> DDScannerApplication.bus.post(new OpenDiveSpotDetailsActivityEvent(diveSpot.getId().toString())));
        }
        switch (getActivityType()) {
            case DIVE_CENTER_INSTRUCTOR_ADD:
            case DIVE_SPOT_PHOTO_LIKE:
            case INSTRUCTOR_LEFT_DIVE_CENTER:
            case DIVE_CENTER_INSTRUCTOR_REMOVE:
                links.add(dotLink);
                links.add(userLink);
                return links;
            case DIVE_SPOT_CHECKIN:
            case DIVE_SPOT_ADDED:
            case DIVE_SPOT_CHANGED:
            case DIVE_SPOT_REVIEW_ADDED:
            case DIVE_SPOT_PHOTOS_ADDED:
            case DIVE_SPOT_MAPS_ADDED:
                links.add(userLink);
                links.add(diveSpotLink);
                return links;
//            case DIVE_CENTER_INSTRUCTOR_REMOVE:
//                returnedString = DDScannerApplication.getInstance().getString(R.string.activity_type_left_dive_center, user.getName(), timeAgo);
//                finalString  = new SpannableString(returnedString);
//                finalString.setSpan(blueColorSpan, 0, user.getName().length(), 0);
//                finalString.setSpan(timeColorSpan, returnedString.indexOf(timeAgo), returnedString.indexOf(timeAgo) + timeAgo.length(), 0);
//                return finalString;
            case DIVE_SPOT_REVIEW_LIKE:
            case DIVE_SPOT_REVIEW_DISLIKE:
            case ACHIEVEMENT_GETTED:
                links.add(userLink);
                return links;
            default:
                return null;
        }
    }


    private String cropString(String incomingString) {
        String outgoingString = "";
        if (incomingString.length() < 30) {
            return incomingString;
        }
        outgoingString = incomingString.substring(0, 26) + "...";
        return outgoingString;
    }


}

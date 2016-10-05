package com.ddscanner.events;

import java.util.ArrayList;

/**
 * Created by Andrei Lashkevich on 21.09.2016.
 */

public class ShowSliderForReviewImagesEvent {

    private boolean isSelfReview;
    private ArrayList<String> photos;
    private int position;
    private int commentPosition;

    public ShowSliderForReviewImagesEvent(boolean isSelfReview, ArrayList<String> photos, int position, int commentPosition) {
        this.isSelfReview = isSelfReview;
        this.photos = photos;
        this.position = position;
        this.commentPosition = commentPosition;
    }

    public boolean isSelfReview() {
        return isSelfReview;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public int getPosition() {
        return position;
    }

    public int getCommentPosition() {
        return commentPosition;
    }
}

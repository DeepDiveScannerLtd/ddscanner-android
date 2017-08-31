package com.ddscanner.events;


public class OpenReviewActivityEvent {

    private String reviewId;

    public OpenReviewActivityEvent(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewId() {
        return reviewId;
    }
}

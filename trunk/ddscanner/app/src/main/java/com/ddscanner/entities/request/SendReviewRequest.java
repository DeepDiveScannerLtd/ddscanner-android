package com.ddscanner.entities.request;

public class SendReviewRequest {
    private String diveSpotId;
    private String comment;
    private String token;
    private String social;
    private String secret;
    private int rating;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDiveSpotId() {
        return diveSpotId;
    }

    public void setDiveSpotId(String diveSpotId) {
        this.diveSpotId = diveSpotId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

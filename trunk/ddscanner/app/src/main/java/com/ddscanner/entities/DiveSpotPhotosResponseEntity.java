package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DiveSpotPhotosResponseEntity {

    @SerializedName("photos")
    private ArrayList<DiveSpotPhoto> diveSpotPhotos;

    @SerializedName("comments")
    private ArrayList<DiveSpotPhoto> commentPhotos;

    public ArrayList<DiveSpotPhoto> getDiveSpotPhotos() {
        return diveSpotPhotos;
    }

    public void setDiveSpotPhotos(ArrayList<DiveSpotPhoto> diveSpotPhotos) {
        this.diveSpotPhotos = diveSpotPhotos;
    }

    public ArrayList<DiveSpotPhoto> getCommentPhotos() {
        return commentPhotos;
    }

    public void setCommentPhotos(ArrayList<DiveSpotPhoto> commentPhotos) {
        this.commentPhotos = commentPhotos;
    }
}

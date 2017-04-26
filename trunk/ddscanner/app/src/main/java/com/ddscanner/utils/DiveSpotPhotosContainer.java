package com.ddscanner.utils;

import com.ddscanner.entities.DiveSpotPhoto;

import java.util.ArrayList;

public class DiveSpotPhotosContainer {

    public  ArrayList<DiveSpotPhoto> photos;

    public DiveSpotPhotosContainer(){

    }

    public  void setPhotos(ArrayList<DiveSpotPhoto> list) {
        photos = list;
    }

    public ArrayList<DiveSpotPhoto> getPhotos() {
        return photos;
    }
}

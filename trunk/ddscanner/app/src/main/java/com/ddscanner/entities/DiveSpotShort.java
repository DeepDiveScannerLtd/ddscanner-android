package com.ddscanner.entities;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lashket on 20.1.16.
 */
public class DiveSpotShort implements Serializable, ClusterItem {

    private static final String TAG = DiveSpotShort.class.getName();
    private long id;
    private String name;
    private float rating;
    private String lat;
    private String lng;
    private LatLng latLng;
    private String reviews;
    @SerializedName("photo")
    private String image;
    @SerializedName("type")
    private int object;
    @SerializedName("is_approved")
    private int isNew;

    public boolean getIsNew() {
        if (1 == isNew) {
            return false;
        }
        return true;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getObject() {
        return object;
    }

    public void setObject(int object) {
        this.object = object;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiveSpotShort diveSpotShort = (DiveSpotShort) o;

        return getId() == diveSpotShort.getId();

    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    @Override
    public LatLng getPosition() {
        if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
            latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        }
        return latLng;
    }

}

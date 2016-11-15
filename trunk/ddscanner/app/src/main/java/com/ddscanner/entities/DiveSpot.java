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
public class DiveSpot implements Serializable, ClusterItem {

    private static final String TAG = DiveSpot.class.getName();
    private long id;
    private String name;
    private float rating;
    private String description;
    private String lat;
    private String lng;
    private List<String> images;
    private LatLng latLng;
    private String reviews;
    @SerializedName("photo")
    private String image;
    @SerializedName("type")
    private int object;
    private String path;
    private String status;
    @SerializedName("is_approved")
    private int isNew;

    public boolean getIsNew() {
        if (isNew == 1) {
            return false;
        }
        return true;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
    /* protected DiveSpot(android.os.Parcel in) {
        id = in.readString();
        description = in.readString();
        rating = in.readInt();
        lat = in.readString();
        lng = in.readString();
    }

    public static final Creator<DiveSpot> CREATOR = new Creator<DiveSpot>() {
        @Override
        public DiveSpot createFromParcel(android.os.Parcel in) {
            return new DiveSpot(in);
        }

        @Override
        public DiveSpot[] newArray(int size) {
            return new DiveSpot[size];
        }
    };
*/


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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiveSpot diveSpot = (DiveSpot) o;

        return getId() == diveSpot.getId();

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
    /*  @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeInt(rating);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeList(images);
    }*/

}

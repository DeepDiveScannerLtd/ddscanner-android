package com.ddscanner.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoAuthor implements Parcelable {

    private String id;
    private String name;
    private String photo;

    protected PhotoAuthor(Parcel in) {
        id = in.readString();
        name = in.readString();
        photo = in.readString();
    }

    public static final Creator<PhotoAuthor> CREATOR = new Creator<PhotoAuthor>() {
        @Override
        public PhotoAuthor createFromParcel(android.os.Parcel in) {
            return new PhotoAuthor(in);
        }

        @Override
        public PhotoAuthor[] newArray(int size) {
            return new PhotoAuthor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(photo);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}

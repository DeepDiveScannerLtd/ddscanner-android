package com.ddscanner.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lashket on 22.7.16.
 */
public class Author implements Parcelable {

    private String type;
    private String name;
    private String photo;
    private String date;
    
    protected Author(Parcel in) {
        type = in.readString();
        name = in.readString();
        photo = in.readString();
        date = in.readString();
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(android.os.Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(name);
        parcel.writeString(photo);
        parcel.writeString(date);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

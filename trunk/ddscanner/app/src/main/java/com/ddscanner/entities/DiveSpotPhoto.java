package com.ddscanner.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DiveSpotPhoto implements Parcelable{

    private String id;
    private String date;
    private PhotoAuthor author;
    @SerializedName("is_liked")
    private boolean isLiked;
    @SerializedName("likes_count")
    private String likesCount;

    protected DiveSpotPhoto(Parcel in) {
        id = in.readString();
        date = in.readString();
        isLiked = in.readByte() != 0;
        likesCount = in.readString();
        author = in.readParcelable(PhotoAuthor.class.getClassLoader());
    }

    public static final Creator<DiveSpotPhoto> CREATOR = new Creator<DiveSpotPhoto>() {
        @Override
        public DiveSpotPhoto createFromParcel(android.os.Parcel in) {
            return new DiveSpotPhoto(in);
        }

        @Override
        public DiveSpotPhoto[] newArray(int size) {
            return new DiveSpotPhoto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(date);
        parcel.writeByte((byte) (isLiked ? 1 : 0));
        parcel.writeString(likesCount);
        parcel.writeParcelable(author, i);
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public PhotoAuthor getAuthor() {
        return author;
    }

    public void setAuthor(PhotoAuthor author) {
        this.author = author;
    }
}

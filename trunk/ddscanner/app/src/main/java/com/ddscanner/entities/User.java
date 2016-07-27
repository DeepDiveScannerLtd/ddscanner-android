package com.ddscanner.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by lashket on 10.3.16.
 */
public class User implements Serializable, Parcelable{

    private String name;
    private String picture;
    private String link;
    private String type;
    private String socialId;
    private String username;
    private String about;
    private String countLike;
    private String countDislike;
    private String countCheckin;
    private String countEdit;
    private String countAdd;
    private String countComment;
    private String id;
    private String countFavorite;

    private boolean imageContainsHttp = true;

    public String getCountFavorite() {
        return countFavorite;
    }

    public void setCountFavorite(String countFavorite) {
        this.countFavorite = countFavorite;
    }

    protected User(Parcel in) {
        name = in.readString();
        picture = in.readString();
        link = in.readString();
        type = in.readString();
        socialId = in.readString();
        username = in.readString();
        about = in.readString();
        countLike = in.readString();
        countDislike = in.readString();
        countCheckin = in.readString();
        countEdit = in.readString();
        countAdd = in.readString();
        countComment = in.readString();
        id = in.readString();
        countFavorite = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(android.os.Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(picture);
        dest.writeString(link);
        dest.writeString(type);
        dest.writeString(socialId);
        dest.writeString(username);
        dest.writeString(about);
        dest.writeString(countLike);
        dest.writeString(countDislike);
        dest.writeString(countCheckin);
        dest.writeString(countEdit);
        dest.writeString(countAdd);
        dest.writeString(countComment);
        dest.writeString(id);
        dest.writeString(countFavorite);
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCountLike() {
        return countLike;
    }

    public void setCountLike(String countLike) {
        this.countLike = countLike;
    }

    public String getCountDislike() {
        return countDislike;
    }

    public void setCountDislike(String countDislike) {
        this.countDislike = countDislike;
    }

    public String getCountCheckin() {
        return countCheckin;
    }

    public void setCountCheckin(String countCheckin) {
        this.countCheckin = countCheckin;
    }

    public String getCountEdit() {
        return countEdit;
    }

    public void setCountEdit(String countEdit) {
        this.countEdit = countEdit;
    }

    public String getCountAdd() {
        return countAdd;
    }

    public void setCountAdd(String countAdd) {
        this.countAdd = countAdd;
    }

    public String getCountComment() {
        return countComment;
    }

    public void setCountComment(String countComment) {
        this.countComment = countComment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getPicture() {
        if (picture != null && imageContainsHttp) {
            picture = picture.replace("http:", "https:");
            imageContainsHttp = false;
        }
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

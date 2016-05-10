package com.ddscanner.entities;

import java.io.Serializable;

/**
 * Created by lashket on 10.3.16.
 */
public class User implements Serializable{

    private String id;

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

package com.ddscanner.entities;

import java.io.Serializable;

/**
 * Created by lashket on 10.3.16.
 */
public class User implements Serializable{

    private String name;
    private String picture;
    private String link;
    private String social;

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

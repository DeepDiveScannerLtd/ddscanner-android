package com.ddscanner.entities;

import java.io.Serializable;

/**
 * Created by lashket on 12.3.16.
 */
public class Comment implements Serializable {
    private String comment;
    private User user;
    private String rating;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

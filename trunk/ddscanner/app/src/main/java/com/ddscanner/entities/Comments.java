package com.ddscanner.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lashket on 12.5.16.
 */
public class Comments implements Serializable {

    private List<Comment> comments;
    private String diveSpotPathMedium;

    public String getDiveSpotPathMedium() {
        return diveSpotPathMedium;
    }

    public void setDiveSpotPathMedium(String diveSpotPathMedium) {
        this.diveSpotPathMedium = diveSpotPathMedium;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}

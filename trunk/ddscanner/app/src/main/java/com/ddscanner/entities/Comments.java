package com.ddscanner.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lashket on 12.5.16.
 */
public class Comments implements Serializable {

    private List<CommentOld> commentOlds;
    private String diveSpotPathMedium;

    public String getDiveSpotPathMedium() {
        return diveSpotPathMedium;
    }

    public void setDiveSpotPathMedium(String diveSpotPathMedium) {
        this.diveSpotPathMedium = diveSpotPathMedium;
    }

    public List<CommentOld> getCommentOlds() {
        return commentOlds;
    }

    public void setCommentOlds(List<CommentOld> commentOlds) {
        this.commentOlds = commentOlds;
    }
}

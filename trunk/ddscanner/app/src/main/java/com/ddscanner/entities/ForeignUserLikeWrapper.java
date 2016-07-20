package com.ddscanner.entities;

import java.util.List;

/**
 * Created by lashket on 19.7.16.
 */
public class ForeignUserLikeWrapper {

    private List<ForeignUserLike> likes;

    public List<ForeignUserLike> getLikes() {
        return likes;
    }

    public void setLikes(List<ForeignUserLike> likes) {
        this.likes = likes;
    }
}

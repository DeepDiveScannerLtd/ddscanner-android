package com.ddscanner.entities;

import java.util.List;

/**
 * Created by lashket on 21.7.16.
 */
public class ForeignUserDislikesWrapper {

    private List<ForeignUserLike> dislikes;

    public List<ForeignUserLike> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<ForeignUserLike> dislikes) {
        this.dislikes = dislikes;
    }
}

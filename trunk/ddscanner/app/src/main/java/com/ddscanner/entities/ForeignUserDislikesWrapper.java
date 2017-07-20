package com.ddscanner.entities;

import java.util.List;

public class ForeignUserDislikesWrapper {

    private List<ForeignUserLike> dislikes;

    public List<ForeignUserLike> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<ForeignUserLike> dislikes) {
        this.dislikes = dislikes;
    }
}

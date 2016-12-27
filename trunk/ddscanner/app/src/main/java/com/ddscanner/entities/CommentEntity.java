package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class CommentEntity {

    @SerializedName("review")
    private Comment comment;
    private User author;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}

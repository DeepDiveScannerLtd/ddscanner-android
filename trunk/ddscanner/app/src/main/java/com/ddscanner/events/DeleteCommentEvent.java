package com.ddscanner.events;

public class DeleteCommentEvent {

    private int commentId;

    public DeleteCommentEvent(int commentId) {
        this.commentId = commentId;
    }

    public int getCommentId() {
        return this.commentId;
    }

}

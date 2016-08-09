package com.ddscanner.events;

/**
 * Created by lashket on 9.8.16.
 */
public class DeleteCommentEvent {

    private int commentId;

    public DeleteCommentEvent(int commentId) {
        this.commentId = commentId;
    }

    public int getCommentId() {
        return this.commentId;
    }

}

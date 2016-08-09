package com.ddscanner.events;

/**
 * Created by lashket on 9.8.16.
 */
public class EditCommentEvent {

    private int commentId;

    public EditCommentEvent(int commentId) {
        this.commentId = commentId;
    }

    public int getCommentId() {
        return this.commentId;
    }
}

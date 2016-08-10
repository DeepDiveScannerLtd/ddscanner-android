package com.ddscanner.events;

/**
 * Created by lashket on 10.8.16.
 */
public class ReportCommentEvent {

    private String commentId;

    public ReportCommentEvent(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentId() {
        return this.commentId;
    }
}

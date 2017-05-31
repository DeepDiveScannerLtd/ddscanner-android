package com.ddscanner.events;

public class ReportCommentEvent {

    private String commentId;

    public ReportCommentEvent(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentId() {
        return this.commentId;
    }
}

package com.ddscanner.events;

import com.ddscanner.entities.Comment;

public class EditCommentEvent {

    private Comment comment;
    private boolean isHaveSealife;

    public EditCommentEvent(Comment comment, boolean isHaveSealife) {
        this.comment = comment;
        this.isHaveSealife = isHaveSealife;
    }

    public boolean isHaveSealife() {
        return isHaveSealife;
    }

    public Comment getComment() {
        return this.comment;
    }
}

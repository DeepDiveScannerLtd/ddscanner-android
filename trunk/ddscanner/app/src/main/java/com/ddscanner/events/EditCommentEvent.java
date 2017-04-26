package com.ddscanner.events;

import com.ddscanner.entities.Comment;

/**
 * Created by lashket on 9.8.16.
 */
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

package com.ddscanner.events;

import com.ddscanner.entities.Comment;

/**
 * Created by lashket on 9.8.16.
 */
public class EditCommentEvent {

    private Comment comment;

    public EditCommentEvent(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return this.comment;
    }
}

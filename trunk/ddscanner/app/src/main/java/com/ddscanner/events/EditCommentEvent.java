package com.ddscanner.events;

import com.ddscanner.entities.CommentEntity;
import com.ddscanner.entities.CommentOld;

/**
 * Created by lashket on 9.8.16.
 */
public class EditCommentEvent {

    private CommentEntity commentOld;

    public EditCommentEvent(CommentEntity commentOld) {
        this.commentOld = commentOld;
    }

    public CommentEntity getComment() {
        return this.commentOld;
    }
}

package com.ddscanner.events;

import com.ddscanner.entities.Comment;
import com.ddscanner.entities.CommentEntity;
import com.ddscanner.entities.SelfCommentEntity;

/**
 * Created by lashket on 9.8.16.
 */
public class EditCommentEvent {

    private Comment comment;

    public EditCommentEvent(Comment commentOld) {
        this.comment = commentOld;
    }

    public Comment getComment() {
        return this.comment;
    }
}

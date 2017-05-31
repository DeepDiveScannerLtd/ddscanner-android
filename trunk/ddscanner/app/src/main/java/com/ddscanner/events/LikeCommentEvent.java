package com.ddscanner.events;

public class LikeCommentEvent {

    private int position;

    public LikeCommentEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}

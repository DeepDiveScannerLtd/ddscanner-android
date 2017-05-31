package com.ddscanner.events;

public class DislikeCommentEvent {

    private int position;

    public DislikeCommentEvent(int position) {
        this.position = position;

    }

    public int getPosition() {
        return position;
    }
}

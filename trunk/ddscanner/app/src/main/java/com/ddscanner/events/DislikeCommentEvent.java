package com.ddscanner.events;

/**
 * Created by Andrei Lashkevich on 15.09.2016.
 */
public class DislikeCommentEvent {

    private int position;

    public DislikeCommentEvent(int position) {
        this.position = position;

    }

    public int getPosition() {
        return position;
    }
}

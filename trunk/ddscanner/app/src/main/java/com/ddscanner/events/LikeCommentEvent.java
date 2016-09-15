package com.ddscanner.events;

/**
 * Created by Andrei Lashkevich on 15.09.2016.
 */
public class LikeCommentEvent {

    private int position;

    public LikeCommentEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}

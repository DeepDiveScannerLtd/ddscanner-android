package com.ddscanner.events;

/**
 * Created by lashket on 17.5.16.
 */
public class ChangePageOfMainViewPagerEvent {

    private int page;

    public ChangePageOfMainViewPagerEvent(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }
}

package com.ddscanner.events;

public class ChangePageOfMainViewPagerEvent {

    private int page;

    public ChangePageOfMainViewPagerEvent(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }
}

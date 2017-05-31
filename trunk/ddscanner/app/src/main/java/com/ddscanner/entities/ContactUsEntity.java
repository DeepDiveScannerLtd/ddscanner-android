package com.ddscanner.entities;

public class ContactUsEntity {

    private String title;
    private String subTitle;
    private int iconResId;
    private String type;

    public ContactUsEntity(String title,String subTitle, int iconResId, String type) {
        this.title = title;
        this.subTitle = subTitle;
        this.iconResId = iconResId;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getIntent() {
        return type;
    }
}

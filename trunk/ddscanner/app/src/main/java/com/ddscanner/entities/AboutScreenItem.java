package com.ddscanner.entities;

import android.content.Intent;

/**
 * Created by lashket on 5.8.16.
 */
public class AboutScreenItem {

    private int iconResId;
    private String name;
    private Intent intent;

    public AboutScreenItem(int iconResId, String name, Intent intent) {
        this.iconResId = iconResId;
        this.name = name;
        this.intent = intent;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getName() {
        return name;
    }

    public Intent getIntent() {
        return intent;
    }
}

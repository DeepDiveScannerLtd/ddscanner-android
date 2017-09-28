package com.ddscanner.utils;


import android.content.Context;
import android.content.Intent;

public final class ShareAppIntentBuilder {

    private final Context context;

    private ShareAppIntentBuilder(Context context) {
        this.context = context;
    }

    public static ShareAppIntentBuilder from(Context context) {
        return new ShareAppIntentBuilder(context);
    }

    public boolean share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://ddscanner.com/");
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
        return true;
    }

}

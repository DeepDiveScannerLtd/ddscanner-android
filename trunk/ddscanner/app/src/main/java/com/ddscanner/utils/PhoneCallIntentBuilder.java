package com.ddscanner.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class PhoneCallIntentBuilder {

    private final Context context;
    private String to = "tel:";

    private PhoneCallIntentBuilder(Context context) {
        this.context = context;
    }

    public static PhoneCallIntentBuilder from(Context context) {
        return new PhoneCallIntentBuilder(context);
    }

    public PhoneCallIntentBuilder to(String to) {
        this.to += to;
        return this;
    }

    public Intent build() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse(to));
        return callIntent;
    }

    public boolean start() {
        Intent callIntent = build();
        context.startActivity(callIntent);
        return true;
    }

}

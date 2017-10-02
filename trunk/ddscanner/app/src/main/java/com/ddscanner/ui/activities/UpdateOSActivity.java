package com.ddscanner.ui.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ddscanner.R;

public class UpdateOSActivity extends BaseAppCompatActivity {

    public static void show(Context context) {
        Intent intent = new Intent(context, UpdateOSActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_android);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}

package com.ddscanner.screens.user.likes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ddscanner.ui.activities.BaseAppCompatActivity;

public class UserLikesActivity extends BaseAppCompatActivity {

    private static final String ARG_ID = "id";
    private String userId;

    public static void show(Context context, String id) {
        Intent intent = new Intent(context, UserLikesActivity.class);
        intent.putExtra(ARG_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra(ARG_ID);
    }
}

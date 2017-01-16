package com.ddscanner.screens.profile.divecenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ddscanner.R;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

import org.apache.http.conn.ConnectTimeoutException;

public class DiveCenterSpotsActivity extends BaseAppCompatActivity {

    public static void show(Context context, String id) {
        Intent intent = new Intent(context, DiveCenterSpotsActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_center_dive_spots);
    }
}

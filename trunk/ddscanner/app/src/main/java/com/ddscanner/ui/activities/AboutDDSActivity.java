package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;

public class AboutDDSActivity extends BaseAppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_about_app);
        setupToolbar(R.string.about_dds, R.id.toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void show(Context context) {
        EventsTracker.trackAboutDDSView();
        Intent intent = new Intent(context, AboutDDSActivity.class);
        context.startActivity(intent);
    }
}

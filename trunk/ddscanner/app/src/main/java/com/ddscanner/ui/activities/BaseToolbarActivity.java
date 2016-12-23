package com.ddscanner.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.ddscanner.R;

public class BaseToolbarActivity extends AppCompatActivity {

    private int menuResourceId = -1;

    /**
     * Show default toolbar with back button with title
     * @param titleresId resource id for toolbar title
     * @param toolbarId toolbar id in layout
     */
    public void setupToolbar(int titleresId, int toolbarId) {
        setSupportActionBar((Toolbar) findViewById(toolbarId));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(titleresId);
    }

    /**
     * Show default toolbar with back button with title and menu
     * @param titleresId resource id for toolbar title
     * @param toolbarId toolbar id in layout
     * @param menuResId resource id for menu
     */
    public void setupToolbar(int titleresId, int toolbarId, int menuResId) {
        this.menuResourceId = menuResId;
        setSupportActionBar((Toolbar) findViewById(toolbarId));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(titleresId);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuResourceId != -1) {
            getMenuInflater().inflate(this.menuResourceId, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
}

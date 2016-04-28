package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ddscanner.R;
import com.ddscanner.ui.adapters.UserListAdapter;

/**
 * Created by lashket on 28.4.16.
 */
public class CheckInPeoplesActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peoples_checkin);
        findViews();
        setupToolbar();
        setUi();
    }

    /**
     * Find views in activity
     * @author Andrei Lashkevich
     */

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        usersRecyclerView = (RecyclerView) findViewById(R.id.peoples_rc);
    }

    /**
     * Create toolbar ui
     * @author Andrei Lashkevich
     */

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.people);
    }

    /**
     * Create ui of activity
     * @author Andrei Lashkevich
     */

    private void setUi() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(linearLayoutManager);
        usersRecyclerView.setAdapter(new UserListAdapter(this));
    }

    /**
     * Show current activity from another place of app
     * @author Andrei Lashkevich
     * @param context
     */

    public static void show(Context context) {
        Intent intent = new Intent(context, CheckInPeoplesActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                    onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

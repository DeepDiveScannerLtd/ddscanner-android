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

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.UserOld;
import com.ddscanner.ui.adapters.EditorsUsersListAdapter;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;

public class EditorsListActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private Toolbar toolbar;
    private ArrayList<UserOld> userOlds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peoples_checkin);
        userOlds = getIntent().getParcelableArrayListExtra("USERS");
        findViews();
        setupToolbar();
        setUi();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        usersRecyclerView = (RecyclerView) findViewById(R.id.peoples_rc);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.people);
    }

    private void setUi() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(linearLayoutManager);
        usersRecyclerView.setAdapter(new EditorsUsersListAdapter(this, userOlds));
    }

    public static void show(Context context, ArrayList<UserOld> userOlds) {
        Intent intent = new Intent(context, EditorsListActivity.class);
        intent.putParcelableArrayListExtra("USERS", userOlds);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

}

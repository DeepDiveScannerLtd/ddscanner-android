package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.User;
import com.ddscanner.events.ShowUserDialogEvent;
import com.ddscanner.ui.adapters.UserListAdapter;
import com.ddscanner.ui.dialogs.ProfileDialog;
import com.ddscanner.utils.Helpers;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by lashket on 2.7.16.
 */
public class EditorsListActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private Toolbar toolbar;
    private ArrayList<User> users;
    private Helpers helpers = new Helpers();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peoples_checkin);
        users = getIntent().getParcelableArrayListExtra("USERS");
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
        usersRecyclerView.setAdapter(new UserListAdapter(this, users));
    }

    public static void show(Context context, ArrayList<User> users) {
        Intent intent = new Intent(context, CheckInPeoplesActivity.class);
        intent.putParcelableArrayListExtra("USERS", users);
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

    @Subscribe
    public void showDialog(ShowUserDialogEvent event) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("profile");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment dialogFragment = ProfileDialog.newInstance(event.getUser());
        dialogFragment.show(fragmentTransaction, "profile");
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
        if (!helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

}

package com.ddscanner.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.daimajia.swipe.util.Attributes;
import com.ddscanner.R;
import com.ddscanner.ui.adapters.SwipableDiveSpotListAdapter;

/**
 * Created by lashket on 18.5.16.
 */
public class UsersDivespotListSwipableActivity extends AppCompatActivity {

    private RecyclerView rc;
    private Toolbar toolbar;
    private SwipableDiveSpotListAdapter swipableDiveSpotListAdapter = new SwipableDiveSpotListAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_dive_spots);
        findViews();
    }

    private void findViews() {
        rc = (RecyclerView) findViewById(R.id.divespots_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setUi();
    }

    private void setUi() {
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dive spots");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);

        rc.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        swipableDiveSpotListAdapter.setMode(Attributes.Mode.Single);

        rc.setAdapter(swipableDiveSpotListAdapter);
    }
}

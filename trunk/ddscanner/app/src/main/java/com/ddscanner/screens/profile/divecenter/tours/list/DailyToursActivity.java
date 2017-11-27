package com.ddscanner.screens.profile.divecenter.tours.list;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.ddscanner.R;
import com.ddscanner.screens.profile.divecenter.tours.details.TourDetailsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

public class DailyToursActivity extends BaseAppCompatActivity {

    private RecyclerView toursList;
    private DailyToursListAdapter dailyToursListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divecenter_daily_tours);
        toursList = findViewById(R.id.tours_list);
        setupToolbar(R.string.daily_tours, R.id.toolbar);
        setupList();
    }

    private void setupList() {
        toursList.setLayoutManager(new LinearLayoutManager(this));
        dailyToursListAdapter = new DailyToursListAdapter(item -> {
            TourDetailsActivity.show(this);});
        toursList.setAdapter(dailyToursListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }
}

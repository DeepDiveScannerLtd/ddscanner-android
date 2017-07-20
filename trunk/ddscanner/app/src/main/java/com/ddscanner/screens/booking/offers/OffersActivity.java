package com.ddscanner.screens.booking.offers;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.ddscanner.R;
import com.ddscanner.databinding.ActivityOffersBinding;
import com.ddscanner.screens.booking.offers.cources.CoursesListFragment;
import com.ddscanner.screens.booking.offers.dailytours.DalyToursFragment;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

public class OffersActivity extends BaseAppCompatActivity {

    private ActivityOffersBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_offers);
        setupToolbar(R.string.offers, R.id.toolbar);
        setupTabs();
    }

    private void setupTabs() {
        OffersViewPagerAdapter offersViewPagerAdapter = new OffersViewPagerAdapter(getSupportFragmentManager());
        offersViewPagerAdapter.addFragment(new DalyToursFragment(), "Daily tours");
        offersViewPagerAdapter.addFragment(new CoursesListFragment(), "Cources");
        binding.viewpager.setAdapter(offersViewPagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewpager);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, OffersActivity.class);
        context.startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}

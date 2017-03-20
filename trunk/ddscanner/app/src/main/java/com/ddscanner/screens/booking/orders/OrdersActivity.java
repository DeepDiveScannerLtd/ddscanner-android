package com.ddscanner.screens.booking.orders;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ddscanner.R;
import com.ddscanner.databinding.ActivityOffersBinding;
import com.ddscanner.entities.Order;
import com.ddscanner.screens.booking.offers.OffersActivity;
import com.ddscanner.screens.booking.offers.OffersViewPagerAdapter;
import com.ddscanner.screens.booking.offers.cources.CoursesListFragment;
import com.ddscanner.screens.booking.offers.dailytours.DalyToursFragment;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    private ActivityOffersBinding binding;
    private OrdersListFragment pastOrderListFragment = new OrdersListFragment();
    private OrdersListFragment upcomingOrderListFragment = new OrdersListFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_offers);
        setupTabs();
    }

    private void setupTabs() {
        OffersViewPagerAdapter offersViewPagerAdapter = new OffersViewPagerAdapter(getSupportFragmentManager());
        offersViewPagerAdapter.addFragment(upcomingOrderListFragment, "Upcoming");
        offersViewPagerAdapter.addFragment(pastOrderListFragment, "Past");
        binding.viewpager.setAdapter(offersViewPagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewpager);
    }


    public static void show(Context context) {
        Intent intent = new Intent(context, OrdersActivity.class);
        context.startActivity(intent);
    }


}

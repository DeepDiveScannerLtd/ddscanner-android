package com.ddscanner.screens.profile.divecenter.tours.details;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.ddscanner.R;
import com.ddscanner.databinding.ActivityDailyTourDetailsBinding;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

public class TourDetailsActivity extends BaseAppCompatActivity {

    private ActivityDailyTourDetailsBinding binding;

    public static void show(Context context) {
        Intent intent = new Intent(context, TourDetailsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_tour_details);
        themeNavAndStatusBar();
        toolbarSettings();
    }

    private void toolbarSettings() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle("");
        binding.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        binding.collapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(this, android.R.color.transparent));
        binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = binding.appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.collapsingToolbar.setTitle("231231331");
                    isShow = true;
                } else if (isShow) {
                    binding.collapsingToolbar.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void themeNavAndStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        w.setNavigationBarColor(ContextCompat.getColor(this ,android.R.color.transparent));
        w.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
    }


}

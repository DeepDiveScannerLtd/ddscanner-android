package com.ddscanner.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;

import com.ddscanner.R;
import com.ddscanner.ui.adapters.MainActivityPagerAdapter;
import com.ddscanner.ui.fragments.MapListFragment;
import com.ddscanner.ui.fragments.NotificationsFragment;
import com.ddscanner.ui.fragments.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 20.4.16.
 */
public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private TabLayout toolbarTabLayout;
    private ViewPager mainViewPager;
    private PercentRelativeLayout menuItemsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setupViewPager(mainViewPager);
        setUi();
        setupTabLayout();
    }

    private void setUi() {
        toolbarTabLayout.setupWithViewPager(mainViewPager);
        mainViewPager.setOffscreenPageLimit(3);
        mainViewPager.setOnPageChangeListener(this);
    }

    private void findViews() {
        toolbarTabLayout = (TabLayout) findViewById(R.id.toolbar_tablayout);
        mainViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        menuItemsLayout = (PercentRelativeLayout) findViewById(R.id.menu_items_layout);
    }

    private void setupTabLayout() {
        toolbarTabLayout.getTabAt(2).setCustomView(R.layout.tab_profile_item);
        toolbarTabLayout.getTabAt(1).setCustomView(R.layout.tab_notification_item);
        toolbarTabLayout.getTabAt(0).setCustomView(R.layout.tab_map_item);
        toolbarTabLayout.getTabAt(0).getCustomView().setSelected(true);
    }

    private void setupViewPager(ViewPager viewPager) {
        MainActivityPagerAdapter adapter = new MainActivityPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapListFragment(), "mapl/list");
        adapter.addFragment(new NotificationsFragment(), "notifications");
        adapter.addFragment(new ProfileFragment(), "profile");
        viewPager.setAdapter(adapter);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onPageSelected(int position) {
        if (position != 0) {
            menuItemsLayout.animate()
                    .translationX(menuItemsLayout.getWidth())
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            menuItemsLayout.setVisibility(View.GONE);
                        }
                    });
        } else {
            menuItemsLayout.animate()
                    .translationX(0)
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            menuItemsLayout.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

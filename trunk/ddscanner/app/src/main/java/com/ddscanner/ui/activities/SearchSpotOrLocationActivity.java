package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.ui.adapters.CustomPagerAdapter;
import com.ddscanner.ui.fragments.SearchDiveSpotsFragment;
import com.ddscanner.ui.fragments.SearchLocationFragment;

/**
 * Created by lashket on 15.6.16.
 */
public class SearchSpotOrLocationActivity extends AppCompatActivity {

    private Menu menu;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Fragment searchDiveSpotFragment = new SearchDiveSpotsFragment();
    private Fragment searchLocationFragment = new SearchLocationFragment();
    private CustomPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_divespots);
        findViews();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.search_view_pager);
        tabLayout = (TabLayout) findViewById(R.id.search_tab_layout);
        setToolbarSettings(toolbar);
        setupViewPager();
    }

    private void setToolbarSettings(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
            getSupportActionBar().setTitle(getString(R.string.search));
        } catch (NullPointerException e) {
            findViews();
        }

    }

    private void setupViewPager() {
        adapter = new CustomPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(searchLocationFragment, getString(R.string.location));
        adapter.addFragment(searchDiveSpotFragment, getString(R.string.dive_spot));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
    }

    private void setupTabLayout() {
        try {
            tabLayout.getTabAt(0).setText(getString(R.string.location));
            tabLayout.getTabAt(1).setText(getString(R.string.dive_spot));
        } catch (NullPointerException e) {
            findViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sealife, menu);
        this.menu = menu;
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getString(R.string.search));
        return true;
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, SearchSpotOrLocationActivity.class);
        context.startActivity(intent);
    }
}

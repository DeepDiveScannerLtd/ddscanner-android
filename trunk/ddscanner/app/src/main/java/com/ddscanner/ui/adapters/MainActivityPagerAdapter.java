package com.ddscanner.ui.adapters;

import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 20.4.16.
 */
public class MainActivityPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public MainActivityPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}

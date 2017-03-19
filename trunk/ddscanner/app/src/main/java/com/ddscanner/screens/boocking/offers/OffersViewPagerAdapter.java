package com.ddscanner.screens.boocking.offers;

import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class OffersViewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter{

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titlesList = new ArrayList<>();

    public OffersViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titlesList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titlesList.add(title);
    }


}

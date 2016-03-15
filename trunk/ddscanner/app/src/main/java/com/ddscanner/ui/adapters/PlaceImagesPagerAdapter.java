package com.ddscanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ddscanner.ui.fragments.PlaceImageFragment;

import java.util.List;

public class PlaceImagesPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> productsImages;
    private String path;
    private Context context;

    public PlaceImagesPagerAdapter(FragmentManager fm, List<String> productsImages, String path, Context context) {
        super(fm);
        this.path = path;
        this.productsImages = productsImages;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new PlaceImageFragment();
        Bundle args = new Bundle();
        args.putString(PlaceImageFragment.IMAGE_URL, path + productsImages.get(position));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return productsImages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}

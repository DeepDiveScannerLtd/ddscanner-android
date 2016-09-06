package com.ddscanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ddscanner.ui.fragments.SLiderImagesFragment;

import java.util.ArrayList;

/**
 * Created by lashket on 29.7.16.
 */
public class ReviewImageSLiderAdapter  extends FragmentStatePagerAdapter {

    private ArrayList<String> productsImages;

    public ReviewImageSLiderAdapter(FragmentManager fm, ArrayList<String> productsImages) {
        super(fm);

        this.productsImages = productsImages;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new SLiderImagesFragment();
        Bundle args = new Bundle();
        args.putString(SLiderImagesFragment.IMAGE_URL, productsImages.get(position));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public  int getCount() {
        return productsImages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
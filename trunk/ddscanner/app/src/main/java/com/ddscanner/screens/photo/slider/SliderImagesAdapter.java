package com.ddscanner.screens.photo.slider;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotPhoto;

import java.util.ArrayList;

/**
 * Created by lashket on 4.3.16.
 */
public class SliderImagesAdapter extends FragmentStatePagerAdapter {

    private ArrayList<DiveSpotPhoto> photos;

    public SliderImagesAdapter(FragmentManager fm, ArrayList<DiveSpotPhoto> photos) {
        super(fm);

        this.photos = photos;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new SliderImagesFragment();
        Bundle args = new Bundle();
        args.putString(SliderImagesFragment.IMAGE_URL, DDScannerApplication.getInstance().getString(R.string.base_photo_url, photos.get(position).getId(), "2"));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public  int getCount() {
        return photos.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}

package com.ddscanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ddscanner.entities.DiveSpot;
import com.ddscanner.ui.fragments.DiveSpotsMapFragment;
import com.ddscanner.ui.fragments.ProductListFragment;
import com.ddscanner.ui.managers.DiveSpotsClusterManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

/**
 * Created by lashket on 21.4.16.
 */
public class MapListPagerAdapter extends FragmentStatePagerAdapter {

    private LatLng latLng;
    private LatLngBounds latLngBounds;
    private RelativeLayout toast;
    private ProgressBar progressBar;
    private FragmentManager fragmentManager;
    private Context context;
    private DiveSpotsClusterManager diveSpotsClusterManager;
    private ViewPager viewPager;
    private ProductListFragment productListFragment;

    public MapListPagerAdapter(Context context, FragmentManager fm, LatLng latLng, LatLngBounds latLngBounds, RelativeLayout toast, ProgressBar progressBar, ViewPager viewPager) {
        super(fm);

        this.context = context;
        this.fragmentManager = fm;
        this.latLng = latLng;
        this.latLngBounds = latLngBounds;
        this.toast = toast;
        this.progressBar = progressBar;
        this.viewPager = viewPager;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new DiveSpotsMapFragment();
                ((DiveSpotsMapFragment) fragment).setUI(toast, progressBar, this, viewPager);
                break;
            case 1:
                fragment = new ProductListFragment();
                productListFragment = (ProductListFragment) fragment;
                ((ProductListFragment) fragment).setViewPager(viewPager);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }


    public void populateDiveSpotsList(ArrayList<DiveSpot> diveSpots) {
        productListFragment.fillDiveSpots(diveSpots);
    }

    public void requestDiveSpots(String currents, String level, String object, int rating, String visibility) {
//        diveSpotsClusterManager.updateFilter(currents, level, object, rating, visibility);
//        diveSpotsClusterManager.requestCityProducts();
    }

}

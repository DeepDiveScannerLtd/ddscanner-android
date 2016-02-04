package travel.ilave.deepdivescanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import travel.ilave.deepdivescanner.ui.fragments.DiveCenterListFragment;


/**
 * Created by lashket on 4.2.16.
 */
public class DiveCentersPagerAdapter extends FragmentStatePagerAdapter {

    public DiveCentersPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (position) {
            case 1:
                fragment = new MapFragment();
                ((MapFragment) fragment).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                    }
                });
                break;
            case 0:
               // fragment = new DiveCenterListFragment();
                break;
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "LIST";
            case 1:
                return "MAP";
            default:
                return "";
        }
    }

}

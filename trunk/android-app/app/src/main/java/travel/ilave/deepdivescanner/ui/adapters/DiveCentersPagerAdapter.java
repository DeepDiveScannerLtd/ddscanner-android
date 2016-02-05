package travel.ilave.deepdivescanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import travel.ilave.deepdivescanner.entities.DiveCenter;
import travel.ilave.deepdivescanner.entities.Divecenters;
import travel.ilave.deepdivescanner.ui.fragments.DiveCenterListFragment;


/**
 * Created by lashket on 4.2.16.
 */
public class DiveCentersPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private Divecenters diveCenters;
    private ArrayList<DiveCenter> divecenters;
    private LatLng latLng;

    public DiveCentersPagerAdapter(Context context, FragmentManager fm, Divecenters diveCenters, LatLng latLng) {
        super(fm);
        this.context = context;
        this.diveCenters = diveCenters;
        this.latLng = latLng;
    }

    @Override
    public Fragment getItem(int position) {
        divecenters = (ArrayList<DiveCenter>)diveCenters.getDivecenters();
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (position) {
            case 1:
                fragment = new MapFragment();
                ((MapFragment) fragment).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8.0f));
                    }
                });
                break;
            case 0:
                fragment = new DiveCenterListFragment();
                args.putParcelableArrayList("DIVESPOTS", divecenters);
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

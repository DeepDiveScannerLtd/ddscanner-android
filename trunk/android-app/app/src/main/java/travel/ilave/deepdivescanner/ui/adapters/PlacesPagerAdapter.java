package travel.ilave.deepdivescanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.ui.fragments.ProductListFragment;


public class PlacesPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = PlacesPagerAdapter.class.getName();
    private static GoogleMap gMap;
    private Context context;
    private LatLng latLng;
    private FragmentManager fm;
    private HashMap<String, String> filters = new HashMap<String, String>();
    private String path;
    private InfoWindowAdapter infoWindowAdapter;
    private MapFragment mapFragment;
    private ProductListFragment productListFragment;

    public PlacesPagerAdapter(Context context, FragmentManager fm, LatLng latLng, HashMap<String, String> filters) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.latLng = latLng;
        this.filters = filters;
    }

    public static LatLng getLastLatlng() {
        if (gMap == null) {
            return null;
        } else {
            return gMap.getCameraPosition().target;
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                mapFragment = new MapFragment();
                fragment = mapFragment;
                ((MapFragment) fragment).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        if (latLng.longitude == 0 && latLng.latitude == 0) {
                            latLng = new LatLng(53.902378, 27.557184);
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8.0f));
                        gMap = googleMap;
                        infoWindowAdapter = new InfoWindowAdapter(context, PlacesPagerAdapter.this, new ArrayList<DiveSpot>(), gMap);
                        gMap.setInfoWindowAdapter(infoWindowAdapter);
                    }
                });
                break;
            case 1:
                productListFragment = new ProductListFragment();
                fragment = productListFragment;
                break;
        }
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
                return "MAP";
            case 1:
                return "LIST";
            default:
                return "";
        }
    }

    public void populateDiveSpotsList(ArrayList<DiveSpot> diveSpots) {
        productListFragment.fillDiveSpots(diveSpots);
    }


}

package travel.ilave.deepdivescanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import travel.ilave.deepdivescanner.entities.City;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.ui.fragments.ImproveLevelFragment;
import travel.ilave.deepdivescanner.ui.fragments.ProductListFragment;


public class PlacesPagerAdapter extends FragmentStatePagerAdapter  {

    public static final String ARGS = "args";
    private static final String TAG = "PlacesPagerAdapter";

    private Context context;
    private LatLng latLng;
    private City city;
    private ArrayList<Product> products;
    private HashMap<Marker, Product> markersMap = new HashMap<>();
    private OnProductSelectedListener onProductSelectedListener;
    private GoogleMap gMap;
    public PlacesPagerAdapter(Context context, FragmentManager fm, City city, ArrayList<Product> products, LatLng latLng, OnProductSelectedListener onProductSelectedListener) {
        super(fm);

        this.context = context;
        this.city = city;
        this.products = products;
        this.onProductSelectedListener = onProductSelectedListener;
        this.latLng = latLng;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                fragment = new MapFragment();
                ((MapFragment) fragment).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8.0f));
                        gMap = googleMap;
                        googleMap.setInfoWindowAdapter(new InfoWindowAdapter(context, products, googleMap));
                    }
                });
                break;
            case 1:
                fragment = new ProductListFragment();
                args.putParcelableArrayList("PRODUCTS", products);
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
                return "MAP";
            case 1:
                return "LIST";
            default:
                return "";
        }
    }

    public interface OnProductSelectedListener {
        void onProductSelected(Product selectedProduct);
    }

}

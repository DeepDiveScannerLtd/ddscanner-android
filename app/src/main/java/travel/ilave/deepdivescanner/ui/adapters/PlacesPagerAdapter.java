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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import travel.ilave.deepdivescanner.entities.City;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.ui.fragments.ImproveLevelFragment;

public class PlacesPagerAdapter extends FragmentStatePagerAdapter implements GoogleMap.OnMarkerClickListener {

    public static final String ARGS = "args";

    private Context context;
    private GoogleMap googleMap;
    private City city;
    private ArrayList<Product> products;
    private HashMap<Marker, Product> markersMap = new HashMap<>();
    private OnProductSelectedListener onProductSelectedListener;
    private Product selected = null;

    public PlacesPagerAdapter(Context context, FragmentManager fm, City city, ArrayList<Product> products, OnProductSelectedListener onProductSelectedListener) {
        super(fm);

        this.context = context;
        this.city = city;
        this.products = products;
        this.onProductSelectedListener = onProductSelectedListener;
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
                        LatLng city = new LatLng(Double.valueOf(PlacesPagerAdapter.this.city.getLat()), Double.valueOf(PlacesPagerAdapter.this.city.getLng()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 8.0f));
                        googleMap.setOnMarkerClickListener(PlacesPagerAdapter.this);


                        for (Product product : products) {
                            if (!product.isHotOffers()) {
                                //googleMap.setInfoWindowAdapter(new InfoWindowAdapter(context));
                                LatLng place = new LatLng(Double.valueOf(product.getLat()), Double.valueOf(product.getLng()));
                                Marker marker = googleMap.addMarker(new MarkerOptions().position(place));
                                marker.setTitle(product.getName());
                                //googleMap.setInfoWindowAdapter(new InfoWindowAdapter(context, product));
                                PlacesPagerAdapter.this.markersMap.put(marker, product);
                            }
                        }
                    }
                });
                break;
            case 1:
                fragment = new MapFragment();
                ((MapFragment) fragment).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng city = new LatLng(Double.valueOf(PlacesPagerAdapter.this.city.getLat()), Double.valueOf(PlacesPagerAdapter.this.city.getLng()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 8.0f));
                        googleMap.setOnMarkerClickListener(PlacesPagerAdapter.this);

                        for (Product product : products) {
                            if (product.isHotOffers()) {
                                //googleMap.setInfoWindowAdapter(new InfoWindowAdapter(context));
                                LatLng place = new LatLng(Double.valueOf(product.getLat()), Double.valueOf(product.getLng()));
                                Marker marker = googleMap.addMarker(new MarkerOptions().position(place));
                                marker.setTitle(product.getName());
                                //googleMap.setInfoWindowAdapter(new InfoWindowAdapter(context, product));
                                PlacesPagerAdapter.this.markersMap.put(marker, product);

                            }
                        }
                    }
                });
                break;
            case 2:
                fragment = new ImproveLevelFragment();
                break;
        }
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "EXPLORE";
            case 1:
                return "SPECIAL OFFERS";
            case 2:
                return "IMPROVE LEVEL";
            default:
                return "";
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selected = markersMap.get(marker);
        System.out.println(selected.getName());
       // marker.showInfoWindow();
        onProductSelectedListener.onProductSelected(markersMap.get(marker));
        return true;
    }

    public interface OnProductSelectedListener {
        void onProductSelected(Product selectedProduct);
    }
}

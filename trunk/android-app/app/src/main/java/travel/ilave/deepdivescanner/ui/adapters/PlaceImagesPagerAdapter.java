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
import java.util.List;

import travel.ilave.deepdivescanner.entities.City;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.entities.ProductDetails;
import travel.ilave.deepdivescanner.ui.fragments.ImproveLevelFragment;
import travel.ilave.deepdivescanner.ui.fragments.PlaceImageFragment;

public class PlaceImagesPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> productsImages;
    private String path;

    public PlaceImagesPagerAdapter(FragmentManager fm, List<String> productsImages, String path) {
        super(fm);
        this.path = path;
        this.productsImages = productsImages;
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

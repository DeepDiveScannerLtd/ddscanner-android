package com.ddscanner.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by lashket on 19.4.16.
 */
public class DiveSpotsMapFragment extends Fragment {

    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dive_sites_map_fragment, container, false);

        return view;
    }
}

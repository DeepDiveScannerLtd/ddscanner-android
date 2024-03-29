package com.ddscanner.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.GoToMyLocationButtonClickedEvent;
import com.ddscanner.ui.adapters.PlacesListAdapter;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class SearchLocationFragment extends Fragment implements View.OnClickListener {

    private RecyclerView locationRecyclerView;
    private LinearLayout goToMyLocation;
    private LinearLayout content;
    private ScrollView noResultsView;
    private ArrayList<String> places;
    private GoogleApiClient googleApiClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_location, container, false);
        findViews(view);
        if (places != null && googleApiClient != null) {
            setList(places, googleApiClient);
        }
        return view;

    }

    private void findViews(View v){
        locationRecyclerView = v.findViewById(R.id.places_recyclerview);
        goToMyLocation = v.findViewById(R.id.go_to_my_location);
        noResultsView = v.findViewById(R.id.no_results);
        content = v.findViewById(R.id.content);

        goToMyLocation.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        locationRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public void setList(ArrayList<String> places, GoogleApiClient googleApiClient) {
        if (locationRecyclerView == null) {
            this.places = places;
            this.googleApiClient = googleApiClient;
            return;
        }
        if (places == null || places.size() == 0) {
            noResultsView.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        } else {
            noResultsView.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            locationRecyclerView.setAdapter(new PlacesListAdapter(places, googleApiClient));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_to_my_location:
                DDScannerApplication.bus.post(new GoToMyLocationButtonClickedEvent());
                break;
        }
    }

}

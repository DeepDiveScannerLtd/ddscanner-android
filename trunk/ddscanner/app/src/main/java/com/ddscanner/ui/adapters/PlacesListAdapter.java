package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddscanner.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 20.6.16.
 */
public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.PlacesListViewHolder>{

    private ArrayList<Place> places;
    private GoogleApiClient googleApiClient;

    public PlacesListAdapter(ArrayList<Place> places, GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
        this.places = places;
    }

    @Override
    public PlacesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_search_dive_spot, parent,false);
        return new PlacesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlacesListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (places != null) {
            return places.size();
        }
        return 0;
    }

    public class PlacesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView placeName;
        private Context context;

        public PlacesListViewHolder(View v) {
            super(v);
            context = v.getContext();
            v.setOnClickListener(this);
            placeName = (TextView) v.findViewById(R.id.diveSpotName);
        }

        @Override
        public void onClick(View v) {

        }
    }
}

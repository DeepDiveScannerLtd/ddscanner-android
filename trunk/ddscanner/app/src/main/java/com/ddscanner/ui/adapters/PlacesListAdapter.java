package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.LocationChosedEvent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.PlacesListViewHolder>{

    private ArrayList<String> places;
    private GoogleApiClient googleApiClient;

    public PlacesListAdapter(ArrayList<String> places, GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
        this.places = places;
    }

    @Override
    public PlacesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_location, parent,false);
        return new PlacesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlacesListViewHolder holder, int position) {
        Places.GeoDataApi.getPlaceById(googleApiClient, places.get(position)).setResultCallback(places -> {
            if (places.getStatus().isSuccess()) {
                try {
                    Place place = places.get(0);
                    holder.placeName.setText(place.getName());
                } catch (IllegalStateException ignored) {

                }
            }
            places.release();
        });
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
            placeName = v.findViewById(R.id.diveSpotName);
        }

        @Override
        public void onClick(View v) {
            DDScannerApplication.bus.post(new LocationChosedEvent(places.get(getAdapterPosition())));
        }
    }
}

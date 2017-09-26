package com.ddscanner.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.SearchFeature;
import com.ddscanner.events.LocationChosedEvent;
import com.ddscanner.interfaces.ListItemClickListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

import java.util.ArrayList;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.PlacesListViewHolder>{

    private ArrayList<SearchFeature> places;
    ListItemClickListener<LatLngBounds> listItemClickListener;

    public PlacesListAdapter(ArrayList<SearchFeature> places, Activity activity) {
        this.places = places;
        this.listItemClickListener = (ListItemClickListener<LatLngBounds>) activity;
    }

    @Override
    public PlacesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_location, parent,false);
        return new PlacesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlacesListViewHolder holder, int position) {
        holder.placeName.setText(places.get(position).getPlaceName());
    }

    @Override
    public int getItemCount() {
        if (places != null) {
            return places.size();
        }
        return 0;
    }

    public class PlacesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView placeName;
        Context context;

        PlacesListViewHolder(View v) {
            super(v);
            context = v.getContext();
            v.setOnClickListener(this);
            placeName = v.findViewById(R.id.diveSpotName);
        }

        @Override
        public void onClick(View v) {
            listItemClickListener.onItemClick(places.get(getAdapterPosition()).getBounds());
//            DDScannerApplication.bus.post(new LocationChosedEvent(places.get(getAdapterPosition())));
        }
    }
}

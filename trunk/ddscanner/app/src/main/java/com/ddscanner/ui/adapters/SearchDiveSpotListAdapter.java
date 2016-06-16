package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;

import java.util.ArrayList;

/**
 * Created by lashket on 15.6.16.
 */
public class SearchDiveSpotListAdapter extends RecyclerView.Adapter<SearchDiveSpotListAdapter.SearchDiveSpotListViewHolder> {

    private ArrayList<DiveSpot> diveSpots;
    private Context context;

    public SearchDiveSpotListAdapter(ArrayList<DiveSpot> diveSpots, Context context) {
        this.diveSpots = diveSpots;
        this.context = context;
    }

    @Override
    public SearchDiveSpotListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_search_dive_spot, parent,false);
        return new SearchDiveSpotListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchDiveSpotListViewHolder holder, int position) {
        holder.diveSpotName.setText(diveSpots.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (diveSpots != null) {
            return diveSpots.size();
        }
        return 0;
    }

    public class SearchDiveSpotListViewHolder extends RecyclerView.ViewHolder {

        protected TextView diveSpotName;

        public SearchDiveSpotListViewHolder(View v) {
            super(v);
            diveSpotName = (TextView) v.findViewById(R.id.diveSpotName);
        }

    }
}

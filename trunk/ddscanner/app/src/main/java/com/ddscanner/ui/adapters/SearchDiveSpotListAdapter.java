package com.ddscanner.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotChosedFromSearch;
import com.ddscanner.entities.DiveSpotShort;

import java.util.ArrayList;

public class SearchDiveSpotListAdapter extends RecyclerView.Adapter<SearchDiveSpotListAdapter.SearchDiveSpotListViewHolder> {

    private ArrayList<DiveSpotShort> diveSpotShorts;

    public SearchDiveSpotListAdapter(ArrayList<DiveSpotShort> diveSpotShorts) {
        this.diveSpotShorts = diveSpotShorts;
    }

    @Override
    public SearchDiveSpotListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_dive_spot, parent,false);
        return new SearchDiveSpotListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchDiveSpotListViewHolder holder, int position) {
        holder.diveSpotName.setText(diveSpotShorts.get(position).getName());
        holder.countryName.setText(diveSpotShorts.get(position).getCountry().getName());
    }

    @Override
    public int getItemCount() {
        if (diveSpotShorts != null) {
            return diveSpotShorts.size();
        }
        return 0;
    }

    public class SearchDiveSpotListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView diveSpotName;
        TextView countryName;

        SearchDiveSpotListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            diveSpotName = v.findViewById(R.id.diveSpotName);
            countryName = v.findViewById(R.id.country);
        }

        @Override
        public void onClick(View v) {
            DDScannerApplication.bus.post(new DiveSpotChosedFromSearch(diveSpotShorts.get(getAdapterPosition())));
        }
    }
}

package com.ddscanner.ui.adapters;

import android.content.Context;
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

/**
 * Created by lashket on 15.6.16.
 */
public class SearchDiveSpotListAdapter extends RecyclerView.Adapter<SearchDiveSpotListAdapter.SearchDiveSpotListViewHolder> {

    private ArrayList<DiveSpotShort> diveSpotShorts;
    private Context context;

    public SearchDiveSpotListAdapter(ArrayList<DiveSpotShort> diveSpotShorts, Context context) {
        this.diveSpotShorts = diveSpotShorts;
        this.context = context;
    }

    @Override
    public SearchDiveSpotListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_dive_spot, parent,false);
        return new SearchDiveSpotListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchDiveSpotListViewHolder holder, int position) {
        holder.diveSpotName.setText(diveSpotShorts.get(position).getName());
        //TODO uncomment 26.05.17
//        holder.countryName.setText(diveSpotShorts.get(position).getCountry().getName());
    }

    @Override
    public int getItemCount() {
        if (diveSpotShorts != null) {
            return diveSpotShorts.size();
        }
        return 0;
    }

    public class SearchDiveSpotListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView diveSpotName;
        protected TextView countryName;
        private Context context;

        public SearchDiveSpotListViewHolder(View v) {
            super(v);
            context = v.getContext();
            v.setOnClickListener(this);
            diveSpotName = (TextView) v.findViewById(R.id.diveSpotName);
            countryName = (TextView) v.findViewById(R.id.country);
        }

        @Override
        public void onClick(View v) {
            DDScannerApplication.bus.post(new DiveSpotChosedFromSearch(diveSpotShorts.get(getAdapterPosition())));
        }
    }
}

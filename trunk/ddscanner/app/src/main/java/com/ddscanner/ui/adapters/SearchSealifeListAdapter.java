package com.ddscanner.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;

public class SearchSealifeListAdapter extends RecyclerView.Adapter<SearchSealifeListAdapter.SearchSealifeListViewHolder> {

    private SealifeSectionedRecyclerViewAdapter sectionAdapter;

    @Override
    public void onBindViewHolder(SearchSealifeListViewHolder holder, int position) {

    }

    @Override
    public SearchSealifeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sealife, parent, false);
        return new SearchSealifeListViewHolder(item);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public void setSectionAdapter(SealifeSectionedRecyclerViewAdapter sectionAdapter) {
        this.sectionAdapter = sectionAdapter;
    }

    class SearchSealifeListViewHolder extends RecyclerView.ViewHolder {

        public SearchSealifeListViewHolder(View view) {
            super(view);
        }

    }

}

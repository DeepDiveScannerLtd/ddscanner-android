package com.ddscanner.screens.profile.edit.divecenter.search;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenterSearchItem;
import com.ddscanner.interfaces.DiveCenterItemClickListener;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class SearchDiveCenterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DIVE_CENTER = 1;
    private static final int VIEW_TYPE_PAGINATION = 2;
    private Context context;
    private ArrayList<DiveCenterSearchItem> diveCentersList = new ArrayList<>();
    private DiveCenterItemClickListener listener;

    public SearchDiveCenterListAdapter(DiveCenterItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<DiveCenterSearchItem> data) {
        this.diveCentersList = data;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<DiveCenterSearchItem> data) {
        this.diveCentersList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case VIEW_TYPE_DIVE_CENTER:
                return new SearchDiveCenterItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_dive_center_search, viewGroup, false));
            case VIEW_TYPE_PAGINATION:
                return new PaginationViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_paginaion_loader, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) == VIEW_TYPE_PAGINATION) {
            return;
        }
        SearchDiveCenterItemViewHolder searchDiveCenterItemViewHolder = (SearchDiveCenterItemViewHolder) viewHolder;
        searchDiveCenterItemViewHolder.address.setText(diveCentersList.get(i).getAddress());
        searchDiveCenterItemViewHolder.name.setText(diveCentersList.get(i).getName());
    }

    @Override
    public int getItemViewType(int position) {
        if (diveCentersList.get(position) == null) {
            return VIEW_TYPE_PAGINATION;
        }
        return VIEW_TYPE_DIVE_CENTER;
    }

    @Override
    public int getItemCount() {
        return diveCentersList.size();
    }

    public void startLoading() {
        diveCentersList.add(null);
        notifyItemInserted(diveCentersList.size() - 1);
    }

    public void dataLoaded() {
        diveCentersList.remove(diveCentersList.size() - 1);
        notifyItemRemoved(diveCentersList.size() - 1);
    }

    class SearchDiveCenterItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView address;

        SearchDiveCenterItemViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            context = view.getContext();
            name = (TextView) view.findViewById(R.id.dive_center_name);
            address = (TextView) view.findViewById(R.id.dive_center_address);
        }

        @Override
        public void onClick(View view) {
            listener.onDiveCenterClicked(diveCentersList.get(getAdapterPosition()));
        }
    }

    class PaginationViewHolder extends RecyclerView.ViewHolder {

        PaginationViewHolder(View view){
            super(view);
        }

    }

}

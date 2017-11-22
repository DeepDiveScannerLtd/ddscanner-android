package com.ddscanner.screens.divespots.map;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.BaseMapEntity;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.ui.views.RatingView;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DiveSpotsMapListAdapter extends RecyclerView.Adapter<DiveSpotsMapListAdapter.DiveSpotListItemViewHolder> {

    private ArrayList<BaseMapEntity> list = new ArrayList<>();
    private Activity context;

    public DiveSpotsMapListAdapter(Activity context) {
        this.context = context;
    }

    public void setList(ArrayList<BaseMapEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public DiveSpotListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiveSpotListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_divespotmap_list_item_dive_spot, parent, false));
    }

    @Override
    public void onBindViewHolder(DiveSpotListItemViewHolder holder, int position) {
        holder.diveSpotName.setText(list.get(position).getName());
        holder.object.setText(list.get(position).getObject());
        holder.ratingView.removeAllViews();
        holder.ratingView.setRating(Math.round(list.get(position).getRating()), R.drawable.ic_list_star_full, R.drawable.ic_list_star_empty);
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, list.get(position).getPhoto(), "1")).resize(Math.round(Helpers.convertDpToPixel(55, context)), Math.round(Helpers.convertDpToPixel(55, context))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).placeholder(R.drawable.placeholder_photo_wit_round_corners).error(R.drawable.ds_list_photo_default).into(holder.logo);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DiveSpotListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView diveSpotName;
        private TextView object;
        private RatingView ratingView;
        private ImageView logo;

        public DiveSpotListItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            diveSpotName = itemView.findViewById(R.id.product_title);
            object = itemView.findViewById(R.id.object);
            ratingView = itemView.findViewById(R.id.stars);
            logo = itemView.findViewById(R.id.logo);
        }

        @Override
        public void onClick(View v) {
            DiveSpotDetailsActivity.show(context, String.valueOf(list.get(getAdapterPosition()).getId()), EventsTracker.SpotViewSource.FROM_LIST);
        }
    }

}

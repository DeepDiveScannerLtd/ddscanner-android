package com.ddscanner.screens.profile.divecenter;


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
import com.ddscanner.entities.DailyTour;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DiveCenterProfileProductsAdapter extends RecyclerView.Adapter<DiveCenterProfileProductsAdapter.DiveCenterProfileProductiewHolder> {

    private ArrayList<DailyTour> dailyTours = new ArrayList<>();
    private Context context;

    @Override
    public DiveCenterProfileProductiewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_divecenter_profile_product, parent, false);
        return new DiveCenterProfileProductiewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiveCenterProfileProductiewHolder holder, int position) {
        DailyTour dailyTour = dailyTours.get(position);
        if (dailyTour.getPhoto() != null) {
            Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, dailyTour.getPhoto(), "1")).resize(Helpers.convertDpToIntPixels(40, context), Helpers.convertDpToIntPixels(40, context)).transform(new RoundedCornersTransformation(Helpers.convertDpToIntPixels(2, context), 0, RoundedCornersTransformation.CornerType.ALL)).placeholder(R.drawable.placeholder_photo_wit_round_corners).into(holder.logo);
        } else {
            holder.logo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.photo_dailytour_default_small));
        }
        holder.name.setText(dailyTour.getName());
        holder.price.setText(dailyTour.getPrice());
        if (dailyTour.getNumberOfDives() != null) {
            holder.divesCount.setText(String.format("%s dives", dailyTour.getNumberOfDives()));
        } else {
            holder.divesCount.setText(R.string.empty_string);
        }
    }


    @Override
    public int getItemCount() {
        return dailyTours.size();
    }

    public void setDailyTours(ArrayList<DailyTour> dailyTours) {
        this.dailyTours = dailyTours;
        notifyDataSetChanged();
    }

    class DiveCenterProfileProductiewHolder extends RecyclerView.ViewHolder {

        private ImageView logo;
        private TextView price;
        private TextView name;
        private TextView divesCount;

        public DiveCenterProfileProductiewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            logo = itemView.findViewById(R.id.product_logo);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.product_name);
            divesCount = itemView.findViewById(R.id.dives_count);

        }
    }

}
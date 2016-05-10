package com.ddscanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.ui.activities.DivePlaceActivity;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.EventTrackerHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lashket on 23.12.15.
 */
public class ProductListAdapter
        extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>{

    private static final String TAG = ProductListAdapter.class.getSimpleName();

    public static ArrayList<DiveSpot> divespots;
    private Context context;

    public ProductListAdapter(ArrayList<DiveSpot> divespots, Context context) {
        this.divespots = divespots;
        this.context = context;
    }

    @Override
    public ProductListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.product_item, viewGroup, false);
        return new ProductListViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ProductListViewHolder productListViewHolder, int i) {
        DiveSpot divespot = new DiveSpot();
        divespot = divespots.get(i);
        productListViewHolder.progressBar.getIndeterminateDrawable().
                setColorFilter(context.getResources().getColor(R.color.primary),
                        PorterDuff.Mode.MULTIPLY);
        if (divespot.getImage() != null) {
            Picasso.with(context).load(divespot.getImage()).resize(130, 130).centerCrop()
                    .transform(new TransformationRoundImage(2,0))
                    .into(productListViewHolder.imageView,
                            new ImageLoadedCallback(productListViewHolder.progressBar) {
                @Override
                public void onSuccess() {
                    if (this.progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            productListViewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.list_photo_default));
        }
        if(divespot.getName() != null) {
             productListViewHolder.title.setText(divespot.getName());
        }
        productListViewHolder.stars.removeAllViews();
        for (int k = 0; k < divespot.getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(0,0,5,0);
            productListViewHolder.stars.addView(iv);
        }
        for (int k = 0; k < 5 - divespot.getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_empty);
            iv.setPadding(0,0,5,0);
            productListViewHolder.stars.addView(iv);
        }
    }

    @Override
    public int getItemCount() {
        if (divespots == null) { return 0; }
        return divespots.size();
    }

    public static class ProductListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView title;
        protected LinearLayout stars;
        protected ProgressBar progressBar;
        private int position;
        private static Context context;
        private final String PRODUCT = "PRODUCT";

        public ProductListViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            v.setOnClickListener(this);
           // productPrice = (TextView) v.findViewById(R.id.product_price);
            imageView = (ImageView) v.findViewById(R.id.product_logo);
            title = (TextView) v.findViewById(R.id.product_title);
            stars = (LinearLayout) v.findViewById(R.id.stars);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, DivePlaceActivity.class);
            i.putExtra(PRODUCT, String.valueOf(divespots.get(getPosition()).getId()));
            DiveSpotDetailsActivity
                    .show(context, String.valueOf(divespots.get(getPosition()).getId()));
        }
    }

    public void setDiveSpots(ArrayList<DiveSpot> diveSpots) {
        this.divespots = diveSpots;
        notifyDataSetChanged();
    }

    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public  ImageLoadedCallback(ProgressBar progBar){
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }



}

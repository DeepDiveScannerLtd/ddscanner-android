package com.ddscanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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
import com.ddscanner.utils.EventTrackerHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lashket on 23.12.15.
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>{

    public static ArrayList<DiveSpot> divespots;
    private Context conText;

    public ProductListAdapter(ArrayList<DiveSpot> divespots, Context conText) {
        this.divespots = divespots;
        this.conText = conText;
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
        productListViewHolder.description.setText(divespot.getDescription());
        productListViewHolder.progressBar.getIndeterminateDrawable().setColorFilter(conText.getResources().getColor(R.color.primary), PorterDuff.Mode.MULTIPLY);
        if (divespot.getImages() != null) {
            Picasso.with(conText).load(divespot.getImages().get(0)).resize(130, 130).centerCrop().into(productListViewHolder.imageView, new ImageLoadedCallback(productListViewHolder.progressBar) {
                @Override
                public void onSuccess() {
                    if (this.progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            productListViewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(conText, R.drawable.list_photo_default));
        }
        if(divespot.getName() != null) {
             productListViewHolder.title.setText(divespot.getName());
        }
        productListViewHolder.stars.removeAllViews();
        for (int k = 0; k < divespot.getRating(); k++) {
            ImageView iv = new ImageView(conText);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(5,0,0,0);
            productListViewHolder.stars.addView(iv);
        }
        for (int k = 0; k < 5 - divespot.getRating(); k++) {
            ImageView iv = new ImageView(conText);
            iv.setImageResource(R.drawable.ic_flag_empty_small);
            iv.setPadding(5,0,0,0);
            productListViewHolder.stars.addView(iv);
        }
    }

    @Override
    public int getItemCount() {
        if (divespots == null) { return 0; }
        return divespots.size();
    }

    public static class ProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView title;
        protected TextView  description;
        protected TextView productPrice;
        protected TextView  price;
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
            description = (TextView) v.findViewById(R.id.product_description);
            stars = (LinearLayout) v.findViewById(R.id.stars);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, DivePlaceActivity.class);
            i.putExtra(PRODUCT, String.valueOf(divespots.get(getPosition()).getId()));
            AppsFlyerLib.getInstance().trackEvent(context,
                    EventTrackerHelper.EVENT_DIVE_SITES_LIST_ITEM_CLICK, new HashMap<String, Object>() {{
                        put(EventTrackerHelper.PARAM_DIVE_SITES_LIST_ITEM_CLICK, divespots.get(getPosition()).getId());
                    }});
            context.startActivity(i);
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

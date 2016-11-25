package com.ddscanner.ui.adapters;

import android.content.Context;
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

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lashket on 23.12.15.
 */
public class DiveSpotsListAdapter
        extends RecyclerView.Adapter<DiveSpotsListAdapter.ProductListViewHolder> {

    private static final String TAG = DiveSpotsListAdapter.class.getSimpleName();

    public ArrayList<DiveSpotShort> divespots;
    private Context context;
    private EventsTracker.SpotViewSource spotViewSource;

    public DiveSpotsListAdapter(ArrayList<DiveSpotShort> divespots, Context context, EventsTracker.SpotViewSource spotViewSource) {
        this.divespots = divespots;
        this.context = context;
        this.spotViewSource = spotViewSource;
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
        DiveSpotShort divespot = new DiveSpotShort();
        divespot = divespots.get(i);
        productListViewHolder.progressBar.getIndeterminateDrawable().
                setColorFilter(ContextCompat.getColor(context, R.color.primary),
                        PorterDuff.Mode.MULTIPLY);
        if (divespot.getImage() != null) {
            String imageAddress = DDScannerApplication.getInstance().getString(R.string.server_api_address) + Constants.IMAGE_PATH_PREVIEW + divespot.getImage();
            Picasso.with(context).load(imageAddress).resize(Math.round(Helpers.convertDpToPixel(130, context)), Math.round(Helpers.convertDpToPixel(130, context))).centerCrop()
                    .transform(new TransformationRoundImage(2, 0))
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
        if (divespot.getName() != null) {
            productListViewHolder.title.setText(divespot.getName());
        }
        productListViewHolder.stars.removeAllViews();
        for (int k = 0; k < divespot.getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(0, 0, 5, 0);
            productListViewHolder.stars.addView(iv);
        }
        for (int k = 0; k < 5 - divespot.getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_empty);
            iv.setPadding(0, 0, 5, 0);
            productListViewHolder.stars.addView(iv);
        }
        productListViewHolder.object.setText(divespot.getObject());
    }

    @Override
    public int getItemCount() {
        if (divespots == null) {
            return 0;
        }
        return divespots.size();
    }

    public class ProductListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView title;
        protected LinearLayout stars;
        protected ProgressBar progressBar;
        protected TextView object;
        private int position;
        private Context context;
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
            object = (TextView) v.findViewById(R.id.object);
        }

        @Override
        public void onClick(View v) {
            DiveSpotDetailsActivity.show(context, String.valueOf(divespots.get(getAdapterPosition()).getId()), spotViewSource);
        }
    }

    public void setDiveSpots(ArrayList<DiveSpotShort> diveSpotShorts) {
        this.divespots = diveSpotShorts;
        notifyDataSetChanged();
    }

    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public ImageLoadedCallback(ProgressBar progBar) {
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

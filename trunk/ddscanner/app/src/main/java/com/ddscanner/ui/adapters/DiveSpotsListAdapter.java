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
import com.ddscanner.ui.views.RatingView;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

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
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, divespot.getImage(), "1")).resize(Math.round(Helpers.convertDpToPixel(130, context)), Math.round(Helpers.convertDpToPixel(130, context))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).placeholder(R.drawable.placeholder_photo_wit_round_corners).error(R.drawable.ds_list_photo_default).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)),0)).into(productListViewHolder.imageView);
        if (divespot.getName() != null) {
            productListViewHolder.title.setText(divespot.getName());
        }
        productListViewHolder.stars.removeAllViews();
        productListViewHolder.stars.setRating(Math.round(divespot.getRating()), R.drawable.ic_list_star_full, R.drawable.ic_list_star_empty);
        productListViewHolder.object.setText(divespot.getObject());
    }

    @Override
    public int getItemCount() {
        if (divespots == null) {
            return 0;
        }
        return divespots.size();
    }

    public class ProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView title;
        protected RatingView stars;
        protected TextView object;
        private int position;
        private Context context;
        private final String PRODUCT = "PRODUCT";

        public ProductListViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            v.setOnClickListener(this);
            imageView = (ImageView) v.findViewById(R.id.product_logo);
            title = (TextView) v.findViewById(R.id.product_title);
            stars = (RatingView) v.findViewById(R.id.stars);
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

}

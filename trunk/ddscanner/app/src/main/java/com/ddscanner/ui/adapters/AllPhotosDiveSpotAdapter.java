package com.ddscanner.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Image;
import com.ddscanner.ui.activities.ImageSliderActivity;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lashket on 11.5.16.
 */
public class AllPhotosDiveSpotAdapter extends RecyclerView.Adapter<AllPhotosDiveSpotAdapter.AllPhotosDIveSpotViewHolder> {

    private String path;
    private ArrayList<Image> images;
    private Activity context;
    private Helpers helpers = new Helpers();

    public AllPhotosDiveSpotAdapter(ArrayList<Image> photos, Activity context, String path) {
        images = photos;
        this.context = context;
        this.path = path;
    }

    @Override
    public void onBindViewHolder(AllPhotosDIveSpotViewHolder holder, int position) {
        Picasso.with(context)
                .load(images.get(position).getName())
                .resize(Math.round(helpers.convertDpToPixel(115, context)),Math.round(helpers.convertDpToPixel(115, context)))
                .centerCrop()
                .into(holder.image);
    }

    @Override
    public AllPhotosDIveSpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_photo_in_photos_activity, parent, false);
        return new AllPhotosDIveSpotViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        if (images == null) {
            return 0;
        }
        return images.size();
    }

    public class AllPhotosDIveSpotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView image;

        public AllPhotosDIveSpotViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            image = (ImageView) v.findViewById(R.id.image);
        }

        @Override
        public void onClick(View v) {
            EventsTracker.trackDiveSpotPhotosView();
            ImageSliderActivity.showForResult(context, images, getAdapterPosition(), path, ActivitiesRequestCodes.PHOTOS_ACTIVITY_REQUEST_CODE_SLIDER);
        }
    }

}

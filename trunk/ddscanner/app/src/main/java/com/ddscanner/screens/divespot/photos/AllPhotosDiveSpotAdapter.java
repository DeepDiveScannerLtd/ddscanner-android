package com.ddscanner.screens.divespot.photos;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.screens.photo.slider.ImageSliderActivity;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lashket on 11.5.16.
 */
public class AllPhotosDiveSpotAdapter extends RecyclerView.Adapter<AllPhotosDiveSpotAdapter.AllPhotosDIveSpotViewHolder> {

    private String path;
    private ArrayList<DiveSpotPhoto> images;
    private Activity context;
    private String diveSpotId;
    private PhotoOpenedSource photoOpenedSource;

    public AllPhotosDiveSpotAdapter(ArrayList<DiveSpotPhoto> photos, Activity context, String diveSpotId, PhotoOpenedSource photoOpenedSource) {
        images = photos;
        this.context = context;
        this.diveSpotId = diveSpotId;
        this.photoOpenedSource = photoOpenedSource;
    }

    @Override
    public void onBindViewHolder(AllPhotosDIveSpotViewHolder holder, int position) {
        Picasso.with(context)
                .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, images.get(position).getId(), "2"))
             //   .placeholder(R.drawable.list_photo_default)
                .resize(Math.round(Helpers.convertDpToPixel(115, context)), Math.round(Helpers.convertDpToPixel(115, context)))
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
            ImageSliderActivity.showForResult(context, images, getAdapterPosition(), ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_ACTIVITY_SLIDER, photoOpenedSource);
        }
    }

}

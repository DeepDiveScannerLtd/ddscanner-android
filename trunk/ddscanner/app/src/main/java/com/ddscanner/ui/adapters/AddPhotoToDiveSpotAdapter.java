package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddPhotoToDiveSpotAdapter extends RecyclerView.Adapter<AddPhotoToDiveSpotAdapter.AddPhotoToDiveSpotViewHolder> {

    private String path;
    private ArrayList<String> images;
    private Context context;

    public AddPhotoToDiveSpotAdapter(ArrayList<String> photos, Context context) {
        images = photos;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(AddPhotoToDiveSpotViewHolder holder, int position) {
        Picasso.with(context)
                .load(images.get(position))
                .resize(Math.round(Helpers.convertDpToPixel(115, context)), Math.round(Helpers.convertDpToPixel(115, context)))
                .centerCrop()
                .into(holder.image);
    }

    @Override
    public AddPhotoToDiveSpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_photo_in_photos_activity, parent, false);
        return new AddPhotoToDiveSpotViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class AddPhotoToDiveSpotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView image;

        public AddPhotoToDiveSpotViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            image = (ImageView) v.findViewById(R.id.image);
        }

        @Override
        public void onClick(View v) {
            EventsTracker.trackDiveSpotPhotosView();
        }
    }

}


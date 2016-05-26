package com.ddscanner.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.activities.ImageSliderActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewPhotosAdapter extends RecyclerView.Adapter<ReviewPhotosAdapter.ReviewPhotosAdapterViewHolder> {

    private static final String TAG = ReviewPhotosAdapter.class.getName();

    public ArrayList<String> photos;
    public String reviewId;
    public String path;
    public Context context;
    public Helpers helpers = new Helpers();

    public ReviewPhotosAdapter(ArrayList<String> photos, Context context, String path) {
        this.photos = photos;
        this.context = context;
        this.path = path;

        helpers.appendImagesWithPath(photos, path);
    }

    @Override
    public ReviewPhotosAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.reviews_photo_item, parent, false);
        return new ReviewPhotosAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewPhotosAdapterViewHolder holder, int position) {
        if(position == 4) {
            holder.morePhotos.setText("+" + String.valueOf(8 - 4));
            holder.morePhotos.setVisibility(View.VISIBLE);
        }
        Picasso.with(context).load(photos.get(position)).resize(60,60).centerCrop()
                .transform(new TransformationRoundImage(2,0)).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        if (photos.size() > 5) {
            return 5;
        }
        return photos.size();
    }

    public class ReviewPhotosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView photo;
        protected TextView morePhotos;

        public ReviewPhotosAdapterViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            photo = (ImageView) v.findViewById(R.id.image);
            morePhotos = (TextView) v.findViewById(R.id.number_of_more_images);
        }

        @Override
        public void onClick(View v) {
            ImageSliderActivity.show(context, photos, getAdapterPosition());
        }
    }

}

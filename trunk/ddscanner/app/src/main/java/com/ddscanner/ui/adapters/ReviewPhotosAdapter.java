package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.activities.ShowImageActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewPhotosAdapter extends RecyclerView.Adapter<ReviewPhotosAdapter.ReviewPhotosAdapterViewHolder> {

    public static ArrayList<String> photos;
    public static String path;
    public static Context context;

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
    }

    @Override
    public int getItemCount() {
     /*   if (photos.size() > 5) {
            return 5;
        }*/
        return 8;
    }

    public static class ReviewPhotosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView photo;
        protected TextView morePhotos;

        public ReviewPhotosAdapterViewHolder(View v) {
            super(v);
            photo = (ImageView) v.findViewById(R.id.image);
            morePhotos = (TextView) v.findViewById(R.id.number_of_more_images);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
package com.ddscanner.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.ShowSliderForReviewImagesEvent;
import com.ddscanner.ui.activities.ImageSliderActivity;
import com.ddscanner.ui.activities.ReviewImageSliderActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.ImageLoadedCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewPhotosAdapter extends RecyclerView.Adapter<ReviewPhotosAdapter.ReviewPhotosAdapterViewHolder> {

    private static final String TAG = ReviewPhotosAdapter.class.getName();

    public ArrayList<String> photos;
    public String reviewId;
    public String path;
    public Context context;
    public boolean isSelfPhotos;

    public ReviewPhotosAdapter(ArrayList<String> photos, Context context, String path, boolean isSelfPhotos) {
        this.photos = photos;
        this.context = context;
        this.path = path;
        this.isSelfPhotos =  isSelfPhotos;

        Helpers.appendImagesWithPath(photos, path);
    }

    @Override
    public ReviewPhotosAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.reviews_photo_item, parent, false);
        return new ReviewPhotosAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReviewPhotosAdapterViewHolder holder, int position) {
        if(position == 4) {
            holder.morePhotos.setText("+" + String.valueOf(8 - 4));
            holder.morePhotos.setVisibility(View.VISIBLE);
        }
        Picasso.with(context).load(photos.get(position)).transform(new TransformationRoundImage(2,0)).resize(70,70).centerCrop().into(holder.photo,
                new ImageLoadedCallback(holder.progressBar){
                    @Override
                    public void onSuccess() {
                        if (holder.progressBar != null) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
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
        private ProgressBar progressBar;

        public ReviewPhotosAdapterViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            photo = (ImageView) v.findViewById(R.id.image);
            morePhotos = (TextView) v.findViewById(R.id.number_of_more_images);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
          // ReviewImageSliderActivity.show(context, photos, getAdapterPosition());
            DDScannerApplication.bus.post(new ShowSliderForReviewImagesEvent(isSelfPhotos, photos, getAdapterPosition()));
        }
    }

}

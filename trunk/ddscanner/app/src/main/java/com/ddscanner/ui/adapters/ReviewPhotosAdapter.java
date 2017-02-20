package com.ddscanner.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.events.ShowSliderForReviewImagesEvent;
import com.ddscanner.ui.activities.PhotosGalleryActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.ImageLoadedCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewPhotosAdapter extends RecyclerView.Adapter<ReviewPhotosAdapter.ReviewPhotosAdapterViewHolder> {

    private static final String TAG = ReviewPhotosAdapter.class.getName();

    public ArrayList<DiveSpotPhoto> photos;
    public String reviewId;
    public Activity context;
    public boolean isSelfPhotos;
    public int commentPosition;
    private int photosCount;
    private String commentId;

    public ReviewPhotosAdapter(ArrayList<DiveSpotPhoto> photos, Activity context, boolean isSelfPhotos, int commentPosition, int photosCount, String commentId) {
        this.photos = photos;
        this.context = context;
        this.isSelfPhotos =  isSelfPhotos;
        this.commentPosition = commentPosition;
        this.photosCount = photosCount;
        this.commentId = commentId;
       // Helpers.appendImagesWithPath(photos, path);
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
            holder.morePhotos.setText("+" + String.valueOf(photosCount - 4));
            holder.morePhotos.setVisibility(View.VISIBLE);
        }
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photos.get(position).getId(), "1")).transform(new TransformationRoundImage(2,0)).resize(70,70).centerCrop().into(holder.photo,
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
          // ReviewImageSliderActivity.showForResult(context, photos, getAdapterPosition());
            if (photosCount > 5) {
                PhotosGalleryActivity.showForResult(commentId, context, PhotoOpenedSource.REVIEW, null, -1);
            } else {
                DDScannerApplication.bus.post(new ShowSliderForReviewImagesEvent(isSelfPhotos, photos, getAdapterPosition(), commentPosition));
            }
        }
    }

}

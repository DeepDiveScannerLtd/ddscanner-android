package com.ddscanner.screens.user.likes;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ItemPhotoLikedBinding;
import com.ddscanner.databinding.ItemReviewLikedLikesActivityBinding;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.LikeEntity;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.screens.photo.slider.ImageSliderActivity;
import com.ddscanner.screens.reiews.list.ReviewsActivity;
import com.ddscanner.screens.user.profile.UserProfileActivity;

import java.util.ArrayList;

public class LikesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PHOTO = 0;
    private static final int TYPE_REVIEW = 1;

    private Activity context;
    private ArrayList<LikeEntity> likes = new ArrayList<>();

    public LikesListAdapter(ArrayList<LikeEntity> likes, Activity context) {
        this.likes = likes;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_PHOTO:
                ItemPhotoLikedBinding photoLikedBinding = ItemPhotoLikedBinding.inflate(inflater, parent, false);
                return new PhotoLikeItemViewHolder(photoLikedBinding.getRoot());
            case TYPE_REVIEW:
                ItemReviewLikedLikesActivityBinding itemReviewLikedLikesActivityBinding = ItemReviewLikedLikesActivityBinding.inflate(inflater, parent, false);
                return new ReviewLikeItemViewHolder(itemReviewLikedLikesActivityBinding.getRoot());
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (likes.get(position).getType()) {
            case REVIEW:
                ReviewLikeItemViewHolder reviewHolder = (ReviewLikeItemViewHolder) holder;
                reviewHolder.binding.setLikeViewModel(new LikeReviewViewModel(likes.get(position)));
                break;
            case PHOTO:
                PhotoLikeItemViewHolder photoHolder = (PhotoLikeItemViewHolder) holder;
                photoHolder.binding.setLikeViewModel(new LikePhotoItemViewModel(likes.get(position)));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return likes.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (likes.get(position).getType()) {
            case REVIEW:
                return TYPE_REVIEW;
            case PHOTO:
                return TYPE_PHOTO;
            default:
                return -1;
        }
    }

    public class PhotoLikeItemViewHolder extends RecyclerView.ViewHolder {

        ItemPhotoLikedBinding binding;

        PhotoLikeItemViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
            binding.setHandlers(this);
        }

        public void avatarClicked(View view) {
            UserProfileActivity.show(view.getContext(), likes.get(getAdapterPosition()).getUser().getId(), likes.get(getAdapterPosition()).getUser().getType());
        }

        public void photoClicked(View view) {
            DDScannerApplication.getInstance().getDiveSpotPhotosContainer().setPhotos(likes.get(getAdapterPosition()).getPhoto());
            ImageSliderActivity.showForResult(context, new ArrayList<DiveSpotPhoto>(), 0, 0, PhotoOpenedSource.PROFILE, "0");
        }

        public void showDiveSpot(View view) {
            DiveSpotDetailsActivity.show(view.getContext(), String.valueOf(likes.get(getAdapterPosition()).getDiveSpot().getId()), EventsTracker.SpotViewSource.FROM_PROFILE_CREATED);
        }

    }

    public class ReviewLikeItemViewHolder extends RecyclerView.ViewHolder {

        ItemReviewLikedLikesActivityBinding binding;

        ReviewLikeItemViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
            binding.setHandlers(this);
        }

        public void avatarClicked(View view) {
            UserProfileActivity.show(view.getContext(), likes.get(getAdapterPosition()).getUser().getId(), likes.get(getAdapterPosition()).getUser().getType());
        }

        public void contentClicked(View view) {
            ReviewsActivity.showForResult(context, likes.get(getAdapterPosition()).getReview().getId(), -1, ReviewsOpenedSource.SINGLE);
        }

    }


}

package com.ddscanner.screens.user.dislikes;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.databinding.ItemDislikedReviewBinding;
import com.ddscanner.entities.LikeEntity;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.screens.reiews.list.ReviewsActivity;
import com.ddscanner.screens.user.profile.UserProfileActivity;

import java.util.ArrayList;

public class DislikesListAdapter extends RecyclerView.Adapter<DislikesListAdapter.DislikedItemViewHolder> {

    private ArrayList<LikeEntity> likes = new ArrayList<>();
    private Activity context;

    public DislikesListAdapter(ArrayList<LikeEntity> likes, Activity context) {
        this.likes = likes;
        this.context = context;
    }

    @Override
    public DislikedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDislikedReviewBinding binding = ItemDislikedReviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DislikedItemViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(DislikedItemViewHolder holder, int position) {
        holder.binding.setLikeViewModel(new DislikeReviewItemViewModel(likes.get(position)));
    }

    @Override
    public int getItemCount() {
        return likes.size();
    }

    public class DislikedItemViewHolder extends RecyclerView.ViewHolder {

        ItemDislikedReviewBinding binding;

        DislikedItemViewHolder(View view) {
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

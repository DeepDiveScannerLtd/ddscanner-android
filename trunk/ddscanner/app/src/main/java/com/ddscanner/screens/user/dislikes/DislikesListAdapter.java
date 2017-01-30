package com.ddscanner.screens.user.dislikes;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.databinding.ItemDislikedReviewBinding;
import com.ddscanner.entities.LikeEntity;

import java.util.ArrayList;

public class DislikesListAdapter extends RecyclerView.Adapter<DislikesListAdapter.DislikedItemViewHolder> {

    private ArrayList<LikeEntity> likes = new ArrayList<>();

    public DislikesListAdapter(ArrayList<LikeEntity> likes) {
        this.likes = likes;
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

    protected class DislikedItemViewHolder extends RecyclerView.ViewHolder {

        ItemDislikedReviewBinding binding;

        DislikedItemViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

    }

}

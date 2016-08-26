package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;

/**
 * Created by Lenovo on 26.08.2016.
 */
public class SelfReviewsListAdapter extends RecyclerView.Adapter<SelfReviewsListAdapter.SelfReviewsListViewHolder>{

    private ArrayList<Comment> comments;
    private Context context;
    private String diveSpotPath;
    private Helpers helpers = new Helpers();

    public SelfReviewsListAdapter(ArrayList<Comment> comments, Context context, String diveSpotPath) {
        this.comments = comments;
        this.context = context;
        this.diveSpotPath = diveSpotPath;
    }

    @Override
    public SelfReviewsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.review_item, parent, false);
        return new SelfReviewsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelfReviewsListViewHolder holder, int position) {
        if (comments.get(holder.getAdapterPosition()).getImages() != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.photos.setNestedScrollingEnabled(false);
            holder.photos.setHasFixedSize(false);
            holder.photos.setLayoutManager(layoutManager);
            holder.photos.setAdapter(new ReviewPhotosAdapter((ArrayList<String>) comments.get(holder.getAdapterPosition()).getImages(), context, diveSpotPath));
        } else {
            holder.photos.setAdapter(null);
        }
        holder.user_name.setText(comments.get(holder.getAdapterPosition()).getDiveSpotName());
        holder.user_review.setText(comments.get(holder.getAdapterPosition()).getComment());
        holder.likesCount.setText(comments.get(holder.getAdapterPosition()).getLikes());
        holder.dislikesCount.setText(comments.get(holder.getAdapterPosition()).getDislikes());
        if (comments.get(position).getDiveSpotImage() == null) {
            holder.user_avatar.setImageResource(R.drawable.list_photo_default);
        } else {
            Picasso.with(context)
                    .load(diveSpotPath + comments.get(position).getDiveSpotImage())
                    .resize(Math.round(helpers.convertDpToPixel(40, context)),Math.round(helpers.convertDpToPixel(40, context)))
                    .centerCrop()
                    .transform(new TransformationRoundImage(4,0))
                    .into(holder.user_avatar);
        }
        for (int k = 0; k < Integer.parseInt(comments.get(holder.getAdapterPosition()).getRating()); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(0, 0, 5, 0);
            holder.rating.addView(iv);
        }
        for (int k = 0; k < 5 - Integer.parseInt(comments.get(holder.getAdapterPosition()).getRating()); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_empty);
            iv.setPadding(0, 0, 5, 0);
            holder.rating.addView(iv);
        }
        if (comments.get(position).getDate() != null && !comments.get(position).getDate().isEmpty()) {
            holder.date.setText(helpers.getCommentDate(comments.get(position).getDate()));
        }
    }

    @Override
    public int getItemCount() {
        if (comments == null) {
            return 0;
        }
        return comments.size();
    }

    public class SelfReviewsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView user_avatar;
        private LinearLayout rating;
        private TextView user_name;
        private TextView user_review;
        private RecyclerView photos;
        private LinearLayout like;
        private LinearLayout dislike;
        private TextView likesCount;
        private TextView dislikesCount;
        private ImageView likeImage;
        private ImageView dislikeImage;
        private LinearLayout stars;
        private TextView date;
        private ImageView menu;

        public SelfReviewsListViewHolder(View v) {
            super(v);

            menu = (ImageView) v.findViewById(R.id.overflow);
            date = (TextView) v.findViewById(R.id.date);
            user_avatar = (ImageView) v.findViewById(R.id.user_avatar);
            rating = (LinearLayout) v.findViewById(R.id.stars);
            user_name = (TextView) v.findViewById(R.id.user_name);
            user_review = (TextView) v.findViewById(R.id.review);
            photos = (RecyclerView) v.findViewById(R.id.review_photos_rc);
            like = (LinearLayout) v.findViewById(R.id.like_layout);
            dislike = (LinearLayout) v.findViewById(R.id.dislike_layout);
            likesCount = (TextView) v.findViewById(R.id.likes_count);
            dislikesCount = (TextView) v.findViewById(R.id.dislikes_count);
            likeImage = (ImageView) v.findViewById(R.id.likes_image);
            dislikeImage = (ImageView) v.findViewById(R.id.dislikes_image);
            user_avatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.user_avatar) {
                DiveSpotDetailsActivity.show(context, comments.get(getAdapterPosition()).getDiveSpotId(), null);
            }
        }
    }

}

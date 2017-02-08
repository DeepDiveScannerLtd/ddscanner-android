package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.CommentOld;
import com.ddscanner.entities.SelfCommentEntity;
import com.ddscanner.events.DeleteCommentEvent;
import com.ddscanner.events.EditCommentEvent;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Lenovo on 26.08.2016.
 */
public class SelfReviewsListAdapter extends RecyclerView.Adapter<SelfReviewsListAdapter.SelfReviewsListViewHolder>{

    private ArrayList<SelfCommentEntity> comments;
    private Context context;

    public SelfReviewsListAdapter(ArrayList<SelfCommentEntity> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public SelfReviewsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.review_item, parent, false);
        return new SelfReviewsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SelfReviewsListViewHolder holder, final int position) {
        holder.rating.removeAllViews();
        final SelfCommentEntity comment = comments.get(position);
        if (comment.getPhotos() != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.photos.setNestedScrollingEnabled(false);
            holder.photos.setHasFixedSize(false);
            holder.photos.setLayoutManager(layoutManager);
            holder.photos.setAdapter(new ReviewPhotosAdapter(comment.getPhotos(), context, false, holder.getAdapterPosition()));
        } else {
            holder.photos.setAdapter(null);
        }
        holder.user_name.setText(comment.getDiveSpot().getName());
        holder.user_review.setText(comment.getReview());
        holder.likesCount.setText(Helpers.formatLikesCommentsCountNumber(comment.getLikes()));
        holder.dislikesCount.setText(Helpers.formatLikesCommentsCountNumber(comment.getDislikes()));
        if (comment.getDiveSpot().getImage() == null) {
            holder.user_avatar.setImageResource(R.drawable.list_photo_default);
        } else {
            Picasso.with(context)
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, comment.getDiveSpot().getImage(), "1"))
                    .resize(Math.round(Helpers.convertDpToPixel(40, context)),Math.round(Helpers.convertDpToPixel(40, context)))
                    .centerCrop()
                    .placeholder(R.drawable.list_photo_default)
                    .transform(new TransformationRoundImage(4,0))
                    .into(holder.user_avatar);
        }
        for (int k = 0; k < comment.getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(0, 0, 5, 0);
            holder.rating.addView(iv);
        }
        for (int k = 0; k < 5 - comment.getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_empty);
            iv.setPadding(0, 0, 5, 0);
            holder.rating.addView(iv);
        }
        if (comment.getDate() != null && !comment.getDate().isEmpty()) {
            holder.date.setText(Helpers.getCommentDate(comment.getDate()));
        }
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.menu, Integer.parseInt(comment.getId()), comments.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (comments == null) {
            return 0;
        }
        return comments.size();
    }

    private void showPopupMenu(View view, int commentId, SelfCommentEntity selfCommentEntity) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_comment, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener(commentId, selfCommentEntity));
        popup.show();
    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int commentId;
        private SelfCommentEntity selfCommentEntity;

        public MenuItemClickListener(int commentId, SelfCommentEntity selfCommentEntity) {
            this.commentId = commentId;
            this.selfCommentEntity = selfCommentEntity;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.comment_edit:
                    DDScannerApplication.bus.post(new EditCommentEvent(selfCommentEntity));
                    return true;
                case R.id.comment_delete:
                    DDScannerApplication.bus.post(new DeleteCommentEvent(commentId));
                    return true;
                default:
            }
            return false;
        }
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
        private TextView expand;

        public SelfReviewsListViewHolder(View v) {
            super(v);
            expand = (TextView) v.findViewById(R.id.button_toggle);
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
                DiveSpotDetailsActivity.show(context, String.valueOf(comments.get(getAdapterPosition()).getDiveSpot().getId()), null);
            }
        }
    }

}

package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.CommentEntity;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.events.DeleteCommentEvent;
import com.ddscanner.events.DislikeCommentEvent;
import com.ddscanner.events.EditCommentEvent;
import com.ddscanner.events.LikeCommentEvent;
import com.ddscanner.events.ReportCommentEvent;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewsListViewHolder> {

    private static final String TAG = ReviewsListAdapter.class.getSimpleName();
    private ArrayList<CommentEntity> comments = new ArrayList<>();
    private Context context;
    private boolean isAdapterSet = false;

    public ReviewsListAdapter(ArrayList<CommentEntity> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public ReviewsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.review_item, viewGroup, false);
        Log.i(TAG, "Try showing content");
        return new ReviewsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewsListViewHolder reviewsListViewHolder, final int i) {
        boolean isLiked;
        boolean isDisliked;
        final CommentEntity commentEntity = comments.get(reviewsListViewHolder.getAdapterPosition());
        reviewsListViewHolder.rating.removeAllViews();
        reviewsListViewHolder.date.setText("");
        reviewsListViewHolder.dislikeImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_review_dislike_empty));
        reviewsListViewHolder.likeImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_review_like_empty));
       // reviewsListViewHolder.photos.setVisibility(View.GONE);
        reviewsListViewHolder.expand.setText("");
        reviewsListViewHolder.dislikesCount.setText("");
        reviewsListViewHolder.likesCount.setText("");
        isLiked = commentEntity.getComment().isLike();
        isDisliked = commentEntity.getComment().isDislike();
        if (isLiked) {
            reviewsListViewHolder.likeImage.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                    context, R.drawable.ic_like_review));
        }
        if (isDisliked) {
            reviewsListViewHolder.dislikeImage.setImageDrawable(AppCompatDrawableManager.get()
                    .getDrawable(context, R.drawable.ic_review_dislike));
        }
        Log.i(TAG, reviewsListViewHolder.toString());
        if (commentEntity.getComment().getPhotos() != null) {
       //     reviewsListViewHolder.photos.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            reviewsListViewHolder.photos.setNestedScrollingEnabled(false);
            reviewsListViewHolder.photos.setHasFixedSize(false);
            reviewsListViewHolder.photos.setLayoutManager(layoutManager);
            if (commentEntity.getAuthor().getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
                reviewsListViewHolder.photos.setAdapter(new ReviewPhotosAdapter(commentEntity.getComment().getPhotos(), context, true, reviewsListViewHolder.getAdapterPosition()));
            } else {
                reviewsListViewHolder.photos.setAdapter(new ReviewPhotosAdapter(commentEntity.getComment().getPhotos(), context, false, reviewsListViewHolder.getAdapterPosition()));
            }
        } else {
            reviewsListViewHolder.photos.setAdapter(null);
        }
        if (!isLiked && !commentEntity.getAuthor().getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
            reviewsListViewHolder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DDScannerApplication.bus.post(new LikeCommentEvent(reviewsListViewHolder.getAdapterPosition()));
                }
            });
        }
        if (!isDisliked && !commentEntity.getAuthor().getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
            reviewsListViewHolder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DDScannerApplication.bus.post(new DislikeCommentEvent(reviewsListViewHolder.getAdapterPosition()));
                }
            });
        }
        if (commentEntity.getAuthor().getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
            reviewsListViewHolder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(reviewsListViewHolder.menu, Integer.parseInt(commentEntity.getComment().getId()), comments.get(reviewsListViewHolder.getAdapterPosition()));
                }
            });
        } else {
            reviewsListViewHolder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showReportMenu(reviewsListViewHolder.menu, Integer.parseInt(commentEntity.getComment().getId()), comments.get(reviewsListViewHolder.getAdapterPosition()));
                }
            });
        }

        reviewsListViewHolder.user_name.setText(commentEntity.getAuthor().getName());
        reviewsListViewHolder.user_review.setText(commentEntity.getComment().getReview());
        reviewsListViewHolder.likesCount.setText(Helpers.formatLikesCommentsCountNumber(commentEntity.getComment().getLikes()));
        reviewsListViewHolder.dislikesCount.setText(Helpers.formatLikesCommentsCountNumber(commentEntity.getComment().getDislikes()));
        isAdapterSet = true;

        if (comments.get(reviewsListViewHolder.getAdapterPosition()).getAuthor().getPhoto() != null) {
            Picasso.with(context)
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, commentEntity.getAuthor().getPhoto(), "1")).resize(Math.round(Helpers.convertDpToPixel(40, context)), Math.round(Helpers.convertDpToPixel(40, context)))
                    .transform(new CropCircleTransformation())
                    .centerCrop()
                    .placeholder(R.drawable.avatar_profile_default)
                    .into(reviewsListViewHolder.user_avatar);
            reviewsListViewHolder.rating.removeAllViews();
        } else {
            reviewsListViewHolder.user_avatar.setImageResource(R.drawable.avatar_profile_default);
        }
        for (int k = 0; k < Integer.parseInt(commentEntity.getComment().getRating()); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(0, 0, 5, 0);
            reviewsListViewHolder.rating.addView(iv);
        }
        for (int k = 0; k < 5 - Integer.parseInt(commentEntity.getComment().getRating()); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_empty);
            iv.setPadding(0, 0, 5, 0);
            reviewsListViewHolder.rating.addView(iv);
        }
        if (commentEntity.getComment().getDate() != null && !commentEntity.getComment().getDate().isEmpty()) {
            reviewsListViewHolder.date.setText(Helpers.getCommentDate(commentEntity.getComment().getDate()));
        }
        if (commentEntity.getComment().getSealifes() != null) {
            reviewsListViewHolder.sealifesLayout.setVisibility(View.VISIBLE);
            LinearLayoutManager sealifeLayoutManager = new LinearLayoutManager(context);
            sealifeLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            reviewsListViewHolder.sealifesList.setLayoutManager(sealifeLayoutManager);
            reviewsListViewHolder.sealifesList.setAdapter(new SealifeReviewListAdapter(commentEntity.getComment().getSealifes(), context));
        } else {
            reviewsListViewHolder.sealifesLayout.setVisibility(View.GONE);
        }
    }

    public void commentLiked(int position) {
        if (comments.get(position).getComment().isDislike()) {
            comments.get(position).getComment().setDislikes(String.valueOf(Integer.parseInt(comments.get(position).getComment().getDislikes()) - 1));
            comments.get(position).getComment().setDislike(false);
        }
        comments.get(position).getComment().setLikes(String.valueOf(Integer.parseInt(comments.get(position).getComment().getLikes()) + 1));
        comments.get(position).getComment().setLike(true);
        notifyItemChanged(position);
    }

    public void commentDisliked(int position) {
        if (comments.get(position).getComment().isLike()) {
            comments.get(position).getComment().setLikes(String.valueOf(Integer.parseInt(comments.get(position).getComment().getLikes()) - 1));
            comments.get(position).getComment().setLike(false);
        }
        comments.get(position).getComment().setDislikes(String.valueOf(Integer.parseInt(comments.get(position).getComment().getDislikes()) + 1));
        comments.get(position).getComment().setDislike(true);
        notifyItemChanged(position);
    }

    public void deleteComment(String id) {
        for (CommentEntity comment : comments) {
            if (comment.getComment().getId().equals(id)) {
                int i = comments.indexOf(comment);
                comments.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public ArrayList<CommentEntity> getCommentsList() {
        return comments;
    }

    private void showPopupMenu(View view, int commentId, CommentEntity CommentEntity) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_comment, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener(commentId, CommentEntity));
        popup.show();
    }

    private void showReportMenu(View view, int commentId, CommentEntity CommentEntity) {
        // inflate menu
        Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_comment_report, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener(commentId, CommentEntity));
        popup.show();
    }

    public void imageDeleted(int commentPosition, ArrayList<DiveSpotPhoto> deletedImages) {
        ArrayList<DiveSpotPhoto> newImagesList =  comments.get(commentPosition).getComment().getPhotos();
        newImagesList.removeAll(deletedImages);
        if (newImagesList.size() == 0) {
            newImagesList = null;
        }
        comments.get(commentPosition).getComment().setPhotos(newImagesList);
        notifyItemChanged(commentPosition);
    }

    @Override
    public int getItemCount() {
        if (comments == null) {
            return 0;
        }
        return comments.size();
    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int commentId;
        private CommentEntity commentEntity;

        public MenuItemClickListener(int commentId, CommentEntity commentEntity) {
            this.commentId = commentId;
            this.commentEntity = commentEntity;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.comment_edit:
                    EventsTracker.trackEditReview();
                    DDScannerApplication.bus.post(new EditCommentEvent( commentEntity.getComment()));
                    return true;
                case R.id.comment_delete:
                    EventsTracker.trackDeleteReview();
                    DDScannerApplication.bus.post(new DeleteCommentEvent(commentId));
                    return true;
                case R.id.comment_report:
                    EventsTracker.trackReviewReport();
                    DDScannerApplication.bus.post(new ReportCommentEvent(String.valueOf(commentId)));
                    return true;
                default:
            }
            return false;
        }
    }
    public class ReviewsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        private boolean isLiked = false;
        private boolean isDisliked = false;
        private LinearLayout sealifesLayout;
        private RecyclerView sealifesList;


        public ReviewsListViewHolder(View v) {
            super(v);
            menu = (ImageView) v.findViewById(R.id.overflow);
            expand = (TextView) v.findViewById(R.id.button_toggle);
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
            sealifesLayout = (LinearLayout) v.findViewById(R.id.sealifes_layout);
            sealifesList = (RecyclerView) v.findViewById(R.id.sealifes_list);
            user_avatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.user_avatar:
                    EventsTracker.trackReviewerProfileView();
                    UserProfileActivity.show(context, comments.get(getAdapterPosition()).getAuthor().getId(), comments.get(getAdapterPosition()).getAuthor().getType());
                    break;
                case R.id.like_layout:
                    if (!isLiked) {
                    }
                    break;
                case R.id.dislike_layout:
                    if (!isDisliked) {
                    }
                    break;
            }
        }
    }

}

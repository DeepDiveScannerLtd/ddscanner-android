package com.ddscanner.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
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
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.ui.views.DislikeView;
import com.ddscanner.ui.views.LikeView;
import com.ddscanner.ui.views.RatingView;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewsListViewHolder> {

    private static final String TAG = ReviewsListAdapter.class.getSimpleName();
    private ArrayList<CommentEntity> comments = new ArrayList<>();
    private Activity context;
    private boolean isAdapterSet = false;
    private String commentAuthorId;
    private String userServerId;
    private SharedPreferenceHelper.UserType userType;

    public ReviewsListAdapter(ArrayList<CommentEntity> comments, Activity context, String userId, String userServerId) {
        this.comments = comments;
        this.context = context;
        this.commentAuthorId = userId;
        this.userServerId = userServerId;
        this.userType = SharedPreferenceHelper.getActiveUserType();
    }

    @Override
    public ReviewsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.divespot_review_item, viewGroup, false);
        Log.i(TAG, "Try showing content");
        return new ReviewsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewsListViewHolder reviewsListViewHolder, final int i) {
        boolean isLiked;
        boolean isDisliked;
        String userId = "";
        final CommentEntity commentEntity = comments.get(reviewsListViewHolder.getAdapterPosition());
        switch (commentEntity.getReviewType()) {
            case USER:
                userId = commentAuthorId;
                break;
            case DIVESPOT:
                userId = commentEntity.getAuthor().getId();
                break;
        }

        reviewsListViewHolder.rating.removeAllViews();
        reviewsListViewHolder.date.setText("");
        reviewsListViewHolder.photos.setVisibility(View.GONE);
        reviewsListViewHolder.expand.setText("");
        isLiked = commentEntity.getComment().isLike();
        isDisliked = commentEntity.getComment().isDislike();
        reviewsListViewHolder.likeView.setLikeValues(isLiked, commentEntity.getComment().getLikes());
        reviewsListViewHolder.dislikeView.setDisikeValues(isDisliked, commentEntity.getComment().getDislikes());
        Log.i(TAG, reviewsListViewHolder.toString());
        if (commentEntity.getComment().getPhotos() != null) {
            reviewsListViewHolder.photos.setVisibility(View.VISIBLE);
            reviewsListViewHolder.photos.setAdapter(new ReviewPhotosAdapter(context, commentEntity.getComment().getPhotos(), false, reviewsListViewHolder.getAdapterPosition(), commentEntity.getComment().getPhotosCount(), commentEntity.getComment().getId()));
        }
        reviewsListViewHolder.likeView.setOnClickListener(null);
        reviewsListViewHolder.dislikeView.setOnClickListener(null);
        if (!commentEntity.isRequestSent()) {
            if (!comments.get(i).getComment().isLike() && !userId.equals(userServerId)) {
                reviewsListViewHolder.likeView.setOnClickListener(v -> DDScannerApplication.bus.post(new LikeCommentEvent(reviewsListViewHolder.getAdapterPosition())));
            }
            if (!comments.get(i).getComment().isDislike() && !userId.equals(userServerId)) {
                reviewsListViewHolder.dislikeView.setOnClickListener(v -> DDScannerApplication.bus.post(new DislikeCommentEvent(reviewsListViewHolder.getAdapterPosition())));
            }
        }
        switch (userType) {
            case DIVECENTER:
                reviewsListViewHolder.menu.setVisibility(View.GONE);
                break;
            default:
                reviewsListViewHolder.menu.setVisibility(View.VISIBLE);
                break;
        }
        if (userId.equals(userServerId)) {
            reviewsListViewHolder.menu.setOnClickListener(view -> showPopupMenu(reviewsListViewHolder.menu, Integer.parseInt(commentEntity.getComment().getId()), comments.get(reviewsListViewHolder.getAdapterPosition())));
        } else {
            reviewsListViewHolder.menu.setOnClickListener(view -> showReportMenu(reviewsListViewHolder.menu, Integer.parseInt(commentEntity.getComment().getId()), comments.get(reviewsListViewHolder.getAdapterPosition())));
        }
        reviewsListViewHolder.user_review.setText(commentEntity.getComment().getReview());
        isAdapterSet = true;

        switch (comments.get(reviewsListViewHolder.getAdapterPosition()).getReviewType()) {
            case DIVESPOT:
                Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, commentEntity.getAuthor().getPhoto(), "1")).resize(Math.round(Helpers.convertDpToPixel(40, context)), Math.round(Helpers.convertDpToPixel(40, context))).transform(new CropCircleTransformation()).centerCrop().placeholder(R.drawable.gray_circle_placeholder).error(R.drawable.avatar_profile_default).into(reviewsListViewHolder.user_avatar);
                reviewsListViewHolder.user_name.setText(commentEntity.getAuthor().getName());
                break;
            case USER:
                Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, commentEntity.getDiveSpot().getImage(), "1")).resize(Math.round(Helpers.convertDpToPixel(40, context)), Math.round(Helpers.convertDpToPixel(40, context))).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).centerCrop().placeholder(R.drawable.gray_circle_placeholder).error(R.drawable.avatar_profile_default).into(reviewsListViewHolder.user_avatar);
                reviewsListViewHolder.user_name.setText(commentEntity.getDiveSpot().getName());
                break;
        }
        reviewsListViewHolder.rating.removeAllViews();
        reviewsListViewHolder.rating.setRating(commentEntity.getComment().getRating(), R.drawable.ic_list_star_full, R.drawable.ic_list_star_empty);
        reviewsListViewHolder.date.setText(Helpers.getCommentDate(commentEntity.getComment().getDate()));
        if (commentEntity.getSealifes() != null) {
            reviewsListViewHolder.sealifesLayout.setVisibility(View.VISIBLE);
            reviewsListViewHolder.sealifesList.setAdapter(new SealifeReviewListAdapter(context, commentEntity.getSealifes()));
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
        comments.get(position).setRequestSent(false);
        notifyItemChanged(position);
    }

    public void commentDisliked(int position) {
        if (comments.get(position).getComment().isLike()) {
            comments.get(position).getComment().setLikes(String.valueOf(Integer.parseInt(comments.get(position).getComment().getLikes()) - 1));
            comments.get(position).getComment().setLike(false);
        }
        comments.get(position).getComment().setDislikes(String.valueOf(Integer.parseInt(comments.get(position).getComment().getDislikes()) + 1));
        comments.get(position).getComment().setDislike(true);
        comments.get(position).setRequestSent(false);
        notifyItemChanged(position);
    }

    public void rateReviewRequestStarted(int position) {
        comments.get(position).setRequestSent(true);
        notifyItemChanged(position);
    }

    public void rateReviewFaled(int position) {
        comments.get(position).setRequestSent(false);
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
        ArrayList<DiveSpotPhoto> newImagesList = comments.get(commentPosition).getComment().getPhotos();
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
                    if (commentEntity.getSealifes() == null) {
                        DDScannerApplication.bus.post(new EditCommentEvent(commentEntity.getComment(), false));
                        return true;
                    }
                    DDScannerApplication.bus.post(new EditCommentEvent(commentEntity.getComment(), true));
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
        private RatingView rating;
        private TextView user_name;
        private TextView user_review;
        private RecyclerView photos;
        private RatingView stars;
        private TextView date;
        private ImageView menu;
        private TextView expand;
        private LinearLayout sealifesLayout;
        private RecyclerView sealifesList;
        private LikeView likeView;
        private DislikeView dislikeView;

        public ReviewsListViewHolder(View v) {
            super(v);
            menu = v.findViewById(R.id.overflow);
            expand = v.findViewById(R.id.button_toggle);
            date = v.findViewById(R.id.date);
            user_avatar = v.findViewById(R.id.user_avatar);
            rating = v.findViewById(R.id.stars);
            user_name = v.findViewById(R.id.user_name);
            user_review = v.findViewById(R.id.review);
            photos = v.findViewById(R.id.review_photos_rc);
            sealifesLayout = v.findViewById(R.id.sealifes_layout);
            sealifesList = v.findViewById(R.id.sealifes_list);
            likeView = v.findViewById(R.id.like_layout);
            dislikeView = v.findViewById(R.id.dislike_layout);
            user_avatar.setOnClickListener(this);


            LinearLayoutManager sealifeLayoutManager = new LinearLayoutManager(context);
            sealifeLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            sealifesList.setLayoutManager(sealifeLayoutManager);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
            photos.setNestedScrollingEnabled(false);
            photos.setLayoutManager(gridLayoutManager);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.user_avatar:
                    switch (comments.get(getAdapterPosition()).getReviewType()) {
                        case USER:
                            DiveSpotDetailsActivity.show(context, String.valueOf(comments.get(getAdapterPosition()).getDiveSpot().getId()), EventsTracker.SpotViewSource.FROM_PROFILE_REVIEWS);
                            break;
                        case DIVESPOT:
                            UserProfileActivity.show(context, comments.get(getAdapterPosition()).getAuthor().getId(), comments.get(getAdapterPosition()).getAuthor().getType());
                            break;
                    }
                    break;
                case R.id.like_layout:

                    break;
                case R.id.dislike_layout:

                    break;
            }
        }
    }

}

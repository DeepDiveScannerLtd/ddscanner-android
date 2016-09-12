package com.ddscanner.ui.adapters;

import android.content.Context;
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
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.GeneralError;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.events.DeleteCommentEvent;
import com.ddscanner.events.EditCommentEvent;
import com.ddscanner.events.IsCommentLikedEvent;
import com.ddscanner.events.ReportCommentEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.activities.ForeignProfileActivity;
import com.ddscanner.ui.dialogs.ProfileDialog;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import at.blogc.android.views.ExpandableTextView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewsListViewHolder> {

    private static final String TAG = ReviewsListAdapter.class.getSimpleName();
    private ArrayList<Comment> comments;
    private Context context;
    private String path;
    private boolean isAdapterSet = false;
    private static ProfileDialog profileDialog = new ProfileDialog();
    private Helpers helpers = new Helpers();

    public ReviewsListAdapter(ArrayList<Comment> comments, Context context, String path) {
        this.path = path;
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
        reviewsListViewHolder.rating.removeAllViews();
        isLiked = comments.get(reviewsListViewHolder.getAdapterPosition()).isLike();
        isDisliked = comments.get(reviewsListViewHolder.getAdapterPosition()).isDislike();
        if (isLiked) {
            reviewsListViewHolder.likeImage.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                    context, R.drawable.ic_like_review));
        }
        if (isDisliked) {
            reviewsListViewHolder.dislikeImage.setImageDrawable(AppCompatDrawableManager.get()
                    .getDrawable(context, R.drawable.ic_review_dislike));
        }
        if (comments.get(reviewsListViewHolder.getAdapterPosition()).getImages() != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            reviewsListViewHolder.photos.setNestedScrollingEnabled(false);
            reviewsListViewHolder.photos.setHasFixedSize(false);
            reviewsListViewHolder.photos.setLayoutManager(layoutManager);
            reviewsListViewHolder.photos.setAdapter(new ReviewPhotosAdapter((ArrayList<String>) comments.get(reviewsListViewHolder.getAdapterPosition()).getImages(), context, path));
        } else {
            reviewsListViewHolder.photos.setAdapter(null);
        }

        reviewsListViewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeComment(comments.get(reviewsListViewHolder.getAdapterPosition()).getId(), reviewsListViewHolder.dislikeImage,
                        reviewsListViewHolder.likeImage,
                        reviewsListViewHolder.likesCount,
                        reviewsListViewHolder.dislikesCount,
                        i);
            }
        });

        reviewsListViewHolder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislikeComment(comments.get(reviewsListViewHolder.getAdapterPosition()).getId(), reviewsListViewHolder.dislikeImage,
                        reviewsListViewHolder.likeImage,
                        reviewsListViewHolder.likesCount,
                        reviewsListViewHolder.dislikesCount,
                        i);

            }
        });

        if (comments.get(i).isEdit()) {
            reviewsListViewHolder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(reviewsListViewHolder.menu, Integer.parseInt(comments.get(i).getId()), comments.get(i));
                }
            });
        } else {
            reviewsListViewHolder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showReportMenu(reviewsListViewHolder.menu, Integer.parseInt(comments.get(i).getId()), comments.get(i));
                }
            });
        }

        reviewsListViewHolder.user_name.setText(comments.get(reviewsListViewHolder.getAdapterPosition()).getUser().getName());
        reviewsListViewHolder.user_review.setText(comments.get(reviewsListViewHolder.getAdapterPosition()).getComment());
        reviewsListViewHolder.likesCount.setText(helpers.formatLikesCommentsCountNumber(comments.get(reviewsListViewHolder.getAdapterPosition()).getLikes()));
        reviewsListViewHolder.dislikesCount.setText(helpers.formatLikesCommentsCountNumber(comments.get(reviewsListViewHolder.getAdapterPosition()).getDislikes()));
        isAdapterSet = true;

        if (comments.get(reviewsListViewHolder.getAdapterPosition()).getUser().getPicture() != null) {
            Picasso.with(context)
                    .load(comments.get(reviewsListViewHolder.getAdapterPosition()).getUser().getPicture()).resize(Math.round(helpers.convertDpToPixel(40, context)), Math.round(helpers.convertDpToPixel(40, context)))
                    .transform(new CropCircleTransformation())
                    .centerCrop()
                    .placeholder(R.drawable.avatar_profile_default)
                    .into(reviewsListViewHolder.user_avatar);
            reviewsListViewHolder.rating.removeAllViews();
        } else {
            reviewsListViewHolder.user_avatar.setImageResource(R.drawable.avatar_profile_default);
        }
        for (int k = 0; k < Integer.parseInt(comments.get(reviewsListViewHolder.getAdapterPosition()).getRating()); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(0, 0, 5, 0);
            reviewsListViewHolder.rating.addView(iv);
        }
        for (int k = 0; k < 5 - Integer.parseInt(comments.get(reviewsListViewHolder.getAdapterPosition()).getRating()); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_empty);
            iv.setPadding(0, 0, 5, 0);
            reviewsListViewHolder.rating.addView(iv);
        }
        if (comments.get(i).getDate() != null && !comments.get(i).getDate().isEmpty()) {
            reviewsListViewHolder.date.setText(helpers.getCommentDate(comments.get(i).getDate()));
        }
    }

    private void showPopupMenu(View view, int commentId, Comment comment) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_comment, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener(commentId, comment));
        popup.show();
    }

    private void showReportMenu(View view, int commentId, Comment comment) {
        // inflate menu
        Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_comment_report, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener(commentId, comment));
        popup.show();
    }

    @Override
    public int getItemCount() {
        if (comments == null) {
            return 0;
        }
        return comments.size();
    }

    private void likeUi(ImageView dislikeImage, ImageView likeImage, TextView likesCount, TextView dislikesCount, int position) {
        if (comments.get(position).isDislike()) {
            dislikeImage.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                    context, R.drawable.ic_review_dislike_empty
            ));
            dislikesCount.setText(helpers.formatLikesCommentsCountNumber(String.valueOf(Integer.parseInt(dislikesCount.getText().toString()) - 1)));
        }
        likeImage.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                context, R.drawable.ic_like_review
        ));
        likesCount.setText(helpers.formatLikesCommentsCountNumber(String.valueOf(Integer.parseInt(likesCount.getText().toString()) + 1)));
        comments.get(position).setLikes(likesCount.getText().toString());
        comments.get(position).setDislikes(dislikesCount.getText().toString());
        comments.get(position).setDislike(false);
        comments.get(position).setLike(true);
    }

    private void dislikeUi(ImageView dislikeImage, ImageView likeImage, TextView likesCount, TextView dislikesCount, int position) {
        if (comments.get(position).isLike()) {
            likeImage.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                    context, R.drawable.ic_review_like_empty
            ));
            likesCount.setText(helpers.formatLikesCommentsCountNumber(String.valueOf(Integer.parseInt(likesCount.getText().toString()) - 1)));
        }

        dislikeImage.setImageDrawable(AppCompatDrawableManager.get()
                .getDrawable(context, R.drawable.ic_review_dislike));
        dislikesCount.setText(helpers.formatLikesCommentsCountNumber(String.valueOf(Integer.parseInt(dislikesCount.getText().toString()) + 1)));
        comments.get(position).setLikes(likesCount.getText().toString());
        comments.get(position).setDislikes(dislikesCount.getText().toString());
        comments.get(position).setLike(false);
        comments.get(position).setDislike(true);
    }

    private void likeComment(String id, final ImageView dislikeImage,
                             final ImageView likeImage,
                             final TextView likesCount, final TextView dislikesCount, final int position) {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            DDScannerApplication.bus.post(new ShowLoginActivityIntent());
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().likeComment(
                id, helpers.getRegisterRequest()
        );
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.raw().code() == 200) {
                        EventsTracker.trackCommentLiked();
                        DDScannerApplication.bus.post(new IsCommentLikedEvent());
                        likeUi(dislikeImage, likeImage, likesCount, dislikesCount, position);
                    }
                }
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.raw().code() == 403) {
                        Gson gson = new Gson();
                        GeneralError generalError;
                        generalError = gson.fromJson(responseString, GeneralError.class);
                        Toast toast = Toast.makeText(context, R.string.yoy_cannot_like_review, Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        DDScannerApplication.bus.post(new ShowLoginActivityIntent());
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(context);
            }
        });
    }

    private void dislikeComment(String id, final ImageView dislikeImage,
                                final ImageView likeImage,
                                final TextView likesCount, final TextView dislikesCount, final int position) {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            DDScannerApplication.bus.post(new ShowLoginActivityIntent());
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().dislikeComment(
                id, helpers.getRegisterRequest()
        );
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.raw().code() == 200) {
                        EventsTracker.trackCommentDisliked();
                        DDScannerApplication.bus.post(new IsCommentLikedEvent());
                        dislikeUi(dislikeImage, likeImage, likesCount, dislikesCount, position);
                    }
                }
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.raw().code() == 403) {
                        Gson gson = new Gson();
                        GeneralError generalError;
                        generalError = gson.fromJson(responseString, GeneralError.class);
                        Toast toast = Toast.makeText(context, R.string.you_cannot_dislike_your_review, Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        DDScannerApplication.bus.post(new ShowLoginActivityIntent());
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(context, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(context);
            }
        });
    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int commentId;
        private Comment comment;

        public MenuItemClickListener(int commentId, Comment comment) {
            this.commentId = commentId;
            this.comment = comment;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.comment_edit:
                    EventsTracker.trackEditReview();
                    DDScannerApplication.bus.post(new EditCommentEvent(comment));
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
        private ExpandableTextView user_review;
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


        public ReviewsListViewHolder(View v) {
            super(v);
            menu = (ImageView) v.findViewById(R.id.overflow);
            expand = (TextView) v.findViewById(R.id.button_toggle);
            date = (TextView) v.findViewById(R.id.date);
            user_avatar = (ImageView) v.findViewById(R.id.user_avatar);
            rating = (LinearLayout) v.findViewById(R.id.stars);
            user_name = (TextView) v.findViewById(R.id.user_name);
            user_review = (ExpandableTextView) v.findViewById(R.id.review);
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
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.user_avatar:
                    EventsTracker.trackReviewerProfileView();
                    ForeignProfileActivity.show(context, comments.get(getAdapterPosition()).getUser().getId());
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

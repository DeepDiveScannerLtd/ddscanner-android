package com.ddscanner.ui.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.events.MarkerClickEvent;
import com.ddscanner.events.ShowUserDialogEvent;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.activities.ProfileActivity;
import com.ddscanner.ui.dialogs.ProfileDialog;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewsListViewHolder> {

    private static final String TAG = ReviewsListAdapter.class.getSimpleName();
    private static ArrayList<Comment> comments;
    private static Context context;
    private boolean isAdapterSet = false;
    private static ProfileDialog profileDialog = new ProfileDialog();
    private static RegisterRequest registerRequest = new RegisterRequest();

    public ReviewsListAdapter(ArrayList<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
        registerRequest.setSocial(SharedPreferenceHelper.getSn());
        registerRequest.setToken(SharedPreferenceHelper.getToken());
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
    public void onBindViewHolder(final ReviewsListViewHolder reviewsListViewHolder, int i) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        reviewsListViewHolder.photos.setNestedScrollingEnabled(false);
        reviewsListViewHolder.photos.setHasFixedSize(false);
        reviewsListViewHolder.photos.setLayoutManager(layoutManager);
        reviewsListViewHolder.photos.setAdapter(new ReviewPhotosAdapter());
        reviewsListViewHolder.user_name.setText(comments.get(i).getUser().getName());
        reviewsListViewHolder.user_review.setText(comments.get(i).getComment());
        reviewsListViewHolder.likesCount.setText(comments.get(i).getLikes());
        reviewsListViewHolder.dislikesCount.setText(comments.get(i).getDislikes());
        isAdapterSet = true;

        Picasso.with(context).load(comments.get(i).getUser().getPicture()).resize(40,40).
                transform(new TransformationRoundImage(50,0)).centerCrop().
                into(reviewsListViewHolder.user_avatar);
        reviewsListViewHolder.rating.removeAllViews();
        for (int k = 0; k < Integer.parseInt(comments.get(i).getRating()); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(0,0,5,0);
            reviewsListViewHolder.rating.addView(iv);
        }
        for (int k = 0; k < 5 - Integer.parseInt(comments.get(i).getRating()); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_empty);
            iv.setPadding(0,0,5,0);
            reviewsListViewHolder.rating.addView(iv);
        }
    }

    @Override
    public int getItemCount() {
        if (comments == null) {
            return 0;
        }
        return comments.size();
    }

    public static class ReviewsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        private boolean isLiked = false;
        private boolean isDisliked = false;


        public ReviewsListViewHolder(View v) {
            super(v);
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
            like.setOnClickListener(this);
            dislike.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.user_avatar:
                    DDScannerApplication.bus.post(new ShowUserDialogEvent(
                            comments.get(getAdapterPosition()).getUser())
                    );
                    break;
                case R.id.like_layout:
                    if (!isLiked) {
                        likeComment();
                    }
                    break;
                case R.id.dislike_layout:
                    if (!isDisliked) {
                        dislikeComment();
                    }
                    break;
            }
        }

        private void likeUi() {
            if (isDisliked) {
                dislikeImage.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                        context, R.drawable.ic_review_dislike_empty
                ));
                dislikesCount.setText(String.valueOf(Integer.parseInt(dislikesCount.getText().toString()) - 1));
                isDisliked = false;
            }
            likeImage.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                    context, R.drawable.ic_like_review
            ));
            likesCount.setText(String.valueOf(Integer.parseInt(likesCount.getText().toString()) + 1));
            isLiked = true;
        }

        private void dislikeUi() {
            if (isLiked) {
                likeImage.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                        context, R.drawable.ic_review_like_empty
                ));
                likesCount.setText(String.valueOf(Integer.parseInt(likesCount.getText().toString()) - 1));
                isLiked = false;
            }
            dislikeImage.setImageDrawable(AppCompatDrawableManager.get()
                    .getDrawable(context, R.drawable.ic_review_dislike));
            dislikesCount.setText(String.valueOf(Integer.parseInt(dislikesCount.getText().toString()) + 1));
            isDisliked = true;
        }

        private void likeComment() {
            Call<ResponseBody> call = RestClient.getServiceInstance().likeComment(
                    comments.get(getPosition()).getId(),
                    registerRequest
            );
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.raw().code() == 200) {
                           likeUi();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.i(TAG, "failure");
                }
            });
        }

        private void dislikeComment() {
            Call<ResponseBody> call = RestClient.getServiceInstance().dislikeComment(
                    comments.get(getPosition()).getId(),
                    registerRequest
            );
            Log.i(TAG, comments.get(getPosition()).getId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.raw().code() == 200) {
                            dislikeUi();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

}

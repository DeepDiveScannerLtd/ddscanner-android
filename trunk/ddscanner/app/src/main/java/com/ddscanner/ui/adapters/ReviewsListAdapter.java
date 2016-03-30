package com.ddscanner.ui.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
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
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.ui.activities.ProfileActivity;
import com.ddscanner.ui.views.LayoutedTextView;
import com.ddscanner.utils.EventTrackerHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewsListViewHolder> {

    private static final String TAG = ReviewsListAdapter.class.getSimpleName();
    private static final int MAX_LINES_COUNT = 2;
    private boolean isExpanded = false;
    private ArrayList<Comment> comments;
    private Context context;
    private Button button;

    public ReviewsListAdapter(ArrayList<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public ReviewsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.review_item, viewGroup, false);
        return new ReviewsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewsListViewHolder reviewsListViewHolder, int i) {
        final Comment comment = comments.get(i);
        reviewsListViewHolder.showMore.setVisibility(View.GONE);
        reviewsListViewHolder.user_review.setMaxLines(10000);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.show(context, comment.getUser());
                AppsFlyerLib.getInstance().trackEvent(context,
                        EventTrackerHelper.EVENT_REVIEW_USER_PROFILE_CLICK, new HashMap<String, Object>());
            }
        };
        reviewsListViewHolder.user_review.setOnLayoutListener(new LayoutedTextView.OnLayoutListener() {
            @Override
            public void onLayouted(TextView view) {
                int i = reviewsListViewHolder.user_review.getLineCount();
                Log.i(TAG, reviewsListViewHolder.user_review.toString() + "onLayouted i=" + i + " text: " + reviewsListViewHolder.user_review.getText());
                if (i > 5) {
                    view.setMaxLines(5);
                    view.setText(view.getText());
                    view.setEllipsize(TextUtils.TruncateAt.END);
                    view.forceLayout();
                    reviewsListViewHolder.showMore.setVisibility(View.VISIBLE);
                    reviewsListViewHolder.showMore.setText("Show more");
//                    reviewsListViewHolder.showMore.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (isExpanded) {
//                                reviewsListViewHolder.user_review.setMaxLines(5);
//                                reviewsListViewHolder.user_review.setEllipsize(TextUtils.TruncateAt.END);
//                                reviewsListViewHolder.showMore.setText("Show more");
//                                ObjectAnimator animation = ObjectAnimator.ofInt(
//                                        reviewsListViewHolder.user_review,
//                                        "maxLines",
//                                        5);
//                                animation.setDuration(4000);
//                                animation.start();
//                            } else {
//                                reviewsListViewHolder.user_review.setMaxLines(Integer.MAX_VALUE);
//                                reviewsListViewHolder.user_review.setEllipsize(null);
//                                reviewsListViewHolder.showMore.setText("Less");
//                                ObjectAnimator animation = ObjectAnimator.ofInt(
//                                        reviewsListViewHolder.user_review,
//                                        "maxLines",
//                                        10000);
//                                animation.setDuration(4000);
//                                animation.start();
//                            }
//                            isExpanded = !isExpanded;
//                        }
//                    });
                }
            }
        });
        reviewsListViewHolder.user_review.setText(comment.getComment());

//        reviewsListViewHolder.user_review.post(new Runnable() {
//            @Override
//            public void run() {
//                int i = reviewsListViewHolder.user_review.getLineCount();
//                if (i > 5) {
//                    reviewsListViewHolder.user_review.setMaxLines(5);
//                    reviewsListViewHolder.showMore.setVisibility(View.VISIBLE);
//                    reviewsListViewHolder.showMore.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (isExpanded) {
//                                reviewsListViewHolder.user_review.setMaxLines(5);
//                                reviewsListViewHolder.user_review.setEllipsize(TextUtils.TruncateAt.END);
//                                reviewsListViewHolder.showMore.setText("Show more");
//                                ObjectAnimator animation = ObjectAnimator.ofInt(
//                                        reviewsListViewHolder.user_review,
//                                        "maxLines",
//                                        5);
//                                animation.setDuration(4000);
//                                animation.start();
//                            } else {
//                                reviewsListViewHolder.user_review.setMaxLines(Integer.MAX_VALUE);
//                                reviewsListViewHolder.user_review.setEllipsize(null);
//                                reviewsListViewHolder.showMore.setText("Less");
//                                ObjectAnimator animation = ObjectAnimator.ofInt(
//                                        reviewsListViewHolder.user_review,
//                                        "maxLines",
//                                        10000);
//                                animation.setDuration(4000);
//                                animation.start();
//                            }
//                            isExpanded = !isExpanded;
//                        }
//                    });
//                }
//            }
//        });

        reviewsListViewHolder.user_name.setText(comment.getUser().getName());
        reviewsListViewHolder.user_name.setOnClickListener(onClickListener);
        reviewsListViewHolder.user_avatar.setOnClickListener(onClickListener);
        reviewsListViewHolder.rating.setText(comment.getRating());
        Picasso.with(context).load(comment.getUser().getPicture()).resize(41, 41).centerCrop().into(reviewsListViewHolder.user_avatar);
        Log.i(TAG, "Try showing content");
    }

    @Override
    public int getItemCount() {
        if (comments == null) {
            return 0;
        }
        return comments.size();
    }

    public static class ReviewsListViewHolder extends RecyclerView.ViewHolder {

        private ImageView user_avatar;
        private TextView rating;
        private TextView user_name;
        private LayoutedTextView user_review;
        private TextView showMore;

        public ReviewsListViewHolder(View v) {
            super(v);
            user_avatar = (ImageView) v.findViewById(R.id.user_avatar);
            rating = (TextView) v.findViewById(R.id.rating);
            user_name = (TextView) v.findViewById(R.id.user_name);
            user_review = (LayoutedTextView) v.findViewById(R.id.review);
            showMore = (TextView) v.findViewById(R.id.show_more);
        }
    }

}

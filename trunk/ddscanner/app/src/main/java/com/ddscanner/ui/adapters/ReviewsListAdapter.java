package com.ddscanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewsListViewHolder> {

    private static final String TAG = ReviewsListAdapter.class.getSimpleName();

    private ArrayList<Comment> comments;
    private Context context;
    private String userName;
    private String socialNetwork;

    public ReviewsListAdapter(ArrayList<Comment> comments, Context context) {
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
    public void onBindViewHolder(ReviewsListViewHolder reviewsListViewHolder, int i) {
        Comment comment = comments.get(i);
        reviewsListViewHolder.user_review.setText(comment.getComment());
       // reviewsListViewHolder.user_name.setText(comment.getUser().getName());
     /*  Spanned html = Html.fromHtml("<a href='"+ comment.getUser().getLink() +
                "'>"+comment.getUser().getName() + "</a>");*/
        reviewsListViewHolder.user_name.setMovementMethod(LinkMovementMethod.getInstance());
        reviewsListViewHolder.user_name.setText(comment.getUser().getName());
     //   reviewsListViewHolder.user_name.setText(html);
        reviewsListViewHolder.user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(userName, socialNetwork);
            }
        });
        reviewsListViewHolder.rating.setText(comment.getRating());
        Picasso.with(context).load(comment.getUser().getPicture()).resize(41,41).centerCrop().into(reviewsListViewHolder.user_avatar);
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
        private TextView user_review;

        public ReviewsListViewHolder(View v) {
            super(v);
            user_avatar = (ImageView) v.findViewById(R.id.user_avatar);
            rating = (TextView) v.findViewById(R.id.rating);
            user_name = (TextView) v.findViewById(R.id.user_name);
            user_review = (TextView) v.findViewById(R.id.review);
        }
    }

    private void openLink(String userName, String socialNetwork) {
        switch (socialNetwork) {
            case "tw":
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("twitter://user?screen_name=" + userName));
                    context.startActivity(intent);

                }catch (Exception e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/#!/" + userName)));
                }
                break;
            case "go":
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://plus.google.com/"+ userName + "/"));
                intent.setPackage("com.google.android.apps.plus"); // don't open the browser, make sure it opens in Google+ app
                context.startActivity(intent);
                break;
            case "fb":
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + userName));
                    context.startActivity(intent1);

                }catch(Exception e){
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/" + userName)));
                }
                break;
        }
    }

}

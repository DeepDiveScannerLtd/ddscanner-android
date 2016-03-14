package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Comment;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewsListViewHolder> {

    private static final String TAG = ReviewsListAdapter.class.getSimpleName();

    private ArrayList<Comment> comments;
    private Context context;

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
        Spanned html = Html.fromHtml("<a href='"+ comment.getUser().getLink() +
                "'>"+comment.getUser().getName() + "</a>");
        reviewsListViewHolder.user_name.setMovementMethod(LinkMovementMethod.getInstance());
        reviewsListViewHolder.user_name.setText(html);
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

}

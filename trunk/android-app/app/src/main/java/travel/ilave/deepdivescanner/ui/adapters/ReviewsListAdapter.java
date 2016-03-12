package travel.ilave.deepdivescanner.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import travel.ilave.deepdivescanner.R;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewsListViewHolder> {


    @Override
    public ReviewsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.review_item, viewGroup, false);
        return new ReviewsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsListViewHolder reviewsListViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 3;
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

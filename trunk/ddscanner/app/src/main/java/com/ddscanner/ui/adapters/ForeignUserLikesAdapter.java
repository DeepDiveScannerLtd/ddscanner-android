package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.ForeignUserLike;
import com.ddscanner.entities.UserLikeEntity;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.ui.activities.ForeignProfileActivity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ForeignUserLikesAdapter extends RecyclerView.Adapter<ForeignUserLikesAdapter.ForeignUserLikesViewHolder> {

    private ArrayList<UserLikeEntity> likes;
    private Context context;
    private boolean isLikes;
    private static final int REVIEW_TEXT_MAX_LENGTH = 30;

    public ForeignUserLikesAdapter(Context context, ArrayList<UserLikeEntity> likes, boolean islikes) {
        this.likes = likes;
        this.context = context;
        this.isLikes = islikes;
    }

    @Override
    public ForeignUserLikesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_profile_dislike, parent, false);
        return new ForeignUserLikesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ForeignUserLikesViewHolder holder, int position) {
        UserLikeEntity userLikeEntity = likes.get(position);
        Picasso.with(context)
                .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url,userLikeEntity.getUser().getPhoto(), "1"))
                .resize(Math.round(Helpers.convertDpToPixel(40, context)), Math.round(Helpers.convertDpToPixel(40, context)))
                .transform(new CropCircleTransformation())
                .into(holder.avatar);
        holder.timeAgo.setText(Helpers.getDate(userLikeEntity.getDate()));
        String comment = userLikeEntity.getReview().getReview();

        if (comment.length() > 30) {
            comment = reformatString(comment);
        }
        String mainText = context.getResources().getString(R.string.foreign_user_likes_main_text, userLikeEntity.getDiveSpot().getName(), comment);
        holder.mainText.setText(mainText);

        if (isLikes) {
            holder.whoCreatedAction.setText(context.getResources().getString(R.string.foreign_user_like_by, userLikeEntity.getUser().getName()));
        } else {
            holder.whoCreatedAction.setText(context.getResources().getString(R.string.foreign_user_dislike_by, userLikeEntity.getUser().getName()));
        }
    }

    @Override
    public int getItemCount() {
        if (likes == null) {
            return 0;
        }
        return likes.size();
    }

    private String reformatString(String firstString) {
        firstString = firstString.substring(0, 27);
        firstString = firstString + "...";
        return firstString;
    }

    public class ForeignUserLikesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected ImageView avatar;
        protected TextView mainText;
        protected TextView whoCreatedAction;
        protected TextView timeAgo;
        protected LinearLayout linearLayout;
        protected Context context;

        public ForeignUserLikesViewHolder(View v) {
            super(v);
            context = v.getContext();
            linearLayout = (LinearLayout) v.findViewById(R.id.main_layout);
            avatar = (ImageView) v.findViewById(R.id.avatar);
            mainText = (TextView) v.findViewById(R.id.text);
            whoCreatedAction = (TextView) v.findViewById(R.id.whoDisliked);
            timeAgo = (TextView) v.findViewById(R.id.timeAgo);

            linearLayout.setOnClickListener(this);
            avatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.avatar:
                  //  ForeignProfileActivity.show(context, likes.get(getAdapterPosition()).getUserOld().getId());
                    break;
                case R.id.main_layout:
                  //  DiveSpotDetailsActivity.show(context, String.valueOf(likes.get(getAdapterPosition()).getDiveSpotId()), EventsTracker.SpotViewSource.FROM_LIST);
                    break;
            }
        }
    }

}

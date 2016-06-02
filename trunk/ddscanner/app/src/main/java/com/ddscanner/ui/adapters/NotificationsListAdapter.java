package com.ddscanner.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Notification;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 25.5.16.
 */
public class NotificationsListAdapter
        extends RecyclerView.Adapter<NotificationsListAdapter.NotificationListViewHolder>{

    private List<Notification> notifications;
    private Context context;
    private Helpers helpers = new Helpers();
    private FragmentManager mFragmentManager;
    private SectionedRecyclerViewAdapter sectionAdapter;

    public NotificationsListAdapter(ArrayList<Notification> notifications, Context context,
                                    FragmentManager fragmentManager) {
        this.context = context;
        this.notifications = notifications;
        this.mFragmentManager = fragmentManager;
    }

    @Override
    public void onBindViewHolder(NotificationListViewHolder holder, int position) {
        if (notifications != null) {
            Notification notification;
            notification = notifications.get(position);
            int color = context.getResources().getColor(R.color.primary);
            ForegroundColorSpan fcs = new ForegroundColorSpan(color);
            if (notification.getType().equals("dislike")) {
                holder.likeDislikeImage.setImageDrawable(AppCompatDrawableManager.get()
                        .getDrawable(context, R.drawable.icon_dislike));
                Picasso.with(context).load(notification.getUser().getPicture()).resize(64,64)
                        .transform(new TransformationRoundImage(50, 0)).into(holder.image);
                String text = Constants.NOTIF_DISLIKE;
                String name = notification.getUser().getName();
                String divespot = notification.getDiveSpot().getName();
                text = String.format(text, name, divespot);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(fcs, 0, name.length(), 0);
                spannableString.setSpan(fcs, text.indexOf(divespot), text.length(), 0);
                holder.text.setText(spannableString);
                holder.timeAgo.setText(helpers.getDate(notification.getDate()));
            }
            if (notification.getType().equals("like")) {
                holder.likeDislikeImage.setImageDrawable(AppCompatDrawableManager.get()
                        .getDrawable(context, R.drawable.icon_like));
                Picasso.with(context).load(notification.getUser().getPicture()).resize(64,64)
                        .transform(new TransformationRoundImage(50,0)).into(holder.image);
                String text = Constants.NOTIF_LIKE;
                String name = notification.getUser().getName();
                String divespot = notification.getDiveSpot().getName();
                text = String.format(text, name, divespot);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(fcs, 0, name.length(), 0);
                spannableString.setSpan(fcs, text.indexOf(divespot), text.length(), 0);
                holder.text.setText(spannableString);
                holder.timeAgo.setText(helpers.getDate(notification.getDate()));

            }
            if (notification.getType().equals("accept")) {
                String text = Constants.NOTIF_ACCEPT;
                String divespot = notification.getDiveSpot().getName();
                text = String.format(text, divespot);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(fcs, text.indexOf(divespot),
                        text.length() - divespot.length(), 0);
                holder.text.setText(spannableString);
                holder.timeAgo.setText(helpers.getDate(notification.getDate()));
            }
        }
    }

    public void setSectionAdapter(SectionedRecyclerViewAdapter sectionAdapter) {
        this.sectionAdapter = sectionAdapter;
    }

    @Override
    public NotificationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_notification, parent, false);
        return new NotificationListViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        if (notifications == null) {
            return 0;
        }
        return notifications.size();
    }

    public class NotificationListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView image;
        private TextView text;
        private TextView timeAgo;
        private PercentRelativeLayout percentRelativeLayout;
        private Context context;
        private ImageView likeDislikeImage;

        public NotificationListViewHolder(View v) {
            super(v);
            context = v.getContext();
            timeAgo = (TextView) v.findViewById(R.id.time_ago);
            text = (TextView) v.findViewById(R.id.text);
            percentRelativeLayout = (PercentRelativeLayout) v.findViewById(R.id.content);
            percentRelativeLayout.setOnClickListener(this);
            image = (ImageView) v.findViewById(R.id.image);
            timeAgo = (TextView) v.findViewById(R.id.time_ago);
            likeDislikeImage = (ImageView) v.findViewById(R.id.like_dislike);
            image.setOnClickListener(this);
            percentRelativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.content:
                    if (sectionAdapter != null) {
                        createAction(sectionAdapter.sectionedPositionToPosition(getAdapterPosition()), false);
                    } else {
                        createAction(getAdapterPosition(), false);
                    }
                    break;
                case R.id.image:
                    if (sectionAdapter != null) {
                        createAction(sectionAdapter.sectionedPositionToPosition(getAdapterPosition()), true);
                    } else {
                        createAction(getAdapterPosition(), true);
                    }
                    break;
            }
        }

        private void createAction(int position, boolean isImage) {
            Notification notification = notifications.get(position);

            if (isImage && (notification.getType().equals("like")
                    || notification.getType().equals("dislike"))) {
                helpers.showDialog(notification.getUser(), mFragmentManager);
            }

            if (!isImage) {
                DiveSpotDetailsActivity.show(context,
                        String.valueOf(notification.getDiveSpot().getId()));
            }

        }

    }

}

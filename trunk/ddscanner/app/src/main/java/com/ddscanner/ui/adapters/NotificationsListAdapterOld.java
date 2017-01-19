package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.NotificationOld;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lashket on 25.5.16.
 */
public class NotificationsListAdapterOld
        extends RecyclerView.Adapter<NotificationsListAdapterOld.NotificationListViewHolder>{

    private List<NotificationOld> notificationOlds;
    private Context context;
    private FragmentManager mFragmentManager;
    private SectionedRecyclerViewAdapter sectionAdapter;

    public NotificationsListAdapterOld(ArrayList<NotificationOld> notificationOlds, Context context,
                                       FragmentManager fragmentManager) {
        this.context = context;
        this.notificationOlds = notificationOlds;
        this.mFragmentManager = fragmentManager;
    }

    @Override
    public void onBindViewHolder(NotificationListViewHolder holder, int position) {
        if (notificationOlds != null) {
            NotificationOld notificationOld;
            notificationOld = notificationOlds.get(position);
            int color = ContextCompat.getColor(context, R.color.primary);
            ForegroundColorSpan fcs = new ForegroundColorSpan(color);
            String divespot = "";
            String text = "";
            SpannableString spannableString;
            switch (notificationOld.getType()) {
                case ACCEPT:
                    divespot = notificationOld.getDiveSpotShort().getName();
                    text = context.getResources().getString(R.string.your_changes_accepted,divespot);
                    spannableString = new SpannableString(text);
                    spannableString.setSpan(fcs, text.indexOf(divespot),
                            text.indexOf(divespot) + divespot.length(), 0);
                    holder.text.setText(spannableString);
                    holder.timeAgo.setText(Helpers.getDate(notificationOld.getDate()));
                    break;
                case LIKE:
                    holder.likeDislikeImage.setImageDrawable(AppCompatDrawableManager.get()
                            .getDrawable(context, R.drawable.icon_like));
                    Picasso.with(context)
                            .load(notificationOld.getUserOld().getPicture())
                            .resize(Math.round(Helpers.convertDpToPixel(64, context)),Math.round(Helpers.convertDpToPixel(64, context)))
                            .centerCrop()
                            .transform(new CropCircleTransformation())
                            .into(holder.image);
                    String name = notificationOld.getUserOld().getName();
                    divespot = notificationOld.getDiveSpotShort().getName();
                    // text = String.format(text, name, divespot);
                    text = context.getResources().getString(R.string.user_liked_your_review, name, divespot);
                    spannableString = new SpannableString(text);
                    spannableString.setSpan(fcs, 0, name.length(), 0);
                    spannableString.setSpan(fcs, text.indexOf(divespot), text.length(), 0);
                    holder.text.setText(spannableString);
                    holder.timeAgo.setText(Helpers.getDate(notificationOld.getDate()));
                    break;
                case DISLIKE:
                    holder.likeDislikeImage.setImageDrawable(AppCompatDrawableManager.get()
                            .getDrawable(context, R.drawable.icon_dislike));
                    Picasso.with(context)
                            .load(notificationOld.getUserOld().getPicture())
                            .resize(Math.round(Helpers.convertDpToPixel(64, context)),Math.round(Helpers.convertDpToPixel(64, context)))
                            .transform(new CropCircleTransformation())
                            .into(holder.image);
                    name = notificationOld.getUserOld().getName();
                    divespot = notificationOld.getDiveSpotShort().getName();
                    text = context.getResources().getString(R.string.user_dislike_your_review, name, divespot);
                    spannableString = new SpannableString(text);
                    spannableString.setSpan(fcs, 0, name.length(), 0);
                    spannableString.setSpan(fcs, text.indexOf(divespot), text.length(), 0);
                    holder.text.setText(spannableString);
                    holder.timeAgo.setText(Helpers.getDate(notificationOld.getDate()));
                    break;
                case ACHIEVE:
                    holder.bottomDivider.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.orange_rectangle));
                    holder.mainLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
                    holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fireworks));
                    holder.text.setText(notificationOld.getMessage());
                    holder.timeAgo.setText(Helpers.getDate(notificationOld.getDate()));
                    break;
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
        if (notificationOlds == null) {
            return 0;
        }
        return notificationOlds.size();
    }

    public class NotificationListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView image;
        private TextView text;
        private TextView timeAgo;
        private RelativeLayout percentRelativeLayout;
        private Context context;
        private ImageView likeDislikeImage;
        private RelativeLayout mainLayout;
        private ImageView bottomDivider;

        public NotificationListViewHolder(View v) {
            super(v);
            context = v.getContext();
            bottomDivider = (ImageView) v.findViewById(R.id.bottom_divider);
            timeAgo = (TextView) v.findViewById(R.id.time_ago);
            text = (TextView) v.findViewById(R.id.text);
            percentRelativeLayout = (RelativeLayout) v.findViewById(R.id.content);
            percentRelativeLayout.setOnClickListener(this);
            image = (ImageView) v.findViewById(R.id.image);
            mainLayout = (RelativeLayout) v.findViewById(R.id.main_layout);
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
            NotificationOld notificationOld = notificationOlds.get(position);
            switch (notificationOld.getType()) {
                case ACHIEVE:
                    DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(2));
                    break;
                default:
                    if (isImage && (notificationOld.getType().name().equalsIgnoreCase("like")
                            || notificationOld.getType().name().equalsIgnoreCase("dislike"))) {
                        //TODO show user profile activity
                    }

                    if (!isImage) {
                        DiveSpotDetailsActivity.show(context, String.valueOf(notificationOld.getDiveSpotShort().getId()), EventsTracker.SpotViewSource.FROM_NOTIFICATIONS);
                    }
                    break;
            }
        }

    }

}

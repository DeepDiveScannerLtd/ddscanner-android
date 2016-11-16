package com.ddscanner.ui.adapters;

import android.app.FragmentManager;
import android.content.Context;
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
import com.ddscanner.entities.Activity;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.ui.activities.ForeignProfileActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lashket on 25.5.16.
 */
public class ActivitiesListAdapter
        extends RecyclerView.Adapter<ActivitiesListAdapter.ActivitiesListViewHolder> {

    private Context context;
    private FragmentManager mFragmentManager;
    private List<Activity> activities;
    private SectionedRecyclerViewAdapter sectionAdapter;

    public ActivitiesListAdapter(Context context, ArrayList<Activity> activities) {
        this.context = context;
        this.activities = activities;
//        this.mFragmentManager = fragmentManager;
    }

    @Override
    public ActivitiesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_activity, parent, false);
        return new ActivitiesListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActivitiesListViewHolder holder, int position) {
        if (activities != null && position < activities.size()) {
            Activity activity = activities.get(position);
            int color = ContextCompat.getColor(context,R.color.primary);
            ForegroundColorSpan fcs = new ForegroundColorSpan(color);
            String divespot = "";
            String name = "";
            String text = "";
            SpannableString spannableString;
            switch (activity.getType()) {
                case CHECKIN:
                    name = activity.getUserOld().getName();
                    divespot = activity.getDiveSpotShort().getName();
                    text = context.getResources().getString(R.string.user_made_checkin, name, divespot);
                    spannableString = new SpannableString(text);
                    spannableString.setSpan(fcs, 0, name.length(), 0);
                    spannableString.setSpan(fcs, text.indexOf(divespot), text.length(), 0);
                    holder.text.setText(spannableString);
                    holder.timeAgo.setText(Helpers.getDate(activity.getDate()));
                    holder.image.setImageDrawable(AppCompatDrawableManager.get()
                            .getDrawable(context, R.drawable.ic_notif_checkin));
                    Picasso.with(context)
                            .load(DDScannerApplication.getInstance().getString(R.string.server_api_address) + Constants.IMAGE_PATH_PREVIEW + activity.getDiveSpotShort().getImage())
                            .resize(Math.round(Helpers.convertDpToPixel(40, context)),Math.round(Helpers.convertDpToPixel(40, context)))
                            .centerCrop()
                            .transform(new TransformationRoundImage(2,0))
                            .into(holder.dsLogo);
                    break;
                case STORE:
                    divespot = activity.getDiveSpotShort().getName();
                    text = context.getResources().getString(R.string.new_dive_spothas_been_added_near, divespot);
                    spannableString = new SpannableString(text);
                    spannableString.setSpan(fcs, text.indexOf(divespot), text.length(), 0);
                    holder.text.setText(spannableString);
                    holder.timeAgo.setText(Helpers.getDate(activity.getDate()));
                    Picasso.with(context)
                            .load(DDScannerApplication.getInstance().getString(R.string.server_api_address) + Constants.IMAGE_PATH_PREVIEW + activity.getDiveSpotShort().getImage())
                            .resize(Math.round(Helpers.convertDpToPixel(40, context)),Math.round(Helpers.convertDpToPixel(40, context)))
                            .centerCrop()
                            .transform(new TransformationRoundImage(2,0))
                            .into(holder.dsLogo);
                    break;
                case UPDATE:
                    holder.image.setImageDrawable(AppCompatDrawableManager.get()
                            .getDrawable(context, R.drawable.ic_notif_changed));
                     divespot = activity.getDiveSpotShort().getName();
                    text = context.getResources().getString(R.string.divespot_was_changed, divespot);
                    spannableString = new SpannableString(text);
                    spannableString.setSpan(fcs, 0, divespot.length(), 0);
                    holder.text.setText(spannableString);
                    holder.timeAgo.setText(Helpers.getDate(activity.getDate()));
                    Picasso.with(context)
                            .load(DDScannerApplication.getInstance().getString(R.string.server_api_address) + Constants.IMAGE_PATH_PREVIEW + activity.getDiveSpotShort().getImage())
                            .resize(Math.round(Helpers.convertDpToPixel(40, context)),Math.round(Helpers.convertDpToPixel(40, context)))
                            .centerCrop()
                            .transform(new TransformationRoundImage(2,0))
                            .into(holder.dsLogo);
                    break;
                case ACHIEVE:
                    Picasso.with(context)
                            .load(activity.getUserOld().getPicture())
                            .resize(Math.round(Helpers.convertDpToPixel(40, context)),Math.round(Helpers.convertDpToPixel(40, context)))
                            .centerCrop()
                            .transform(new CropCircleTransformation())
                            .into(holder.image);
                    holder.timeAgo.setText(Helpers.getDate(activity.getDate()));
                    holder.text.setText(activity.getMessage());
                    holder.text.setTextColor(ContextCompat.getColor(context, R.color.primary));
                    holder.dsLogo.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.text.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    holder.text.setLayoutParams(layoutParams);
                    break;
            }
        }
    }

    public void setSectionAdapter(SectionedRecyclerViewAdapter sectionAdapter) {
        this.sectionAdapter = sectionAdapter;
    }

    @Override
    public int getItemCount() {
        if (activities !=null) {
            return activities.size();
        }
        return 0;
    }

    public class ActivitiesListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView text;
        private TextView timeAgo;
        private RelativeLayout percentRelativeLayout;
        private ImageView image;
        private ImageView dsLogo;
        private Context context;

        public ActivitiesListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            context = v.getContext();
            timeAgo = (TextView) v.findViewById(R.id.time_ago); 
            text = (TextView) v.findViewById(R.id.text);
            percentRelativeLayout = (RelativeLayout) v.findViewById(R.id.content);
            image = (ImageView) v.findViewById(R.id.image);
            dsLogo = (ImageView) v.findViewById(R.id.ds_logo);
        }

        @Override
        public void onClick(View v) {
            if (sectionAdapter != null) {
                if (activities.get(sectionAdapter.sectionedPositionToPosition(getAdapterPosition())).getType().equals(Activity.ActivityType.ACHIEVE)) {
                    ForeignProfileActivity.show(context, activities.get(sectionAdapter.sectionedPositionToPosition(getAdapterPosition())).getUserOld().getId());
                    return;
                }
                DiveSpotDetailsActivity.show(context,
                        String.valueOf(activities.get(sectionAdapter.sectionedPositionToPosition(getAdapterPosition())).getDiveSpotShort().getId()), EventsTracker.SpotViewSource.FROM_ACTIVITIES);
                return;
            }
            if (activities.get(getAdapterPosition()).getType().equals(Activity.ActivityType.ACHIEVE)) {
                ForeignProfileActivity.show(context, activities.get(getAdapterPosition()).getUserOld().getId());
                return;
            }
            DiveSpotDetailsActivity.show(context,
                    String.valueOf(activities.get(getAdapterPosition()).getDiveSpotShort().getId()), EventsTracker.SpotViewSource.FROM_ACTIVITIES);
        }
    }

}

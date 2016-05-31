package com.ddscanner.ui.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Activity;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 25.5.16.
 */
public class ActivitiesListAdapter
        extends RecyclerView.Adapter<ActivitiesListAdapter.ActivitiesListViewHolder> {

    private Context context;
    private Helpers helpers = new Helpers();
    private FragmentManager mFragmentManager;
    private List<Activity> activities;

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
            int color = context.getResources().getColor(R.color.primary);
            ForegroundColorSpan fcs = new ForegroundColorSpan(color);
            if (activity.getType().equals("checkin")) {
                String text = Constants.NOTIF_CHECKIN;
                String name = activity.getUser().getName();
                String divespot = activity.getDiveSpot().getName();
                text = String.format(text, name, divespot);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(fcs, 0, name.length(), 0);
                spannableString.setSpan(fcs, text.indexOf(divespot), text.length(), 0);
                holder.text.setText(spannableString);
                holder.timeAgo.setText(helpers.getDate(activity.getDate()));
            }
            if (activity.getType().equals("store")) {
                String text = Constants.NOTIF_NEWDS;
                String divespot = activity.getDiveSpot().getName();
                text = String.format(text, divespot);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(fcs, text.indexOf(divespot), text.length(), 0);
                holder.text.setText(spannableString);
                holder.timeAgo.setText(helpers.getDate(activity.getDate()));
            }
            if (activity.getType().equals("update")) {
                String time = helpers.getDate(activity.getDate());
                String text = Constants.NOTIF_ACCEPT;
                String divespot = activity.getDiveSpot().getName();
                text = String.format(text, divespot);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(fcs, text.indexOf(divespot), text.indexOf(divespot)
                        + divespot.length(), 0);
                holder.text.setText(spannableString);
                holder.timeAgo.setText(helpers.getDate(activity.getDate()));
            }
        }
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

        private ImageView dsLogo;
        private TextView text;
        private TextView timeAgo;
        private PercentRelativeLayout percentRelativeLayout;
        private Context context;

        public ActivitiesListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            context = v.getContext();
            timeAgo = (TextView) v.findViewById(R.id.time_ago); 
            text = (TextView) v.findViewById(R.id.text);
            percentRelativeLayout = (PercentRelativeLayout) v.findViewById(R.id.content);
        }

        @Override
        public void onClick(View v) {
            DiveSpotDetailsActivity.show(context,
                    String.valueOf(activities.get(getAdapterPosition()).getDiveSpot().getId()));
        }
    }

}

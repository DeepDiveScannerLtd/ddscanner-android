package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.screens.achievements.AchievementsActivity;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AchievmentProfileListAdapter extends RecyclerView.Adapter<AchievmentProfileListAdapter.AchievmentProfileListViewHolder> {

    private ArrayList<ProfileAchievement> achievmentProfiles;
    private Context context;
    private static final int MAX_FLAGS_COUNT = 6;
    private boolean isSelf;

    public AchievmentProfileListAdapter(ArrayList<ProfileAchievement> achievmentProfiles, Context context, boolean isSelf) {
        this.achievmentProfiles = achievmentProfiles;
        this.context = context;
        this.isSelf = isSelf;
    }

    @Override
    public AchievmentProfileListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievment_profile, parent, false);
        return new AchievmentProfileListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AchievmentProfileListViewHolder holder, int position) {
        ProfileAchievement achievmentProfile = achievmentProfiles.get(position);
        holder.title.setText(achievmentProfile.getName());
        List<String> countries = new ArrayList<String>();
        if (achievmentProfile.getCountries() != null) {
            countries = achievmentProfile.getCountries();
            countries.removeAll(Arrays.asList("", null));
        }
        if (countries.size() > 0) {
            for (int i = 0; i < countries.size(); i++) {
                if (i == MAX_FLAGS_COUNT) {
                    holder.moreCount.setText(DDScannerApplication.getInstance().getString(R.string.pattern_more_countries, String.valueOf(countries.size() - i)));
                    holder.moreCount.setVisibility(View.VISIBLE);
                    break;
                } else {
                    holder.moreCount.setVisibility(View.VISIBLE);
                }
                CircleImageView imageView = new CircleImageView(context);
                imageView.setBorderColor(ContextCompat.getColor(context, R.color.achievement_item_background));
                imageView.setBorderWidth(Math.round(Helpers.convertDpToPixel(2, context)));
                int resiId;
                try {
                    resiId = Helpers.getResId(countries.get(i).toLowerCase(), R.drawable.class);
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, resiId));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(Helpers.convertDpToPixel(18, context)), Math.round(Helpers.convertDpToPixel(18, context)));
                    if (i != 0) {
                        layoutParams.setMargins(0, 0, Integer.parseInt("-" + String.valueOf(Math.round(Helpers.convertDpToPixel(6, context)))),0);
                    }
                    imageView.setLayoutParams(layoutParams);
                    holder.countries.addView(imageView);
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return achievmentProfiles.size();
    }

    class AchievmentProfileListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected LinearLayout countries;
        protected TextView title;
        protected TextView moreCount;

        public AchievmentProfileListViewHolder(View view) {
            super(view);
            if (isSelf) {
                view.setOnClickListener(this);
            }
            countries = view.findViewById(R.id.countries);
            title = view.findViewById(R.id.title);
            moreCount = view.findViewById(R.id.more_count);
        }

        @Override
        public void onClick(View view) {
            AchievementsActivity.show(context);
        }
    }

}

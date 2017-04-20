package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.ui.views.AchievementCountryFlagView;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AchievmentProfileListAdapter extends RecyclerView.Adapter<AchievmentProfileListAdapter.AchievmentProfileListViewHolder> {

    private ArrayList<ProfileAchievement> achievmentProfiles;
    private Context context;
    private static final int MAX_FLAGS_COUNT = 6;

    public AchievmentProfileListAdapter(ArrayList<ProfileAchievement> achievmentProfiles, Context context) {
        this.achievmentProfiles = achievmentProfiles;
        this.context = context;
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
                AchievementCountryFlagView imageView = new AchievementCountryFlagView(context);
                int resiId;
                try {
                    resiId = Helpers.getResId(countries.get(i).toLowerCase(), R.drawable.class);
                    imageView.setFlagBitmap(resiId);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(Helpers.convertDpToPixel(33, context)), Math.round(Helpers.convertDpToPixel(33, context)));
                    if (i != 0) {
                        layoutParams.setMargins(Integer.parseInt("-" + String.valueOf(Math.round(Helpers.convertDpToPixel(18, context)))), 0, 0, 0);
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

    class AchievmentProfileListViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout countries;
        protected TextView title;
        protected TextView moreCount;

        public AchievmentProfileListViewHolder(View view) {
            super(view);
            countries = (LinearLayout) view.findViewById(R.id.countries);
            title = (TextView) view.findViewById(R.id.title);
            moreCount = (TextView) view.findViewById(R.id.more_count);
        }

    }

}

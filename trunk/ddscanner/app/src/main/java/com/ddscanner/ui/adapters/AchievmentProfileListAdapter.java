package com.ddscanner.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.AchievmentProfile;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AchievmentProfileListAdapter extends RecyclerView.Adapter<AchievmentProfileListAdapter.AchievmentProfileListViewHolder> {

    private ArrayList<ProfileAchievement> achievmentProfiles;
    private Context context;

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
        if ( countries.size() > 0) {
            for (int i = 0; i < countries.size(); i++) {
                CircleImageView circleImageView = new CircleImageView(context);
                circleImageView.setImageDrawable(ContextCompat.getDrawable(context, Helpers.getResId(countries.get(i).toLowerCase(), R.drawable.class)));
                if (i != 0) {
                    circleImageView.setPadding(Integer.parseInt("-10"), 0, 0, 0);
                }
              //  circleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.path_1));
                circleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                circleImageView.setBorderWidth(Math.round(Helpers.convertDpToPixel(2, context)));
                circleImageView.setBorderColor(Color.parseColor("#ffffff"));
                holder.countries.addView(circleImageView);
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

        public AchievmentProfileListViewHolder(View view) {
            super(view);
            countries = (LinearLayout) view.findViewById(R.id.countries);
            title = (TextView) view.findViewById(R.id.title);

        }

    }

}

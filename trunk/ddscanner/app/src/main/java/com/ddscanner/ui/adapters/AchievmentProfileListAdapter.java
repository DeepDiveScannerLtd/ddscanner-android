package com.ddscanner.ui.adapters;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

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
        ImageView view = new ImageView(context);
        view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.path_1));
        holder.countries.addView(view);
        for (int i = 0; i < 6; i++) {
            view = new ImageView(context);
            view.setPadding(Integer.parseInt("-10"),0,0,0);
            view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.path_1));
            holder.countries.addView(view);
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

package com.ddscanner.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.AchievmentProfile;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.ui.views.AchievementCountryFlagView;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

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
                AchievementCountryFlagView imageView = new AchievementCountryFlagView(context);
                imageView.setFlagBitmap(Helpers.getResId(countries.get(i).toLowerCase(), R.drawable.class));
              //  Picasso.with(context).load(Helpers.getResId(countries.get(i).toLowerCase(), R.drawable.class)).transform(new CropCircleTransformation()).into(imageView)
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(Helpers.convertDpToPixel(33, context)), Math.round(Helpers.convertDpToPixel(33, context)));
                if (i != 0) {
                    layoutParams.setMargins(Integer.parseInt("-" + String.valueOf(Math.round(Helpers.convertDpToPixel(18, context)))), 0, 0, 0);
                    //layoutParams.setMargins(Integer.parseInt("-40"), 0, 0, 0);
                }
                imageView.setLayoutParams(layoutParams);
                holder.countries.addView(imageView);
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

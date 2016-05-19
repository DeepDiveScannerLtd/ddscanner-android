package com.ddscanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.ui.activities.DiveCenterDetailsActivity;
import com.ddscanner.utils.EventTrackerHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lashket on 29.1.16.
 */
public class DiveCentersListAdapter extends RecyclerView.Adapter<DiveCentersListAdapter.DiveCentersListViewHolder> {

    private static final String TAG = DiveCentersListAdapter.class.getName();
    public static ArrayList<DiveCenter> diveCenters;
    private static String logoPath;
    private Context context;

    public DiveCentersListAdapter(ArrayList<DiveCenter> diveCenters, String logoPath, Context context) {
        this.diveCenters = diveCenters;
        this.logoPath = logoPath;
        this.context = context;
    }

    public static String getLogopath() {
        return logoPath;
    }

    @Override
    public DiveCentersListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.dive_center_item, viewGroup, false);

        return new DiveCentersListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DiveCentersListViewHolder diveCentersListViewHolder, int i) {
        int rating = 0;
        final DiveCenter diveCenter = diveCenters.get(i);
        if (diveCenter.getName() != null) {
            diveCentersListViewHolder.dcName.setText(diveCenter.getName());
        }
        if (diveCenter.getAddress() != null) {
            diveCentersListViewHolder.dcAddress.setVisibility(View.VISIBLE);
            diveCentersListViewHolder.dcAddress.setText(diveCenter.getAddress());
        }
        if (diveCenter.getLogo() != null) {
            String imageUrlPath = logoPath + diveCenter.getLogo();
            Picasso.with(context).load(imageUrlPath).into(diveCentersListViewHolder.imgLogo);
        } else {
            diveCentersListViewHolder.imgLogo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.avatar_dc_list_empty));
        }
        rating = Math.round(diveCenter.getRating());
        diveCentersListViewHolder.starsLayout.removeAllViews();
        for (int k = 0; k < rating; k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(2, 0, 0, 0);
            diveCentersListViewHolder.starsLayout.addView(iv);
        }
        for (int k = 0; k < 5 - rating; k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_flag_empty_small);
            iv.setPadding(2, 0, 0, 0);
            diveCentersListViewHolder.starsLayout.addView(iv);
        }

    }

    public void setDiveCenters(ArrayList<DiveCenter> diveCenters, String logoPathN) {
        this.diveCenters = diveCenters;
        this.logoPath = logoPathN;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (diveCenters == null) {
            return 0;
        }

        return diveCenters.size();
    }

    public static class DiveCentersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imgLogo;
        private TextView dcName;
        private TextView dcAddress;
        private LinearLayout starsLayout;
        private Context context;
        private CardView cardView;

        public DiveCentersListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            context = itemView.getContext();
            imgLogo = (ImageView) v.findViewById(R.id.dc_avatar);
            dcName = (TextView) v.findViewById(R.id.dc_name);
            dcAddress = (TextView) v.findViewById(R.id.dc_address);
            starsLayout = (LinearLayout) v.findViewById(R.id.stars);
            cardView = (CardView) v.findViewById(R.id.cv);
        }

        @Override
        public void onClick(View v) {

            AppsFlyerLib.getInstance().trackEvent(context,
                    EventTrackerHelper.EVENT_DIVE_CENTERS_LIST_ITEM_CLICK, new HashMap<String, Object>() {{
                        put(EventTrackerHelper.PARAM_DIVE_CENTERS_LIST_ITEM_CLICK, diveCenters.get(getPosition()).getId());
                    }});

            DiveCenterDetailsActivity.show(context, diveCenters.get(getPosition()), DiveCentersListAdapter.getLogopath());
        }

    }

}

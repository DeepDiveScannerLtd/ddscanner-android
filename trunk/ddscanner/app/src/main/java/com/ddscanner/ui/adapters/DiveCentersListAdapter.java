package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lashket on 29.1.16.
 */
public class DiveCentersListAdapter extends RecyclerView.Adapter<DiveCentersListAdapter.DiveCentersListViewHolder> {

    private static final String TAG = DiveCentersListAdapter.class.getName();
    public ArrayList<DiveCenter> diveCenters;
    private String logoPath;
    private Context context;

    public DiveCentersListAdapter(ArrayList<DiveCenter> diveCenters, String logoPath, Context context) {
        this.diveCenters = diveCenters;
        this.logoPath = logoPath;
        this.context = context;
    }

    public String getLogopath() {
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
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, diveCenter.getLogo(), "1")).placeholder(R.drawable.placeholder_photo_wit_round_corners).error(R.drawable.avatar_dc_profile_def).into(diveCentersListViewHolder.imgLogo);
        rating = Math.round(diveCenter.getRating());
        diveCentersListViewHolder.starsLayout.removeAllViews();
        for (int k = 0; k < rating; k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(2, 0, 0, 0);
            diveCentersListViewHolder.starsLayout.addView(iv);
        }
        for (int k = 0; k < 5 - rating; k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_empty);
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

    public class DiveCentersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            UserProfileActivity.show(context, diveCenters.get(getAdapterPosition()).getId(), 0);
//            DiveCenterDetailsActivity.show(context, diveCenters.get(getAdapterPosition()), DiveCentersListAdapter.this.getLogopath(), EventsTracker.SpotViewSource.FROM_LIST);
        }

    }

}

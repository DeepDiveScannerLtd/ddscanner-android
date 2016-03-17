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
import com.ddscanner.entities.DiveCenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lashket on 29.1.16.
 */
public class DiveCentersListAdapter extends RecyclerView.Adapter<DiveCentersListAdapter.DiveCentersListViewHolder> {

    private static final String TAG = DiveCentersListAdapter.class.getName();
    public static ArrayList<DiveCenter> diveCenters;
    private String logoPath;
    private Context context;

    public DiveCentersListAdapter(ArrayList<DiveCenter> diveCenters, String logoPath, Context context) {
        this.diveCenters = diveCenters;
        this.logoPath = logoPath;
        this.context = context;
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
        DiveCenter diveCenter = diveCenters.get(i);
        if (diveCenter.getName() != null) {
            diveCentersListViewHolder.dcName.setText(diveCenter.getName());
        }
        if (diveCenter.getPhone() != null) {
            String phone = diveCenter.getPhone();
            phone = phone.replaceAll(" ", "");
            phone = phone.replaceAll("\\-+\\+", "\n");
            diveCentersListViewHolder.dcPhone.setVisibility(View.VISIBLE);
            diveCentersListViewHolder.dcPhone.setText(phone);
            diveCentersListViewHolder.ic_phone.setVisibility(View.VISIBLE);
        }
        if (diveCenter.getAddress() != null) {
            diveCentersListViewHolder.dcAddress.setVisibility(View.VISIBLE);
            diveCentersListViewHolder.dcAddress.setText(diveCenter.getAddress());
        }
        if (diveCenter.getEmail() != null) {
            diveCentersListViewHolder.ic_email.setVisibility(View.VISIBLE);
            diveCentersListViewHolder.dcEmail.setVisibility(View.VISIBLE);
            diveCentersListViewHolder.dcEmail.setText(diveCenter.getEmail());

        }
        if (diveCenter.getLogo() != null) {
            String imageUrlPath = logoPath + diveCenter.getLogo();
            Picasso.with(context).load(imageUrlPath).into(diveCentersListViewHolder.imgLogo);
        } else {
            diveCentersListViewHolder.imgLogo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dc_avatar_empty));
        }
        rating = Math.round(diveCenter.getRating());
        diveCentersListViewHolder.starsLayout.removeAllViews();
        for (int k = 0; k < rating; k++) {
            System.out.println(Math.round(diveCenter.getRating()));
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(5, 0, 5, 0);
            diveCentersListViewHolder.starsLayout.addView(iv);
        }
        for (int k = 0; k < 5 - rating; k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_flag_empty_small);
            iv.setPadding(5, 0, 5, 0);
            diveCentersListViewHolder.starsLayout.addView(iv);
        }

    }

    @Override
    public int getItemCount() {
        if (diveCenters == null) {
            return 0;
        }

        return diveCenters.size();
    }

    public static class DiveCentersListViewHolder extends RecyclerView.ViewHolder {

        private ImageView ic_phone, ic_email;
        private ImageView imgLogo;
        private TextView dcEmail;
        private TextView dcName;
        private TextView dcAddress;
        private TextView dcPhone;
        private LinearLayout starsLayout;

        public DiveCentersListViewHolder(View v) {
            super(v);
            dcEmail = (TextView) v.findViewById(R.id.dc_email);
            ic_phone = (ImageView) v.findViewById(R.id.ic_phone);
            ic_email = (ImageView) v.findViewById(R.id.dc_ic_mail);
            imgLogo = (ImageView) v.findViewById(R.id.dc_avatar);
            dcName = (TextView) v.findViewById(R.id.dc_name);
            dcAddress = (TextView) v.findViewById(R.id.dc_address);
            dcPhone = (TextView) v.findViewById(R.id.dc_telefon);
            starsLayout = (LinearLayout) v.findViewById(R.id.stars);
        }

    }

}

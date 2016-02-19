package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.DiveCenter;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.entities.Divecenters;
import travel.ilave.deepdivescanner.ui.activities.DivePlaceActivity;

/**
 * Created by lashket on 29.1.16.
 */
public class DiveCentersListAdapter extends RecyclerView.Adapter<DiveCentersListAdapter.DiveCentersListViewHolder> {

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
        diveCentersListViewHolder.dcName.setText(diveCenter.getName());
        diveCentersListViewHolder.dcPhone.setText(diveCenter.getPhone());
        diveCentersListViewHolder.dcAddress.setText(diveCenter.getAddress());
        if(diveCenter.getLogo() != null) {
            String imageUrlPath = logoPath + diveCenter.getLogo();
            Picasso.with(context).load(imageUrlPath).into(diveCentersListViewHolder.imgLogo);
        }
        rating = Math.round(diveCenter.getRating());
        for (int k = 0; k < rating; k++) {
            System.out.println(Math.round(diveCenter.getRating()));
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(5,0,5,0);
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
        if(diveCenters == null)
        {
            return 0;
        } else {
            return diveCenters.size();
        }
    }

    public static class DiveCentersListViewHolder extends RecyclerView.ViewHolder  {

        private ImageView imgLogo;
        private TextView dcName;
        private TextView dcAddress;
        private TextView dcPhone;
        private LinearLayout starsLayout;

        public DiveCentersListViewHolder(View v) {
            super(v);
            imgLogo = (ImageView) v.findViewById(R.id.dc_avatar);
            dcName = (TextView) v.findViewById(R.id.dc_name);
            dcAddress = (TextView) v.findViewById(R.id.dc_address);
            dcPhone = (TextView) v.findViewById(R.id.dc_telefon);
            starsLayout = (LinearLayout) v.findViewById(R.id.stars);


        }

    }

}

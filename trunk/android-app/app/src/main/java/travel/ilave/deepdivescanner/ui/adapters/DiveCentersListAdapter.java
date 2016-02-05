package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public DiveCentersListAdapter(ArrayList<DiveCenter> diveCenters) {
        this.diveCenters = diveCenters;
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
        DiveCenter diveCenter = diveCenters.get(i);
        //set texts
    }

    @Override
    public int getItemCount() {
        return diveCenters.size();
    }

    public static class DiveCentersListViewHolder extends RecyclerView.ViewHolder  {

        private ImageView imgLogo;
        private TextView dcName;
        private TextView dcAddress;
        private TextView dcPhone;

        public DiveCentersListViewHolder(View v) {
            super(v);
            imgLogo = (ImageView) v.findViewById(R.id.dc_avatar);
            dcName = (TextView) v.findViewById(R.id.dc_name);
            dcAddress = (TextView) v.findViewById(R.id.dc_address);
            dcPhone = (TextView) v.findViewById(R.id.dc_telefon);


        }

    }

}

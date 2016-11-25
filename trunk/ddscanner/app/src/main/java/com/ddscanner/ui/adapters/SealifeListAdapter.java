package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotSealife;
import com.ddscanner.entities.Sealife;
import com.ddscanner.screens.sealife.details.SealifeDetailsActivity;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lashket on 17.2.16.
 */
public class SealifeListAdapter extends RecyclerView.Adapter<SealifeListAdapter.SealifeListViewHolder>{

    public ArrayList<DiveSpotSealife> sealifes;
    private Context context;

    public SealifeListAdapter(ArrayList<DiveSpotSealife> sealifes, Context context) {
        this.sealifes = sealifes;
        this.context = context;
    }

    @Override
    public SealifeListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.sealife_item, viewGroup, false);
        return new SealifeListViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(SealifeListViewHolder sealifeListViewHolder, int i) {
        DiveSpotSealife sealife = sealifes.get(i);
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.server_api_address) + Constants.SEALIFE_IMAGE_PATH_PREVIEW + sealife.getPhoto()).resize(Math.round(Helpers.convertDpToPixel(125, context)), Math.round(Helpers.convertDpToPixel(70, context))).centerCrop().into(sealifeListViewHolder.sealifeLogo);
        sealifeListViewHolder.sealifeName.setText(sealife.getName());
    }

    @Override
    public int getItemCount() {
        return sealifes.size();
    }


    public class SealifeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView sealifeLogo;
        protected TextView sealifeName;
        private Context context;

        public SealifeListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            context = itemView.getContext();

            sealifeLogo = (ImageView) v.findViewById(R.id.seaife_logo);
            sealifeName = (TextView) v.findViewById(R.id.sealife_name);
        }

        @Override
        public void onClick(View v) {
            SealifeDetailsActivity.show(context, sealifes.get(getAdapterPosition()).getId());
            EventsTracker.trackDiveSpotSealifeView();
        }
    }
}

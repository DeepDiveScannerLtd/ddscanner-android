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
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.screens.sealife.details.SealifeDetailsActivity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class SealifeListAdapter extends RecyclerView.Adapter<SealifeListAdapter.SealifeListViewHolder>{

    public ArrayList<SealifeShort> sealifes;
    private Context context;

    public SealifeListAdapter(ArrayList<SealifeShort> sealifes, Context context) {
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
        SealifeShort sealife = sealifes.get(i);
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, sealife.getImage(), "1")).resize(Math.round(Helpers.convertDpToPixel(125, context)), Math.round(Helpers.convertDpToPixel(70, context))).centerCrop().placeholder(R.drawable.placeholder_photo_wit_round_corners).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).into(sealifeListViewHolder.sealifeLogo);
        sealifeListViewHolder.sealifeName.setText(sealife.getName());
    }

    @Override
    public int getItemCount() {
        if (sealifes == null) {
            return 0;
        }
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

            sealifeLogo = v.findViewById(R.id.seaife_logo);
            sealifeName = v.findViewById(R.id.sealife_name);
        }

        @Override
        public void onClick(View v) {
            SealifeDetailsActivity.show(context, sealifes.get(getAdapterPosition()).getId(), EventsTracker.SealifeViewSource.DIVE_SPOT);
        }
    }
}

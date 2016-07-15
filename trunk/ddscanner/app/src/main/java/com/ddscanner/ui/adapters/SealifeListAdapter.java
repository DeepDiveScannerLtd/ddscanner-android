package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Sealife;
import com.ddscanner.ui.activities.SealifeDetails;
import com.ddscanner.utils.EventTrackerHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lashket on 17.2.16.
 */
public class SealifeListAdapter extends RecyclerView.Adapter<SealifeListAdapter.SealifeListViewHolder>{

    public ArrayList<Sealife> sealifes;
    private Context context;
    private String pathSmall;
    private String pathMedium;

    public SealifeListAdapter(ArrayList<Sealife> sealifes, Context context, String pathSmall, String pathMedium) {
        this.sealifes = sealifes;
        this.context = context;
        this.pathMedium = pathMedium;
        this.pathSmall = pathSmall;
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
        Sealife sealife = sealifes.get(i);
        Picasso.with(context).load(pathSmall + sealife.getImage()).into(sealifeListViewHolder.sealifeLogo);
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
            SealifeDetails.show(context, sealifes.get(getPosition()), pathMedium);
            EventsTracker.trackDiveSpotSealifeView();
            AppsFlyerLib.getInstance().trackEvent(context,
                    EventTrackerHelper.EVENT_SEALIFE_CLICKED, new HashMap<String, Object>() {{
                        put(EventTrackerHelper.PARAM_SEALIFE_CLICKED, sealifes.get(getPosition()));
                    }});
        }
    }
}

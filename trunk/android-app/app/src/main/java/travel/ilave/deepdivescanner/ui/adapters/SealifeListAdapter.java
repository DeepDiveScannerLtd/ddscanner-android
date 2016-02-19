package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Sealife;
import travel.ilave.deepdivescanner.ui.activities.SealifeDetails;

/**
 * Created by lashket on 17.2.16.
 */
public class SealifeListAdapter extends RecyclerView.Adapter<SealifeListAdapter.SealifeListViewHolder>{

    public static ArrayList<Sealife> sealifes;

    @Override
    public SealifeListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.sealife_item, viewGroup, false);
        return new SealifeListViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(SealifeListViewHolder sealifeListViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }


    public static class SealifeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView sealifeLogo;
        protected TextView sealifeName;
        private static Context context;

        public SealifeListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            context = itemView.getContext();

            sealifeLogo = (ImageView) v.findViewById(R.id.seaife_logo);
            sealifeName = (TextView) v.findViewById(R.id.sealife_name);
        }

        @Override
        public void onClick(View v) {
            SealifeDetails.show(context);
        }
    }
}

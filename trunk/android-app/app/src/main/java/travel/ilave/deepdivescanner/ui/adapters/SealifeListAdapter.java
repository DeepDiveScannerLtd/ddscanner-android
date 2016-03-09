package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Sealife;
import travel.ilave.deepdivescanner.ui.activities.SealifeDetails;

/**
 * Created by lashket on 17.2.16.
 */
public class SealifeListAdapter extends RecyclerView.Adapter<SealifeListAdapter.SealifeListViewHolder>{

    public static ArrayList<Sealife> sealifes;
    private Context context;
    private String pathSmall;
    private static String pathMedium;

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
        Picasso.with(context).load(pathSmall + sealife.getImage()).resize(80,60).centerCrop().into(sealifeListViewHolder.sealifeLogo);
        sealifeListViewHolder.sealifeName.setText(sealife.getName());
    }

    @Override
    public int getItemCount() {
        return sealifes.size();
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
            SealifeDetails.show(context, sealifes.get(getPosition()), pathMedium);
        }
    }
}

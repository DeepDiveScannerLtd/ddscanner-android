package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.ui.activities.DivePlaceActivity;

/**
 * Created by lashket on 29.1.16.
 */
public class DiveCentersAdapter extends RecyclerView.Adapter<DiveCentersAdapter.DiveCentersViewHolder> {



    @Override
    public DiveCentersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.dive_center_item, viewGroup, false);
        return new DiveCentersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DiveCentersViewHolder productListViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class DiveCentersViewHolder extends RecyclerView.ViewHolder  {
        

        public DiveCentersViewHolder(View v) {
            super(v);

        }

    }

}

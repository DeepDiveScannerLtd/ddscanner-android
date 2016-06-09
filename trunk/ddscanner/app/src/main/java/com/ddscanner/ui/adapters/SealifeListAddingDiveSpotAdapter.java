package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddscanner.R;
import com.ddscanner.entities.Sealife;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 6.5.16.
 */
public class SealifeListAddingDiveSpotAdapter extends RecyclerView.Adapter<SealifeListAddingDiveSpotAdapter.SealifeListAddingDivespotViewHolder>{

    private Context context;
    private ArrayList<Sealife> sealifes;
    private TextView title;
    private Helpers helpers = new Helpers();

    public SealifeListAddingDiveSpotAdapter(ArrayList<Sealife> sealifes, Context context, TextView title) {
        this.sealifes = sealifes;
        this.context = context;
        this.title = title;
    }

    @Override
    public SealifeListAddingDivespotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_added_sealife_list, parent, false);
        return new SealifeListAddingDivespotViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SealifeListAddingDivespotViewHolder holder, final int position) {
        holder.sealifeName.setText(sealifes.get(position).getName());
        holder.ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sealifes.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeRemoved(position, sealifes.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (sealifes.size() > 0) {
            title.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
        }
        return sealifes.size();
    }

    public void add(Sealife sealife) {
        if (helpers.checkIsSealifeAlsoInList(sealifes, sealife.getId())) {
            Toast toast = Toast.makeText(context, "This sealife also in list", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        sealifes.add(sealife);
        notifyDataSetChanged();
    }

    public List<Sealife> getSealifes() {
        return this.sealifes;
    }

    public class SealifeListAddingDivespotViewHolder extends RecyclerView.ViewHolder{

        protected ImageView ic_delete;
        protected TextView sealifeName;

        public SealifeListAddingDivespotViewHolder(View v) {
            super(v);
            ic_delete = (ImageView) v.findViewById(R.id.delete_item);
            sealifeName = (TextView) v.findViewById(R.id.sealife_name);
        }

    }

}

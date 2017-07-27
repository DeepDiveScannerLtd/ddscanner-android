package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.GuideItem;
import com.ddscanner.ui.activities.GuideDescriptionActivity;

import java.util.ArrayList;

public class GuideListAdapter extends RecyclerView.Adapter<GuideListAdapter.GuideListViewHolder> {

    private ArrayList<GuideItem> guideItems;
    private Context context;

    public GuideListAdapter(ArrayList<GuideItem> guideItems, Context context) {
        this.guideItems = guideItems;
        this.context = context;
    }

    @Override
    public GuideListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_guide, parent, false);
        return new GuideListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GuideListViewHolder holder, int position) {
        holder.title.setText(guideItems.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return guideItems.size();
    }

    public class GuideListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title;

        public GuideListViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            GuideDescriptionActivity.show(context, guideItems.get(getAdapterPosition()));
        }
    }

}

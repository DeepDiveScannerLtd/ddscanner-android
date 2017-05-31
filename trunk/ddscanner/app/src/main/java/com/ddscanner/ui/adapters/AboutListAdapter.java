package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.AboutScreenItem;

import java.util.ArrayList;

public class AboutListAdapter extends RecyclerView.Adapter<AboutListAdapter.AboutListViewholder> {

    private ArrayList<AboutScreenItem> aboutScreenItems;
    private Context context;

    public AboutListAdapter(ArrayList<AboutScreenItem> aboutScreenItems, Context context) {
        this.aboutScreenItems = aboutScreenItems;
        this.context = context;
    }

    @Override
    public AboutListViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_about, parent, false);
        return new AboutListViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(AboutListViewholder holder, int position) {
        holder.icon.setImageResource(aboutScreenItems.get(position).getIconResId());
        holder.title.setText(aboutScreenItems.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return aboutScreenItems.size();
    }

    public class AboutListViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title;
        protected ImageView icon;

        public AboutListViewholder(View v) {
            super(v);
            v.setOnClickListener(this);
            title = (TextView) v.findViewById(R.id.title);
            icon = (ImageView) v.findViewById(R.id.icon);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(aboutScreenItems.get(getAdapterPosition()).getIntent());
        }
    }

}

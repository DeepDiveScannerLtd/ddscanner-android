package com.ddscanner.screens.profile.divecenter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.FunDive;
import com.ddscanner.interfaces.ListItemClickListener;
import com.ddscanner.utils.Helpers;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DiveCenterFunDivesAdapter extends RecyclerView.Adapter<DiveCenterFunDivesAdapter.DiveCenterFunDiveItemViewHolder> {

    private ArrayList<FunDive> funDives = new ArrayList<>();
    private ListItemClickListener<FunDive> listItemClickListener;
    private Context context;

    public DiveCenterFunDivesAdapter(ListItemClickListener<FunDive> listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public void setFunDives(ArrayList<FunDive> funDives) {
        this.funDives = funDives;
    }

    @Override
    public DiveCenterFunDiveItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dive_center_profile_fun_dive, parent, false);
        return new DiveCenterFunDiveItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiveCenterFunDiveItemViewHolder holder, int position) {
        FunDive funDive = funDives.get(position);
        if (funDive.getPhoto() != null) {
            Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, funDive.getPhoto(), "1")).resize(Helpers.convertDpToIntPixels(40, context), Helpers.convertDpToIntPixels(40, context)).transform(new RoundedCornersTransformation(Helpers.convertDpToIntPixels(2, context), 0, RoundedCornersTransformation.CornerType.ALL)).placeholder(R.drawable.placeholder_photo_wit_round_corners).into(holder.logo);
        } else {
            holder.logo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.photo_dailytour_default_small));
        }
        holder.name.setText(funDive.getName());
        if (funDive.getPriceFrom() != null) {
            Link link = new Link(funDive.getPriceFrom());
            link.setUnderlined(false);
            link.setBold(true);
            link.setTextColor(ContextCompat.getColor(context, R.color.price_color));
            holder.price.setText(String.format("From %s", funDive.getPriceFrom()));
            LinkBuilder.on(holder.price).addLink(link).build();
        } else {
            holder.price.setText(R.string.empty_string);
        }
        if (funDive.getDiverLevelString() != null) {
            holder.level.setText(String.format("%s dives", funDive.getDiverLevelString()));
        } else {
            holder.level.setText(R.string.empty_string);
        }
    }

    @Override
    public int getItemCount() {
        return funDives.size();
    }

    class DiveCenterFunDiveItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView logo;
        private TextView name;
        private TextView level;
        private TextView price;

        public DiveCenterFunDiveItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
            logo = itemView.findViewById(R.id.fun_dive_logo);
            name = itemView.findViewById(R.id.fun_dive_name);
            level = itemView.findViewById(R.id.diver_level);
            price = itemView.findViewById(R.id.price);

        }

        @Override
        public void onClick(View v) {
            listItemClickListener.onItemClick(funDives.get(getAdapterPosition()));
        }
    }

}

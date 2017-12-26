package com.ddscanner.screens.profile.divecenter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.CourseDetails;
import com.ddscanner.interfaces.ListItemClickListener;

import java.util.ArrayList;

public class DiveCenterCoursesAdapter extends RecyclerView.Adapter<DiveCenterCoursesAdapter.DiveCenterCourseViewHolder> {

    private ArrayList<CourseDetails> cources = new ArrayList<>();
    private ListItemClickListener<CourseDetails> listItemClickListener;
    private Context context;

    public DiveCenterCoursesAdapter(ListItemClickListener<CourseDetails> listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public void setCources(ArrayList<CourseDetails> cources) {
        this.cources = cources;
        notifyDataSetChanged();
    }

    @Override
    public DiveCenterCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_divecenter_profile_course, parent, false);
        return new DiveCenterCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiveCenterCourseViewHolder holder, int position) {
        holder.name.setText(cources.get(position).getName());
        holder.duration.setText(cources.get(position).getDurationDivesString());
        holder.price.setText(cources.get(position).getPrice());
        holder.logo.setImageDrawable(ContextCompat.getDrawable(context, cources.get(position).getResourceId()));
    }

    @Override
    public int getItemCount() {
        return cources.size();
    }

    class DiveCenterCourseViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView price;
        private ImageView logo;
        private TextView duration;

        public DiveCenterCourseViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            logo = itemView.findViewById(R.id.logo);
            duration = itemView.findViewById(R.id.duration);

        }
    }

}

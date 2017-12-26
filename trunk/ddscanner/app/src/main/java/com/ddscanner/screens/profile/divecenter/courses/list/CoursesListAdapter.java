package com.ddscanner.screens.profile.divecenter.courses.list;


import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.databinding.ItemCourcesListBinding;
import com.ddscanner.entities.CourseDetails;
import com.ddscanner.interfaces.ListItemClickListener;

import java.util.ArrayList;

public class CoursesListAdapter extends RecyclerView.Adapter<CoursesListAdapter.CourseItemViewHolder> {

    private ListItemClickListener<CourseDetails> listItemClickListener;
    private ArrayList<CourseDetails> cources = new ArrayList<>();

    public CoursesListAdapter(ListItemClickListener<CourseDetails> listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public void setCources(ArrayList<CourseDetails> cources) {
        this.cources = cources;
        notifyDataSetChanged();
    }

    @Override
    public CourseItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCourcesListBinding itemCourcesListBinding = ItemCourcesListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseItemViewHolder(itemCourcesListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(CourseItemViewHolder holder, int position) {
        holder.binding.setViewModel(new CourseListItemViewModel(cources.get(position)));
    }

    @Override
    public int getItemCount() {
        return cources.size();
    }

    class CourseItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemCourcesListBinding binding;

        public CourseItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void onClick(View v) {
            listItemClickListener.onItemClick(cources.get(getAdapterPosition()));
        }
    }

}

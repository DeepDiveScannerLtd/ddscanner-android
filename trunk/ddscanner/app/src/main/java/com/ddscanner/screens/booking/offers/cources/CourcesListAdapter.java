package com.ddscanner.screens.booking.offers.cources;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.databinding.ItemCourseBinding;
import com.ddscanner.entities.Course;

import java.util.ArrayList;

public class CourcesListAdapter extends RecyclerView.Adapter<CourcesListAdapter.CourseViewHolder> {

    private ArrayList<Course> courses = new ArrayList<>();

    public CourcesListAdapter(ArrayList<Course> courses) {
        this.courses = courses;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCourseBinding binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        holder.binding.setCourseViewModel(new CourseItemViewModel(courses.get(position)));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {

        private ItemCourseBinding binding;

        CourseViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

    }

}

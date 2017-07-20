package com.ddscanner.screens.booking.offers.cources;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.databinding.FragmentOffersListBinding;
import com.ddscanner.entities.Course;

import java.util.ArrayList;

public class CoursesListFragment extends Fragment {

    private FragmentOffersListBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offers_list, container, false);
        binding.offersList.setLayoutManager(new LinearLayoutManager(getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupList();
    }

    private void setupList() {
        ArrayList<Course> courses = new ArrayList<>();
        Course course = new Course();
        course.setCourceLength("3 day, 5 dives");
        course.setCourceName("Discover SCUBA Diving");
        course.setPrice("13000 B");
        course.setDiveCenterLogo("http://img.diveadvisor.com/diveshops/5974.jpg");
        course.setImage("http://www.scubadiving.com/sites/scubadiving.com/files/styles/large_1x_/public/scuba-myths-shutterstock_208265431.jpg");
        courses.add(course);
        courses.add(course);
        courses.add(course);
        courses.add(course);
        courses.add(course);
        binding.offersList.setAdapter(new CourcesListAdapter(courses));
    }

}

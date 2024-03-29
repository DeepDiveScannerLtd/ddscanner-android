package com.ddscanner.screens.profile.divecenter.courses.details;


import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.ddscanner.entities.CourseDetails;

public class CourseDetailsActivityViewModel {

    private CourseDetails courseDetails;

    public CourseDetailsActivityViewModel(CourseDetails courseDetails) {
        this.courseDetails = courseDetails;
    }

    public CourseDetails getCourseDetails() {
        return courseDetails;
    }

    @BindingAdapter("loadAssociationLogo")
    public static void loadAssociationPhoto(ImageView view, CourseDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), viewModel.getCourseDetails().getResourceId()));
        }
    }

}

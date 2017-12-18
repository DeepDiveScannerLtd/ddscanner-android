package com.ddscanner.screens.profile.divecenter.courses;


import com.ddscanner.entities.CourseDetails;

public class CourseDetailsActivityViewModel {

    private CourseDetails courseDetails;

    public CourseDetailsActivityViewModel(CourseDetails courseDetails) {
        this.courseDetails = courseDetails;
    }

    public CourseDetails getCourseDetails() {
        return courseDetails;
    }

}

package com.ddscanner.screens.booking.offers.cources;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.ddscanner.entities.Course;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CourseItemViewModel {

    private Course course;

    public CourseItemViewModel(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    @BindingAdapter({"loadDiveCenterLogoFrom"})
    public static void loadDiveCenterLogo(ImageView view, CourseItemViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(viewModel.getCourse().getDiveCenterLogo()).into(view);
        }
    }

    @BindingAdapter({"loadPhotoFrom"})
    public static void loadOfferImage(ImageView view, CourseItemViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(viewModel.getCourse().getImage()).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, view.getContext())), 0, RoundedCornersTransformation.CornerType.TOP)).into(view);
        }
    }

}

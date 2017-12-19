package com.ddscanner.screens.profile.divecenter.courses.details;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ddscanner.R;
import com.ddscanner.databinding.ActivityCourseDetailsBinding;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

public class CourseDetailsActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private static final String ARG_ID = "id";

    public static void show(Context context, long courseId) {
        Intent intent = new Intent(context, CourseDetailsActivity.class);
        intent.putExtra(ARG_ID, courseId);
        context.startActivity(intent);
    }

    ActivityCourseDetailsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_course_details);
    }

    @Override
    public void onDialogClosed(int requestCode) {

    }
}

package com.ddscanner.screens.profile.divecenter.courses.details;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.ActivityCourseDetailsBinding;
import com.ddscanner.entities.CourseDetails;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.profile.divecenter.courses.certificate.CertificateDetailsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;

public class CourseDetailsActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private DDScannerRestClient.ResultListener<CourseDetails> resultListener = new DDScannerRestClient.ResultListener<CourseDetails>() {
        @Override
        public void onSuccess(CourseDetails result) {
            binding.setViewModel(new CourseDetailsActivityViewModel(result));
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, 1, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, 1, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, 1, false);
        }
    };

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
        setupToolbar("Course details", R.id.toolbar, R.menu.menu_course_details);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getCourseDetails(resultListener, getIntent().getLongExtra(ARG_ID, -1));
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.about:
                CertificateDetailsActivity.show(this, binding.getViewModel().getCourseDetails().getCertificate().getId(), binding.getViewModel().getCourseDetails().getCertificate().getName());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

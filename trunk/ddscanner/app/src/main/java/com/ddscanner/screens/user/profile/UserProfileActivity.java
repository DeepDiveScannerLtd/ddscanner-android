package com.ddscanner.screens.user.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.GalleryOpenedSource;
import com.ddscanner.entities.PhotoAuthor;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.entities.User;
import com.ddscanner.entities.ProfileResponseEntity;
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.PhotosGalleryActivity;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

public class UserProfileActivity extends AppCompatActivity implements DialogClosedListener {

    private ProgressView progressView;
    private Toolbar toolbar;
    private String userId;
    private PhotoAuthor photoAuthor;
    private int userType;

    private DDScannerRestClient.ResultListener<User> resultListener = new DDScannerRestClient.ResultListener<User>() {
        @Override
        public void onSuccess(User result) {
//            binding.setUserProfileViewModel(new ProfileFragmentViewModel(result));
            progressView.setVisibility(View.GONE);
            setupFragment(result.getType(), result);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

    };

    private DDScannerRestClient.ResultListener<DiveCenterProfile> diveCenterProfileResultListener = new DDScannerRestClient.ResultListener<DiveCenterProfile>() {
        @Override
        public void onSuccess(DiveCenterProfile result) {
            progressView.setVisibility(View.GONE);
            setupFragment(result.getType(), result);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra("id");
        userType = getIntent().getIntExtra("type", 0);
        setContentView(R.layout.activity_user_profile);
        progressView = (ProgressView) findViewById(R.id.progress_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.profile);
        if (userType != 0) {
            DDScannerApplication.getInstance().getDdScannerRestClient().getUserProfileInformation(userId, resultListener);
        } else {
            DDScannerApplication.getInstance().getDdScannerRestClient().getDiveCenterInformation(userId, diveCenterProfileResultListener);
        }
    }

    public static void show(Context context, String userId, int userType) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra("id", userId);
        intent.putExtra("type", userType);
        context.startActivity(intent);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    private void setupFragment(int userType, Object object) {
        switch (userType) {
            case 0:
                DiveCenterProfile diveCenterProfile = (DiveCenterProfile) object;
                photoAuthor = new PhotoAuthor(String.valueOf(diveCenterProfile.getId()), diveCenterProfile.getName(), diveCenterProfile.getPhoto(), diveCenterProfile.getType());
                FragmentTransaction dcfragmentTransaction = getSupportFragmentManager().beginTransaction();
                DiveCenterProfileFragment diveCenterProfileFragment = DiveCenterProfileFragment.newInstance(diveCenterProfile);
                dcfragmentTransaction.replace(R.id.content, diveCenterProfileFragment);
                dcfragmentTransaction.commit();
                break;
            case 1:
            case 2:
                User user = (User) object;
                photoAuthor = new PhotoAuthor(user.getId(), user.getName(), user.getPhoto(), user.getType());
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(user);
                fragmentTransaction.replace(R.id.content, userProfileFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Subscribe
    public void openPhotosActivity(OpenPhotosActivityEvent event) {
        PhotosGalleryActivity.show(userId, this, PhotoOpenedSource.PROFILE, new Gson().toJson(photoAuthor));
    }

}

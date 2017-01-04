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
import com.ddscanner.entities.GalleryOpenedSource;
import com.ddscanner.entities.PhotoAuthor;
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

public class UserProfileActivity extends AppCompatActivity implements InfoDialogFragment.DialogClosedListener{

    private ProgressView progressView;
    private Toolbar toolbar;
    private String userId;
    private PhotoAuthor photoAuthor;

    private DDScannerRestClient.ResultListener<ProfileResponseEntity> resultListener = new DDScannerRestClient.ResultListener<ProfileResponseEntity>() {
        @Override
        public void onSuccess(ProfileResponseEntity result) {
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
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra("id");
        setContentView(R.layout.activity_user_profile);
        progressView = (ProgressView) findViewById(R.id.progress_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.profile);
        DDScannerApplication.getInstance().getDdScannerRestClient().getUserProfileInformation(userId, resultListener);
    }

    public static void show(Context context, String userId) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra("id", userId);
        context.startActivity(intent);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    private void setupFragment(int userType, ProfileResponseEntity user) {
        switch (userType) {
            case 0:

                break;
            case 1:
            case 2:
                photoAuthor = new PhotoAuthor(user.getDiver().getId(), user.getDiver().getName(), user.getDiver().getPhoto());
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(user.getDiver());
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
        PhotosGalleryActivity.show(userId, this, GalleryOpenedSource.USER_PROFILE, new Gson().toJson(photoAuthor));
    }

}

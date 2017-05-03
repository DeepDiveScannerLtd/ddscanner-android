package com.ddscanner.screens.user.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.PhotoAuthor;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.entities.User;
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespots.list.DiveSpotsListActivity;
import com.ddscanner.screens.profile.divecenter.DiveCenterProfileFragment;
import com.ddscanner.screens.profile.user.ProfileFragment;
import com.ddscanner.screens.reiews.list.ReviewsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.PhotosGalleryActivity;
import com.ddscanner.ui.activities.UserLikesDislikesActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import static com.ddscanner.utils.ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_SHOW_EDIT_PROFILE_ACTIVITY;

public class UserProfileActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private ProgressView progressView;
    private Toolbar toolbar;
    private String userId;
    private PhotoAuthor photoAuthor;
    private int userType;
    private ProfileFragment profileFragment;

    private DDScannerRestClient.ResultListener<User> resultListener = new DDScannerRestClient.ResultListener<User>() {
        @Override
        public void onSuccess(User result) {
//            binding.setUserProfileViewModel(new ProfileFragmentViewModel(result));
            progressView.setVisibility(View.GONE);
            setupFragment(result.getType(), result);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
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
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra("id");
        userType = getIntent().getIntExtra("type", 0);
        setContentView(R.layout.activity_user_profile);
        progressView = (ProgressView) findViewById(R.id.progress_view);
        setupToolbar(R.string.profile, R.id.toolbar);
        if (userType != 0) {
            DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserProfileInformation(userId, resultListener);
        } else {
            DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterInformation(userId, diveCenterProfileResultListener);
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
                if (String.valueOf(diveCenterProfile.getId()).equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
                    DiveCenterProfileFragment diveCenterProfileFragment = DiveCenterProfileFragment.newInstance(diveCenterProfile);
                    setActiveFragment(diveCenterProfileFragment);
                    break;
                }
                setActiveFragment(UserDiveCenterProfileFragment.newInstance(diveCenterProfile));
                break;
            case 1:
            case 2:
                User user = (User) object;
                photoAuthor = new PhotoAuthor(user.getId(), user.getName(), user.getPhoto(), user.getType());
                if (user.getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
                    profileFragment = ProfileFragment.newInstance(user);
                    setActiveFragment(profileFragment);
                    break;
                }
                setActiveFragment(UserProfileFragment.newInstance(user));
                break;
        }
    }

    private void setActiveFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
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
//        DDScannerApplication.bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        DDScannerApplication.bus.unregister(this);
    }

    @Subscribe
    public void openPhotosActivity(OpenPhotosActivityEvent event) {
        PhotosGalleryActivity.showForResult(userId, this, PhotoOpenedSource.PROFILE, new Gson().toJson(photoAuthor), ActivitiesRequestCodes.REQUEST_CODE_SHOW_USER_PROFILE_PHOTOS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_CHECKINS:
                if (resultCode == RESULT_OK) {
                    DiveSpotsListActivity.show(this, DiveSpotListSource.CHECKINS, userId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_EDITED:
                if (resultCode == RESULT_OK) {
                    DiveSpotsListActivity.show(this, DiveSpotListSource.EDITED, userId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_CREATED:
                if (resultCode == RESULT_OK) {
                    DiveSpotsListActivity.show(this, DiveSpotListSource.ADDED, userId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_REVIEWS:
                if (resultCode == RESULT_OK) {
                    ReviewsActivity.showForResult(this, userId, -1, ReviewsOpenedSource.USER);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_LIKES:
                if (resultCode == RESULT_OK) {
                    UserLikesDislikesActivity.show(this, true, userId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_DISLIKES:
                if (resultCode == RESULT_OK) {
                    UserLikesDislikesActivity.show(this, false, userId);
                }
                break;
            case REQUEST_CODE_MAIN_ACTIVITY_SHOW_EDIT_PROFILE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    profileFragment.reloadData();
                }
        }
    }
}

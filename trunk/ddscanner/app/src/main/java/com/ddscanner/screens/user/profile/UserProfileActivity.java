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
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveCenterSearchItem;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.PhotoAuthor;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.entities.User;
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.brands.BrandsActivity;
import com.ddscanner.screens.divecemter.profile.languages.DiveCenterProfileLanguagesActivity;
import com.ddscanner.screens.divespots.list.DiveSpotsListActivity;
import com.ddscanner.screens.instructors.InstructorsActivity;
import com.ddscanner.screens.profile.divecenter.DiveCenterProfileFragment;
import com.ddscanner.screens.profile.divecenter.DiveCenterSpotsActivity;
import com.ddscanner.screens.profile.divecenter.tours.list.DailyToursActivity;
import com.ddscanner.screens.profile.user.ProfileFragment;
import com.ddscanner.screens.reiews.list.ReviewsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.PhotosGalleryActivity;
import com.ddscanner.ui.activities.UserLikesDislikesActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.google.android.gms.maps.model.LatLng;
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
    private boolean isDiveCenterLegacy = false;
    private static final String ARG_DIVE_SPOT_ID = "divespot_id";
    private static final String ARG_TYPE = "type";
    private static final String ARG_USER_ID = "user_id";
    private LatLng diveCeneterLocation;

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
            setupFragment(0, result);
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

    public static void show(Context context, String userId, int userType) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(ARG_USER_ID, userId);
        intent.putExtra(ARG_TYPE, userType);
        context.startActivity(intent);
    }

    public static void showForBooking(Context context, String userId, int userType, String diveSpotId) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(ARG_USER_ID, userId);
        intent.putExtra(ARG_TYPE, userType);
        intent.putExtra(ARG_DIVE_SPOT_ID, diveSpotId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra(ARG_USER_ID);
        userType = getIntent().getIntExtra(ARG_TYPE, 0);
        setContentView(R.layout.activity_user_profile);
        progressView = findViewById(R.id.progress_view);
        setupToolbar(R.string.profile, R.id.toolbar);
        switch (userType) {
            case 0:
                if (getIntent().getStringExtra(ARG_DIVE_SPOT_ID) == null) {
                    EventsTracker.trackDiveCenterView(userId, DiveCenterSearchItem.DiveCenterType.USER.getType());
                }
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterInformation(userId, diveCenterProfileResultListener);
                break;
            case 1:
            case 2:
                EventsTracker.trackReviewerProfileView();
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserProfileInformation(userId, resultListener);
                break;
            default:
                isDiveCenterLegacy = true;
                if (getIntent().getStringExtra(ARG_DIVE_SPOT_ID) == null) {
                    EventsTracker.trackDiveCenterView(userId, DiveCenterSearchItem.DiveCenterType.LEGACY.getType());
                }
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getLegacyDiveCenterInformation(userId, diveCenterProfileResultListener);
                break;
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    private void setupFragment(int userType, Object object) {
        try {
            switch (userType) {
                case 0:
                    DiveCenterProfile diveCenterProfile = (DiveCenterProfile) object;
                    diveCeneterLocation = diveCenterProfile.getAddresses().get(0).getPosition();
                    photoAuthor = new PhotoAuthor(String.valueOf(diveCenterProfile.getId()), diveCenterProfile.getName(), diveCenterProfile.getPhoto(), 0);
                    if (String.valueOf(diveCenterProfile.getId()).equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
                        DiveCenterProfileFragment diveCenterProfileFragment = DiveCenterProfileFragment.newInstance(diveCenterProfile);
                        setActiveFragment(diveCenterProfileFragment);
                        break;
                    }
                    if (!isDiveCenterLegacy) {
                        setActiveFragment(UserDiveCenterProfileFragment.newInstance(diveCenterProfile, 1, getIntent().getStringExtra(ARG_DIVE_SPOT_ID)));
                        break;
                    }
                    setActiveFragment(UserDiveCenterProfileFragment.newInstance(diveCenterProfile, 2, null));
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
        } catch (IllegalStateException e) {
            finish();
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
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DC_PROFILE_SHOW_BRANDS:
                if (resultCode == RESULT_OK) {
                    BrandsActivity.show(this, userId, BrandsActivity.BrandSource.DIVECENTER);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DC_PROFILE_SHOW_CREATED:
                if (resultCode == RESULT_OK) {
                    DiveSpotsListActivity.show(this, DiveSpotListSource.ADDED, userId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DC_PROFILE_SHOW_EDITED:
                if (resultCode == RESULT_OK) {
                    DiveSpotsListActivity.show(this, DiveSpotListSource.EDITED, userId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DC_PROFILE_SHOW_INSTRUCTORS:
                if (resultCode == RESULT_OK) {
                    InstructorsActivity.showForResult(this, -1, userId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DC_PROFILE_SHOW_LANGUAGES:
                if (resultCode == RESULT_OK) {
                    DiveCenterProfileLanguagesActivity.show(userId, this);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DC_PROFILE_SHOW_PRODUCTS:
                if (resultCode == RESULT_OK) {
                    DailyToursActivity.show(this, userId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DC_PROFILE_SHOW_WORKING_SPOTS:
                if (resultCode == RESULT_OK) {
                    DiveCenterSpotsActivity.show(this, userId, diveCeneterLocation);
                }
                break;
        }
    }
}

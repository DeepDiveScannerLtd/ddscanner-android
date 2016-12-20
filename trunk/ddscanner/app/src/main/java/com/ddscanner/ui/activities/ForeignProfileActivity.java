package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.entities.UserOld;
import com.ddscanner.entities.UserResponseEntity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.achievements.AchievementsActivity;
import com.ddscanner.ui.adapters.AchievmentProfileListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ForeignProfileActivity extends AppCompatActivity implements View.OnClickListener, InfoDialogFragment.DialogClosedListener {

    private String FACEBOOK_URL;
    private String FACEBOOK_PAGE_ID;
    private Toolbar toolbar;
    private TextView userCommentsCount;
    private TextView userLikesCount;
    private TextView userDislikesCount;
    private ImageView avatar;
    private TextView userFullName;
    private TextView userAbout;
    private TextView checkInCount;
    private TextView addedCount;
    private TextView editedCount;
    private TextView openOn;
    private LinearLayout showAllCheckins;
    private LinearLayout showAllAdded;
    private LinearLayout showAllEdited;
    private LinearLayout openInSocialNetwork;
    private ScrollView aboutLayout;
    private String userId;
    private UserOld userOld;
    private ProgressView progressView;
    private RelativeLayout likeLayout;
    private LinearLayout dislikeLayout;
    private LinearLayout openOnSocialLayout;
    private RecyclerView achievmentRecyclerView;
    private RelativeLayout showAchivementDetails;
    private LinearLayout achievementsLayout;
    private TextView noAchievements;

    private DDScannerRestClient.ResultListener<UserResponseEntity> userResultListener = new DDScannerRestClient.ResultListener<UserResponseEntity>() {
        @Override
        public void onSuccess(UserResponseEntity result) {
            userOld = result.getUserOld();
            FACEBOOK_URL = Constants.PROFILE_DIALOG_FACEBOOK_URL + userOld.getSocialId();
            FACEBOOK_PAGE_ID = userOld.getSocialId();
            setUi(result);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_FOREIGN_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(ForeignProfileActivity.this, ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_FOREIGN_PROFILE_ACTIVITY_UNKNOWN_ERROR, false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_user);
        userId = getIntent().getStringExtra("USERID");
        findViews();
        toolbarSettings();
       // DDScannerApplication.getInstance().getDdScannerRestClient().getUserInformation(userId, userResultListener);
    }

    private void findViews() {
        achievmentRecyclerView = (RecyclerView) findViewById(R.id.achievment_rv);
        checkInCount = (TextView) findViewById(R.id.checkin_count);
        addedCount = (TextView) findViewById(R.id.added_count);
        editedCount = (TextView) findViewById(R.id.edited_count);
        aboutLayout = (ScrollView) findViewById(R.id.about);
        progressView = (ProgressView) findViewById(R.id.progressBarFull);
        userCommentsCount = (TextView) findViewById(R.id.user_comments);
        userLikesCount = (TextView) findViewById(R.id.user_likes);
        userDislikesCount = (TextView) findViewById(R.id.user_dislikes);
        avatar = (ImageView) findViewById(R.id.user_avatar);
        userFullName = (TextView) findViewById(R.id.user_name);
        userAbout = (TextView) findViewById(R.id.user_about);
        showAllCheckins = (LinearLayout) findViewById(R.id.checkins_activity);
        showAllAdded = (LinearLayout) findViewById(R.id.created_activity);
        showAllEdited = (LinearLayout) findViewById(R.id.edited_activity);
        likeLayout = (RelativeLayout) findViewById(R.id.likeLayout);
        dislikeLayout = (LinearLayout) findViewById(R.id.dislikeLayout);
        openOnSocialLayout = (LinearLayout) findViewById(R.id.openSocialNetwork);
        achievementsLayout = (LinearLayout) findViewById(R.id.achievements_layout);
        openOn = (TextView) findViewById(R.id.openOn);
    }

    private void toolbarSettings() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.profile);
    }

    private void setUi(UserResponseEntity userResponseEntity) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        achievmentRecyclerView.setLayoutManager(linearLayoutManager);
        if (userResponseEntity.getAchievements() != null && userResponseEntity.getAchievements().size() > 0) {
            achievementsLayout.setVisibility(View.VISIBLE);
            achievmentRecyclerView.setVisibility(View.VISIBLE);
            achievmentRecyclerView.setAdapter(new AchievmentProfileListAdapter((ArrayList<ProfileAchievement>) userResponseEntity.getAchievements(), this));
        }
        openOnSocialLayout.setOnClickListener(this);
        userAbout.setVisibility(View.GONE);
        addedCount.setText(userOld.getCountAdd() + getDiveSpotString(Integer.parseInt(userOld.getCountAdd())));
        editedCount.setText(userOld.getCountEdit() + getDiveSpotString(Integer.parseInt(userOld.getCountEdit())));
        checkInCount.setText(userOld.getCountCheckin() + getDiveSpotString(Integer.parseInt(userOld.getCountFavorite())));
        if (!userOld.getCountCheckin().equals("0")) {
            showAllCheckins.setOnClickListener(this);
        }
        if (!userOld.getCountEdit().equals("0")) {
            showAllEdited.setOnClickListener(this);
        }
        if (!userOld.getCountAdd().equals("0")) {
            showAllAdded.setOnClickListener(this);
        }

        if (userOld.getAbout() != null && !userOld.getAbout().isEmpty()) {
            userAbout.setVisibility(View.VISIBLE);
            userAbout.setText(userOld.getAbout());
        }
        userCommentsCount.setText(Helpers.formatLikesCommentsCountNumber(userOld.getCountComment()));
        userDislikesCount.setText(Helpers.formatLikesCommentsCountNumber(userOld.getCountDislike()));
        userLikesCount.setText(Helpers.formatLikesCommentsCountNumber(userOld.getCountLike()));
        Picasso.with(this).load(userOld.getPicture())
                .resize(Math.round(Helpers.convertDpToPixel(100, this)),
                        Math.round(Helpers.convertDpToPixel(100, this))).centerCrop()
                .transform(new CropCircleTransformation()).into(avatar);
        userFullName.setText(userOld.getName());
        progressView.setVisibility(View.GONE);
        aboutLayout.setVisibility(View.VISIBLE);
        switch (userOld.getType()) {
            case "fb":
                openOn.setText(R.string.open_on_facebook);
                break;
            case "tw":
                openOn.setText(R.string.open_on_twitter);
                break;
            case "go":
                openOn.setText(R.string.open_on_google_plus);
                break;
        }
    }

    private String getDiveSpotString(int count) {
        if (count > 1 || count == 0) {
            return getString(R.string.dive_spos);
        }
        if (count == 1) {
            return getString(R.string.one_dive_spot);
        }
        return "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void show(Context context, String id){
        Intent intent = new Intent(context, ForeignProfileActivity.class);
        intent.putExtra("USERID", id);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN:
                if (resultCode == RESULT_OK) {
                  //  DDScannerApplication.getInstance().getDdScannerRestClient().getUserInformation(userId, userResultListener);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SEE_CHECKINS:
                if (resultCode == RESULT_OK) {
                    EventsTracker.trackReviewerCheckInsView();
                    ForeignUserDiveSpotList.show(this, false, false, true, userId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SEE_CREATED:
                if (resultCode == RESULT_OK) {
                    EventsTracker.trackUserCreatedView();
                    ForeignUserDiveSpotList.show(this, false, true, false, userId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SEE_EDITED:
                if (resultCode == RESULT_OK) {
                    EventsTracker.trackReviewerEditedView();
                    ForeignUserDiveSpotList.show(this, true, false, false, userId);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkins_activity:
                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
                    EventsTracker.trackReviewerCheckInsView();
                    ForeignUserDiveSpotList.show(this, false, false, true, userId);
                } else {
                    Intent intent = new Intent(ForeignProfileActivity.this, LoginActivity.class);
                    startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SEE_CHECKINS);
                }
                break;
            case R.id.created_activity:
                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
                    EventsTracker.trackUserCreatedView();
                    ForeignUserDiveSpotList.show(this, false, true, false, userId);
                } else {
                    Intent intent = new Intent(ForeignProfileActivity.this, LoginActivity.class);
                    startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SEE_CREATED);
                }
                break;
            case R.id.edited_activity:
                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
                    EventsTracker.trackReviewerEditedView();
                    ForeignUserDiveSpotList.show(this, true, false, false, userId);
                } else {
                    Intent intent = new Intent(ForeignProfileActivity.this, LoginActivity.class);
                    startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SEE_EDITED);
                }
                break;
            case R.id.openSocialNetwork:
                openLink(userOld.getSocialId(), userOld.getType());
                break;
            case R.id.show_achievments_details:
                AchievementsActivity.show(this);
                break;
        }
    }

    private void openLink(String userName, String socialNetwork) {
        switch (socialNetwork) {
            case "tw":
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(Constants.PROFILE_DIALOG_TWITTER_URI + userName));
                    startActivity(intent);

                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(Constants.PROFILE_DIALOG_TWITTER_URL + userName)));
                }
                break;
            case "go":
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Constants.PROFILE_DIALOG_GOOGLE_URL + userName));
                    intent.setPackage("com.google.android.apps.plus");
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW, Uri.parse(Constants.PROFILE_DIALOG_GOOGLE_URL + userName)));
                }
                break;
            case "fb":
                EventsTracker.trackReviewrFacebookOpened();
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(this);
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
                break;
        }
    }

    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                return Constants.PROFILE_DIALOG_FACEBOOK_OLD_URI + FACEBOOK_URL;
            } else {
                return Constants.PROFILE_DIALOG_FACEBOOK_NEW_URI + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_FOREIGN_PROFILE_ACTIVITY_UNKNOWN_ERROR:
            case DialogsRequestCodes.DRC_FOREIGN_PROFILE_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }
}

package com.ddscanner.ui.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.AchievmentProfile;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.entities.User;
import com.ddscanner.entities.UserResponseEntity;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.LoadUserProfileInfoEvent;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.AboutActivity;
import com.ddscanner.ui.activities.AchievementsActivity;
import com.ddscanner.ui.activities.DiveSpotsListActivity;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.activities.SelfCommentsActivity;
import com.ddscanner.ui.activities.UserLikesDislikesActivity;
import com.ddscanner.ui.adapters.AchievmentProfileListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.views.CustomSwipeRefreshLayout;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by lashket on 20.4.16.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener, LoginView.LoginStateChangeListener, InfoDialogFragment.DialogClosedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ProfileFragment.class.getName();

    private static final int MAX_LENGTH_NAME = 30;
    private static final int MAX_LENGTH_ABOUT = 250;

    private LinearLayout editProfile;
    private ScrollView aboutLayout;
    private ScrollView editLayout;
    private LinearLayout logout;
    private LinearLayout capturePhoto;
    private ImageView newPhoto;
    private Button cancelButton;
    private User user;
    private TextView userCommentsCount;
    private TextView userLikesCount;
    private TextView userDislikesCount;
    private TextView nameLeftSymbols;
    private TextView aboutLeftSymbols;
    private ImageView avatar;
    private EditText aboutEdit;
    private EditText fullNameEdit;
    private Button saveChanges;
    private TextView userFullName;
    private TextView userAbout;
    private TextView checkInCount;
    private TextView favouriteCount;
    private TextView addedCount;
    private TextView editedCount;
    private LinearLayout pickPhotoFromGallery;
    private LinearLayout showAllCheckins;
    private LinearLayout showAllFavorites;
    private LinearLayout showAllAdded;
    private LinearLayout showAllEdited;
    private LinearLayout aboutDDsLayout;
    private RelativeLayout likeLayout;
    private LinearLayout dislikeLayout;
    private LinearLayout commentsLayout;
    private CustomSwipeRefreshLayout swipeRefreshLayout;
    private TextView error_name;
    private TextView error_about;
    private RelativeLayout loginView;
    private RecyclerView achievmentRecyclerView;
    private RelativeLayout showAchivementDetails;
    private TextView noAchievements;
    private boolean isClickedChosingPhotoButton = false;
    private boolean isAboutChanged = false;
    private boolean isNamChanged = false;

    private RequestBody requestSecret = null;
    private RequestBody requestSocial = null;
    private RequestBody requestToken = null;
    private RequestBody about = null;
    private RequestBody username = null;
    private RequestBody name = null;
    private RequestBody requestType = null;
    private MultipartBody.Part image = null;
    private MaterialDialog materialDialog;
    private Map<String, TextView> errorsMap = new HashMap<>();

    private String uri = null;
    private Uri uriFromCamera = null;

    private DDScannerRestClient.ResultListener<UserResponseEntity> updateProfileInfoResultListener = new DDScannerRestClient.ResultListener<UserResponseEntity>() {
        @Override
        public void onSuccess(UserResponseEntity result) {
            materialDialog.dismiss();
            uri = null;
            changeUi(result);
            aboutLayout.setVisibility(View.VISIBLE);
            aboutLayout.scrollTo(0,0);
            editLayout.setVisibility(View.GONE);
            getUserDataRequest(SharedPreferenceHelper.getUserServerId());
            swipeRefreshLayout.setEnabled(true);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.show(getFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    onLoggedOut();
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getFragmentManager(), url, errorMessage);
                    break;
            }
        }
    };

    private DDScannerRestClient.ResultListener<Void> logoutReslutListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            aboutLayout.setVisibility(View.GONE);
            SharedPreferenceHelper.logout();
            DDScannerApplication.bus.post(new LoggedOutEvent());
            DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
            swipeRefreshLayout.setEnabled(false);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.show(getFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            Helpers.handleUnexpectedServerError(getFragmentManager(), url, errorMessage);
        }
    };

    private DDScannerRestClient.ResultListener<UserResponseEntity> getUserInformationResultListener = new DDScannerRestClient.ResultListener<UserResponseEntity>() {
        @Override
        public void onSuccess(UserResponseEntity result) {
            if (editLayout.getVisibility() != View.VISIBLE) {
                aboutLayout.setVisibility(View.VISIBLE);
            }
            changeUi(result);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForFragmentResult(getFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    DDScannerApplication.bus.post(new LoggedOutEvent());
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForFragmentResult(getFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_UNEXPECTED_ERROR, false);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "ProfileFragment onCreateView, this = " + this);
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(v);
        editProfile.setOnClickListener(this);
        capturePhoto.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        saveChanges.setOnClickListener(this);
        pickPhotoFromGallery.setOnClickListener(this);
        logout.setOnClickListener(this);
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            getUserDataRequest(SharedPreferenceHelper.getUserServerId());
        }
        materialDialog = Helpers.getMaterialDialog(getContext());
        createErrorsMap();
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            onLoggedIn();
        } else {
            onLoggedOut();
        }
        return v;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.i(TAG, "onAttach(Context context)");
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

        Log.i(TAG, "onAttach(Activity context)");
        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(context);
        }
    }

    protected void onAttachToContext(Context context) {
        try {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.setProfileFragment(this);
        } catch (ClassCastException e) {
            // waaat?
            e.printStackTrace();
        }
    }

    private void findViews(View v) {
        noAchievements = (TextView) v.findViewById(R.id.no_achievements_view);
        showAchivementDetails = (RelativeLayout) v.findViewById(R.id.show_achievments_details);
        checkInCount = (TextView) v.findViewById(R.id.checkin_count);
        achievmentRecyclerView = (RecyclerView) v.findViewById(R.id.achievment_rv);
        addedCount = (TextView) v.findViewById(R.id.added_count);
        favouriteCount = (TextView) v.findViewById(R.id.favourites_count);
        editedCount = (TextView) v.findViewById(R.id.edited_count);
        aboutLayout = (ScrollView) v.findViewById(R.id.about);
        editLayout = (ScrollView) v.findViewById(R.id.editProfile);
        editProfile = (LinearLayout) v.findViewById(R.id.edit_profile);
        capturePhoto = (LinearLayout) v.findViewById(R.id.capture_photo);
        newPhoto = (ImageView) v.findViewById(R.id.user_chosed_photo);
        cancelButton = (Button) v.findViewById(R.id.cancel_button);
        userCommentsCount = (TextView) v.findViewById(R.id.user_comments);
        userLikesCount = (TextView) v.findViewById(R.id.user_likes);
        userDislikesCount = (TextView) v.findViewById(R.id.user_dislikes);
        avatar = (ImageView) v.findViewById(R.id.user_avatar);
        fullNameEdit = (EditText) v.findViewById(R.id.fullName);
        aboutEdit = (EditText) v.findViewById(R.id.aboutEdit);
        saveChanges = (Button) v.findViewById(R.id.button_save);
        userFullName = (TextView) v.findViewById(R.id.user_name);
        userAbout = (TextView) v.findViewById(R.id.user_about);
        pickPhotoFromGallery = (LinearLayout) v.findViewById(R.id.pick_photo_from_gallery);
        logout = (LinearLayout) v.findViewById(R.id.logout);
        showAllCheckins = (LinearLayout) v.findViewById(R.id.checkins_activity);
        showAllFavorites = (LinearLayout) v.findViewById(R.id.favorites_activity);
        showAllAdded = (LinearLayout) v.findViewById(R.id.created_activity);
        showAllEdited = (LinearLayout) v.findViewById(R.id.edited_activity);
        error_about = (TextView) v.findViewById(R.id.error_about);
        error_name = (TextView) v.findViewById(R.id.error_name);
        nameLeftSymbols = (TextView) v.findViewById(R.id.name_count);
        aboutLeftSymbols = (TextView) v.findViewById(R.id.about_count);
        loginView = (RelativeLayout) v.findViewById(R.id.login_view_root);
        aboutDDsLayout = (LinearLayout) v.findViewById(R.id.about_dss_layout);
        aboutDDsLayout.setOnClickListener(this);
        likeLayout = (RelativeLayout) v.findViewById(R.id.likeLayout);
        dislikeLayout = (LinearLayout) v.findViewById(R.id.dislikeLayout);
        commentsLayout = (LinearLayout) v.findViewById(R.id.comments_layout);
        swipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setSwipeableChildren(R.id.about);

        swipeRefreshLayout.setOnRefreshListener(this);
        aboutEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isAboutChanged) {
                    aboutLeftSymbols.setVisibility(View.VISIBLE);
                }
                if (MAX_LENGTH_ABOUT - aboutEdit.length() < 10) {
                    aboutLeftSymbols.setTextColor(ContextCompat.getColor(getContext(), R.color.tw__composer_red));
                } else {
                    aboutLeftSymbols.setTextColor(Color.parseColor("#b2b2b2"));
                }
                aboutLeftSymbols.setText(String.valueOf(MAX_LENGTH_ABOUT - aboutEdit.length()));
                isAboutChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fullNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isNamChanged) {
                    nameLeftSymbols.setVisibility(View.VISIBLE);
                }
                if (MAX_LENGTH_NAME - fullNameEdit.length() < 10) {
                    nameLeftSymbols.setTextColor(ContextCompat.getColor(getContext(), R.color.tw__composer_red));
                } else {
                    nameLeftSymbols.setTextColor(Color.parseColor("#b2b2b2"));
                }
                nameLeftSymbols.setText(String.valueOf(MAX_LENGTH_NAME - fullNameEdit.length()));
                isNamChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile:
                swipeRefreshLayout.setEnabled(false);
                aboutLayout.setVisibility(View.GONE);
                editLayout.setVisibility(View.VISIBLE);
                fullNameEdit.setText(user.getName());
                fullNameEdit.setSelection(user.getName().length());
                if (user.getAbout() != null) {
                    aboutEdit.setText(user.getAbout());
                    aboutEdit.setSelection(user.getAbout().length());
                }
                nameLeftSymbols.setVisibility(View.GONE);
                aboutLeftSymbols.setVisibility(View.GONE);
                break;
            case R.id.capture_photo:
                isClickedChosingPhotoButton = true;
                DDScannerApplication.bus.post(new TakePhotoFromCameraEvent());
                break;
            case R.id.cancel_button:
                swipeRefreshLayout.setEnabled(true);
                isClickedChosingPhotoButton = false;
                aboutLayout.setVisibility(View.VISIBLE);
                aboutLayout.scrollTo(0,0);
                editLayout.setVisibility(View.GONE);
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                break;
            case R.id.button_save:
                createUpdateRequest();
                break;
            case R.id.pick_photo_from_gallery:
                isClickedChosingPhotoButton = true;
                DDScannerApplication.bus.post(new PickPhotoFromGallery());
                break;
            case R.id.logout:
                materialDialog.show();
                SharedPreferenceHelper.setLastShowingNotificationTime(0);
                DDScannerApplication.getDdScannerRestClient().postLogout(logoutReslutListener);
                break;
            case R.id.checkins_activity:
                EventsTracker.trackUserCheckinsView();
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.CHECKINS, EventsTracker.SpotViewSource.FROM_PROFILE_CHECKINS   );
                break;
            case R.id.favorites_activity:
                EventsTracker.trackUserFavoritesView();
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.FAVORITES, EventsTracker.SpotViewSource.FROM_PROFILE_FAVOURITES);
                break;
            case R.id.edited_activity:
                EventsTracker.trackUserEditedView();
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.EDITED, EventsTracker.SpotViewSource.FROM_PROFILE_EDITED);
                break;
            case R.id.created_activity:
                EventsTracker.trackUserCreatedView();
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.ADDED, EventsTracker.SpotViewSource.FROM_PROFILE_CREATED);
                break;
            case R.id.about_dss_layout:
                AboutActivity.show(getContext());
                break;
            case R.id.likeLayout:
                EventsTracker.trackUserLikesView();
                UserLikesDislikesActivity.show(getActivity(), true, user.getId());
                break;
            case R.id.dislikeLayout:
                EventsTracker.trackUserDislikesView();
                UserLikesDislikesActivity.show(getActivity(), false, user.getId());
                break;
            case R.id.comments_layout:
                SelfCommentsActivity.show(getContext(), user.getId());
                break;
            case R.id.show_achievments_details:
                AchievementsActivity.show(getContext(), user.getId());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.i(TAG, "ProfileFragment onCreateView, this = " + this);
//        if (SharedPreferenceHelper.isUserLoggedIn()) {
//            if (!isClickedChosingPhotoButton) {
//             //   getUserDataRequest(SharedPreferenceHelper.getUserServerId());
//            }
//        }
//        if (!getUserVisibleHint()) {
//            return;
//        }
    }

    @Subscribe
    public void getUserProfileInfo(LoadUserProfileInfoEvent event) {
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            if (!isClickedChosingPhotoButton) {
                getUserDataRequest(SharedPreferenceHelper.getUserServerId());
            }
        } else {
            onLoggedOut();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ProfileFragment onStart, this = " + this);
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ProfileFragment onStop, this = " + this);
        DDScannerApplication.bus.unregister(this);
    }

    public void setImage(String uri) {
        Picasso.with(getContext()).load("file://" + uri)
                .resize(Math.round(Helpers.convertDpToPixel(80, getContext())),
                        Math.round(Helpers.convertDpToPixel(80, getContext()))).centerCrop()
                .transform(new CropCircleTransformation()).into(newPhoto);
        this.uri = uri;
        this.uriFromCamera = null;
    }

    public void setImage(Uri uri) {
        Picasso.with(getContext()).load(uri)
                .resize(Math.round(Helpers.convertDpToPixel(80, getContext())),
                        Math.round(Helpers.convertDpToPixel(80, getContext()))).centerCrop()
                .transform(new CropCircleTransformation()).into(newPhoto);
        this.uriFromCamera = uri;
        this.uri = null;
    }

    private void getUserDataRequest(String id) {
        DDScannerApplication.getDdScannerRestClient().getUserInformation(id, getUserInformationResultListener);
    }

    private void changeUi(UserResponseEntity userResponseEntity) {
        if (getContext() == null) {
            return;
        }
        this.user = userResponseEntity.getUser();
        showAchivementDetails.setOnClickListener(this);
        ArrayList<ProfileAchievement> achievmentProfiles = new ArrayList<>();
        if (userResponseEntity.getAchievements() != null && userResponseEntity.getAchievements().size() > 0) {
            achievmentProfiles = (ArrayList<ProfileAchievement>) userResponseEntity.getAchievements();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            achievmentRecyclerView.setLayoutManager(linearLayoutManager);
            achievmentRecyclerView.setAdapter(new AchievmentProfileListAdapter(achievmentProfiles, getContext()));
            noAchievements.setVisibility(View.GONE);
            achievmentRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noAchievements.setVisibility(View.VISIBLE);
            achievmentRecyclerView.setVisibility(View.GONE);
        }
        if (user != null) {
            if (!user.getCountLike().equals("0")) {
                likeLayout.setOnClickListener(this);
            }
            if (!user.getCountDislike().equals("0")) {
                dislikeLayout.setOnClickListener(this);
            }
            if (!user.getCountComment().equals("0")) {
                commentsLayout.setOnClickListener(this);
            }
            if (user.getPicture() == null) {
                Picasso.with(getContext()).load(R.drawable.avatar_profile_default)
                        .resize(Math.round(Helpers.convertDpToPixel(100, getContext())),
                                Math.round(Helpers.convertDpToPixel(100, getContext()))).centerCrop()
                        .placeholder(R.drawable.avatar_profile_default)
                        .transform(new CropCircleTransformation()).into(avatar);
            } else {
                Picasso.with(getContext()).load(user.getPicture())
                        .resize(Math.round(Helpers.convertDpToPixel(80, getContext())),
                                Math.round(Helpers.convertDpToPixel(80, getContext()))).centerCrop()
                        .placeholder(R.drawable.avatar_profile_default)
                        .error(R.drawable.avatar_profile_default)
                        .transform(new CropCircleTransformation()).into(avatar);
            }
            userCommentsCount.setText(Helpers.formatLikesCommentsCountNumber(user.getCountComment()));
            userLikesCount.setText(Helpers.formatLikesCommentsCountNumber(user.getCountLike()));
            userDislikesCount.setText(Helpers.formatLikesCommentsCountNumber(user.getCountDislike()));
            Picasso.with(getContext()).load(user.getPicture())
                    .resize(Math.round(Helpers.convertDpToPixel(80, getContext())),
                            Math.round(Helpers.convertDpToPixel(80, getContext()))).centerCrop()
                    .placeholder(R.drawable.avatar_profile_default)
                    .error(R.drawable.avatar_profile_default)
                    .transform(new CropCircleTransformation()).into(newPhoto);
            if (user.getAbout() != null && !user.getAbout().isEmpty()) {
                userAbout.setVisibility(View.VISIBLE);
                userAbout.setText(user.getAbout());
            } else {
                userAbout.setVisibility(View.GONE);
            }
            userFullName.setText(user.getName());
            addedCount.setText( user.getCountAdd() + getDiveSpotString(Integer.parseInt(user.getCountAdd())));
            editedCount.setText(user.getCountEdit() + getDiveSpotString(Integer.parseInt(user.getCountEdit())));
            favouriteCount.setText(user.getCountFavorite() + getDiveSpotString(Integer.parseInt(user.getCountFavorite())));
            checkInCount.setText(user.getCountCheckin() + getDiveSpotString(Integer.parseInt(user.getCountFavorite())));
            showAllCheckins.setOnClickListener(this);
            if (Integer.parseInt(user.getCountCheckin()) == 0) {
                showAllCheckins.setOnClickListener(null);
            }
            showAllFavorites.setOnClickListener(this);

            if (user.getCountFavorite() == null || Integer.parseInt(user.getCountFavorite()) == 0) {
                showAllFavorites.setOnClickListener(null);
            }
            showAllEdited.setOnClickListener(this);
            if (Integer.parseInt(user.getCountEdit()) == 0) {
                showAllEdited.setOnClickListener(null);
            }
            showAllAdded.setOnClickListener(this);
            if (Integer.parseInt(user.getCountAdd()) == 0) {
                showAllAdded.setOnClickListener(null);
            }
        } else {
            SharedPreferenceHelper.logout();
        }
    }

    @Override
    public void setUserVisibleHint(final boolean visible) {
        super.setUserVisibleHint(visible);
//        Log.i(TAG, "ProfileFragment setUserVisibleHint, this = " + this);
//        if (visible) {
//            if (SharedPreferenceHelper.isUserLoggedIn()) {
//                getUserDataRequest(SharedPreferenceHelper.getUserServerId());
//            }
//        }
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

    private void createUpdateRequest() {
        isClickedChosingPhotoButton = false;
        materialDialog.show();
        if (!aboutEdit.equals(user.getAbout())) {
            about = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    aboutEdit.getText().toString());
        }
        if (!fullNameEdit.equals(user.getName()) && !fullNameEdit.getText().toString().equals("")) {
            name = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    fullNameEdit.getText().toString());
        }

        if (SharedPreferenceHelper.isUserLoggedIn()) {
            requestSocial = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    SharedPreferenceHelper.getSn());
            requestToken = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    SharedPreferenceHelper.getToken());
        }

        if (uri != null) {
            File file;
            if (!uri.toString().contains("file:")) {
                file = new File(uri);
            } else {
                file = new File(uri);
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        if (uriFromCamera != null) {
            File file;
            if (!uriFromCamera.toString().contains("file:")) {
                file = new File(Helpers.getRealPathFromURI(getContext(), uriFromCamera));
            } else {
                file = new File(uriFromCamera.getPath());
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        requestType = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), "PUT");

        DDScannerApplication.getDdScannerRestClient().putUpdateUserProfileInfo(updateProfileInfoResultListener, SharedPreferenceHelper.getUserServerId(), image, requestType,  name, username, about, requestToken, requestSocial);
    }


    private void createErrorsMap() {
        errorsMap.put("name", error_name);
        errorsMap.put("about", error_about);
    }

    @Override
    public void onLoggedIn() {
        Log.i(TAG, "ProfileFragment onLoggedIn, this = " + this);
        if (loginView != null && aboutLayout != null) {
            loginView.setVisibility(View.GONE);
            swipeRefreshLayout.setEnabled(true);
            DDScannerApplication.getDdScannerRestClient().getUserInformation(SharedPreferenceHelper.getUserServerId(), getUserInformationResultListener);
            if (editLayout.getVisibility() != View.VISIBLE) {
                aboutLayout.setVisibility(View.VISIBLE);
            } else {
                aboutLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoggedOut() {
        if (loginView != null && aboutLayout != null) {
            swipeRefreshLayout.setEnabled(false);
            loginView.setVisibility(View.VISIBLE);
            aboutLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {

    }

    @Override
    public void onRefresh() {
        DDScannerApplication.getDdScannerRestClient().getUserInformation(SharedPreferenceHelper.getUserServerId(), getUserInformationResultListener);
    }
}

package com.ddscanner.screens.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.FragmentProfileBinding;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.entities.ProfileResponseEntity;
import com.ddscanner.entities.User;
import com.ddscanner.entities.UserOld;
import com.ddscanner.entities.UserResponseEntity;
import com.ddscanner.events.LoadUserProfileInfoEvent;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.achievements.AchievementsActivity;
import com.ddscanner.ui.activities.AboutActivity;
import com.ddscanner.events.ChangeLoginViewEvent;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.adapters.AchievmentProfileListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.fragments.BaseFragment;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileFragment extends BaseFragment implements View.OnClickListener, LoginView.LoginStateChangeListener, InfoDialogFragment.DialogClosedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ProfileFragment.class.getName();

    private static final int MAX_LENGTH_NAME = 30;
    private static final int MAX_LENGTH_ABOUT = 250;

    private UserOld userOld;
    private boolean isClickedChosingPhotoButton = false;
    private boolean isAboutChanged = false;
    private boolean isNamChanged = false;

    private MaterialDialog materialDialog;
    private Map<String, TextView> errorsMap = new HashMap<>();

    private String uri = null;
    private Uri uriFromCamera = null;
    private User user;

    private FragmentProfileBinding binding;

    ColorStateList colorStateList;


    private DDScannerRestClient.ResultListener<UserResponseEntity> updateProfileInfoResultListener = new DDScannerRestClient.ResultListener<UserResponseEntity>() {
        @Override
        public void onSuccess(UserResponseEntity result) {
//            materialDialog.dismiss();
//            uri = null;
//            changeUi(result);
//            aboutLayout.setVisibility(View.VISIBLE);
//            aboutLayout.scrollTo(0,0);
//            editLayout.setVisibility(View.GONE);
//            getUserDataRequest(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId());
//            swipeRefreshLayout.setEnabled(true);
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
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    onLoggedOut();
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getFragmentManager(), url, errorMessage);
                    break;
            }
        }
    };

    private DDScannerRestClient.ResultListener<ProfileResponseEntity> userResultListener = new DDScannerRestClient.ResultListener<ProfileResponseEntity>() {
        @Override
        public void onSuccess(ProfileResponseEntity result) {
            if (binding != null && binding.editProfileLayout.getVisibility() != View.VISIBLE) {
                binding.about.setVisibility(View.VISIBLE);
            }
            switch (result.getType()) {
                case 2:
                case 1:
                    user = result.getDiver();
                    user.setToken(DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
                    user.setType(result.getType());
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setActiveUser(user);
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setActiveUserType(result.getType());
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsUserLoggedIn(true);
                    break;
                case 0:
                    break;
            }
            binding.setProfileFragmentViewModel(new ProfileFragmentViewModel(result.getDiver()));
            binding.swiperefresh.setRefreshing(false);
            changeUi();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    DDScannerApplication.bus.post(new LoggedOutEvent());
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_UNEXPECTED_ERROR, false);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "ProfileFragment onCreateView, this = " + this);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View v = binding.getRoot();
        colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        ContextCompat.getColor(getContext(), R.color.radio_button_empty)
                        , ContextCompat.getColor(getContext(), R.color.radio_button_fill),
                }
        );
        setupUi();
        binding.setHandlers(this);

        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn() && DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == 1) {
            getUserDataRequest();
        }
        
        createErrorsMap();
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        userResultListener.setCancelled(true);
        updateProfileInfoResultListener.setCancelled(true);
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

    private void setupRadioButtons(String title) {
        AppCompatRadioButton button = new AppCompatRadioButton(getContext());
        button.setSupportButtonTintList(colorStateList);
        button.setText(title);
        button.setPadding(Math.round(Helpers.convertDpToPixel(15, getContext())), Math.round(Helpers.convertDpToPixel(10, getContext())), Math.round(Helpers.convertDpToPixel(20, getContext())), Math.round(Helpers.convertDpToPixel(10, getContext())));
        binding.radiogroup.addView(button);
    }

    private void setupUi() {
        setupRadioButtons("Diver");
        setupRadioButtons("Instructor");
        binding.swiperefresh.setOnRefreshListener(this);
        binding.aboutEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isAboutChanged) {
                    binding.aboutCount.setVisibility(View.VISIBLE);
                }
                if (MAX_LENGTH_ABOUT - binding.aboutEdit.length() < 10) {
                    binding.aboutCount.setTextColor(ContextCompat.getColor(getContext(), R.color.tw__composer_red));
                } else {
                    binding.aboutCount.setTextColor(Color.parseColor("#b2b2b2"));
                }
                binding.aboutCount.setText(String.valueOf(MAX_LENGTH_ABOUT - binding.aboutEdit.length()));
                isAboutChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isNamChanged) {
                    binding.nameCount.setVisibility(View.VISIBLE);
                }
                if (MAX_LENGTH_NAME - binding.fullName.length() < 10) {
                    binding.nameCount.setTextColor(ContextCompat.getColor(getContext(), R.color.tw__composer_red));
                } else {
                    binding.nameCount.setTextColor(Color.parseColor("#b2b2b2"));
                }
                binding.nameCount.setText(String.valueOf(MAX_LENGTH_NAME - binding.fullName.length()));
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
            case R.id.button_save:
             //   createUpdateRequest();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.i(TAG, "ProfileFragment onCreateView, this = " + this);
//        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
//            if (!isClickedChosingPhotoButton) {
//             //   getUserDataRequest(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId());
//            }
//        }
//        if (!getUserVisibleHint()) {
//            return;
//        }

        userResultListener.setCancelled(false);
        updateProfileInfoResultListener.setCancelled(false);
    }

    @Subscribe
    public void changeLoginView(ChangeLoginViewEvent event) {
        binding.loginView.changeViewToStart();
    }

    @Subscribe
    public void getUserProfileInfo(LoadUserProfileInfoEvent event) {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            if (!isClickedChosingPhotoButton) {
                getUserDataRequest();
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
                .transform(new CropCircleTransformation()).into(binding.userChosedPhoto);
        this.uri = uri;
        this.uriFromCamera = null;
    }

    public void setImage(Uri uri) {
        Picasso.with(getContext()).load(uri)
                .resize(Math.round(Helpers.convertDpToPixel(80, getContext())),
                        Math.round(Helpers.convertDpToPixel(80, getContext()))).centerCrop()
                .transform(new CropCircleTransformation()).into(binding.userChosedPhoto);
        this.uriFromCamera = uri;
        this.uri = null;
    }

    private void getUserDataRequest() {
     //   DDScannerApplication.getDdScannerRestClient().getUserInformation(id, getUserInformationResultListener);
        DDScannerApplication.getInstance().getDdScannerRestClient().getUserSelfInformation(userResultListener);
    }

    private void changeUi() {
        if (getContext() == null) {
            return;
        }
        ArrayList<ProfileAchievement> achievmentProfiles = new ArrayList<>();
        if (binding.getProfileFragmentViewModel().getUser().getAchievements() != null && binding.getProfileFragmentViewModel().getUser().getAchievements().size() > 0) {
            achievmentProfiles = (ArrayList<ProfileAchievement>) binding.getProfileFragmentViewModel().getUser().getAchievements();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            binding.achievmentRv.setLayoutManager(linearLayoutManager);
            binding.achievmentRv.setAdapter(new AchievmentProfileListAdapter(achievmentProfiles, getContext()));
            binding.noAchievementsView.setVisibility(View.GONE);
            binding.achievmentRv.setVisibility(View.VISIBLE);
        } else {
            binding.noAchievementsView.setVisibility(View.VISIBLE);
            binding.achievmentRv.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserVisibleHint(final boolean visible) {
        super.setUserVisibleHint(visible);
//        Log.i(TAG, "ProfileFragment setUserVisibleHint, this = " + this);
//        if (visible) {
//            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
//                getUserDataRequest(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId());
//            }
//        }
    }

//    private void createUpdateRequest() {
//        isClickedChosingPhotoButton = false;
//        materialDialog.show();
//        if (!aboutEdit.equals(userOld.getAbout())) {
//            about = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
//                    aboutEdit.getText().toString());
//        }
//        if (!fullNameEdit.equals(userOld.getName()) && !fullNameEdit.getText().toString().equals("")) {
//            name = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
//                    fullNameEdit.getText().toString());
//        }
//
//        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
//            requestSocial = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
//                    DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
//            requestToken = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
//                    DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
//        }
//
//        if (uri != null) {
//            File file;
//            if (!uri.toString().contains("file:")) {
//                file = new File(uri);
//            } else {
//                file = new File(uri);
//            }
//            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
//            image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//        }
//
//        if (uriFromCamera != null) {
//            File file;
//            if (!uriFromCamera.toString().contains("file:")) {
//                file = new File(Helpers.getRealPathFromURI(getContext(), uriFromCamera));
//            } else {
//                file = new File(uriFromCamera.getPath());
//            }
//            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
//            image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//        }
//
//        requestType = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), "PUT");
//
//        DDScannerApplication.getDdScannerRestClient().putUpdateUserProfileInfo(updateProfileInfoResultListener, DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId(), image, requestType,  name, username, about, requestToken, requestSocial);
//    }


    private void createErrorsMap() {
        errorsMap.put("name", binding.errorName);
        errorsMap.put("about", binding.errorAbout);
    }

    @Override
    public void onLoggedIn() {
        Log.i(TAG, "ProfileFragment onLoggedIn, this = " + this);
        if (binding != null && binding.loginView != null && binding.about != null) {
            binding.loginView.setVisibility(View.GONE);
            binding.swiperefresh.setEnabled(true);
            DDScannerApplication.getInstance().getDdScannerRestClient().getUserSelfInformation(userResultListener);
          //  DDScannerApplication.getDdScannerRestClient().getUserInformation(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId(), getUserInformationResultListener);
            if (binding.editProfileLayout.getVisibility() != View.VISIBLE) {
                binding.about.setVisibility(View.VISIBLE);
            } else {
                binding.about.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoggedOut() {
        if (binding != null && binding.loginView != null && binding.about != null) {
            binding.swiperefresh.setEnabled(false);
            binding.loginView.setVisibility(View.VISIBLE);
            binding.about.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {

    }

    @Override
    public void onRefresh() {
        DDScannerApplication.getInstance().getDdScannerRestClient().getUserSelfInformation(userResultListener);
      //  DDScannerApplication.getDdScannerRestClient().getUserInformation(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId(), getUserInformationResultListener);
    }

    public void logoutUser(View view) {
        DDScannerApplication.getInstance().getSharedPreferenceHelper().setLastShowingNotificationTime(0);
        DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
        onLoggedOut();
    }

    public void showComments(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getCommentsCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showLikes(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getLikesCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showDislikes(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getDislikesCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showCheckinns(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getCheckinsCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showAdded(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getAddedCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showEdited(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getEditedCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showFavorites(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getFavoritesCount() > 0) {
            //TODO show comments activity
        }
    }

    public void cancelButtonClicked(View view) {
        binding.swiperefresh.setEnabled(true);
        isClickedChosingPhotoButton = false;
        binding.about.setVisibility(View.VISIBLE);
        binding.about.scrollTo(0,0);
        binding.editProfileLayout.setVisibility(View.GONE);
        View currentView = getActivity().getCurrentFocus();
        if (currentView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        }
    }

    public void showEditLayout(View view) {
        binding.swiperefresh.setEnabled(false);
        binding.about.setVisibility(View.GONE);
        binding.editProfileLayout.setVisibility(View.VISIBLE);
        binding.nameCount.setVisibility(View.GONE);
        binding.aboutCount.setVisibility(View.GONE);
    }

    public void showAboutDDSButtonClicked(View view) {
        AboutActivity.show(getContext());
    }

    public void capturePhoto(View view) {
        isClickedChosingPhotoButton = true;
        DDScannerApplication.bus.post(new TakePhotoFromCameraEvent());
    }

    public void pickPhotoFromGallery(View view) {
        isClickedChosingPhotoButton = true;
        DDScannerApplication.bus.post(new PickPhotoFromGallery());
    }

    public void showAchievementsDetails(View view) {
        AchievementsActivity.show(getContext());
    }

}

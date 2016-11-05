package com.ddscanner.ui.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
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
import com.ddscanner.databinding.FragmentProfileBinding;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.entities.User;
import com.ddscanner.entities.UserOld;
import com.ddscanner.entities.UserResponseEntity;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.LoadUserProfileInfoEvent;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.AboutActivity;
import com.ddscanner.ui.activities.AchievementsActivity;
import com.ddscanner.ui.activities.ChangeLoginViewEvent;
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

    private UserOld userOld;
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

    private FragmentProfileBinding binding;

    private DDScannerRestClient.ResultListener<UserResponseEntity> updateProfileInfoResultListener = new DDScannerRestClient.ResultListener<UserResponseEntity>() {
        @Override
        public void onSuccess(UserResponseEntity result) {
//            materialDialog.dismiss();
//            uri = null;
//            changeUi(result);
//            aboutLayout.setVisibility(View.VISIBLE);
//            aboutLayout.scrollTo(0,0);
//            editLayout.setVisibility(View.GONE);
//            getUserDataRequest(SharedPreferenceHelper.getUserServerId());
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
         //   aboutLayout.setVisibility(View.GONE);
            SharedPreferenceHelper.logout();
            DDScannerApplication.bus.post(new LoggedOutEvent());
            DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
            binding.swiperefresh.setEnabled(false);
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

    private DDScannerRestClient.ResultListener<User> userResultListener = new DDScannerRestClient.ResultListener<User>() {
        @Override
        public void onSuccess(User result) {
            result.setPhoto("https://pp.vk.me/c626824/v626824069/3fae/lZ_07Lvm9MA.jpg");
            binding.setUserViewModel(new UserViewModel(result));
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }
    };

    private DDScannerRestClient.ResultListener<UserResponseEntity> getUserInformationResultListener = new DDScannerRestClient.ResultListener<UserResponseEntity>() {
        @Override
        public void onSuccess(UserResponseEntity result) {
//            if (editLayout.getVisibility() != View.VISIBLE) {
//                aboutLayout.setVisibility(View.VISIBLE);
//            }
//            changeUi(result);
//            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_FAILED_TO_CONNECT, false);
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
                    InfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_UNEXPECTED_ERROR, false);
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
        Log.i(TAG, "ProfileFragment onCreateView, this = " + this);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View v = binding.getRoot();
        findViews(v);
        binding.setHandlers(this);

        if (SharedPreferenceHelper.isUserLoggedIn()) {
            getUserDataRequest(SharedPreferenceHelper.getUserServerId());
        }
//        materialDialog = Helpers.getMaterialDialog(getContext());
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
            case R.id.show_achievments_details:
                AchievementsActivity.show(getContext(), userOld.getId());
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
    public void changeLoginView(ChangeLoginViewEvent event) {
        binding.loginView.changeViewToStart();
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

    private void getUserDataRequest(String id) {
     //   DDScannerApplication.getDdScannerRestClient().getUserInformation(id, getUserInformationResultListener);
        DDScannerApplication.getDdScannerRestClient().getUserSelfInformation(userResultListener);
    }

    private void changeUi(UserResponseEntity userResponseEntity) {
        if (getContext() == null) {
            return;
        }
        this.userOld = userResponseEntity.getUserOld();
//        showAchivementDetails.setOnClickListener(this);
//        ArrayList<ProfileAchievement> achievmentProfiles = new ArrayList<>();
//        if (userResponseEntity.getAchievements() != null && userResponseEntity.getAchievements().size() > 0) {
//            achievmentProfiles = (ArrayList<ProfileAchievement>) userResponseEntity.getAchievements();
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//            achievmentRecyclerView.setLayoutManager(linearLayoutManager);
//            achievmentRecyclerView.setAdapter(new AchievmentProfileListAdapter(achievmentProfiles, getContext()));
//            noAchievements.setVisibility(View.GONE);
//            achievmentRecyclerView.setVisibility(View.VISIBLE);
//        } else {
//            noAchievements.setVisibility(View.VISIBLE);
//            achievmentRecyclerView.setVisibility(View.GONE);
//        }
        if (userOld != null) {
            if (userOld.getPicture() == null) {
                Picasso.with(getContext()).load(R.drawable.avatar_profile_default)
                        .resize(Math.round(Helpers.convertDpToPixel(100, getContext())),
                                Math.round(Helpers.convertDpToPixel(100, getContext()))).centerCrop()
                        .placeholder(R.drawable.avatar_profile_default)
                        .transform(new CropCircleTransformation()).into(binding.userAvatar);
            } else {
                Picasso.with(getContext()).load(userOld.getPicture())
                        .resize(Math.round(Helpers.convertDpToPixel(80, getContext())),
                                Math.round(Helpers.convertDpToPixel(80, getContext()))).centerCrop()
                        .placeholder(R.drawable.avatar_profile_default)
                        .error(R.drawable.avatar_profile_default)
                        .transform(new CropCircleTransformation()).into(binding.userAvatar);
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
//        if (SharedPreferenceHelper.isUserLoggedIn()) {
//            requestSocial = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
//                    SharedPreferenceHelper.getSn());
//            requestToken = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
//                    SharedPreferenceHelper.getToken());
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
//        DDScannerApplication.getDdScannerRestClient().putUpdateUserProfileInfo(updateProfileInfoResultListener, SharedPreferenceHelper.getUserServerId(), image, requestType,  name, username, about, requestToken, requestSocial);
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
            DDScannerApplication.getDdScannerRestClient().getUserSelfInformation(userResultListener);
          //  DDScannerApplication.getDdScannerRestClient().getUserInformation(SharedPreferenceHelper.getUserServerId(), getUserInformationResultListener);
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
        DDScannerApplication.getDdScannerRestClient().getUserSelfInformation(userResultListener);
      //  DDScannerApplication.getDdScannerRestClient().getUserInformation(SharedPreferenceHelper.getUserServerId(), getUserInformationResultListener);
    }

    public void logoutUser(View view) {
        SharedPreferenceHelper.setLastShowingNotificationTime(0);
        SharedPreferenceHelper.logout();
        onLoggedOut();
    }

    public void showComments(View view) {
        if (binding.getUserViewModel().getUser().getCounters().getCommentsCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showLikes(View view) {
        if (binding.getUserViewModel().getUser().getCounters().getLikesCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showDislikes(View view) {
        if (binding.getUserViewModel().getUser().getCounters().getDislikesCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showCheckinns(View view) {
        if (binding.getUserViewModel().getUser().getCounters().getCheckinsCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showAdded(View view) {
        if (binding.getUserViewModel().getUser().getCounters().getAddedCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showEdited(View view) {
        if (binding.getUserViewModel().getUser().getCounters().getEditedCount() > 0) {
            //TODO show comments activity
        }
    }

    public void showFavorites(View view) {
        if (binding.getUserViewModel().getUser().getCounters().getFavoritesCount() > 0) {
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

}

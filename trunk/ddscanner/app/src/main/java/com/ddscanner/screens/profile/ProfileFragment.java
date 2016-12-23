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
import com.ddscanner.screens.profile.edit.EditUserProfileActivity;
import com.ddscanner.ui.activities.AboutActivity;
import com.ddscanner.events.ChangeLoginViewEvent;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.adapters.AchievmentProfileListAdapter;
import com.ddscanner.ui.adapters.CharacteristicSpinnerItemsAdapter;
import com.ddscanner.ui.adapters.DiverLevelSpinnerAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.fragments.BaseFragment;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.gson.Gson;
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

public class ProfileFragment extends BaseFragment implements LoginView.LoginStateChangeListener, InfoDialogFragment.DialogClosedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ProfileFragment.class.getName();

    private static final int MAX_LENGTH_NAME = 30;
    private static final int MAX_LENGTH_ABOUT = 250;

    private Map<String, TextView> errorsMap = new HashMap<>();

    private User user;

    private FragmentProfileBinding binding;

    private DDScannerRestClient.ResultListener<ProfileResponseEntity> userResultListener = new DDScannerRestClient.ResultListener<ProfileResponseEntity>() {
        @Override
        public void onSuccess(ProfileResponseEntity result) {
            if (binding != null) {
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
        setupUi();
        binding.setHandlers(this);

        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn() && DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == 1) {
            getUserDataRequest();
        }
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

    private void setupUi() {
        binding.swiperefresh.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        userResultListener.setCancelled(false);
    }

    @Subscribe
    public void changeLoginView(ChangeLoginViewEvent event) {
        binding.loginView.changeViewToStart();
    }

    @Subscribe
    public void getUserProfileInfo(LoadUserProfileInfoEvent event) {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn())  {
            if (binding != null) {
                binding.about.scrollTo(0, 0);
            }
            getUserDataRequest();
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

    private void getUserDataRequest() {
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
    }

    @Override
    public void onLoggedIn() {
        Log.i(TAG, "ProfileFragment onLoggedIn, this = " + this);
        if (binding != null && binding.loginView != null && binding.about != null) {
            binding.loginView.setVisibility(View.GONE);
            binding.swiperefresh.setEnabled(true);
            binding.about.setVisibility(View.VISIBLE);
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

    public void showEditLayout(View view) {
        if (user != null) {
            EditUserProfileActivity.showForResult(getActivity(), new Gson().toJson(user), ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_SHOW_EDIT_PROFILE_ACTIVITY);
        }
    }

    public void showAboutDDSButtonClicked(View view) {
        AboutActivity.show(getContext());
    }

    public void showAchievementsDetails(View view) {
        AchievementsActivity.show(getContext());
    }

}

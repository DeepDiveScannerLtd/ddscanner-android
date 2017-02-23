package com.ddscanner.screens.profile.user;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.FragmentProfileBinding;
import com.ddscanner.entities.BaseUser;
import com.ddscanner.entities.BusRegisteringListener;
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoAuthor;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.entities.User;
import com.ddscanner.events.LoadUserProfileInfoEvent;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.achievements.AchievementsActivity;
import com.ddscanner.screens.profile.edit.EditUserProfileActivity;
import com.ddscanner.screens.reiews.list.ReviewsActivity;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.ui.activities.AboutActivity;
import com.ddscanner.events.ChangeLoginViewEvent;
import com.ddscanner.ui.activities.DiveSpotsListActivity;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.activities.PhotosGalleryActivity;
import com.ddscanner.ui.activities.SelfCommentsActivity;
import com.ddscanner.ui.activities.UserLikesDislikesActivity;
import com.ddscanner.ui.adapters.AchievmentProfileListAdapter;
import com.ddscanner.ui.adapters.UserPhotosListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment implements LoginView.LoginStateChangeListener, DialogClosedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ProfileFragment.class.getName();

    private BusRegisteringListener busListener = new BusRegisteringListener();

    private User user;

    private FragmentProfileBinding binding;

    private DDScannerRestClient.ResultListener<User> userResultListener = new DDScannerRestClient.ResultListener<User>() {
        @Override
        public void onSuccess(User result) {
            if (binding != null) {
                binding.about.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
            switch (result.getType()) {
                case 2:
                case 1:
                    user = result;
                    user.setToken(DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
                    BaseUser baseUser = DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUser();
                    baseUser.setName(result.getName());
                    baseUser.setPhoto(result.getPhoto());
                    baseUser.setType(result.getType());
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().addUserToList(baseUser);
                    break;
                case 0:
                    break;
            }
            binding.setProfileFragmentViewModel(new ProfileFragmentViewModel(result));
            binding.swiperefresh.setRefreshing(false);
            changeUi();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_UNEXPECTED_ERROR, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.bus.post(new LoggedOutEvent());
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_UNEXPECTED_ERROR, false);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_UNEXPECTED_ERROR, false);
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

        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn() && DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == 1) {
            getUserDataRequest();
        }
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
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
        if (binding.getProfileFragmentViewModel() == null) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.about.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void changeLoginView(ChangeLoginViewEvent event) {
        binding.loginView.changeViewToStart();
    }

    @Subscribe
    public void getUserProfileInfo(LoadUserProfileInfoEvent event) {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn())  {
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
        if (!busListener.isRegistered()) {
            DDScannerApplication.bus.register(this);
            busListener.setRegistered(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ProfileFragment onStop, this = " + this);
        if (busListener.isRegistered()) {
            DDScannerApplication.bus.unregister(this);
            busListener.setRegistered(false);
        }
    }

    private void getUserDataRequest() {
        DDScannerApplication.getInstance().getDdScannerRestClient().getUserSelfInformation(userResultListener);
        binding.about.scrollTo(0,0);
    }

    public void reloadData() {
        getUserDataRequest();
    }

    private void changeUi() {
        if (getContext() == null) {
            return;
        }
        if (binding.getProfileFragmentViewModel().getUser().getPhotos() != null) {
            binding.noPhotosView.setVisibility(View.GONE);
            binding.photosList.setLayoutManager(new GridLayoutManager(getContext(), 4));
            binding.photosList.setAdapter(new UserPhotosListAdapter((ArrayList<DiveSpotPhoto>) binding.getProfileFragmentViewModel().getUser().getPhotos(), binding.getProfileFragmentViewModel().getUser().getPhotosCount(), getActivity(), binding.getProfileFragmentViewModel().getUser().getId()));
            binding.photosList.setVisibility(View.VISIBLE);
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
            binding.progressBar.setVisibility(View.GONE);
            binding.setProfileFragmentViewModel(null);
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
            ReviewsActivity.showForResult(getActivity(), binding.getProfileFragmentViewModel().getUser().getId(), -1, ReviewsOpenedSource.USER);
        }
    }

    public void showLikes(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getLikesCount() > 0) {
            UserLikesDislikesActivity.show(getActivity(), true, binding.getProfileFragmentViewModel().getUser().getId());
        }
    }

    public void showDislikes(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getDislikesCount() > 0) {
            UserLikesDislikesActivity.show(getActivity(), false, binding.getProfileFragmentViewModel().getUser().getId());
        }
    }

    public void showCheckinns(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getCheckinsCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.CHECKINS, binding.getProfileFragmentViewModel().getUser().getId());
        }
    }

    public void showAdded(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getAddedCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.ADDED, binding.getProfileFragmentViewModel().getUser().getId());
        }
    }

    public void showEdited(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getEditedCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.EDITED, binding.getProfileFragmentViewModel().getUser().getId());
        }
    }

    public void showFavorites(View view) {
        if (binding.getProfileFragmentViewModel().getUser().getCounters().getFavoritesCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.FAVORITES, binding.getProfileFragmentViewModel().getUser().getId());
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

    @Subscribe
    public void openPhotosActivity(OpenPhotosActivityEvent event) {
        PhotosGalleryActivity.showForResult(binding.getProfileFragmentViewModel().getUser().getId(), getActivity(), PhotoOpenedSource.PROFILE, new Gson().toJson(new PhotoAuthor(binding.getProfileFragmentViewModel().getUser().getId(), binding.getProfileFragmentViewModel().getUser().getName(), binding.getProfileFragmentViewModel().getUser().getPhoto(), binding.getProfileFragmentViewModel().getUser().getType())), ActivitiesRequestCodes.REQUEST_CODE_SHOW_USER_PROFILE_PHOTOS);
    }

    public void showDiveCenter(View view) {
        UserProfileActivity.show(getContext(), String.valueOf(binding.getProfileFragmentViewModel().getUser().getDiveCenter().getId()), 0);
    }

}

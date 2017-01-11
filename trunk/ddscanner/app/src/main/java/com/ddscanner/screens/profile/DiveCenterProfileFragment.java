package com.ddscanner.screens.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ViewDivecenterProfileBinding;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.ProfileResponseEntity;
import com.ddscanner.entities.User;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.LoadUserProfileInfoEvent;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.instructors.InstructorsActivity;
import com.ddscanner.screens.profile.edit.EditDiveCenterProfileActivity;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.adapters.UserPhotosListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.fragments.BaseFragment;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

public class DiveCenterProfileFragment extends BaseFragment implements LoginView.LoginStateChangeListener, InfoDialogFragment.DialogClosedListener{

    private DiveCenterProfile diveCenterProfile;
    private ViewDivecenterProfileBinding binding;
    private DDScannerRestClient.ResultListener<DiveCenterProfile> userResultListener = new DDScannerRestClient.ResultListener<DiveCenterProfile>() {
        @Override
        public void onSuccess(DiveCenterProfile result) {
            switch (result.getType()) {
                case 2:
                case 1:
                    break;
                case 0:
                    diveCenterProfile = result;
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setUserServerId(String.valueOf(diveCenterProfile.getId()));
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().saveDiveCenter(diveCenterProfile);
                    binding.setDiveCenterViewModel(new DiveCenterProfileFragmentViewModel(diveCenterProfile));
                    setUi();
                    break;
            }
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
//                    InfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_UNEXPECTED_ERROR, false);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.view_divecenter_profile, container, false);
        binding.setHandlers(this);
        View view = binding.getRoot();
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == 0) {
            DDScannerApplication.getInstance().getDdScannerRestClient().getDiveCenterSelfInformation(userResultListener);
        }
        return view;
    }

    private void setUi() {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotos() != null) {
            binding.photosList.setLayoutManager(new GridLayoutManager(getContext(), 4));
            binding.photosList.setAdapter(new UserPhotosListAdapter(binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotos(), binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotosCount(), getActivity()));
            binding.scrollView.scrollTo(0,0);
        }
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(context);
        }
    }


    protected void onAttachToContext(Context context) {
        try {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.setDiveCenterProfileFragment(this);
        } catch (ClassCastException e) {
            // waaat?
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
        userResultListener.setCancelled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
        userResultListener.setCancelled(true);
    }

    @Subscribe
    public void getUserProfileInfo(LoadUserProfileInfoEvent event) {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == 0) {
            DDScannerApplication.getInstance().getDdScannerRestClient().getDiveCenterSelfInformation(userResultListener);
        }
    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    public void editProfileButtonClicked(View view) {
        EditDiveCenterProfileActivity.showForResult(getActivity(), new Gson().toJson(diveCenterProfile), ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_SHOW_EDIT_DC_PROFILE_ACTIVITY);
    }

    public void showInstructors(View view) {
        InstructorsActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_SHOW_INSTRUCTORS_ACTIVITY, String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()));
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_PROFILE_FRAGMENT_FAILED_TO_CONNECT:
                DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
                break;
        }
    }
}

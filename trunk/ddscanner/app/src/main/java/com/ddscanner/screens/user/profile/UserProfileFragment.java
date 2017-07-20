package com.ddscanner.screens.user.profile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.FragmentUserProfileBinding;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.entities.User;
import com.ddscanner.screens.divespots.list.DiveSpotsListActivity;
import com.ddscanner.screens.profile.user.ProfileFragmentViewModel;
import com.ddscanner.screens.reiews.list.ReviewsActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.UserLikesDislikesActivity;
import com.ddscanner.ui.adapters.AchievmentProfileListAdapter;
import com.ddscanner.ui.adapters.UserPhotosListAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;

import java.util.ArrayList;

public class UserProfileFragment extends Fragment {

    private User user;
    FragmentUserProfileBinding binding;
    private String FACEBOOK_URL = "https://www.facebook.com/";
    private String FACEBOOK_PAGE_ID;

    public static UserProfileFragment newInstance(User user) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        userProfileFragment.setArguments(bundle);
        return userProfileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false);
        user = (User) getArguments().getSerializable("user");
        binding.setUserProfileViewModel(new ProfileFragmentViewModel(user));
        View v = binding.getRoot();
        binding.setHandlers(this);
        if (user.getAchievements() != null) {
            binding.achievmentRv.setNestedScrollingEnabled(false);
            binding.achievmentRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
            binding.achievmentRv.setAdapter(new AchievmentProfileListAdapter((ArrayList<ProfileAchievement>) user.getAchievements(), getActivity()));
        }
        if (user.getPhotos() != null) {
            binding.photosList.setNestedScrollingEnabled(false);
            binding.photosList.setLayoutManager(new GridLayoutManager(getContext(), 4));
            binding.photosList.setAdapter(new UserPhotosListAdapter((ArrayList<DiveSpotPhoto>) user.getPhotos(), user.getPhotosCount(), getActivity(), binding.getUserProfileViewModel().getUser().getId()));
        }
        return v;
    }

    public void showCheckinns(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getCheckinsCount() > 0) {
            EventsTracker.trackReviewerCheckInsView();
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.CHECKINS, binding.getUserProfileViewModel().getUser().getId());
            } else {
                LoginActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_CHECKINS);
            }
        }
    }

    public void showAdded(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getAddedCount() > 0) {
            EventsTracker.trackReviewerCreatedView();
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.ADDED, binding.getUserProfileViewModel().getUser().getId());
            } else {
                LoginActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_CREATED);
            }
        }
    }

    public void showEdited(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getEditedCount() > 0) {
            EventsTracker.trackReviewerEditedView();
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.EDITED, binding.getUserProfileViewModel().getUser().getId());
            } else {
                LoginActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_EDITED);
            }
        }
    }

    public void showComments(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getCommentsCount() > 0) {
            EventsTracker.trackReviewerReviewsView();
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                ReviewsActivity.showForResult(getActivity(), binding.getUserProfileViewModel().getUser().getId(), -1, ReviewsOpenedSource.USER);
            } else {
                LoginActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_REVIEWS);
            }
        }
    }

    public void showLikes(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getLikesCount() > 0) {
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                UserLikesDislikesActivity.show(getActivity(), true, binding.getUserProfileViewModel().getUser().getId());
            } else {
                LoginActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_LIKES);
            }
        }
    }

    public void showDislikes(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getDislikesCount() > 0) {
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                UserLikesDislikesActivity.show(getActivity(), false, binding.getUserProfileViewModel().getUser().getId());
            } else {
                LoginActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_DISLIKES);
            }
        }
    }

    public void showDiveCenter(View view) {
        if (binding.getUserProfileViewModel().getUser().getDiveCenter().getType() == 1) {
            UserProfileActivity.show(getContext(), String.valueOf(binding.getUserProfileViewModel().getUser().getDiveCenter().getId()), 0);
        } else if (binding.getUserProfileViewModel().getUser().getDiveCenter().getType() == 1) {
            UserProfileActivity.show(getContext(), String.valueOf(binding.getUserProfileViewModel().getUser().getDiveCenter().getId()), -1);
        }
    }

    public void openFacebook(View view) {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = getFacebookPageURL();
        facebookIntent.setData(Uri.parse(facebookUrl));
        try {
            startActivity(facebookIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL + binding.getUserProfileViewModel().getUser().getFacebookLink())));
        }
    }

    public String getFacebookPageURL() {
        EventsTracker.trackReviewrFacebookOpened();
        FACEBOOK_PAGE_ID = binding.getUserProfileViewModel().getUser().getFacebookLink();
        PackageManager packageManager = getActivity().getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL + FACEBOOK_PAGE_ID;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (Exception e) {
            return FACEBOOK_URL + FACEBOOK_PAGE_ID; //normal web url
        }
    }

}

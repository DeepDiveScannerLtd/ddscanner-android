package com.ddscanner.screens.user.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.FragmentUserProfileBinding;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.entities.User;
import com.ddscanner.screens.profile.user.ProfileFragmentViewModel;
import com.ddscanner.screens.reiews.list.ReviewsActivity;
import com.ddscanner.ui.activities.DiveSpotsListActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.SelfCommentsActivity;
import com.ddscanner.ui.activities.UserLikesDislikesActivity;
import com.ddscanner.ui.adapters.AchievmentProfileListAdapter;
import com.ddscanner.ui.adapters.UserPhotosListAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;

import java.util.ArrayList;

public class UserProfileFragment extends Fragment {

    private User user;
    FragmentUserProfileBinding binding;

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
            binding.achievmentRv.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.achievmentRv.setAdapter(new AchievmentProfileListAdapter((ArrayList<ProfileAchievement>) user.getAchievements(), getActivity()));
        }
        if (user.getPhotos() != null) {
            binding.photosList.setLayoutManager(new GridLayoutManager(getContext(), 4));
            binding.photosList.setAdapter(new UserPhotosListAdapter((ArrayList<DiveSpotPhoto>) user.getPhotos(), user.getPhotosCount(), getActivity(), binding.getUserProfileViewModel().getUser().getId()));
        }
        return v;
    }

    public void showCheckinns(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getCheckinsCount() > 0) {
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.CHECKINS, binding.getUserProfileViewModel().getUser().getId());
            } else {
                LoginActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_CHECKINS);
            }
        }
    }

    public void showAdded(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getAddedCount() > 0) {
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.ADDED, binding.getUserProfileViewModel().getUser().getId());
            } else {
                LoginActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_CREATED);
            }
        }
    }

    public void showEdited(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getEditedCount() > 0) {
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.EDITED, binding.getUserProfileViewModel().getUser().getId());
            } else {
                LoginActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_EDITED);
            }
        }
    }

    public void showComments(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getCommentsCount() > 0) {
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


}

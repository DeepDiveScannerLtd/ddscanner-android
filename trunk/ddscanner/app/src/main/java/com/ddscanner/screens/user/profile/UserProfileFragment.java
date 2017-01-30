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

import com.ddscanner.R;
import com.ddscanner.databinding.FragmentUserProfileBinding;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.ProfileAchievement;
import com.ddscanner.entities.User;
import com.ddscanner.screens.profile.user.ProfileFragmentViewModel;
import com.ddscanner.ui.activities.DiveSpotsListActivity;
import com.ddscanner.ui.activities.SelfCommentsActivity;
import com.ddscanner.ui.activities.UserLikesDislikesActivity;
import com.ddscanner.ui.adapters.AchievmentProfileListAdapter;
import com.ddscanner.ui.adapters.UserPhotosListAdapter;

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
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.CHECKINS, binding.getUserProfileViewModel().getUser().getId());
        }
    }

    public void showAdded(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getAddedCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.ADDED, binding.getUserProfileViewModel().getUser().getId());
        }
    }

    public void showEdited(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getEditedCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.EDITED, binding.getUserProfileViewModel().getUser().getId());
        }
    }

    public void showFavorites(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getFavoritesCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.FAVORITES, binding.getUserProfileViewModel().getUser().getId());
        }
    }

    public void showComments(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getCommentsCount() > 0) {
            SelfCommentsActivity.show(getContext(), binding.getUserProfileViewModel().getUser().getId());
        }
    }

    public void showLikes(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getLikesCount() > 0) {
            UserLikesDislikesActivity.show(getActivity(), true, binding.getUserProfileViewModel().getUser().getId());
        }
    }

    public void showDislikes(View view) {
        if (binding.getUserProfileViewModel().getUser().getCounters().getDislikesCount() > 0) {
            UserLikesDislikesActivity.show(getActivity(), false, binding.getUserProfileViewModel().getUser().getId());
        }
    }


}

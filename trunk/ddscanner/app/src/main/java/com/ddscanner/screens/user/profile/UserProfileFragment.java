package com.ddscanner.screens.user.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.databinding.FragmentUserProfileBinding;
import com.ddscanner.entities.User;
import com.ddscanner.screens.profile.ProfileFragmentViewModel;

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
        return v;

    }
}
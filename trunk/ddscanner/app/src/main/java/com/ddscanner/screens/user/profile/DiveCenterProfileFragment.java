package com.ddscanner.screens.user.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.databinding.FragmentDiveCenterProfileBinding;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.screens.profile.divecenter.DiveCenterProfileFragmentViewModel;

public class DiveCenterProfileFragment extends Fragment {

    private DiveCenterProfile diveCenterProfile;
    private FragmentDiveCenterProfileBinding binding;

    public static DiveCenterProfileFragment newInstance(DiveCenterProfile diveCenterProfile) {
        DiveCenterProfileFragment diveCenterProfileFragment = new DiveCenterProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", diveCenterProfile);
        diveCenterProfileFragment.setArguments(bundle);
        return diveCenterProfileFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dive_center_profile, container, false);
        diveCenterProfile = (DiveCenterProfile) getArguments().getSerializable("user");
        binding.setDiveCenterViewModel(new DiveCenterProfileFragmentViewModel(diveCenterProfile));
        View v = binding.getRoot();
        binding.setHandlers(this);
        return v;
    }

}

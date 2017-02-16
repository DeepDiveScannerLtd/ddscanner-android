package com.ddscanner.screens.user.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.databinding.FragmentDiveCenterProfileBinding;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.screens.profile.divecenter.DiveCenterProfileFragmentViewModel;
import com.ddscanner.ui.activities.DiveSpotsListActivity;
import com.ddscanner.ui.adapters.UserPhotosListAdapter;

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
        setupUi();
        return v;
    }

    private void setupUi() {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotos() != null) {
            binding.photosList.setLayoutManager(new GridLayoutManager(getContext(), 4));
            binding.photosList.setAdapter(new UserPhotosListAdapter(binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotos(), binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotosCount(), getActivity(), String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId())));
        }
    }

    public void showDiveSpots(View view) {

    }

    public void showCreated(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getCreatedSpotsCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.ADDED, String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()));
        }
    }

    public void showEdited(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getEditedSpotsCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.EDITED, String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()));
        }
    }

}

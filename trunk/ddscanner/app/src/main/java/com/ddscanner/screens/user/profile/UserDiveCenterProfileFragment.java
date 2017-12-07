package com.ddscanner.screens.user.profile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.FragmentDiveCenterProfileBinding;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.screens.brands.BrandsActivity;
import com.ddscanner.screens.divecemter.profile.languages.DiveCenterProfileLanguagesActivity;
import com.ddscanner.screens.divespots.list.DiveSpotsListActivity;
import com.ddscanner.screens.instructors.InstructorsActivity;
import com.ddscanner.screens.profile.divecenter.BrandsGridListAdapter;
import com.ddscanner.screens.profile.divecenter.DiveCenterProfileFragmentViewModel;
import com.ddscanner.screens.profile.divecenter.DiveCenterProfileProductsAdapter;
import com.ddscanner.screens.profile.divecenter.DiveCenterSpotsActivity;
import com.ddscanner.screens.profile.divecenter.tours.list.DailyToursActivity;
import com.ddscanner.ui.adapters.TagsAdapter;
import com.ddscanner.ui.adapters.UserPhotosListAdapter;
import com.ddscanner.utils.EmailIntentBuilder;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.PhoneCallIntentBuilder;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class UserDiveCenterProfileFragment extends Fragment {

    private DiveCenterProfile diveCenterProfile;
    private FragmentDiveCenterProfileBinding binding;
    private LatLng diveCenterLocation = null;
    private int diveCenterType;
    private static final String ARG_TYPE = "type";
    private static final String ARG_USER = "user";
    private static final String ARG_DIVE_SPOT_ID = "divespot_id";

    public static UserDiveCenterProfileFragment newInstance(DiveCenterProfile diveCenterProfile, int diveCenterType, String diveSpotId) {
        UserDiveCenterProfileFragment userDiveCenterProfileFragment = new UserDiveCenterProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_USER, diveCenterProfile);
        bundle.putInt(ARG_TYPE, diveCenterType);
        bundle.putString(ARG_DIVE_SPOT_ID, diveSpotId);
        userDiveCenterProfileFragment.setArguments(bundle);
        return userDiveCenterProfileFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dive_center_profile, container, false);
        diveCenterProfile = (DiveCenterProfile) getArguments().getSerializable(ARG_USER);
        if (getArguments().getString(ARG_DIVE_SPOT_ID) != null) {
            EventsTracker.trackBookingDcProfileView();
            diveCenterProfile.setForBooking(true);
            diveCenterProfile.setDiveSpotBookingId(getArguments().getString(ARG_DIVE_SPOT_ID));
        }
        binding.setDiveCenterViewModel(new DiveCenterProfileFragmentViewModel(diveCenterProfile));
        diveCenterType = getArguments().getInt(ARG_TYPE, 0);
        if (diveCenterProfile.getAddresses() != null && diveCenterProfile.getAddresses().get(0).getPosition() != null) {
            diveCenterLocation = diveCenterProfile.getAddresses().get(0).getPosition();
        }
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
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getAbout() != null) {
            binding.about.setText(binding.getDiveCenterViewModel().getDiveCenterProfile().getAbout());
            binding.about.setVisibility(View.VISIBLE);
            checkLines();
        } else {
            binding.about.setVisibility(View.GONE);
        }
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getProducts() != null) {
            DiveCenterProfileProductsAdapter diveCenterProfileProductsAdapter = new DiveCenterProfileProductsAdapter();
            diveCenterProfileProductsAdapter.setDailyTours(binding.getDiveCenterViewModel().getDiveCenterProfile().getProducts());
            binding.productList.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.productList.setNestedScrollingEnabled(false);
            binding.productList.setAdapter(diveCenterProfileProductsAdapter);
        }
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getAssociations() != null) {
            ArrayList<String> tags = new ArrayList<>();
            for (Integer integer : binding.getDiveCenterViewModel().getDiveCenterProfile().getAssociations()) {
                tags.add(Helpers.getAssociationByCode(integer).getName());
            }
            TagsAdapter tagsAdapter = new TagsAdapter();
            tagsAdapter.setStrings(tags);
            ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(getContext())
                    .setOrientation(ChipsLayoutManager.HORIZONTAL)
                    .build();
            binding.tags.addItemDecoration(new SpacingItemDecoration(Helpers.convertDpToIntPixels(2, getContext()), Helpers.convertDpToIntPixels(2, getContext())));
            binding.tags.setLayoutManager(chipsLayoutManager);
            binding.tags.setAdapter(tagsAdapter);
        }
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().isDiveShop()) {
            binding.type.setText(R.string.divecenter_type_dive_shop);
        } else {
            binding.type.setText(R.string.divecenter_type_dc);
        }

        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getBrands() != null) {
            binding.brandsList.setNestedScrollingEnabled(false);
            binding.brandsList.setLayoutManager(new GridLayoutManager(getContext(), 6));
            BrandsGridListAdapter brandsGridListAdapter = new BrandsGridListAdapter();
            binding.brandsList.setAdapter(brandsGridListAdapter);
            brandsGridListAdapter.setBrands(binding.getDiveCenterViewModel().getDiveCenterProfile().getBrands());
        }
        checkLines();
    }

    public void showDiveSpots(View view) {
        EventsTracker.trackDcSpotsView();
        DiveCenterSpotsActivity.show(getContext(), String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), diveCenterLocation);
    }

    public void showCreated(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getCreatedSpotsCount() > 0) {
            EventsTracker.trackDcCreatedView();
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.ADDED, String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()));
        }
    }

    public void showEdited(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getEditedSpotsCount() > 0) {
            EventsTracker.trackDcEditedView();
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.EDITED, String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()));
        }
    }

    public void showLanguages(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getLanguages() != null) {
            EventsTracker.trackDcLanguagesView();
            DiveCenterProfileLanguagesActivity.show(String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), getContext());
        }
    }

    public void showInstructors(View view) {
        if (Integer.parseInt(binding.getDiveCenterViewModel().getDiveCenterProfile().getInstructorsCount()) > 0 && diveCenterType == 1) {
            EventsTracker.trackInstructorsView();
            InstructorsActivity.showForResult(getActivity(), -1, String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()));
        }
    }

    public void showAllBrands(View view) {
        EventsTracker.trackDcBrandsView();
        BrandsActivity.show(getContext(), String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), BrandsActivity.BrandSource.DIVECENTER);
    }

    public void showAllProducts(View view) {
        DailyToursActivity.show(getContext(), binding.getDiveCenterViewModel().getDiveCenterProfile().getId().toString());
    }

    private void checkLines() {
        boolean flag = true;
        ViewTreeObserver viewTreeObserver = binding.about.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(() -> {
                Layout l = binding.about.getLayout();
                if (l != null) {
                    int lines = l.getLineCount();
                    if (lines > 0) {
                        if (l.getEllipsisCount(lines - 1) > 0) {
                            binding.showMore.setVisibility(View.VISIBLE);
                        }
                    }
                }

        });

    }

    public void showMoreClicked(View view) {
        if (binding.about.isExpanded()) {
            binding.about.collapse();
            binding.showMore.setText(R.string.show_more);
        } else {
            binding.about.expand();
            binding.showMore.setText(R.string.show_less);
        }
    }

}

package com.ddscanner.screens.profile.divecenter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ViewDivecenterProfileBinding;
import com.ddscanner.entities.BaseUser;
import com.ddscanner.entities.BusRegisteringListener;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.PhotoAuthor;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.LoadUserProfileInfoEvent;
import com.ddscanner.events.LogoutEvent;
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.brands.BrandsActivity;
import com.ddscanner.screens.divecemter.profile.languages.DiveCenterProfileLanguagesActivity;
import com.ddscanner.screens.divespots.list.DiveSpotsListActivity;
import com.ddscanner.screens.instructors.InstructorsActivity;
import com.ddscanner.screens.profile.divecenter.courses.details.CourseDetailsActivity;
import com.ddscanner.screens.profile.divecenter.courses.list.CoursesListActivity;
import com.ddscanner.screens.profile.divecenter.fundives.details.FunDiveDetailsActivity;
import com.ddscanner.screens.profile.divecenter.fundives.list.FunDivesActivity;
import com.ddscanner.screens.profile.divecenter.tours.details.TourDetailsActivity;
import com.ddscanner.screens.profile.divecenter.tours.list.DailyToursActivity;
import com.ddscanner.screens.profile.edit.EditDiveCenterProfileActivity;
import com.ddscanner.ui.activities.AboutActivity;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.activities.PhotosGalleryActivity;
import com.ddscanner.ui.adapters.TagsAdapter;
import com.ddscanner.ui.adapters.UserPhotosListAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.ShareAppIntentBuilder;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class DiveCenterProfileFragment extends Fragment implements LoginView.LoginStateChangeListener, DialogClosedListener {

    private DiveCenterProfile diveCenterProfile;
    private ViewDivecenterProfileBinding binding;
    private static final String ARG_USER = "ARG_USER";
    private boolean isHaveSpots = false;
    private LatLng diveCenterLocation = null;
    private BusRegisteringListener busListener = new BusRegisteringListener();
    private DDScannerRestClient.ResultListener<DiveCenterProfile> userResultListener = new DDScannerRestClient.ResultListener<DiveCenterProfile>() {
        @Override
        public void onSuccess(DiveCenterProfile result) {
                    diveCenterProfile = result;
                    BaseUser baseUser = SharedPreferenceHelper.getActiveUser();
                    baseUser.setName(diveCenterProfile.getName());
                    baseUser.setPhoto(diveCenterProfile.getPhoto());
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().addUserToList(baseUser);
                    binding.setDiveCenterViewModel(new DiveCenterProfileFragmentViewModel(diveCenterProfile));
                    setUi();
                    if (diveCenterProfile.getAddresses() != null && diveCenterProfile.getAddresses().get(0).getPosition() != null) {
                        diveCenterLocation = diveCenterProfile.getAddresses().get(0).getPosition();
                    }

        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_FAILED_TO_CONNECT, false);
        }

    };

    public static DiveCenterProfileFragment newInstance(DiveCenterProfile user) {
        DiveCenterProfileFragment userProfileFragment = new DiveCenterProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_USER, new Gson().toJson(user));
        userProfileFragment.setArguments(bundle);
        return userProfileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.view_divecenter_profile, container, false);
        binding.setHandlers(this);
        View view = binding.getRoot();
        Bundle bundle = new Bundle();
        bundle = getArguments();
        if (getArguments() != null && bundle.getString(ARG_USER) != null) {
            binding.logout.setVisibility(View.GONE);
            diveCenterProfile = new Gson().fromJson(bundle.getString(ARG_USER), DiveCenterProfile.class);
            binding.setDiveCenterViewModel(new DiveCenterProfileFragmentViewModel(diveCenterProfile));
            setUi();
        } else {
            if (SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
                DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getDiveCenterSelfInformation(userResultListener);
            }
        }
        return view;
    }

    private void setUi() {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getWorkingCount() != null && binding.getDiveCenterViewModel().getDiveCenterProfile().getWorkingCount() > 0) {
            isHaveSpots = true;
        }
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getAbout() != null) {
            binding.about.setText(binding.getDiveCenterViewModel().getDiveCenterProfile().getAbout());
            binding.about.setVisibility(View.VISIBLE);
            checkLines();
        } else {
            binding.about.setVisibility(View.GONE);
        }
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getProducts() != null) {
            DiveCenterProfileProductsAdapter diveCenterProfileProductsAdapter = new DiveCenterProfileProductsAdapter(item -> TourDetailsActivity.show(getContext(), item.getId()));
            diveCenterProfileProductsAdapter.setDailyTours(binding.getDiveCenterViewModel().getDiveCenterProfile().getProducts());
            binding.productList.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.productList.setNestedScrollingEnabled(false);
            binding.productList.setAdapter(diveCenterProfileProductsAdapter);
        }

        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getFunDives() != null) {
            DiveCenterFunDivesAdapter diveCenterFunDivesAdapter = new DiveCenterFunDivesAdapter(item -> FunDiveDetailsActivity.show(getContext(), item.getId()));
            diveCenterFunDivesAdapter.setFunDives(binding.getDiveCenterViewModel().getDiveCenterProfile().getFunDives());
            binding.funDivesList.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.funDivesList.setNestedScrollingEnabled(false);
            binding.funDivesList.setAdapter(diveCenterFunDivesAdapter);
        }
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getCourses() != null) {
            DiveCenterCoursesAdapter diveCenterCoursesAdapter = new DiveCenterCoursesAdapter(item -> CourseDetailsActivity.show(getContext(), item.getId()));
            diveCenterCoursesAdapter.setCources(binding.getDiveCenterViewModel().getDiveCenterProfile().getCourses());
            binding.coursesList.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.coursesList.setNestedScrollingEnabled(false);
            binding.coursesList.setAdapter(diveCenterCoursesAdapter);
        }
        binding.progressBarLoading.setVisibility(View.GONE);
        binding.aboutLayout.setVisibility(View.VISIBLE);
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotos() != null) {
            binding.photosList.setNestedScrollingEnabled(false);
            binding.photosList.setLayoutManager(new GridLayoutManager(getContext(), 4));
            binding.photosList.setAdapter(new UserPhotosListAdapter(binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotos(), binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotosCount(), getActivity(), String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId())));
            binding.scrollView.scrollTo(0,0);
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
        userResultListener.setCancelled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (busListener.isRegistered()) {
            DDScannerApplication.bus.unregister(this);
            busListener.setRegistered(false);
        }
        userResultListener.setCancelled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!busListener.isRegistered()) {
            DDScannerApplication.bus.register(this);
            busListener.setRegistered(true);
        }
        userResultListener.setCancelled(false);
        if (binding.getDiveCenterViewModel() == null) {
            binding.progressBarLoading.setVisibility(View.VISIBLE);
            binding.aboutLayout.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void getUserProfileInfo(LoadUserProfileInfoEvent event) {
        if (SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
            DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getDiveCenterSelfInformation(userResultListener);
        }
    }

    public void reloadData() {
        if (SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
            DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getDiveCenterSelfInformation(userResultListener);
            if (binding.scrollView != null){
                binding.scrollView.scrollTo(0, 0);
            }
        }
    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    public void editProfileButtonClicked(View view) {
        EditDiveCenterProfileActivity.showForResult(getActivity(), new Gson().toJson(diveCenterProfile), ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_SHOW_EDIT_PROFILE_ACTIVITY, isHaveSpots);
    }

    public void showInstructors(View view) {
        if (Integer.parseInt(binding.getDiveCenterViewModel().getDiveCenterProfile().getInstructorsCount()) > 0) {
            EventsTracker.trackMyInstructorsView();
            InstructorsActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_SHOW_INSTRUCTORS_ACTIVITY, String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()));
        }
    }

    public void showDiveSpots(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getWorkingCount() > 0) {
            EventsTracker.trackMyDiveSpotsView();
            if (binding.getDiveCenterViewModel().getDiveCenterProfile().getAddresses() == null || binding.getDiveCenterViewModel().getDiveCenterProfile().getAddresses().get(0) == null) {
                DiveCenterSpotsActivity.show(getContext(), String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), diveCenterLocation);
                return;
            }
            DiveCenterSpotsActivity.show(getContext(), String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), binding.getDiveCenterViewModel().getDiveCenterProfile().getAddresses().get(0).getPosition());
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_PROFILE_FRAGMENT_FAILED_TO_CONNECT:
                DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
                break;
        }
    }

    @Subscribe
    public void openPhotosActivity(OpenPhotosActivityEvent event) {
        EventsTracker.trackMyPhotosView();
        PhotosGalleryActivity.showForResult(String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), getActivity(), PhotoOpenedSource.PROFILE, new Gson().toJson(new PhotoAuthor(String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), binding.getDiveCenterViewModel().getDiveCenterProfile().getName(), binding.getDiveCenterViewModel().getDiveCenterProfile().getPhoto(), 0)), ActivitiesRequestCodes.REQUEST_CODE_SHOW_USER_PROFILE_PHOTOS);
    }

    public void showLanguages(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getLanguages() != null) {
            EventsTracker.trackMyLanguagesView();
            DiveCenterProfileLanguagesActivity.show(String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), getContext());
        }
    }

    public void showCreated(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getCreatedSpotsCount() > 0) {
            EventsTracker.trackMyCreatedView();
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.ADDED, binding.getDiveCenterViewModel().getDiveCenterProfile().getId().toString());
        }
    }

    public void showEdited(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getEditedSpotsCount() > 0) {
            EventsTracker.trackMyEditedView();
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.EDITED, binding.getDiveCenterViewModel().getDiveCenterProfile().getId().toString());
        }
    }

    public void showAboutDDSButtonClicked(View view) {
        AboutActivity.show(getContext());
    }

    public void logout(View view) {
        DDScannerApplication.bus.post(new LogoutEvent());
    }

    public void shareApp(View view) {
        ShareAppIntentBuilder.from(getContext()).share();
    }

    public void showAllBrands(View view) {
        EventsTracker.trackMyBrandsView();
        BrandsActivity.show(getContext(), String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), BrandsActivity.BrandSource.DIVECENTER);
    }

    public void showAllProducts(View view) {
        DailyToursActivity.show(getContext(), binding.getDiveCenterViewModel().getDiveCenterProfile().getId().toString());
    }

    private void checkLines() {
        Layout l = binding.about.getLayout();
        if (l != null) {
            int lines = l.getLineCount();
            if (lines > 0) {
                if (l.getEllipsisCount(lines - 1) > 0) {
                    binding.showMore.setVisibility(View.VISIBLE);
                } else {
                    binding.showMore.setVisibility(View.GONE);
                }
            }
        }
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

    public void showAllFunDives(View view) {
        FunDivesActivity.show(getContext(), binding.getDiveCenterViewModel().getDiveCenterProfile().getId());
    }

    public void showAllCourses(View view) {
        CoursesListActivity.show(getContext(), binding.getDiveCenterViewModel().getDiveCenterProfile().getId());
    }

}

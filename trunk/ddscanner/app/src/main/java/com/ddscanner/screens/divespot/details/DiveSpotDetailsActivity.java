package com.ddscanner.screens.divespot.details;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ActivityDiveSpotDetailsBinding;
import com.ddscanner.entities.DiveSpotDetailsEntity;
import com.ddscanner.entities.FlagsEntity;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.ddscanner.events.PickPhotoForCheckedInDialogEvent;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.interfaces.PhotoItemCLickListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.diecenters.list.DiveCentersActivityNew;
import com.ddscanner.screens.divespot.edit.EditDiveSpotActivity;
import com.ddscanner.screens.divespot.photos.DiveSpotPhotosActivity;
import com.ddscanner.screens.photo.slider.ImageSliderActivity;
import com.ddscanner.screens.reiews.add.LeaveReviewActivity;
import com.ddscanner.screens.reiews.list.ReviewsActivity;
import com.ddscanner.ui.activities.AddPhotosDoDiveSpotActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.CheckInPeoplesActivity;
import com.ddscanner.ui.activities.DiveCentersActivity;
import com.ddscanner.ui.activities.EditorsListActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.PhotosGalleryActivity;
import com.ddscanner.ui.activities.ShowDsLocationActivity;
import com.ddscanner.ui.adapters.PhotosGridListAdapter;
import com.ddscanner.ui.adapters.SealifeListAdapter;
import com.ddscanner.ui.dialogs.CheckedInDialogFragment;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.toptas.fancyshowcase.DismissListener;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class DiveSpotDetailsActivity extends BaseAppCompatActivity implements RatingBar.OnRatingBarChangeListener, DialogClosedListener, CompoundButton.OnCheckedChangeListener, ConfirmationDialogClosedListener, BaseAppCompatActivity.PictureTakenListener {

    private static final String TAG = DiveSpotDetailsActivity.class.getName();

    private static final String EXTRA_ID = "ID";

    private String diveSpotId;
    private boolean isCheckedIn = false;
    private boolean isFavorite = false;
    private boolean isNewDiveSpot = false;
    private boolean isMapsShown = false;
    private boolean isWorkingHere = false;
    private boolean isClickedCkeckin = false;
    private boolean isClickedFavorite = false;
    private boolean isCheckedInRequestStarted = false;
    private PhotosGridListAdapter mapsAdapter, photosAdapter;
    private CheckedInDialogFragment checkedInDialogFragment;
    private boolean isClickedEdit = false;
    private int lastChosedRating;

    /*Ui*/
    private MapFragment mapFragment;
    private Menu menu;

    private MaterialDialog materialDialog;

    private DiveSpotDetailsEntity diveSpotDetailsEntity;

    private ActivityDiveSpotDetailsBinding binding;
    private ArrayList<String> photosForReiew = new ArrayList<>();
    private WorkingHereResultListener addWorkingResultListener = new WorkingHereResultListener(true);
    private WorkingHereResultListener removeWorkngResultListener = new WorkingHereResultListener(false);
    private ApproveResultListener trueApproveResultListener = new ApproveResultListener(true);
    private ApproveResultListener falseApproveResultListener = new ApproveResultListener(false);

    private DismissListener checkinTutorialDismissListener = new DismissListener() {
        @Override
        public void onDismiss(String id) {
            DDScannerApplication.getInstance().getTutorialHelper().showAddPhotoTutorial(DiveSpotDetailsActivity.this, binding.addPhotosButon, addPhotoDismissListener);
        }

        @Override
        public void onSkipped(String id) {

        }
    };

    private DismissListener addPhotoDismissListener = new DismissListener() {
        @Override
        public void onDismiss(String id) {
            binding.scrollView.fullScroll(View.FOCUS_DOWN);
            DDScannerApplication.getInstance().getTutorialHelper().showWriteReviewTutorial(DiveSpotDetailsActivity.this, binding.btnShowAllReviews);
        }

        @Override
        public void onSkipped(String id) {

        }
    };

    private DDScannerRestClient.ResultListener<FlagsEntity> flagsResultListener = new DDScannerRestClient.ResultListener<FlagsEntity>() {
        @Override
        public void onSuccess(FlagsEntity result) {
            binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().setFlags(result);
            updateMenuItems(menu);
            changeUiAccordingNewFlags(result);
            if (isClickedEdit) {
                if (result.isEditable() || (SharedPreferenceHelper.getActiveUserType().equals(SharedPreferenceHelper.UserType.DIVECENTER) && result.isWorkingHere())) {
                    tryToCallEditDiveSpotActivity();
                }
            }
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
        }
    };

    private DDScannerRestClient.ResultListener<DiveSpotDetailsEntity> diveSpotDetailsResultListener = new DDScannerRestClient.ResultListener<DiveSpotDetailsEntity>() {
        @Override
        public void onSuccess(DiveSpotDetailsEntity result) {
            diveSpotDetailsEntity = result;
            if (SharedPreferenceHelper.getIsUserSignedIn()) {
                if (result.getFlags() == null) {
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logoutFromAllAccounts();
                } else {
                    isCheckedIn = result.getFlags().isCheckedIn();
                    isWorkingHere = result.getFlags().isWorkingHere();
                    isFavorite = result.getFlags().isFavorite();
                }
            }
            binding.setDiveSpotViewModel(new DiveSpotDetailsActivityViewModel(diveSpotDetailsEntity, binding.progressBar));
            setUi();
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
        }

    };

    private CheckInCheckoutResultListener checkInResultListener = new CheckInCheckoutResultListener(true);
    private CheckInCheckoutResultListener checkOutResultListener = new CheckInCheckoutResultListener(false);
    private AddRemoveFromFavouritesResultListener addToFavouritesResultListener = new AddRemoveFromFavouritesResultListener(true);
    private AddRemoveFromFavouritesResultListener removeToFavouritesResultListener = new AddRemoveFromFavouritesResultListener(false);

    private DiveSpotValidationListener diveSpotValidationResultListener = new DiveSpotValidationListener();

    public static void show(Context context, String id, EventsTracker.SpotViewSource spotViewSource) {
        if (spotViewSource != null) {
            EventsTracker.trackDiveSpotView(id, spotViewSource);
        }
        Intent intent = new Intent(context, DiveSpotDetailsActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    public static void showForResult(Activity context, String id, EventsTracker.SpotViewSource spotViewSource, int requestCode) {
        if (spotViewSource != null) {
            EventsTracker.trackDiveSpotView(id, spotViewSource);
        }
        Intent intent = new Intent(context, DiveSpotDetailsActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dive_spot_details);
        binding.scrollView.setNestedScrollingEnabled(false);
        themeNavAndStatusBar();
        binding.setHandlers(this);
        findViews();
        toolbarSettings();
        isNewDiveSpot = getIntent().getBooleanExtra(Constants.DIVE_SPOT_DETAILS_ACTIVITY_EXTRA_IS_FROM_AD_DIVE_SPOT, false);
        diveSpotId = getIntent().getStringExtra(EXTRA_ID);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotDetails(diveSpotId, diveSpotDetailsResultListener);
    }

    private void findViews() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_map_fragment);
    }

    private void toolbarSettings() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle("");
        binding.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        binding.collapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void changeUiAccordingNewFlags(FlagsEntity flagsEntity) {
        if (SharedPreferenceHelper.getActiveUserType().equals(SharedPreferenceHelper.UserType.DIVECENTER) && flagsEntity.isWorkingHere()) {
            flagsEntity.setEditable(true);
        }
        DiveSpotDetailsActivityViewModel.setVisibilityReviewsBlock(binding.reviewsRatingLayout, binding.getDiveSpotViewModel());
        DiveSpotDetailsActivityViewModel.setVisibilityOfRatingLayout(binding.ratingLayout, binding.getDiveSpotViewModel());
        DiveSpotDetailsActivityViewModel.setBookButtonVisibility(binding.buttonShowDivecenters, binding.getDiveSpotViewModel());
        switch (SharedPreferenceHelper.getActiveUserType()) {
            case DIVECENTER:
                binding.fabCheckin.setVisibility(View.GONE);
                binding.workinLayout.setVisibility(View.VISIBLE);
                if (flagsEntity.isWorkingHere()) {
                    binding.switchWorkingButton.setOnCheckedChangeListener(null);
                    binding.switchWorkingButton.setChecked(true);
                    binding.switchWorkingButton.setOnCheckedChangeListener(this);

                }
                if (!flagsEntity.isApproved()) {
                    binding.approveLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.approveLayout.setVisibility(View.GONE);
                }
                break;
            case INSTRUCTOR:
                if (!flagsEntity.isApproved()) {
                    binding.approveLayout.setVisibility(View.VISIBLE);
                    binding.buttonShowDivecenters.setVisibility(View.GONE);
                } else {
                    binding.approveLayout.setVisibility(View.GONE);
                    binding.buttonShowDivecenters.setVisibility(View.VISIBLE);
                }
            case DIVER:
                if (isClickedCkeckin) {
                    if (flagsEntity.isCheckedIn()) {
                        checkInUi();
                    } else {
                        checkIn();
                    }
                }
                isClickedCkeckin = false;
                updateMenuItems(menu);
                if (isClickedFavorite) {
                    if (flagsEntity.isFavorite()) {
                        updateMenuItems(menu);
                    } else {
                        addDiveSpotToFavorites();
                    }
                }
                isClickedFavorite = false;
                break;
        }
    }

    private void setUi() {
        if (SharedPreferenceHelper.getActiveUserType() != SharedPreferenceHelper.UserType.DIVECENTER && !isCheckedIn) {
            binding.fabCheckin.setVisibility(View.VISIBLE);
        }
        if (SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER && binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().isWorkingHere()) {
            binding.switchWorkingButton.setChecked(true);
        }
        binding.switchWorkingButton.setOnCheckedChangeListener(this);
        binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = binding.appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.collapsingToolbar.setTitle(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getName());
                    isShow = true;
                } else if (isShow) {
                    binding.collapsingToolbar.setTitle("");
                    isShow = false;
                }
            }
        });
        materialDialog = Helpers.getMaterialDialog(this);
        binding.ratingBar.setOnRatingBarChangeListener(this);
        if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags() != null) {
            isFavorite = binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().isFavorite();
        }
        if (SharedPreferenceHelper.getIsUserSignedIn()) {
            isFavorite = binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().isFavorite();
        }
        if (!SharedPreferenceHelper.getIsUserSignedIn()) {
            updateMenuItems(menu);
        } else {
            updateMenuItems(menu);
        }
        binding.divePlaceDescription.post(() -> {
            if (binding.divePlaceDescription.getLineCount() > 3) {
                binding.divePlaceDescription.setMaxLines(3);
                binding.divePlaceDescription.setEllipsize(TextUtils.TruncateAt.END);
                binding.showmore.setVisibility(View.VISIBLE);
            }
        });

        binding.photosRc.setNestedScrollingEnabled(false);
        binding.mapsRc.setNestedScrollingEnabled(false);
        binding.photosRc.setLayoutManager(new GridLayoutManager(DiveSpotDetailsActivity.this, 4));
        binding.mapsRc.setLayoutManager(new GridLayoutManager(DiveSpotDetailsActivity.this, 4));

        if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPhotos() != null) {
            PhotoItemCLickListener photoItemCLickListener = (isNeedToOpenGallery, position) -> {
                if (isNeedToOpenGallery) {
                    DiveSpotPhotosActivity.showForResult(DiveSpotDetailsActivity.this, diveSpotId, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PHOTOS);
                    return;
                }
                ImageSliderActivity.showForResult(DiveSpotDetailsActivity.this, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPhotos(), position, -1, PhotoOpenedSource.DIVESPOT, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()));
            };
            photosAdapter = new PhotosGridListAdapter(R.layout.dive_spot_photos_list_item, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPhotos(), binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPhotosCount(), 8, photoItemCLickListener);
//            photosAdapter = new DiveSpotPhotosAdapter(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPhotos(), DiveSpotDetailsActivity.this, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPhotosCount());
            binding.photosRc.setVisibility(View.VISIBLE);
            binding.addPhotosLayout.setVisibility(View.GONE);
            binding.photosRc.setAdapter(photosAdapter);
            binding.addPhotosButon.setVisibility(View.VISIBLE);
        }

        if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getMaps() != null) {
            PhotoItemCLickListener mapsItemCLickListener = (isNeedToOpenGallery, position) -> {
                if (isNeedToOpenGallery) {
                    PhotosGalleryActivity.showForResult(diveSpotId, DiveSpotDetailsActivity.this, PhotoOpenedSource.MAPS, null, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PHOTOS);
                    return;
                }
                ImageSliderActivity.showForResult(DiveSpotDetailsActivity.this, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getMaps(), position, -1, PhotoOpenedSource.MAPS, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()));
            };
//            mapsAdapter = new DiveSpotPhotosAdapter(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getMaps(), DiveSpotDetailsActivity.this, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getMapsPhotosCount());
            mapsAdapter = new PhotosGridListAdapter(R.layout.dive_spot_photos_list_item, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getMaps(), binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getMapsPhotosCount(), 8, mapsItemCLickListener);
            binding.mapsRc.setAdapter(mapsAdapter);
            binding.addPhotosLayout.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(DiveSpotDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.sealifeRc.setNestedScrollingEnabled(false);
        binding.sealifeRc.setHasFixedSize(false);
        binding.sealifeRc.setLayoutManager(layoutManager);
        binding.sealifeRc.setAdapter(new SealifeListAdapter((ArrayList<SealifeShort>) binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getSealifes(), this));
        mapFragment.getMapAsync(googleMap -> workWithMap(googleMap));

        if (isCheckedIn) {
            binding.fabCheckin.setVisibility(View.GONE);
//            checkInUi();
        } else {
            binding.fabCheckin.setVisibility(View.VISIBLE);
            checkOutUi();
        }
        binding.progressBarFull.setVisibility(View.GONE);
        binding.informationLayout.setVisibility(View.VISIBLE);
        binding.buttonShowDivecenters.setVisibility(View.VISIBLE);
        binding.scrollView.setNestedScrollingEnabled(true);
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsMstToShowDiveSpotTutorial()) {
            new Handler().postDelayed(() -> DDScannerApplication.getInstance().getTutorialHelper().showBookingTutorial(this, binding.buttonShowDivecenters), 500);
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustToShowDiveSpotDetailsTutorial(false);
//            DDScannerApplication.getInstance().getTutorialHelper().showCheckinTutorial(this, binding.fabCheckin, checkinTutorialDismissListener);
        }
    }


    private void workWithMap(GoogleMap googleMap) {
//        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds))
//                .position(diveSpotCoordinates));
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_ds)))
                .position(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition(), 7.0f));
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMapClickListener(latLng -> {
            Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
            intent.putExtra("LATLNG", binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition());
            startActivity(intent);
        });
        googleMap.setOnMarkerClickListener(marker -> {
            Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
            intent.putExtra("LATLNG", binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition());
            startActivity(intent);
            return false;
        });
    }

    public boolean checkReadStoragePermission(Activity context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.DIVE_SPOT_DETAILS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePickerActivity(ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PICK_PHOTOS);
                } else {
                    Toast.makeText(DiveSpotDetailsActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void openImagePickerActivity(int requestCode) {
        if (!SharedPreferenceHelper.getIsUserSignedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_PICK_PHOTOS);
        } else {
            pickPhotosFromGallery();
        }
    }

    private void tryToCallEditDiveSpotActivity() {
        if (SharedPreferenceHelper.getIsUserSignedIn()) {
            EditDiveSpotActivity.showForResult(new Gson().toJson(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity()), this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_EDIT_DIVE_SPOT);
        } else {
            isClickedEdit = true;
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_EDIT_SPOT);
        }
    }


    private void checkIn() {
        checkInUi();
        if (!SharedPreferenceHelper.getIsUserSignedIn()) {
            checkOutUi();
            isClickedCkeckin = true;
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_IN);
            return;
        }
        isCheckedInRequestStarted = true;
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postCheckIn(diveSpotId, checkInResultListener);
    }

    private void checkInUi() {
        binding.fabCheckin.setImageDrawable(ContextCompat.getDrawable(DiveSpotDetailsActivity.this, R.drawable.ic_acb_pin_checked));
        binding.fabCheckin.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        isCheckedIn = true;
        isClickedCkeckin = false;
    }

    private void checkOutUi() {
        binding.fabCheckin.setImageDrawable(ContextCompat.getDrawable(DiveSpotDetailsActivity.this, R.drawable.ic_acb_pin));
        binding.fabCheckin.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange)));
        isCheckedIn = false;
    }

    private void checkOut() {
        checkOutUi();
        if (!SharedPreferenceHelper.getIsUserSignedIn()) {
            checkInUi();
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_OUT);
            return;
        }
        isCheckedInRequestStarted = true;
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postCheckOut(diveSpotId, checkOutResultListener);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        lastChosedRating = Math.round(rating);
        tryingToShowLeaveReviewActivity(lastChosedRating, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.favorite:
                if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags() != null && !binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().isFavorite()) {
                    addDiveSpotToFavorites();
                } else {
                    removeFromFavorites();
                }
                break;
            case R.id.edit_dive_spot:
                tryToCallEditDiveSpotActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void themeNavAndStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        w.setNavigationBarColor(ContextCompat.getColor(this ,android.R.color.transparent));
        w.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void addDiveSpotToFavorites() {
        isClickedFavorite = true;
        if (!SharedPreferenceHelper.getIsUserSignedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_ADD_TO_FAVOURITES);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddDiveSpotToFavourites(diveSpotId, addToFavouritesResultListener);
    }

    private void removeFromFavorites() {
        if (!SharedPreferenceHelper.getIsUserSignedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_REMOVE_FROM_FAVOURITES);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient(this).deleteDiveSpotFromFavourites(diveSpotId, removeToFavouritesResultListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LEAVE_REVIEW:
                if (resultCode == RESULT_OK) {
                    binding.ratingLayout.setVisibility(View.GONE);
                    binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().setReviewsCount(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getReviewsCount() + 1);
                    DiveSpotDetailsActivityViewModel.setReviewsCount(binding.btnShowAllReviews, binding.getDiveSpotViewModel());
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_LOAD_DATA:
                if (resultCode == RESULT_OK) {
                    refreshActivity();
                    break;
                }
                finish();
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_REVIEWS:
                if (resultCode == RESULT_OK) {
                    if (data.getStringExtra("count") != null) {
                        try {
                            binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().setReviewsCount(Integer.parseInt(data.getStringExtra("count")));
                            DiveSpotDetailsActivityViewModel.setReviewsCount(binding.btnShowAllReviews, binding.getDiveSpotViewModel());
                        } catch (NullPointerException e) {
                            finish();
                        }
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PHOTOS:
                if (resultCode == RESULT_OK) {
                    refreshActivity();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_EDIT_DIVE_SPOT:
                if (resultCode == RESULT_OK) {
                    refreshActivity();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_ADD_PHOTOS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotDetails(String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()), diveSpotDetailsResultListener);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_PICK_PHOTOS:
                if (resultCode == RESULT_OK) {
                    pickPhotosFromGallery();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_VALIDATE_SPOT:
                if (resultCode == RESULT_OK) {
//                    validateDiveSpot(true);
                } else {
//                    isInfoValidLayout.setVisibility(View.VISIBLE);
//                    thanksLayout.setVisibility(View.GONE);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_INVALIDATE_SPOT:
                if (resultCode == RESULT_OK) {
//                    validateDiveSpot(false);
                } else {
//                    isInfoValidLayout.setVisibility(View.VISIBLE);
//                    thanksLayout.setVisibility(View.GONE);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_EDIT_SPOT:
                if (resultCode == RESULT_OK) {
                    reloadFlags();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_IN:
                if (resultCode == RESULT_OK) {
                    reloadFlags();
                } else {
                    isClickedCkeckin = false;
                    checkOutUi();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_OUT:
                if (resultCode == RESULT_OK) {
                    reloadFlags();
                    checkOut();
                } else {
                    isClickedFavorite = false;
                    checkInUi();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_ADD_TO_FAVOURITES:
                if (resultCode == RESULT_OK) {
                    reloadFlags();
                    addDiveSpotToFavorites();
                } else {
                    updateMenuItems(menu);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_REMOVE_FROM_FAVOURITES:
                if (resultCode == RESULT_OK) {
                    removeFromFavorites();
                } else {
                    updateMenuItems(menu);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_SHOW_FOR_ADD_MAPS:
                if (resultCode == RESULT_OK) {
                    refreshActivity();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_SHOW_FOR_ADD_PHOTOS:
                if (resultCode == RESULT_OK) {
                    refreshActivity();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_LEAVE_REVIEW:
                if (resultCode == RESULT_OK) {
                    if (SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
                        UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.sorry, R.string.dive_centers_cannot_leave_review, false);
                        reloadFlags();
                    } else {
                        DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                        tryingToShowLeaveReviewActivity(lastChosedRating, true);
                    }

                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateMenuItems(Menu menu) {
        if (SharedPreferenceHelper.getIsUserSignedIn()) {
            switch (SharedPreferenceHelper.getActiveUserType()) {
                case DIVECENTER:
                    menu.findItem(R.id.menu_three_dots).setVisible(true);
                    menu.findItem(R.id.favorite).setVisible(false);
                    if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().isEditable() || binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().isWorkingHere()) {
                        menu.findItem(R.id.edit_dive_spot).setVisible(true);
                    } else {
                        menu.findItem(R.id.menu_three_dots).setVisible(false);
                    }
                    break;
                case INSTRUCTOR:
                case DIVER:
                    try {
                        menu.findItem(R.id.menu_three_dots).setVisible(true);
                        menu.findItem(R.id.favorite).setVisible(true);
                        if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().isFavorite()) {
                            menu.findItem(R.id.favorite).setTitle(R.string.reove_from_facorites);
                        } else {
                            menu.findItem(R.id.favorite).setTitle(R.string.add_to_favorites);
                        }
                        if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().isEditable()) {
                            menu.findItem(R.id.edit_dive_spot).setVisible(true);
                        } else {
                            menu.findItem(R.id.edit_dive_spot).setVisible(false);
                        }
                    } catch (Exception ignored) {

                    }
                    break;
            }
        } else {
            menu.findItem(R.id.menu_three_dots).setVisible(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsMustRefreshDiveSpotActivity()) {
            reloadFlags();
        }
    }

    private void reloadFlags() {
        DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(false);
        switch (SharedPreferenceHelper.getActiveUserType()) {
            case DIVECENTER:
                isClickedCkeckin = false;
                isClickedFavorite = false;
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterStatusInDiveSpot(flagsResultListener, diveSpotId);
                break;
            case DIVER:
            case INSTRUCTOR:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserStatusInDiveSpot(flagsResultListener, diveSpotId);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_FAILED_TO_CONNECT:
            case DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        binding.switchWorkingButton.setClickable(false);
        if (!isWorkingHere) {
            DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddDiveSpotToDiveCenter(diveSpotId, addWorkingResultListener);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postRemoveDiveSpotToDiveCenter(diveSpotId, removeWorkngResultListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }

    public void showMoreDescription(View view) {
        binding.showmore.setVisibility(View.GONE);
        binding.divePlaceDescription.setMaxLines(10000);
        binding.divePlaceDescription.setEllipsize(null);
    }

    public void showCheckinsActivity(View view) {
        if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getCheckinCount() > 0) {
            CheckInPeoplesActivity.show(this, diveSpotId);
        }
    }

    public void addPhotoToDiveSpotButtonClicked(View view) {
        if (checkReadStoragePermission(this)) {
            openImagePickerActivity(ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PICK_PHOTOS);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.DIVE_SPOT_DETAILS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE);
        }
    }

    public void photosButtonClicked(View view) {
        if (isMapsShown) {
            changeViewState(binding.photosButton, binding.maps);
            isMapsShown = !isMapsShown;
            binding.mapsRc.setVisibility(View.GONE);
            if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPhotos() != null) {
                binding.photosRc.setVisibility(View.VISIBLE);
                binding.addPhotosLayout.setVisibility(View.GONE);
                binding.addPhotosButon.setVisibility(View.VISIBLE);
                return;
            }
            binding.photosRc.setVisibility(View.GONE);
            binding.addPhotosLayout.setVisibility(View.VISIBLE);
            binding.addPhotosButon.setVisibility(View.GONE);
        }
    }

    public void mapsButtonClicked(View view) {
        if (!isMapsShown) {
            changeViewState(binding.maps, binding.photosButton);
            isMapsShown = !isMapsShown;
            binding.photosRc.setVisibility(View.GONE);
            if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getMaps() != null) {
                binding.mapsRc.setVisibility(View.VISIBLE);
                binding.addPhotosLayout.setVisibility(View.GONE);
                binding.addPhotosButon.setVisibility(View.VISIBLE);
                return;
            }
            binding.mapsRc.setVisibility(View.GONE);
            binding.addPhotosLayout.setVisibility(View.VISIBLE);
            binding.addPhotosButon.setVisibility(View.GONE);
        }
    }

    public void checkInClicked(View view) {
        if (!isCheckedInRequestStarted) {
            if (isCheckedIn) {
                checkOut();
                return;
            }
            checkIn();
        }
    }

    public void openMapActivityClicked(View view) {
        EventsTracker.trackDiveSpotLocationOnMapView();
        Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
        intent.putExtra("LATLNG", binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition());
        startActivity(intent);
    }

    public void showDiveCentersButtonClicked(View view) {
//        EventsTracker.trackContactDiveCenter();
        if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().isSomebodyWorkingHere()) {
            DiveCentersActivityNew.show(this, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()));
            return;
        }
        UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.no_dive_centers, R.string.booking_will_be_soon, false);
//        DiveCentersActivity.show(this, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition(), binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getName(), binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId().toString(), binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().isSomebodyWorkingHere());
    }

    private void changeViewState(TextView activeTextView, TextView disableTextView) {
        activeTextView.setTextColor(ContextCompat.getColor(this, R.color.black_text));
        activeTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_rectangle));

        disableTextView.setTextColor(ContextCompat.getColor(this, R.color.diactive_button_photo_color));
        disableTextView.setBackground(null);
    }

    @Subscribe
    public void addPhotoToCheckedInDialogFragment(PickPhotoForCheckedInDialogEvent event) {
        if (checkReadStoragePermission(this)) {
            openImagePickerActivity(ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PICK_PHOTO_FOR_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.DIVE_SPOT_DETAILS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE);
        }
    }

    public void trueApproveDiveSpot(View view) {
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postApproveDiveSpot(diveSpotId, true, trueApproveResultListener);
    }

    public void showCreatorsActivity(View view) {
        EventsTracker.trackEditorsView();
        EditorsListActivity.show(this, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()));
    }

    public void falseApproveDiveSpot(View view) {
        DDScannerApplication.getInstance().getDialogHelpers().showNegativeApproveDialog(getSupportFragmentManager());
    }

    @Override
    public void onPositiveDialogClicked() {
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postApproveDiveSpot(diveSpotId, false, falseApproveResultListener);
    }

    @Override
    public void onPicturesTaken(ArrayList<String> pictures) {
        if (isMapsShown) {
            AddPhotosDoDiveSpotActivity.showForAddPhotos(true, DiveSpotDetailsActivity.this, pictures, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()), ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_SHOW_FOR_ADD_MAPS);
            return;
        }
        AddPhotosDoDiveSpotActivity.showForAddPhotos(false, DiveSpotDetailsActivity.this, pictures, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()), ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_SHOW_FOR_ADD_PHOTOS);

    }

    @Override
    public void onPictureFromCameraTaken(File picture) {

    }

    @Override
    public void onNegativeDialogClicked() {
        tryToCallEditDiveSpotActivity();
    }

    public void writeReviewClicked(View view) {
        tryingToShowLeaveReviewActivity(1, false);
    }

    private void tryingToShowLeaveReviewActivity(int rating, boolean isFromRatingBar) {
        if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getReviewsCount() < 1) {
            if (SharedPreferenceHelper.getIsUserSignedIn()) {
                LeaveReviewActivity.showForResult(this, diveSpotId, rating, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LEAVE_REVIEW, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition(), EventsTracker.SendReviewSource.WRITE_REVIEW_BUTTON);
            } else {
                LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_LEAVE_REVIEW);
            }
        } else {
            EventsTracker.trackReviewShowAll();
            if (isFromRatingBar) {
                LeaveReviewActivity.showForResult(this, diveSpotId, rating, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LEAVE_REVIEW, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition(), EventsTracker.SendReviewSource.FROM_RATING_BAR);
                return;
            }
            ReviewsActivity.showForDiveSpot(this, diveSpotId, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_REVIEWS, ReviewsOpenedSource.DIVESPOT, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition());
        }
    }

    private class ApproveResultListener extends DDScannerRestClient.ResultListener<Void> {

        private boolean isTrue;

        ApproveResultListener(boolean isTrue) {
            this.isTrue = isTrue;
        }

        @Override
        public void onSuccess(Void result) {
            if (!isTrue) {
                setResult(RESULT_CODE_DIVE_SPOT_REMOVED);
                finish();
                EventsTracker.trackDiveSpotInvalid();
                return;
            }
            EventsTracker.trackDiveSpotValid();
            binding.approveLayout.setVisibility(View.GONE);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);

        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    }

    private class WorkingHereResultListener extends DDScannerRestClient.ResultListener<Void> {

        private boolean isAddToWorking;

        WorkingHereResultListener(boolean isAddToWorking) {
            this.isAddToWorking = isAddToWorking;
        }

        @Override
        public void onSuccess(Void result) {
            binding.switchWorkingButton.setClickable(true);
            if (isAddToWorking) {
                binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().setWorkingHere(true);
                updateMenuItems(menu);
                DiveSpotDetailsActivity.this.isWorkingHere = true;
                return;
            }
            binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().setWorkingHere(false);
            updateMenuItems(menu);
            DiveSpotDetailsActivity.this.isWorkingHere = false;
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            binding.switchWorkingButton.setClickable(true);
            if (isAddToWorking) {
                binding.switchWorkingButton.setChecked(false);
                return;
            }
            binding.switchWorkingButton.setChecked(true);
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_ADD_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onConnectionFailure() {
            binding.switchWorkingButton.setClickable(true);
            if (isAddToWorking) {
                binding.switchWorkingButton.setChecked(false);
                return;
            }
            binding.switchWorkingButton.setChecked(true);
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            binding.switchWorkingButton.setClickable(true);
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    }

    private class CheckInCheckoutResultListener extends DDScannerRestClient.ResultListener<Void> {
        private boolean isCheckIn;

        CheckInCheckoutResultListener(boolean isCheckIn) {
            isCheckedInRequestStarted = false;
            this.isCheckIn = isCheckIn;
        }

        @Override
        public void onSuccess(Void result) {
            isCheckedInRequestStarted = false;
            if (isCheckIn) {
                DiveSpotDetailsActivity.this.isCheckedIn = true;
                binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().setCheckinCount(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getCheckinCount() + 1);
                DiveSpotDetailsActivityViewModel.setCheckinsCount(binding.numberOfCheckingPeople, binding.getDiveSpotViewModel());
                EventsTracker.trackCheckIn();
                checkedInDialogFragment = CheckedInDialogFragment.getCheckedInDialog(diveSpotId, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition(),DiveSpotDetailsActivity.this);
                if (!isPopupShown) {
                    checkedInDialogFragment.show(getSupportFragmentManager(), "");
                }
            } else {
                DiveSpotDetailsActivity.this.isCheckedIn = false;
                binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().setCheckinCount(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getCheckinCount() - 1);
                DiveSpotDetailsActivityViewModel.setCheckinsCount(binding.numberOfCheckingPeople, binding.getDiveSpotViewModel());
                EventsTracker.trackCheckOut();
            }
        }

        @Override
        public void onConnectionFailure() {
            isCheckedInRequestStarted = false;
            if (isCheckIn) {
                checkOutUi();
            } else {
                checkInUi();
            }
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            isCheckedInRequestStarted = false;
            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    if (isCheckIn) {
                        checkOutUi();
                    } else {
                        checkInUi();
                    }
                    // This is unexpected so track it
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_message_dive_spot_not_found);
                    break;
                default:
                    if (isCheckIn) {
                        checkOutUi();
                    } else {
                        checkInUi();
                    }
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    }

    private class AddRemoveFromFavouritesResultListener extends DDScannerRestClient.ResultListener<Void> {

        private boolean isAddToFavourites;

        AddRemoveFromFavouritesResultListener(boolean isAddToFavourites) {
            this.isAddToFavourites = isAddToFavourites;
        }

        @Override
        public void onSuccess(Void result) {
            binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().setFavorite(isAddToFavourites);
            updateMenuItems(menu);
            Toast.makeText(DiveSpotDetailsActivity.this, isAddToFavourites ? R.string.added_to_favorites : R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionFailure() {
            updateMenuItems(menu);
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_message_dive_spot_not_found);
                    break;
                case BAD_REQUEST_ERROR_400:
                    isFavorite = isAddToFavourites;
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, isAddToFavourites ? R.string.error_message_already_added_to_favourites : R.string.error_message_already_removed_from_favourites, false);
                    break;
                case UNPROCESSABLE_ENTITY_ERROR_422:
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
            }
            updateMenuItems(menu);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    }

    private class DiveSpotValidationListener extends DDScannerRestClient.ResultListener<Void> {
        private boolean isValid;

        public void setValid(boolean valid) {
            isValid = valid;
        }

        @Override
        public void onSuccess(Void result) {

            materialDialog.dismiss();
            if (isValid) {
//                EventsTracker.trackDiveSpotValid();
            } else {
            }
            if (isValid) {
                binding.approveLayout.setVisibility(View.GONE);
            } else {
                setResult(RESULT_CODE_DIVE_SPOT_REMOVED);
                finish();
            }
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_message_dive_spot_not_found);
                    break;
                case BAD_REQUEST_ERROR_400:
                    if (menu != null && menu.findItem(R.id.edit_dive_spot) != null) {
                        menu.findItem(R.id.edit_dive_spot).setVisible(false);
                    }
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_already_validated_dive_spot_data, false);
                    break;
                case UNPROCESSABLE_ENTITY_ERROR_422:
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    }

    @Subscribe
    public void openDiveSpotPhotosActivity(OpenPhotosActivityEvent event) {
        if (isMapsShown) {
            PhotosGalleryActivity.showForResult(String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()), this, PhotoOpenedSource.MAPS, null, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PHOTOS);
            return;
        }
        DiveSpotPhotosActivity.showForResult(DiveSpotDetailsActivity.this, diveSpotId, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PHOTOS);
    }

}
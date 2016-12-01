package com.ddscanner.screens.divespot.details;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ActivityDiveSpotDetailsBinding;
import com.ddscanner.entities.DiveSpotDetailsEntity;
import com.ddscanner.entities.DiveSpotSealife;
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.AddPhotosDoDiveSpotActivity;
import com.ddscanner.ui.activities.DiveCentersActivity;
import com.ddscanner.ui.activities.EditDiveSpotActivity;
import com.ddscanner.ui.activities.LeaveReviewActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.ShowDsLocationActivity;
import com.ddscanner.ui.adapters.SealifeListAdapter;
import com.ddscanner.ui.dialogs.CheckedInDialogFragment;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DiveSpotDetailsActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener, InfoDialogFragment.DialogClosedListener {

    private static final String TAG = DiveSpotDetailsActivity.class.getName();

    private static final String EXTRA_ID = "ID";

    private String diveSpotId;
    private boolean isCheckedIn = false;
    private boolean isFavorite = false;
    private boolean isNewDiveSpot = false;
    private boolean isMapsShown = false;
    private DiveSpotPhotosAdapter mapsAdapter, photosAdapter;

    /*Ui*/
    private MapFragment mapFragment;
    private Menu menu;

    private MaterialDialog materialDialog;

    private DiveSpotDetailsEntity diveSpotDetailsEntity;

    private ActivityDiveSpotDetailsBinding binding;

    private DDScannerRestClient.ResultListener<DiveSpotDetailsEntity> diveSpotDetailsResultListener = new DDScannerRestClient.ResultListener<DiveSpotDetailsEntity>() {
        @Override
        public void onSuccess(DiveSpotDetailsEntity result) {
            diveSpotDetailsEntity = result;
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
                isCheckedIn = result.getFlags().isCheckedIn();
            }
            binding.setDiveSpotViewModel(new DiveSpotDetailsActivityViewModel(diveSpotDetailsEntity, binding.progressBar));
            setUi();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_dive_spot_not_found, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
            }
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dive_spot_details);
        binding.setHandlers(this);
        findViews();
        toolbarSettings();
        isNewDiveSpot = getIntent().getBooleanExtra(Constants.DIVE_SPOT_DETAILS_ACTIVITY_EXTRA_IS_FROM_AD_DIVE_SPOT, false);
        diveSpotId = getIntent().getStringExtra(EXTRA_ID);
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotDetails(diveSpotId, diveSpotDetailsResultListener);
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

    private void setUi() {
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
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            isFavorite = binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getFlags().isFavorite();
        }
        updateMenuItems(menu, isFavorite);
        binding.divePlaceDescription.post(new Runnable() {
            @Override
            public void run() {
                if (binding.divePlaceDescription.getLineCount() > 3) {
                    binding.divePlaceDescription.setMaxLines(3);
                    binding.divePlaceDescription.setEllipsize(TextUtils.TruncateAt.END);
                    binding.showmore.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.photosRc.setLayoutManager(new GridLayoutManager(DiveSpotDetailsActivity.this, 4));
        binding.mapsRc.setLayoutManager(new GridLayoutManager(DiveSpotDetailsActivity.this, 4));

        if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPhotos() != null) {
            photosAdapter = new DiveSpotPhotosAdapter((ArrayList<String>) binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPhotos(), DiveSpotDetailsActivity.this);
            binding.photosRc.setVisibility(View.VISIBLE);
            binding.addPhotosLayout.setVisibility(View.GONE);
            binding.photosRc.setAdapter(photosAdapter);
        }

        if (binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getMaps() != null) {
            mapsAdapter = new DiveSpotPhotosAdapter((ArrayList<String>) binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getMaps(), DiveSpotDetailsActivity.this);
            binding.mapsRc.setAdapter(mapsAdapter);
            binding.addPhotosLayout.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(DiveSpotDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.sealifeRc.setNestedScrollingEnabled(false);
        binding.sealifeRc.setHasFixedSize(false);
        binding.sealifeRc.setLayoutManager(layoutManager);
        binding.sealifeRc.setAdapter(new SealifeListAdapter((ArrayList<DiveSpotSealife>) binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getSealifes(), this));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                workWithMap(googleMap);
            }
        });

        if (isCheckedIn) {
            checkInUi();
        } else {
            checkOutUi();
        }
//        if (diveSpot.getValidation() != null) {
//            if (!diveSpot.getValidation()) {
//                isInfoValidLayout.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        isInfoValidLayout.setVisibility(View.VISIBLE);
//                    }
//                });
//            } else {
//                if (menu != null && menu.findItem(R.id.edit_dive_spot) != null) {
//                    menu.findItem(R.id.edit_dive_spot).setVisible(false);
//                }
//            }
//        }
        binding.progressBarFull.setVisibility(View.GONE);
        binding.informationLayout.setVisibility(View.VISIBLE);
        binding.buttonShowDivecenters.setVisibility(View.VISIBLE);
    }


    private void workWithMap(GoogleMap googleMap) {
        // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds))
//                .position(diveSpotCoordinates));
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_ds)))
                .position(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition(), 7.0f));
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
                intent.putExtra("LATLNG", binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition());
                startActivity(intent);
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
                intent.putExtra("LATLNG", binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition());
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_layout:
                Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
                intent.putExtra("LATLNG", binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition());
                startActivity(intent);
                break;
            case R.id.check_in_peoples:
                EventsTracker.trackDiveSpotCheckinsView();
              //  CheckInPeoplesActivity.show(DiveSpotDetailsActivity.this, (ArrayList<UserOld>) usersCheckins);
                break;
            case R.id.creator:
//                EditorsListActivity.show(DiveSpotDetailsActivity.this, (ArrayList<UserOld>) creatorsEditorsList);
                break;
            case R.id.button_show_divecenters:
                DiveCentersActivity.show(this, binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getPosition(), binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getName());
                break;
            case R.id.photos:
//                DDScannerApplication.bus.post(new OpenPhotosActivityEvent());
                break;
            case R.id.btn_add_photo:
                if (checkReadStoragePermission(this)) {
                    addPhotosToDiveSpot();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.DIVE_SPOT_DETAILS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE);
                }
                break;
        }
    }

    public boolean checkReadStoragePermission(Activity context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.DIVE_SPOT_DETAILS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPhotosToDiveSpot();
                } else {
                    Toast.makeText(DiveSpotDetailsActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void addPhotosToDiveSpot() {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_PICK_PHOTOS);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= 18) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PICK_PHOTOS);
        }
    }

    private void tryToCallEditDiveSpotActivity() {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            Intent editDiveSpotIntent = new Intent(DiveSpotDetailsActivity.this, EditDiveSpotActivity.class);
            editDiveSpotIntent.putExtra(Constants.DIVESPOTID, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()));
            startActivityForResult(editDiveSpotIntent, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_EDIT_DIVE_SPOT);
        } else {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_EDIT_SPOT);
        }
    }


    private void checkIn() {
        checkInUi();
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            checkOutUi();
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_IN);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postCheckIn(diveSpotId, checkInResultListener);
    }

    private void checkInUi() {
        binding.fabCheckin.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                DiveSpotDetailsActivity.this, R.drawable.ic_acb_pin_checked));
        binding.fabCheckin.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        isCheckedIn = true;
    }

    private void checkOutUi() {
        binding.fabCheckin.setImageDrawable(AppCompatDrawableManager.get().getDrawable(DiveSpotDetailsActivity.this, R.drawable.ic_acb_pin));
        binding.fabCheckin.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange)));
        isCheckedIn = false;
    }

    private void checkOut() {
        checkOutUi();
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            checkInUi();
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_OUT);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postCheckOut(diveSpotId, checkOutResultListener);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        LeaveReviewActivity.showForResult(this, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()), rating, EventsTracker.SendReviewSource.FROM_RATING_BAR, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LEAVE_REVIEW);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.favorite:
                if (!isFavorite) {
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

    private void validateDiveSpot(final boolean isValid) {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            LoginActivity.showForResult(DiveSpotDetailsActivity.this, isValid ? ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_VALIDATE_SPOT : ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_INVALIDATE_SPOT);
            return;
        }
        materialDialog.show();
        diveSpotValidationResultListener.setValid(isValid);
        DDScannerApplication.getInstance().getDdScannerRestClient().postValidateDiveSpot(diveSpotId, isValid, diveSpotValidationResultListener);
    }

    private void hideThanksLayout() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.thanksLayout.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void addDiveSpotToFavorites() {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_ADD_TO_FAVOURITES);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postAddDiveSpotToFavourites(diveSpotId, addToFavouritesResultListener);
    }

    private void removeFromFavorites() {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_REMOVE_FROM_FAVOURITES);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().deleteDiveSpotFromFavourites(diveSpotId, removeToFavouritesResultListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LEAVE_REVIEW:
                if (resultCode == RESULT_OK) {
//                    if (diveSpot != null) {
//                        //TODO send count changed reviews
//                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_REVIEWS:
                if (resultCode == RESULT_OK) {
//                    if (diveSpot != null) {
//                        //TODO send count changed reviews
//                    }
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
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PICK_PHOTOS:
                if (resultCode == RESULT_OK) {
                    List<String> urisList = new ArrayList<>();
                    Uri uri = Uri.parse("");
                    if (resultCode == RESULT_OK) {
                        if (data.getClipData() != null) {
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                String filename = "DDScanner" + String.valueOf(System.currentTimeMillis());
                                try {
                                    uri = data.getClipData().getItemAt(i).getUri();
                                    String mimeType = getContentResolver().getType(uri);
                                    String sourcePath = getExternalFilesDir(null).toString();
                                    File file = new File(sourcePath + "/" + filename);
                                    if (Helpers.isFileImage(uri.getPath()) || mimeType.contains("image")) {
                                        try {
                                            Helpers.copyFileStream(file, uri, this);
                                            Log.i(TAG, file.toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        urisList.add(file.getPath());
                                    } else {
                                        Toast.makeText(this, "You can choose only images", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (data.getData() != null) {
                            String filename = "DDScanner" + String.valueOf(System.currentTimeMillis());
                            try {
                                uri = data.getData();
                                String mimeType = getContentResolver().getType(uri);
                                String sourcePath = getExternalFilesDir(null).toString();
                                File file = new File(sourcePath + "/" + filename);
                                if (Helpers.isFileImage(uri.getPath()) || mimeType.contains("image")) {
                                    try {
                                        Helpers.copyFileStream(file, uri, this);
                                        Log.i(TAG, file.toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    urisList.add(file.getPath());
                                } else {
                                    Toast.makeText(this, "You can choose only images", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (isMapsShown) {
                            AddPhotosDoDiveSpotActivity.showForAddPhotos(true, DiveSpotDetailsActivity.this, (ArrayList<String>) urisList, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()), ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_SHOW_FOR_ADD_MAPS);
                            return;
                        }
                        AddPhotosDoDiveSpotActivity.showForAddPhotos(false, DiveSpotDetailsActivity.this, (ArrayList<String>) urisList, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()), ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_SHOW_FOR_ADD_PHOTOS);
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_ADD_PHOTOS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotDetails(String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()), diveSpotDetailsResultListener);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_PICK_PHOTOS:
                if (resultCode == RESULT_OK) {

                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_VALIDATE_SPOT:
                if (resultCode == RESULT_OK) {
                    validateDiveSpot(true);
                } else {
//                    isInfoValidLayout.setVisibility(View.VISIBLE);
//                    thanksLayout.setVisibility(View.GONE);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_INVALIDATE_SPOT:
                if (resultCode == RESULT_OK) {
                    validateDiveSpot(false);
                } else {
//                    isInfoValidLayout.setVisibility(View.VISIBLE);
//                    thanksLayout.setVisibility(View.GONE);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_EDIT_SPOT:
                if (resultCode == RESULT_OK) {
                    Intent editDiveSpotIntent = new Intent(DiveSpotDetailsActivity.this, EditDiveSpotActivity.class);
                    editDiveSpotIntent.putExtra(Constants.DIVESPOTID, String.valueOf(binding.getDiveSpotViewModel().getDiveSpotDetailsEntity().getId()));
                    startActivityForResult(editDiveSpotIntent, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_EDIT_DIVE_SPOT);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_IN:
                if (resultCode == RESULT_OK) {
                    checkIn();
                } else {
                    checkOutUi();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_OUT:
                if (resultCode == RESULT_OK) {
                    checkOut();
                } else {
                    checkInUi();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_ADD_TO_FAVOURITES:
                if (resultCode == RESULT_OK) {
                    addDiveSpotToFavorites();
                } else {
                    updateMenuItems(menu, false);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_REMOVE_FROM_FAVOURITES:
                if (resultCode == RESULT_OK) {
                    removeFromFavorites();
                } else {
                    updateMenuItems(menu, true);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_SHOW_FOR_ADD_MAPS:
                if (resultCode == RESULT_OK) {
                    mapsAdapter.addPhotos(data.getStringArrayListExtra("images"));
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_SHOW_FOR_ADD_PHOTOS:
                if (resultCode == RESULT_OK) {
                    photosAdapter.addPhotos(data.getStringArrayListExtra("images"));
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

    private void updateMenuItems(Menu menu, boolean isFavorite) {
        if (isFavorite) {
            menu.findItem(R.id.favorite).setTitle("Remove from favorites");
            return;
        }
        if (menu != null) {
            if (menu.findItem(R.id.favorite) != null) {
                menu.findItem(R.id.favorite).setTitle("Add to favorites");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Subscribe
    public void openImagesActivity(OpenPhotosActivityEvent event) {
        
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
    public void onBackPressed() {
        finish();
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }

    private class CheckInCheckoutResultListener extends DDScannerRestClient.ResultListener<Void> {
        private boolean isCheckIn;

        CheckInCheckoutResultListener(boolean isCheckIn) {
            this.isCheckIn = isCheckIn;
        }

        @Override
        public void onSuccess(Void result) {
            if (isCheckIn) {
                DiveSpotDetailsActivity.this.isCheckedIn = true;
                EventsTracker.trackCheckIn(EventsTracker.CheckInStatus.SUCCESS);
                CheckedInDialogFragment.showCheckedInDialog(diveSpotId, DiveSpotDetailsActivity.this);
            } else {
                DiveSpotDetailsActivity.this.isCheckedIn = false;
                EventsTracker.trackCheckOut();
            }
        }

        @Override
        public void onConnectionFailure() {
            if (isCheckIn) {
                checkOutUi();
            } else {
                checkInUi();
            }
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
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
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    if (isCheckIn) {
                        checkOutUi();
                        LoginActivity.showForResult(DiveSpotDetailsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_IN);
                    } else {
                        checkInUi();
                        LoginActivity.showForResult(DiveSpotDetailsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_OUT);
                    }
                    break;
                case BAD_REQUEST_ERROR_400:
                    if (isCheckIn) {
                        checkInUi();
                    } else {
                        checkOutUi();
                    }
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, isCheckIn ? R.string.error_message_already_checked_in : R.string.error_message_already_checked_out, false);
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
    }

    private class AddRemoveFromFavouritesResultListener extends DDScannerRestClient.ResultListener<Void> {

        private boolean isAddToFavourites;

        AddRemoveFromFavouritesResultListener(boolean isAddToFavourites) {
            this.isAddToFavourites = isAddToFavourites;
        }

        @Override
        public void onSuccess(Void result) {
            isFavorite = isAddToFavourites;
            updateMenuItems(menu, isFavorite);
            Toast.makeText(DiveSpotDetailsActivity.this, isAddToFavourites ? R.string.added_to_favorites : R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionFailure() {
            updateMenuItems(menu, isFavorite);
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_message_dive_spot_not_found);
                    break;
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    if (isAddToFavourites) {
                        LoginActivity.showForResult(DiveSpotDetailsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_ADD_TO_FAVOURITES);
                    } else {
                        LoginActivity.showForResult(DiveSpotDetailsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_REMOVE_FROM_FAVOURITES);
                    }
                    break;
                case BAD_REQUEST_ERROR_400:
                    isFavorite = isAddToFavourites;
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, isAddToFavourites ? R.string.error_message_already_added_to_favourites : R.string.error_message_already_removed_from_favourites, false);
                    break;
                case UNPROCESSABLE_ENTITY_ERROR_422:
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
            }
            updateMenuItems(menu, isFavorite);
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
            if (menu != null && menu.findItem(R.id.edit_dive_spot) != null) {
                menu.findItem(R.id.edit_dive_spot).setVisible(false);
            }
//            isInfoValidLayout.setVisibility(View.GONE);
//            thanksLayout.setVisibility(View.VISIBLE);
            hideThanksLayout();
            if (isValid) {
                EventsTracker.trackDiveSpotValid();
            } else {
                EventsTracker.trackDiveSpotInvalid();
            }
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_message_dive_spot_not_found);
                    break;
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(DiveSpotDetailsActivity.this, isValid ? ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_VALIDATE_SPOT : ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_INVALIDATE_SPOT);
                    break;
                case BAD_REQUEST_ERROR_400:
                    if (menu != null && menu.findItem(R.id.edit_dive_spot) != null) {
                        menu.findItem(R.id.edit_dive_spot).setVisible(false);
                    }
//                    isInfoValidLayout.setVisibility(View.GONE);
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_already_validated_dive_spot_data, false);
                    break;
                case UNPROCESSABLE_ENTITY_ERROR_422:
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
            }
        }
    }

    public void showMoreDescription(View view) {
        binding.showmore.setVisibility(View.GONE);
        binding.divePlaceDescription.setMaxLines(10000);
        binding.divePlaceDescription.setEllipsize(null);
    }

    public void showCheckinsActivity(View view) {

    }

    public void addPhotoToDiveSpotButtonClicked(View view) {
        if (checkReadStoragePermission(this)) {
            addPhotosToDiveSpot();
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
        if (isCheckedIn) {
            checkOut();
            return;
        }
        checkIn();
    }

    private void changeViewState(TextView activeTextView, TextView disableTextView) {
        activeTextView.setTextColor(ContextCompat.getColor(this, R.color.black_text));
        activeTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_rectangle));

        disableTextView.setTextColor(ContextCompat.getColor(this, R.color.diactive_button_photo_color));
        disableTextView.setBackground(null);
    }

}
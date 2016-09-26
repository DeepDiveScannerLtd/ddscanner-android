package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotDetails;
import com.ddscanner.entities.Image;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.PhotosActivityPagerAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.fragments.DiveSpotAllPhotosFragment;
import com.ddscanner.ui.fragments.DiveSpotPhotosFragment;
import com.ddscanner.ui.fragments.DiveSpotReviewsPhoto;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class DiveSpotPhotosActivity extends AppCompatActivity implements View.OnClickListener, InfoDialogFragment.DialogClosedListener {

    private static final String TAG = DiveSpotPhotosActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager photosViewPager;
    private Toolbar toolbar;
    private ArrayList<Image> diveSpotImages;
    private String path;
    private ArrayList<Image> reviewsImages;
    private ArrayList<Image> allPhotos;
    private FloatingActionButton fabAddPhoto;
    private String dsId;
    private PhotosActivityPagerAdapter photosActivityPagerAdapter;
    private DiveSpotDetails diveSpotDetails;
    private boolean isDataChanged = false;

    private DiveSpotAllPhotosFragment diveSpotAllPhotosFragment = new DiveSpotAllPhotosFragment();
    private DiveSpotPhotosFragment diveSpotPhotosFragment = new DiveSpotPhotosFragment();
    private DiveSpotReviewsPhoto diveSpotReviewsPhoto = new DiveSpotReviewsPhoto();

    private ProgressView progressView;

    private DDScannerRestClient.ResultListener<DiveSpotDetails> diveSpotDetailsResultListener = new DDScannerRestClient.ResultListener<DiveSpotDetails>() {
        @Override
        public void onSuccess(DiveSpotDetails result) {
            diveSpotDetails = result;
            updateFragments(diveSpotDetails);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_SPOT_PHOTOS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_dive_spot_not_found, DialogsRequestCodes.DRC_DIVE_SPOT_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_DIVE_SPOT_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_photos);
        findViews();
        photosActivityPagerAdapter = new PhotosActivityPagerAdapter(getSupportFragmentManager());
        Bundle bundle = getIntent().getExtras();
        diveSpotImages = bundle.getParcelableArrayList("images");
        reviewsImages =  bundle.getParcelableArrayList("reviewsImages");

        path = getIntent().getStringExtra("path");
        dsId = getIntent().getStringExtra("id");
        if (diveSpotImages != null) {
            diveSpotImages = Helpers.appendFullImagesWithPath(diveSpotImages, path);
        }
        diveSpotPhotosFragment.setList(diveSpotImages, path);

        if (reviewsImages != null) {
            reviewsImages = Helpers.appendFullImagesWithPath(reviewsImages, path);
        }
        diveSpotReviewsPhoto.setList(reviewsImages, path);

        allPhotos = Helpers.compareObjectsArray(reviewsImages, diveSpotImages);

        diveSpotAllPhotosFragment.setList(allPhotos, path);

        setupViewPager();
        setUi();
        setUpTabLayout();
    }

    private void setUpTabLayout() {
        tabLayout.getTabAt(2).setText(R.string.reviews_tab);
        tabLayout.getTabAt(1).setText(R.string.dive_spot_tab);
        tabLayout.getTabAt(0).setText(R.string.all);
    }

    private void setUi() {
        tabLayout.setupWithViewPager(photosViewPager);
        photosViewPager.setOffscreenPageLimit(3);
    }

    private void setupViewPager() {
        photosActivityPagerAdapter.addFragment(diveSpotAllPhotosFragment, "all");
        photosActivityPagerAdapter.addFragment(diveSpotPhotosFragment, "divespot");
        photosActivityPagerAdapter.addFragment(diveSpotReviewsPhoto, "reviews");
        photosViewPager.setAdapter(photosActivityPagerAdapter);
    }

    private void findViews() {
        progressView = (ProgressView) findViewById(R.id.progressBar);
        tabLayout = (TabLayout) findViewById(R.id.photos_tab_layout);
        photosViewPager = (ViewPager) findViewById(R.id.photos_view_pager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fabAddPhoto = (FloatingActionButton) findViewById(R.id.fab_add_photo);
        fabAddPhoto.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.photos);
    }

    public static void show(Context context, ArrayList<String> images, String path,
                            ArrayList<String> reviewsImages, String id) {
        Intent intent = new Intent(context, DiveSpotPhotosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("images", images);
        bundle.putString("path", path);
        bundle.putSerializable("reviewsImages", reviewsImages);
        bundle.putString("id", id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_ADD_PHOTOS:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    getDiveSpotPhotos();
                    //   finish();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_SELECT_PHOTOS:
                if (resultCode == RESULT_OK) {
                    List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity
                            .EXTRA_RESULT);
                    AddPhotosDoDiveSpotActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_ADD_PHOTOS, (ArrayList<String>)path, dsId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_LOGIN:
                if (resultCode == RESULT_OK) {
                    MultiImageSelector.create(this).start(this, ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_SELECT_PHOTOS);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_ACTIVITY_SLIDER:
                if (resultCode == RESULT_OK) {
                    getDiveSpotPhotos();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_photo:
                if (checkReadStoragePermission(this)) {
                    addPhotosToDiveSpot();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.PHOTOS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE);
                }
                break;
        }
    }

    private void addPhotosToDiveSpot() {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            SocialNetworks.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_LOGIN);
        } else {
            MultiImageSelector.create(this).count(3).start(this, ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_SELECT_PHOTOS);
        }
    }

    private void getDiveSpotPhotos() {
        progressView.setVisibility(View.VISIBLE);
        photosViewPager.setVisibility(View.GONE);
        DDScannerApplication.getDdScannerRestClient().getDiveSpotPhotos(dsId, diveSpotDetailsResultListener);
    }

    private void updateFragments(DiveSpotDetails diveSpotDetails) {
        isDataChanged = true;

        reviewsImages = (ArrayList<Image>) diveSpotDetails.getDivespot().getCommentImages();
        diveSpotImages = (ArrayList<Image>) diveSpotDetails.getDivespot().getImages();
        if (diveSpotImages != null) {
            diveSpotImages = Helpers.appendFullImagesWithPath(diveSpotImages, path);
        }
        if (reviewsImages != null) {
            reviewsImages = Helpers.appendFullImagesWithPath(reviewsImages, path);
        }

        diveSpotReviewsPhoto.setList(reviewsImages, path);
        allPhotos = new ArrayList<>();
        allPhotos = Helpers.compareObjectsArray(reviewsImages, diveSpotImages);
        diveSpotAllPhotosFragment.setList(allPhotos, path);
        diveSpotPhotosFragment.setList(diveSpotImages, path);

        progressView.setVisibility(View.GONE);
        photosViewPager.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isDataChanged) {
            setResult(RESULT_OK);
            finish();
        } else {
            finish();
        }
    }

    public boolean checkReadStoragePermission(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PHOTOS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPhotosToDiveSpot();
                } else {
                    Toast.makeText(DiveSpotPhotosActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_DIVE_SPOT_PHOTOS_ACTIVITY_CONNECTION_FAILURE:
            case DialogsRequestCodes.DRC_DIVE_SPOT_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}

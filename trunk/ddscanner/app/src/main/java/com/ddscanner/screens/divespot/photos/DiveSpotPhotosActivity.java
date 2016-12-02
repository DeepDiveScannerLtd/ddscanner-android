package com.ddscanner.screens.divespot.photos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.DiveSpotPhotosResponseEntity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.AddPhotosDoDiveSpotActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.adapters.PhotosActivityPagerAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;

public class DiveSpotPhotosActivity extends AppCompatActivity implements View.OnClickListener, InfoDialogFragment.DialogClosedListener {

    private static final String TAG = DiveSpotPhotosActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager photosViewPager;
    private Toolbar toolbar;
    private ArrayList<DiveSpotPhoto> diveSpotImages;
    private String path;
    private ArrayList<DiveSpotPhoto> reviewsImages;
    private ArrayList<DiveSpotPhoto> allPhotos;
    private FloatingActionButton fabAddPhoto;
    private String dsId;
    private PhotosActivityPagerAdapter photosActivityPagerAdapter;
    private DiveSpotPhotosResponseEntity diveSpotDetails;
    private boolean isDataChanged = false;

    private DiveSpotAllPhotosFragment diveSpotAllPhotosFragment = new DiveSpotAllPhotosFragment();
    private DiveSpotPhotosFragment diveSpotPhotosFragment = new DiveSpotPhotosFragment();
    private DiveSpotReviewsPhotoFragment diveSpotReviewsPhotoFragment = new DiveSpotReviewsPhotoFragment();

    private ProgressView progressView;

    private DDScannerRestClient.ResultListener<DiveSpotPhotosResponseEntity> diveSpotPhotosResultListener = new DDScannerRestClient.ResultListener<DiveSpotPhotosResponseEntity>() {
        @Override
        public void onSuccess(DiveSpotPhotosResponseEntity result) {
            diveSpotDetails = result;
            updateFragments(diveSpotDetails);
            progressView.setVisibility(View.GONE);
            photosViewPager.setVisibility(View.VISIBLE);
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
        dsId = getIntent().getStringExtra("id");

        setupViewPager();
        setUi();
        setUpTabLayout();
        getDiveSpotPhotos();
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
        photosActivityPagerAdapter.addFragment(diveSpotReviewsPhotoFragment, "reviews");
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

    public static void show(Context context, String id) {
        Intent intent = new Intent(context, DiveSpotPhotosActivity.class);
        Bundle bundle = new Bundle();
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
                List<String> path= new ArrayList<>();
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

                                    path.add(file.getPath());
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

                                path.add(file.getPath());
                            } else {
                                Toast.makeText(this, "You can choose only images", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    AddPhotosDoDiveSpotActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_ADD_PHOTOS, (ArrayList<String>)path, dsId);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_LOGIN:
                if (resultCode == RESULT_OK) {
                    MultiImageSelector.create().showCamera(false).multi().count(3).start(this, ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_SELECT_PHOTOS);
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
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_LOGIN);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= 18) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_SELECT_PHOTOS);
            // MultiImageSelector.create().showCamera(false).multi().count(3).start(this, ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_SELECT_PHOTOS);
        }
    }

    private void getDiveSpotPhotos() {
        progressView.setVisibility(View.VISIBLE);
        photosViewPager.setVisibility(View.GONE);
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotPhotos(dsId, diveSpotPhotosResultListener);
    }

    private void updateFragments(DiveSpotPhotosResponseEntity diveSpotPhotosResponseEntity) {
        isDataChanged = true;

        reviewsImages = diveSpotPhotosResponseEntity.getCommentPhotos();
        diveSpotImages = diveSpotPhotosResponseEntity.getDiveSpotPhotos();

        allPhotos = new ArrayList<>();
        allPhotos = Helpers.compareObjectsArray(reviewsImages, diveSpotImages);
        diveSpotReviewsPhotoFragment.setList(reviewsImages, path, dsId);
        diveSpotAllPhotosFragment.setList(allPhotos, path, dsId);
        diveSpotPhotosFragment.setList(diveSpotImages, path, dsId);

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

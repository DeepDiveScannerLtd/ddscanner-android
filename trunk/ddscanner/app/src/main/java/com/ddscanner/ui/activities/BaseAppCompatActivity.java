package com.ddscanner.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.interfaces.PermissionsGrantedListener;
import com.ddscanner.interfaces.ShowPopupLstener;
import com.ddscanner.screens.dialogs.popup.AchievementPopupDialogFrament;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LocationHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class BaseAppCompatActivity extends AppCompatActivity implements ShowPopupLstener, AchievementPopupDialogFrament.PopupHideListener {

    private static final String TAG = BaseAppCompatActivity.class.getName();
    public static final int RESULT_CODE_PROFILE_LOGOUT = 1010;
    public static final int RESULT_CODE_FILTERS_RESETED = 1011;
    public static final int RESULT_CODE_DIVE_SPOT_REMOVED = 1012;
    public static final int RESULT_CODE_DIVE_SPOT_ADDED = 1013;

    private PermissionsGrantedListener permissionsGrantedListener;
    private LocationHelper locationHelper;
    private HashSet<Integer> requestCodes = new HashSet<>();
    private int menuResourceId = -1;
    private PictureTakenListener takedListener;
    private File tempFile;
    public boolean isPopupShown = false;
    private boolean isCloseActivityAfterPopupClosed = false;

    /**
     * Call this method to get user location. Subscribe to LocationReadyEvent for result
     */
    public void getLocation(int requestCode) {
        Log.i(TAG, "location check: getLocation request code = " + requestCode + " request codes = " + requestCodes);
        if (requestCode != -1) {
            requestCodes.add(requestCode);
        }
        if (locationHelper == null) {
            locationHelper = new LocationHelper(this);
        }
        try {
            locationHelper.checkLocationConditions();
            locationHelper.requestLocation(requestCodes);
            requestCodes.clear();
        } catch (LocationHelper.LocationProvidersNotAvailableException e) {
            DialogHelpers.showDialogForEnableLocationProviders(this);
        } catch (LocationHelper.LocationPPermissionsNotGrantedException e) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PERMISSION_NOT_GRANTED_ACTIVITY_LOCATION_PERMISSION);
        }
    }

    public void grandLocationPermission(PermissionsGrantedListener listener) {
        if (Build.VERSION.SDK_INT < 23 || android.support.v13.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Helpers.isLocatinEnabled(this)) {
                listener.onPermissionGrated();
                return;
            }
            this.permissionsGrantedListener = listener;
            DialogHelpers.showDialogForEnableLocationProviders(this);
            return;
        }
        this.permissionsGrantedListener = listener;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PERMISSION_NOT_GRANTED_ACTIVITY_LOCATION_PERMISSION);
    }

    /**
     * Show default toolbar with back button with title
     * @param titleresId resource id for toolbar title
     * @param toolbarId toolbar id in layout
     */
    public void setupToolbar(int titleresId, int toolbarId) {
        setSupportActionBar(findViewById(toolbarId));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(titleresId);
    }

    /**
     * Show default toolbar with back button with title and menu
     * @param titleresId resource id for toolbar title
     * @param toolbarId toolbar id in layout
     * @param menuResId resource id for menu
     */
    public void setupToolbar(int titleresId, int toolbarId, int menuResId) {
        this.menuResourceId = menuResId;
        setSupportActionBar(findViewById(toolbarId));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(titleresId);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuResourceId != -1) {
            getMenuInflater().inflate(this.menuResourceId, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void pickSinglePhotoFromGallery() {
        if (checkReadStoragePermission()) {
            pickphotoFromGallery();
        } else {
            android.support.v13.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PERMISSIO_READ_STORAGE);
        }
    }

    public void pickPhotoFromCamera() {
        if (checkWriteStoragePermission() && checkReadStoragePermission()) {
            dispatchTakePictureIntent();
        } else {
            android.support.v13.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PERMISSIO_WRITE_STORAGE);
        }
    }

    public void pickPhotosFromGallery() {
        if (checkReadStoragePermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= 18) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PICK_PHOTO_FROM_GALLERY);
        } else {
            android.support.v13.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActivitiesRequestCodes.REQUEST_PERMISSION_FRO_PICK_MULTIPLY_PHOTOS);
        }
    }

    private boolean checkReadStoragePermission() {
        return android.support.v13.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkWriteStoragePermission() {
        return android.support.v13.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void pickphotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PICK_PHOTO_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PICK_PHOTO_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    takedListener = (PictureTakenListener) this;
                    takedListener.onPictureFromCameraTaken(tempFile);
                }

                break;
            case ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PICK_PHOTO_FROM_GALLERY:
                if (resultCode == RESULT_OK) {
                    takedListener = (PictureTakenListener) this;
                    takedListener.onPicturesTaken(Helpers.getPhotosFromIntent(data, this));
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PROVIDERS_NOT_AVAILABLE_ACTIVITY_TURN_ON_LOCATION_SETTINGS:
                if (resultCode == RESULT_OK) {
                    permissionsGrantedListener.onPermissionGrated();
//                    getLocation(-1);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PERMISSION_NOT_GRANTED_ACTIVITY_LOCATION_PERMISSION:
                Log.i(TAG, "onRequestPermissionsResult grantResults = " + grantResults[0] + " " + grantResults[1]);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    getLocation(-1);
                    if (Helpers.isLocatinEnabled(this)) {
                        permissionsGrantedListener.onPermissionGrated();
                    } else {
                        DialogHelpers.showDialogForEnableLocationProviders(this);
                    }
                }
                break;
            case ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PERMISSIO_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickphotoFromGallery();
                } else {
                    Toast.makeText(BaseAppCompatActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                break;
            case ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PERMISSIO_WRITE_STORAGE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(BaseAppCompatActivity.this, "Grand permissions to pick photo from camera!", Toast.LENGTH_SHORT).show();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_PERMISSION_FRO_PICK_MULTIPLY_PHOTOS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhotosFromGallery();
                } else {
                    Toast.makeText(BaseAppCompatActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    public interface PictureTakenListener {
        void onPicturesTaken(ArrayList<String> pictures);
        void onPictureFromCameraTaken(File picture);
    }


    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                tempFile = photoFile;
            } catch (IOException ignored) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, getString(R.string.application_id), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PICK_PHOTO_FROM_CAMERA);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Override
    public void onPopupMustBeShown(String popup) {
        AchievementPopupDialogFrament.showDialog(getSupportFragmentManager(), popup);
        isPopupShown = true;
    }

    @Override
    public void finish() {
        if (isPopupShown) {
            isCloseActivityAfterPopupClosed = true;
            return;
        }
        super.finish();
    }

    @Override
    public void onPopupClosed() {
        isPopupShown = false;
        if (isCloseActivityAfterPopupClosed) {
            finish();
        }
    }
}

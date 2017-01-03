package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.ddscanner.R;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LocationHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class BaseAppCompatActivity extends AppCompatActivity {

    private static final String TAG = BaseAppCompatActivity.class.getName();
    public static final int RESULT_CODE_PROFILE_LOGOUT = 1010;


    private LocationHelper locationHelper;
    private HashSet<Integer> requestCodes = new HashSet<>();
    private int menuResourceId = -1;
    private Uri capturedImageUri;
    private PictureTakenListener takedListener;

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

    /**
     * Show default toolbar with back button with title
     * @param titleresId resource id for toolbar title
     * @param toolbarId toolbar id in layout
     */
    public void setupToolbar(int titleresId, int toolbarId) {
        setSupportActionBar((Toolbar) findViewById(toolbarId));
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
        setSupportActionBar((Toolbar) findViewById(toolbarId));
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
        if (checkReadStoragePermission(this)) {
            pickphotoFromGallery();
        } else {
            android.support.v13.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PERMISSIO_READ_STORAGE);
        }
    }

    public void pickPhotoFromCamera() {
        if (checkWriteStoragePermission()) {
            dispatchTakePictureIntent();
        } else {
            android.support.v13.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_CAMERA_AND_WRITE_STORAGE);
        }
    }

    public void pickPhotosFromGallery() {
        if (checkReadStoragePermission(this)) {
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

    private boolean checkReadStoragePermission(Activity context) {
        if (android.support.v13.app.ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private boolean checkWriteStoragePermission() {
        if (android.support.v13.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void pickphotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PICK_PHOTO_FROM_GALLERY);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DDScanner");
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, "DDS_" + timeStamp + ".png");
        capturedImageUri = Uri.fromFile(image);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PICK_PHOTO_FROM_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PICK_PHOTO_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    takedListener = (PictureTakenListener) this;
                    takedListener.onPictureFromCameraTaken(capturedImageUri.toString());
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
                    getLocation(-1);
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
                    getLocation(-1);
                } else {
                    // Do nothing. Keep showing this activity
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
        void onPictureFromCameraTaken(String picture);
    }

}

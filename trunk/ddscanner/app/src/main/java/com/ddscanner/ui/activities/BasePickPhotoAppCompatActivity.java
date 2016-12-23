package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.widget.Toast;

import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Helpers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BasePickPhotoAppCompatActivity extends BaseToolbarActivity {

    private Uri capturedImageUri;
    private PictureTakenListener takedListener;

    public void pickSinlePhotoFromGallery() {
        if (checkReadStoragePermission(this)) {
            pickphotoFromGallery();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PERMISSIO_READ_STORAGE);
        }
    }

    public void pickPhotoFromCamera() {
        if (checkWriteStoragePermission()) {
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_CAMERA_AND_WRITE_STORAGE);
        }
    }

    private boolean checkReadStoragePermission(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private boolean checkWriteStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PERMISSIO_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickphotoFromGallery();
                } else {
                    Toast.makeText(BasePickPhotoAppCompatActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
            break;
            case ActivitiesRequestCodes.BASE_PICK_PHOTOS_ACTIVITY_PERMISSIO_WRITE_STORAGE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(BasePickPhotoAppCompatActivity.this, "Grand permissions to pick photo from camera!", Toast.LENGTH_SHORT).show();
                }
            break;

        }
    }

    public interface PictureTakenListener {
        void onPicturesTaken(ArrayList<String> pictures);
        void onPictureFromCameraTaken(String picture);
    }

}

package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.events.ImageDeletedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.Helpers;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class LeaveReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LeaveReviewActivity.class.getSimpleName();
    private static final String ID = "ID";
    private static final String RATING = "RATING";
    private static final String SOURCE = "SOURCE";
    private static final int COMMENT_MAX_LENGTH = 250;

    private Toolbar toolbar;
    private Comment comment = new Comment();
    private String diveSpotId;
    private EditText text;
    private RatingBar ratingBar;
    private TextView errorText;
    private float rating;
    private MaterialDialog materialDialog;
    private TextView symbolNumberLeft;
    private ImageButton btnAddPhoto;
    private RecyclerView photos_rc;
    private List<String> imageUris = new ArrayList<String>();
    private TextView addPhotoTitle;
    private AddPhotoToDsListAdapter addPhotoToDsListAdapter;
    private List<String> imagesEncodedList = new ArrayList<>();
    private String imageEncoded;
    private Map<String, TextView> errorsMap = new HashMap<>();
    private RequestBody requestId = null, requestComment = null, requestRating = null;
    private RequestBody requessToken = null;
    private RequestBody requestSocial = null;
    private RequestBody requestSecret = null;
    private int maxPhotos = 3;

    private EventsTracker.SendReviewSource sendReviewSource;

    private DDScannerRestClient.ResultListener<Void> commentAddedResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            setResult(RESULT_OK);
            finish();
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
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(LeaveReviewActivity.this, ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_LOGIN);
                    break;
                case UNPROCESSABLE_ENTITY_ERROR_422:
                    Helpers.errorHandling(errorsMap, (ValidationError) errorData);
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        Bundle bundle = getIntent().getExtras();
        diveSpotId = bundle.getString(Constants.DIVESPOTID);
        rating = getIntent().getExtras().getFloat(RATING);
        sendReviewSource = EventsTracker.SendReviewSource.getByName(getIntent().getStringExtra(SOURCE));
        findViews();
        toolbarSettings();
        setRcSettings();
        setProgressDialog();
        addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris, LeaveReviewActivity.this);
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        text = (EditText) findViewById(R.id.review_text);
        text.setTag("comment");
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        if (rating > 0) {
            ratingBar.setRating(rating);
        } else {
            ratingBar.setRating(1);
        }
        symbolNumberLeft = (TextView) findViewById(R.id.left_number);
        photos_rc = (RecyclerView) findViewById(R.id.photos_rc);
        btnAddPhoto = (ImageButton) findViewById(R.id.btn_add_photo);
        addPhotoTitle = (TextView) findViewById(R.id.add_photo_title);
        errorText = (TextView) findViewById(R.id.comment_error);
        errorsMap.put("comment", errorText);
        btnAddPhoto.setOnClickListener(this);
    }

    private void setRcSettings() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(LeaveReviewActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);
        photos_rc.setAdapter(addPhotoToDsListAdapter);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.new_review);
    }

    private void setProgressDialog() {
        materialDialog = Helpers.getMaterialDialog(this);
    }

    public static void show(Context context, String id, float rating, EventsTracker.SendReviewSource sendReviewSource) {
        context.startActivity(getShowIntent(context, id, rating, sendReviewSource));
    }

    public static void showForResult(Activity context, String id, float rating, EventsTracker.SendReviewSource sendReviewSource, int requestCode) {
        context.startActivityForResult(getShowIntent(context, id, rating, sendReviewSource), requestCode);
    }

    private static Intent getShowIntent(Context context, String id, float rating, EventsTracker.SendReviewSource sendReviewSource) {
        Intent intent = new Intent(context, LeaveReviewActivity.class);
        intent.putExtra(Constants.DIVESPOTID, id);
        intent.putExtra(RATING, rating);
        intent.putExtra(SOURCE, sendReviewSource.getName());
        return intent;
    }

    private void sendReview() {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            LoginActivity.showForResult(LeaveReviewActivity.this, ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_LOGIN);
            return;
        }
        List<MultipartBody.Part> images = new ArrayList<>();
        //  imageUris = addPhotoToDsListAdapter.getNewFilesUrisList();
        for (int i = 0; i < imageUris.size(); i++) {
            File image = new File(imageUris.get(i));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData("images[]", image.getName(),
                    requestFile);
            images.add(part);
        }
        if (text.getText().toString().trim().isEmpty() && images.size() != 0) {
            Toast.makeText(LeaveReviewActivity.this, R.string.please_write_a_review, Toast.LENGTH_SHORT).show();
            return;
        }
        if (text.getText().toString().trim().length() < 30) {
            Toast.makeText(LeaveReviewActivity.this, R.string.review_error, Toast.LENGTH_SHORT).show();
            return;
        }
        materialDialog.show();
        requestRating = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(Math.round(ratingBar.getRating())));
        requestId = RequestBody.create(MediaType.parse("multipart/form-data"), diveSpotId);
        if (!text.getText().toString().trim().isEmpty()) {
            requestComment = RequestBody.create(MediaType.parse("multipart/form-data"),
                    text.getText().toString().trim());
        }
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            requestSocial = RequestBody.create(MediaType.parse("multipart/form-data"),
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
            requessToken = RequestBody.create(MediaType.parse("multipart/form-data"),
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postLeaveReview(requestId, requestComment, requestRating, images, requessToken, requestSocial, commentAddedResultListener);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DialogHelpers.showDialogAfterChanging(R.string.dialog_leave_title, R.string.dialog_leave_review_message, this, this);
//                onBackPressed();
                return true;
            case R.id.send_review:
                sendReview();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_review, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    sendReview();
                }
                if (resultCode == RESULT_CANCELED) {
                    Log.i(TAG, "Error with login");
                    materialDialog.dismiss();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PICK_PHOTO:
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

                                    imageUris.add(file.getPath());
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

                                imageUris.add(file.getPath());
                            } else {
                                Toast.makeText(this, "You can choose only images", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris, LeaveReviewActivity.this));

                }
                break;
        }
    }

    private void pickPhotoFromGallery() {
        if (checkReadStoragePermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= 18) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PICK_PHOTO);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PERMISSION_READ_STORAGE);
        }
    }

    public boolean checkReadStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_photo:
                pickPhotoFromGallery();
                break;
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
    public void deleteImage(ImageDeletedEvent event) {
        maxPhotos++;
        imageUris.remove(event.getImageIndex());
        photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris, LeaveReviewActivity.this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhotoFromGallery();
                } else {
                    Toast.makeText(LeaveReviewActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

}

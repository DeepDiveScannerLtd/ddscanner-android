package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.events.ImageDeletedEvent;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCommentActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = LeaveReviewActivity.class.getSimpleName();
    private static final String ID = "ID";
    private static final String RATING = "RATING";
    private static final String SOURCE = "SOURCE";
    private static final int COMMENT_MAX_LENGTH = 250;

    private Toolbar toolbar;
    private List<MultipartBody.Part> deletedImages = new ArrayList<>();
    private List<MultipartBody.Part> newImages = new ArrayList<>();
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
    private RequestBody requestComment = null, requestRating = null;
    private RequestBody requessToken = null;
    private RequestBody requestSocial = null;
    private RequestBody requestSecret = null;
    private RequestBody _method = null;
    private int maxPhotos = 3;
    private Comment comment;
    private ArrayList<String> deleted = new ArrayList<>();
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        comment = (Comment) getIntent().getSerializableExtra("COMMENT");
        path = getIntent().getStringExtra("PATH");
        findViews();
    }

    private void setUi() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditCommentActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);
        if (comment.getImages() != null) {
            maxPhotos = maxPhotos - comment.getImages().size();
            imageUris = comment.getImages();
            addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(comment.getImages(), this, addPhotoTitle);
            photos_rc.setAdapter(addPhotoToDsListAdapter);
           // setRcSettings();
        }
        ratingBar.setRating(Integer.parseInt(comment.getRating()));
        text.setText(comment.getComment());

    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        text = (EditText) findViewById(R.id.review_text);
        text.setTag("comment");
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        symbolNumberLeft = (TextView) findViewById(R.id.left_number);
        photos_rc = (RecyclerView) findViewById(R.id.photos_rc);
        btnAddPhoto = (ImageButton) findViewById(R.id.btn_add_photo);
        addPhotoTitle = (TextView) findViewById(R.id.add_photo_title);
        errorText = (TextView) findViewById(R.id.comment_error);
        errorsMap.put("comment", errorText);
        btnAddPhoto.setOnClickListener(this);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (COMMENT_MAX_LENGTH - text.length() < 10) {
                    symbolNumberLeft.setTextColor(ContextCompat.getColor(EditCommentActivity.this, R.color.tw__composer_red));
                } else {
                    symbolNumberLeft.setTextColor(Color.parseColor("#9f9f9f"));
                }
                symbolNumberLeft.setText(String.valueOf(COMMENT_MAX_LENGTH - text.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Comment");
        setUi();
    }

    private void setRcSettings() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditCommentActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);
        photos_rc.setAdapter(addPhotoToDsListAdapter);
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_photo:
                pickPhotoFromGallery();
                break;
        }
    }

    private void pickPhotoFromGallery() {
        if (checkReadStoragePermission()) {
            MultiImageSelector.create(this)
                    .count(maxPhotos)
                    .start(this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_COMMENT_ACTIVITY_PICK_PHOTOS);
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

    public static void showForResult(Activity context, Comment comment, String path, int requestCode) {
        Intent intent = new Intent(context, EditCommentActivity.class);
        intent.putExtra("COMMENT", comment);
        intent.putExtra("PATH", path);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.send_review:
                updateReview();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_COMMENT_ACTIVITY_PICK_PHOTOS:
                if (resultCode == RESULT_OK) {
                    maxPhotos = maxPhotos - data.getStringArrayListExtra(MultiImageSelectorActivity
                            .EXTRA_RESULT).size();
                    imageUris.addAll(data.getStringArrayListExtra(MultiImageSelectorActivity
                            .EXTRA_RESULT));
                    addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris,
                            EditCommentActivity.this, addPhotoTitle);
                    addPhotoTitle.setVisibility(View.GONE);
                    photos_rc.setVisibility(View.VISIBLE);
                    photos_rc.setAdapter(addPhotoToDsListAdapter);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_COMMENT_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    updateReview();
                }
                if (resultCode == RESULT_CANCELED) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    private void updateReview() {
        materialDialog.show();
        if (text.getText().toString().trim().isEmpty() && deleted.size() != 0 && addPhotoToDsListAdapter.getNewFilesUrisList() != null && addPhotoToDsListAdapter.getNewFilesUrisList().size() != 0 ) {
            Toast.makeText(EditCommentActivity.this, "Please write a review to dive spot", Toast.LENGTH_SHORT).show();
            materialDialog.dismiss();
            return;
        }
        if (text.getText().toString().trim().length() < 30) {
            Toast.makeText(EditCommentActivity.this, R.string.review_error, Toast.LENGTH_SHORT).show();
            return;
        }
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            requestSocial = RequestBody.create(MediaType.parse("multipart/form-data"),
                    SharedPreferenceHelper.getSn());
            requessToken = RequestBody.create(MediaType.parse("multipart/form-data"),
                    SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                requestSecret = RequestBody.create(MediaType.parse("multipart/form-data"),
                        SharedPreferenceHelper.getSecret());
            }
        }
        requestRating = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(Math.round(ratingBar.getRating())));
        _method = RequestBody.create(MediaType.parse("multipart/form-data"), "PUT");
        if (!text.getText().toString().trim().isEmpty()) {
            requestComment = RequestBody.create(MediaType.parse("multipart/form-data"),
                    text.getText().toString().trim());
        }
        List<String> newFilesUrisList = new ArrayList<>();
        if (addPhotoToDsListAdapter != null && addPhotoToDsListAdapter.getNewFilesUrisList() != null) {
            newFilesUrisList = addPhotoToDsListAdapter.getNewFilesUrisList();
        }
        if (newFilesUrisList.size() == 0) {
            newImages = null;
        } else {
            newImages = new ArrayList<>();
            for (String newImageUri : newFilesUrisList) {
                File image = new File(newImageUri);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData("images_new[]", image.getName(),
                        requestFile);
                newImages.add(part);
            }
        }

        if (deleted.size() == 0) {
            deletedImages = null;
        } else {
            deleted = removeAdressPart(deleted);
            deletedImages = new ArrayList<>();
            for (int i = 0; i < deleted.size(); i++) {
                deletedImages.add(MultipartBody.Part.createFormData("images_del[]", deleted.get(i)));
            }
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().updateComment(
                comment.getId(),
                _method,
                requestComment,
                requestRating,
                newImages,
                deletedImages,
                requessToken,
                requestSocial,
                requestSecret);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                materialDialog.dismiss();
                if (response.isSuccessful()) {
                    EventsTracker.trackReviewEdited();
                    setResult(RESULT_OK);
                    finish();
                }
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    materialDialog.dismiss();
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        Helpers.showToast(EditCommentActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(EditCommentActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        Helpers.errorHandling(EditCommentActivity.this, errorsMap, responseString);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(EditCommentActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(EditCommentActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(EditCommentActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        LoginActivity.showForResult(EditCommentActivity.this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_COMMENT_ACTIVITY_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(EditCommentActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private ArrayList<String> removeAdressPart(ArrayList<String> deleted) {

        for (int i = 0; i < deleted.size(); i++) {
            deleted.set(i, deleted.get(i).replace(path, ""
            ));
        }

        return deleted;
    }

    @Subscribe
    public void deleteImage(ImageDeletedEvent event) {
        imageUris.remove(event.getImageIndex());
        if (addPhotoToDsListAdapter.getListOfDeletedImages() != null) {
            deleted.addAll((ArrayList<String>) addPhotoToDsListAdapter.getListOfDeletedImages());
        }
        addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris,
                EditCommentActivity.this, addPhotoTitle);
        photos_rc.setAdapter(addPhotoToDsListAdapter);
        if (addPhotoToDsListAdapter.getNewFilesUrisList() != null) {
            maxPhotos = 3 - addPhotoToDsListAdapter.getNewFilesUrisList().size();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhotoFromGallery();
                } else {
                    Toast.makeText(EditCommentActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }
}

package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import com.ddscanner.entities.CommentEntity;
import com.ddscanner.entities.CommentOld;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.events.ImageDeletedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.edit.EditSpotPhotosListAdapter;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditCommentActivity extends BaseAppCompatActivity implements InfoDialogFragment.DialogClosedListener, BaseAppCompatActivity.PictureTakenListener {

    private static final int COMMENT_MAX_LENGTH = 250;

    private Toolbar toolbar;
    private List<MultipartBody.Part> deletedImages = new ArrayList<>();
    private List<MultipartBody.Part> newImages = new ArrayList<>();
    private EditText text;
    private RatingBar ratingBar;
    private TextView errorText;
    private MaterialDialog materialDialog;
    private TextView symbolNumberLeft;
    private RecyclerView photosRecyclerView;
    private EditSpotPhotosListAdapter editSpotPhotosListAdapter;
    private Map<String, TextView> errorsMap = new HashMap<>();
    private CommentEntity comment;
    private ArrayList<String> deleted = new ArrayList<>();

    private DDScannerRestClient.ResultListener<Void> editCommentResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackReviewEdited();
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
                    LoginActivity.showForResult(EditCommentActivity.this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_COMMENT_ACTIVITY_LOGIN);
                    break;
                case COMMENT_NOT_FOUND_ERROR_C803:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_comment_not_found, DialogsRequestCodes.DRC_EDIT_COMMENT_ACTIVITY_COMMENT_NOT_FOUND, false);
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        comment = new Gson().fromJson(getIntent().getStringExtra("COMMENT"), CommentEntity.class);
        editSpotPhotosListAdapter = new EditSpotPhotosListAdapter(this);
        setupToolbar(R.string.edit_comment, R.id.toolbar, R.menu.menu_add_review);
        findViews();
    }

    private void setUi() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditCommentActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photosRecyclerView.setNestedScrollingEnabled(false);
        photosRecyclerView.setHasFixedSize(false);
        photosRecyclerView.setLayoutManager(layoutManager);
        photosRecyclerView.setAdapter(editSpotPhotosListAdapter);
        if (comment.getComment().getPhotos() != null) {
            editSpotPhotosListAdapter.addServerPhoto(comment.getComment().getPhotos());
           // setRcSettings();
        }
        ratingBar.setRating(Integer.parseInt(comment.getComment().getRating()));
        text.setText(comment.getComment().getReview());

    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        text = (EditText) findViewById(R.id.review_text);
        text.setTag("comment");
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        symbolNumberLeft = (TextView) findViewById(R.id.left_number);
        photosRecyclerView = (RecyclerView) findViewById(R.id.photos_rc);
        errorText = (TextView) findViewById(R.id.comment_error);
        errorsMap.put("comment", errorText);
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
        setUi();
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

    public static void showForResult(Activity context, CommentEntity comment, int requestCode) {
        Intent intent = new Intent(context, EditCommentActivity.class);
        intent.putExtra("COMMENT", new Gson().toJson(comment));
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DialogHelpers.showDialogAfterChanging(R.string.dialog_leave_title, R.string.dialog_leave_review_message, this, this);
//                onBackPressed();
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
        List<String> newFilesUrisList = new ArrayList<>();
        newFilesUrisList = editSpotPhotosListAdapter.getNewPhotos();
        if (newFilesUrisList.size() == 0) {
            newImages = null;
        } else {
            newImages = new ArrayList<>();
            for (String newImageUri : newFilesUrisList) {
                File image = new File(newImageUri);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData("new_photos[]", image.getName(), requestFile);
                newImages.add(part);
            }
        }
        deleted = (ArrayList<String>) editSpotPhotosListAdapter.getDeletedPhotos();
        if (deleted.size() == 0) {
            deletedImages = null;
        } else {
            deletedImages = new ArrayList<>();
            for (int i = 0; i < deleted.size(); i++) {
                deletedImages.add(MultipartBody.Part.createFormData("deleted_photos[]", deleted.get(i)));
            }
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postUpdateReview(editCommentResultListener, newImages, deletedImages, Helpers.createRequestBodyForString(comment.getComment().getId()), Helpers.createRequestBodyForString(String.valueOf(Math.round(ratingBar.getRating()))), Helpers.createRequestBodyForString( text.getText().toString().trim()));
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_EDIT_COMMENT_ACTIVITY_COMMENT_NOT_FOUND:
                finish();
                break;
        }
    }

    @Subscribe
    public void addPhotoClicked(AddPhotoDoListEvent event) {
        pickPhotosFromGallery();
    }

    @Override
    public void onPictureFromCameraTaken(String picture) {

    }

    @Override
    public void onPicturesTaken(ArrayList<String> pictures) {
        editSpotPhotosListAdapter.addDevicePhotos(pictures);
    }
}

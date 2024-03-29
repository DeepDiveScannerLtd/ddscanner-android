package com.ddscanner.screens.reiews.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.SearchSealifeActivity;
import com.ddscanner.ui.adapters.PhotosListAdapterWithoutCover;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.model.LatLng;
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

public class EditCommentActivity extends BaseAppCompatActivity implements View.OnClickListener, DialogClosedListener, BaseAppCompatActivity.PictureTakenListener, ConfirmationDialogClosedListener {

    private static final String ARG_DIVE_SPOT_LOCATION = "location";
    private static final int COMMENT_MAX_LENGTH = 250;

    private Toolbar toolbar;
    private List<MultipartBody.Part> deletedImages = null;
    private List<MultipartBody.Part> newImages = new ArrayList<>();
    private EditText text;
    private RatingBar ratingBar;
    private TextView errorText;
    private MaterialDialog materialDialog;
    private TextView symbolNumberLeft;
    private RecyclerView photosRecyclerView;
    private LinearLayout buttonAddSealife;
    private PhotosListAdapterWithoutCover editSpotPhotosListAdapter;
    private Map<String, TextView> errorsMap = new HashMap<>();
    private Comment comment;
    private ArrayList<String> deleted = new ArrayList<>();
    private RecyclerView sealifeList;
    private SealifeListAddingDiveSpotAdapter sealifesAdapter;
    private boolean isHaveSealifes;
    private TextView errorRating;

    private DDScannerRestClient.ResultListener<ArrayList<SealifeShort>> sealifeResultListener = new DDScannerRestClient.ResultListener<ArrayList<SealifeShort>>() {
        @Override
        public void onSuccess(ArrayList<SealifeShort> result) {
            materialDialog.dismiss();
            sealifesAdapter.addSealifesList(result);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_LEAVE_REVIEW_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_LEAVE_REVIEW_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_LEAVE_REVIEW_ACTIVITY_FAILED_TO_CONNECT, false);
        }
    };

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
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        comment = new Gson().fromJson(getIntent().getStringExtra("COMMENT"), Comment.class);
        isHaveSealifes = getIntent().getBooleanExtra("ISSEALIFE", false);
        editSpotPhotosListAdapter = new PhotosListAdapterWithoutCover(this);
        setupToolbar(R.string.edit_comment, R.id.toolbar, R.menu.menu_add_review);
        findViews();
    }

    private void setUi() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditCommentActivity.this);
        sealifesAdapter = new SealifeListAddingDiveSpotAdapter(new ArrayList<SealifeShort>(), this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photosRecyclerView.setNestedScrollingEnabled(false);
        photosRecyclerView.setHasFixedSize(false);
        photosRecyclerView.setLayoutManager(layoutManager);
        photosRecyclerView.setAdapter(editSpotPhotosListAdapter);
        if (comment.getPhotos() != null) {
            ArrayList<String> serverPhotos = new ArrayList<>();
            for (DiveSpotPhoto diveSpotPhoto : comment.getPhotos()) {
                serverPhotos.add(diveSpotPhoto.getId());
            }
            editSpotPhotosListAdapter.addServerPhoto(serverPhotos);
           // setRcSettings();
        }
        ratingBar.setRating(comment.getRating());
        text.setText(comment.getReview());
        LinearLayoutManager sealifLayoutManager = new LinearLayoutManager(this);
        sealifeList.setLayoutManager(sealifLayoutManager);
        sealifeList.setAdapter(sealifesAdapter);
        if (isHaveSealifes) {
            materialDialog.show();
            DDScannerApplication.getInstance().getDdScannerRestClient(this).getReviewSealifes(sealifeResultListener, comment.getId());
            //
            //sealifesAdapter.addSealifesList(comment.getSealifes());
        }
    }

    private void findViews() {
        errorRating = findViewById(R.id.rating_error);
        sealifeList= findViewById(R.id.sealife_list);
        buttonAddSealife = findViewById(R.id.btn_add_sealife);
        buttonAddSealife.setOnClickListener(this);
        materialDialog = Helpers.getMaterialDialog(this);
        toolbar = findViewById(R.id.toolbar);
        text = findViewById(R.id.review_text);
        text.setTag("comment");
        ratingBar = findViewById(R.id.rating_bar);
//        symbolNumberLeft = (TextView) findViewById(R.id.left_number);
        photosRecyclerView = findViewById(R.id.photos_rc);
        errorText = findViewById(R.id.comment_error);
        errorsMap.put("comment", errorText);

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
    }

    @Override
    public void onStart() {
        super.onStart();
//        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        DDScannerApplication.bus.unregister(this);
    }

    public static void showForResult(Activity context, Comment comment, int requestCode, boolean isSealifes) {
        Intent intent = new Intent(context, EditCommentActivity.class);
        intent.putExtra("COMMENT", new Gson().toJson(comment));
        intent.putExtra("ISSEALIFE", isSealifes);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.send_review:
                Helpers.hideKeyboard(this);
                updateReview();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DialogHelpers.showDialogAfterChangesInActivity(getSupportFragmentManager());
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
            case ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PICK_SEALIFE:
                if (resultCode == RESULT_OK) {
                    SealifeShort sealifeShort = (SealifeShort) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);
                    sealifesAdapter.add(sealifeShort);
                }
                break;
        }
    }

    private boolean isDataValid() {
        boolean isDataValid = true;
        errorRating.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
        if (text.getText().toString().trim().length() < 30) {
            errorText.setVisibility(View.VISIBLE);
            isDataValid = false;
        }
        if (ratingBar.getRating() < 1) {
            errorRating.setVisibility(View.VISIBLE);
            isDataValid = false;
        }
        return isDataValid;
    }

    private void updateReview() {
        if (!isDataValid()) {
            return;
        }
        materialDialog.show();
        List<String> newFilesUrisList = new ArrayList<>();
        newFilesUrisList = editSpotPhotosListAdapter.getNewPhotos();
        if (newFilesUrisList.size() == 0) {
            newImages = null;
        } else {
            newImages = new ArrayList<>();
            for (String newImageUri : newFilesUrisList) {
                File image = new File(newImageUri);
                image = Helpers.compressFile(image, this);
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
        List<MultipartBody.Part> sealifes = new ArrayList<>();
        for (SealifeShort sealife : sealifesAdapter.getSealifes()) {
            sealifes.add(MultipartBody.Part.createFormData("sealifes[]", sealife.getId()));
        }
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postUpdateReview(editCommentResultListener, newImages, deletedImages, sealifes, Helpers.createRequestBodyForString(comment.getId()), Helpers.createRequestBodyForString(String.valueOf(Math.round(ratingBar.getRating()))), Helpers.createRequestBodyForString( text.getText().toString().trim()));
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_LEAVE_REVIEW_ACTIVITY_FAILED_TO_CONNECT:
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
    public void onPictureFromCameraTaken(File picture) {

    }

    @Override
    public void onPicturesTaken(ArrayList<String> pictures) {
        editSpotPhotosListAdapter.addDevicePhotos(pictures);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_sealife:
                EventsTracker.trackSearchSeaLife();
                SearchSealifeActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PICK_SEALIFE, null);
                break;
        }
    }

    @Override
    public void onNegativeDialogClicked() {

    }

    @Override
    public void onPositiveDialogClicked() {
        finish();
    }

}

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
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.edit.EditSpotPhotosListAdapter;
import com.ddscanner.screens.reiews.add.LeaveReviewActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.SearchSealifeActivity;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
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

public class EditCommentActivity extends BaseAppCompatActivity implements View.OnClickListener, DialogClosedListener, BaseAppCompatActivity.PictureTakenListener {

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
    private LinearLayout buttonAddSealife;
    private EditSpotPhotosListAdapter editSpotPhotosListAdapter;
    private Map<String, TextView> errorsMap = new HashMap<>();
    private Comment comment;
    private ArrayList<String> deleted = new ArrayList<>();
    private RecyclerView sealifeList;
    private SealifeListAddingDiveSpotAdapter sealifesAdapter;

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
                    LoginActivity.showForResult(EditCommentActivity.this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_COMMENT_ACTIVITY_LOGIN);
                    break;
                default:
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        comment = new Gson().fromJson(getIntent().getStringExtra("COMMENT"), Comment.class);
        editSpotPhotosListAdapter = new EditSpotPhotosListAdapter(this);
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
        ratingBar.setRating(Integer.parseInt(comment.getRating()));
        text.setText(comment.getReview());
        LinearLayoutManager sealifLayoutManager = new LinearLayoutManager(this);
        sealifeList.setLayoutManager(sealifLayoutManager);
        sealifeList.setAdapter(sealifesAdapter);
        if (comment.getSealifes() != null) {
            sealifesAdapter.addSealifesList(comment.getSealifes());
        }
    }

    private void findViews() {
        sealifeList= (RecyclerView) findViewById(R.id.sealife_list);
        buttonAddSealife = (LinearLayout) findViewById(R.id.btn_add_sealife);
        buttonAddSealife.setOnClickListener(this);
        materialDialog = Helpers.getMaterialDialog(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        text = (EditText) findViewById(R.id.review_text);
        text.setTag("comment");
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
//        symbolNumberLeft = (TextView) findViewById(R.id.left_number);
        photosRecyclerView = (RecyclerView) findViewById(R.id.photos_rc);
        errorText = (TextView) findViewById(R.id.comment_error);
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

    public static void showForResult(Activity context, Comment comment, int requestCode) {
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
            case ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PICK_SEALIFE:
                if (resultCode == RESULT_OK) {
                    SealifeShort sealifeShort = (SealifeShort) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);
                    sealifesAdapter.add(sealifeShort);
                }
                break;
        }
    }

    private void updateReview() {
        errorText.setVisibility(View.GONE);
        if (text.getText().toString().trim().length() < 30) {
            errorText.setVisibility(View.VISIBLE);
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
        DDScannerApplication.getInstance().getDdScannerRestClient().postUpdateReview(editCommentResultListener, newImages, deletedImages, sealifes, Helpers.createRequestBodyForString(comment.getId()), Helpers.createRequestBodyForString(String.valueOf(Math.round(ratingBar.getRating()))), Helpers.createRequestBodyForString( text.getText().toString().trim()));
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
                SearchSealifeActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PICK_SEALIFE);
                break;
        }
    }
}

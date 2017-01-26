package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.ddscanner.entities.CommentOld;
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.events.ImageDeletedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DialogsRequestCodes;
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

public class LeaveReviewActivity extends BaseAppCompatActivity implements BaseAppCompatActivity.PictureTakenListener, DialogClosedListener {

    private static final String TAG = LeaveReviewActivity.class.getSimpleName();
    private static final String ID = "ID";
    private static final String RATING = "RATING";
    private static final String SOURCE = "SOURCE";
    private static final int COMMENT_MAX_LENGTH = 250;

    private Toolbar toolbar;
    private CommentOld commentOld = new CommentOld();
    private String diveSpotId;
    private EditText text;
    private RatingBar ratingBar;
    private TextView errorText;
    private float rating;
    private MaterialDialog materialDialog;
    private TextView symbolNumberLeft;
    private ImageButton btnAddPhoto;
    private RecyclerView photosRecyclerView;
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
                    LoginActivity.showForResult(LeaveReviewActivity.this, ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_LOGIN);
                    break;
                case RIGHTS_NOT_FOUND_403:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    UserActionInfoDialogFragment.showForActivityResult(LeaveReviewActivity.this, R.string.sorry, R.string.dive_centers_cannot_leave_review, DialogsRequestCodes.DRC_LEAVE_REVIEW_ACTIVITY_CLOSE);
                    break;
                default:
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        Bundle bundle = getIntent().getExtras();
        diveSpotId = bundle.getString(Constants.DIVESPOTID);
        rating = getIntent().getExtras().getFloat(RATING);
        findViews();
        setupToolbar(R.string.new_review, R.id.toolbar, R.menu.menu_add_review);
        setRcSettings();
        setProgressDialog();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        text = (EditText) findViewById(R.id.review_text);
        text.setTag("review");
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        if (rating > 0) {
            ratingBar.setRating(rating);
        } else {
            ratingBar.setRating(1);
        }
        photosRecyclerView = (RecyclerView) findViewById(R.id.photos_rc);
        errorText = (TextView) findViewById(R.id.comment_error);
        errorsMap.put("review", errorText);
    }

    private void setRcSettings() {
        addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris, LeaveReviewActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(LeaveReviewActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photosRecyclerView.setNestedScrollingEnabled(false);
        photosRecyclerView.setHasFixedSize(false);
        photosRecyclerView.setLayoutManager(layoutManager);
        photosRecyclerView.setAdapter(addPhotoToDsListAdapter);
    }

    private void setProgressDialog() {
        materialDialog = Helpers.getMaterialDialog(this);
    }

    public static void show(Context context, String id, float rating) {
        context.startActivity(getShowIntent(context, id, rating));
    }

    public static void showForResult(Activity context, String id, float rating, int requestCode) {
        context.startActivityForResult(getShowIntent(context, id, rating), requestCode);
    }

    private static Intent getShowIntent(Context context, String id, float rating) {
        Intent intent = new Intent(context, LeaveReviewActivity.class);
        intent.putExtra(Constants.DIVESPOTID, id);
        intent.putExtra(RATING, rating);
        return intent;
    }

    private void sendReview() {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            LoginActivity.showForResult(LeaveReviewActivity.this, ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_LOGIN);
            return;
        }
        List<MultipartBody.Part> images = new ArrayList<>();
        imageUris = addPhotoToDsListAdapter.getNewFilesUrisList();
        for (int i = 0; i < imageUris.size(); i++) {
            File image = new File(imageUris.get(i));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData("photos[]", image.getName(),
                    requestFile);
            images.add(part);
        }
        if (text.getText().toString().trim().length() < 30) {
            errorText.setVisibility(View.VISIBLE);
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
        DDScannerApplication.getInstance().getDdScannerRestClient().postLeaveCommentForDiveSpot(commentAddedResultListener, images, requestId, requestRating, requestComment);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() != 0) {
                        sendReview();
                    } else {
                        //TODO dive center try leave review, must handle this case
                    }
                }
                if (resultCode == RESULT_CANCELED) {
                    materialDialog.dismiss();
                }
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
    public void pickPhoto(AddPhotoDoListEvent event) {
        pickPhotosFromGallery();
    }

    @Subscribe
    public void deleteImage(ImageDeletedEvent event) {
        imageUris.remove(event.getImageIndex());
        photosRecyclerView.setAdapter(new AddPhotoToDsListAdapter(imageUris, LeaveReviewActivity.this));
    }

    @Override
    public void onPicturesTaken(ArrayList<String> pictures) {
        imageUris.addAll(pictures);
        photosRecyclerView.setAdapter(new AddPhotoToDsListAdapter(imageUris, this));
        photosRecyclerView.scrollToPosition(imageUris.size());
    }

    @Override
    public void onPictureFromCameraTaken(File picture) {

    }

    @Override
    public void onDialogClosed(int requestCode) {

    }
}

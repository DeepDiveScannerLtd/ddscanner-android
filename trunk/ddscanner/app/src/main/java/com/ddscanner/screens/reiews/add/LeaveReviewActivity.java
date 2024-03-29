package com.ddscanner.screens.reiews.add;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.SearchSealifeActivity;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class LeaveReviewActivity extends BaseAppCompatActivity implements View.OnClickListener, BaseAppCompatActivity.PictureTakenListener, DialogClosedListener, ConfirmationDialogClosedListener {

    private static final String TAG = LeaveReviewActivity.class.getSimpleName();
    private static final String ID = "ID";
    private static final String RATING = "RATING";
    private static final String ARG_SOURCE = "SOURCE";
    private static final int COMMENT_MAX_LENGTH = 250;
    private static final String ARG_DIVE_SPOT_LOCATION = "location";

    private String diveSpotId;
    private EditText text;
    private RatingBar ratingBar;
    private TextView errorText;
    private TextView errorRating;
    private float rating;
    private LinearLayout buttonAddSealife;
    private MaterialDialog materialDialog;
    private RecyclerView photosRecyclerView;
    private List<String> imageUris = new ArrayList<String>();
    private AddPhotoToDsListAdapter addPhotoToDsListAdapter;
    private Map<String, TextView> errorsMap = new HashMap<>();
    private RequestBody requestId = null, requestComment = null, requestRating = null;
    private RecyclerView sealifeList;
    private SealifeListAddingDiveSpotAdapter sealifesAdapter;
    private LatLng diveSpotLocation;
    private EventsTracker.SendReviewSource source;

    private DDScannerRestClient.ResultListener<Void> commentAddedResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackReviewSent(source);
            if (!isPopupShown) {
                Toast.makeText(LeaveReviewActivity.this, R.string.review_sent_toast, Toast.LENGTH_SHORT).show();
            }
            materialDialog.dismiss();
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
            switch (errorType) {
                case RIGHTS_NOT_FOUND_403:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.sorry, R.string.dive_centers_cannot_leave_review, DialogsRequestCodes.DRC_LEAVE_REVIEW_ACTIVITY_CLOSE, false);
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        Bundle bundle = getIntent().getExtras();
        source = (EventsTracker.SendReviewSource) bundle.getSerializable(ARG_SOURCE);
        diveSpotId = bundle.getString(Constants.DIVESPOTID);
        diveSpotLocation = bundle.getParcelable(ARG_DIVE_SPOT_LOCATION);
        rating = getIntent().getExtras().getFloat(RATING);
        findViews();
        setupToolbar(R.string.new_review, R.id.toolbar, R.menu.menu_add_review);
        setRcSettings();
        setProgressDialog();
    }

    private void findViews() {
        errorRating = findViewById(R.id.rating_error);
        text = findViewById(R.id.review_text);
        sealifeList= findViewById(R.id.sealife_list);
        text.setTag("review");
        ratingBar = findViewById(R.id.rating_bar);
        buttonAddSealife = findViewById(R.id.btn_add_sealife);
        if (rating > 0) {
            ratingBar.setRating(rating);
        } else {
            ratingBar.setRating(1);
        }
        photosRecyclerView = findViewById(R.id.photos_rc);
        errorText = findViewById(R.id.comment_error);
        errorsMap.put("review", errorText);
        buttonAddSealife.setOnClickListener(this);
    }

    private void setRcSettings() {
        sealifesAdapter = new SealifeListAddingDiveSpotAdapter(new ArrayList<>(), LeaveReviewActivity.this);
        addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris, LeaveReviewActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(LeaveReviewActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photosRecyclerView.setNestedScrollingEnabled(false);
        photosRecyclerView.setHasFixedSize(false);
        photosRecyclerView.setLayoutManager(layoutManager);
        photosRecyclerView.setAdapter(addPhotoToDsListAdapter);
        LinearLayoutManager sealifLayoutManager = new LinearLayoutManager(LeaveReviewActivity.this);
        sealifeList.setLayoutManager(sealifLayoutManager);
        sealifeList.setAdapter(sealifesAdapter);
    }

    private void setProgressDialog() {
        materialDialog = Helpers.getMaterialDialog(this);
    }

    public static void showForResult(Activity context, String id, float rating, int requestCode, LatLng diveSpotLocation, EventsTracker.SendReviewSource source) {
        EventsTracker.trackSendReview(source);
        context.startActivityForResult(getShowIntent(context, id, rating, diveSpotLocation, source), requestCode);
    }

    private static Intent getShowIntent(Context context, String id, float rating, LatLng diveSpotLocation, EventsTracker.SendReviewSource source) {
        Intent intent = new Intent(context, LeaveReviewActivity.class);
        intent.putExtra(Constants.DIVESPOTID, id);
        intent.putExtra(RATING, rating);
        intent.putExtra(ARG_DIVE_SPOT_LOCATION, diveSpotLocation);
        intent.putExtra(ARG_SOURCE, source);
        return intent;
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

    private void sendReview() {
        if (!SharedPreferenceHelper.getIsUserSignedIn()) {
            LoginActivity.showForResult(LeaveReviewActivity.this, ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_LOGIN);
            return;
        }
        if (!isDataValid()) {
            return;
        }
        List<MultipartBody.Part> sealifes = new ArrayList<>();
        for (SealifeShort sealife : sealifesAdapter.getSealifes()) {
            sealifes.add(MultipartBody.Part.createFormData("sealifes[]", sealife.getId()));
        }
        List<MultipartBody.Part> images = new ArrayList<>();
        imageUris = addPhotoToDsListAdapter.getNewFilesUrisList();
        for (int i = 0; i < imageUris.size(); i++) {
            File image = new File(imageUris.get(i));
            image = Helpers.compressFile(image, this);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData("photos[]", image.getName(), requestFile);
            images.add(part);
        }
        materialDialog.show();
        requestRating = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(Math.round(ratingBar.getRating())));
        requestId = RequestBody.create(MediaType.parse("multipart/form-data"), diveSpotId);
        if (!text.getText().toString().trim().isEmpty()) {
            requestComment = RequestBody.create(MediaType.parse("multipart/form-data"),
                    text.getText().toString().trim());
        }
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postLeaveCommentForDiveSpot(commentAddedResultListener, images, sealifes, requestId, requestRating, requestComment);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.send_review:
                Helpers.hideKeyboard(this);
                sendReview();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (addPhotoToDsListAdapter.getItemCount() > 1 || sealifesAdapter.getItemCount() > 0 || text.getText().toString().length() > 0) {
            DialogHelpers.showDialogAfterChangesInActivity(getSupportFragmentManager());
            return;
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_sealife:
                EventsTracker.trackSearchSeaLife();
                SearchSealifeActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PICK_SEALIFE, diveSpotLocation);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    if (SharedPreferenceHelper.getActiveUserType() != SharedPreferenceHelper.UserType.DIVECENTER) {
                        sendReview();
                    } else {
                        Toast.makeText(this, R.string.dc_cant_leave_review, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                }
                if (resultCode == RESULT_CANCELED) {
                    materialDialog.dismiss();
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

    @Subscribe
    public void pickPhoto(AddPhotoDoListEvent event) {
        pickPhotosFromGallery();
    }

    @Override
    public void onPicturesTaken(ArrayList<String> pictures) {
        addPhotoToDsListAdapter.addPhotos(pictures);
        photosRecyclerView.scrollToPosition(addPhotoToDsListAdapter.getNewFilesUrisList().size());
    }

    @Override
    public void onPictureFromCameraTaken(File picture) {

    }

    @Override
    public void onDialogClosed(int requestCode) {

    }

    @Override
    public void onNegativeDialogClicked() {

    }

    @Override
    public void onPositiveDialogClicked() {
        finish();
    }
}

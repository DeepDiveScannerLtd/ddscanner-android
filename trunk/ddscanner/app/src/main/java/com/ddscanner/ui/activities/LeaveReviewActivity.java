package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
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
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
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
import retrofit2.Response;

/**
 * Created by lashket on 12.3.16.
 */
public class LeaveReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_PICK_PHOTO = 9001;

    private static final String TAG = LeaveReviewActivity.class.getSimpleName();
    private static final String ID = "ID";
    private static final String RATING = "RATING";
    private static final String SOURCE = "SOURCE";
    private static final int RC_LOGIN = 8001;
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
    private Helpers helpers = new Helpers();
    private int maxPhotos = 3;

    private EventsTracker.SendReviewSource sendReviewSource;

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
        addPhotoToDsListAdapter = new AddPhotoToDsListAdapter
                (imageUris, LeaveReviewActivity.this, addPhotoTitle);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (COMMENT_MAX_LENGTH - text.length() < 10) {
                    symbolNumberLeft.setTextColor(getResources().getColor(R.color.tw__composer_red));
                } else {
                    symbolNumberLeft.setTextColor(Color.parseColor("#9f9f9f"));
                }
                symbolNumberLeft.setText(String.valueOf(COMMENT_MAX_LENGTH - text.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
        getSupportActionBar().setTitle("New review");
    }

    private void setProgressDialog() {
        materialDialog = helpers.getMaterialDialog(this);
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
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            SocialNetworks.showForResult(LeaveReviewActivity.this, RC_LOGIN);
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
            Toast.makeText(LeaveReviewActivity.this, "Please write a review to dive spot", Toast.LENGTH_SHORT).show();
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

        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addCommentToDiveSpot(
                requestId,
                requestComment,
                requestRating,
                images,
                requessToken,
                requestSocial,
                requestSecret
        );
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().isSuccessful()) {
                    String responseString = null;
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    EventsTracker.trackReviewSending(sendReviewSource);
//                    comment = new Gson().fromJson(responseString, Comment.class);
                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("COMMENT", comment);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                    materialDialog.dismiss();
                } else {
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
                        helpers.showToast(LeaveReviewActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(LeaveReviewActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        helpers.errorHandling(LeaveReviewActivity.this, errorsMap, responseString);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(LeaveReviewActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(LeaveReviewActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(LeaveReviewActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SocialNetworks.showForResult(LeaveReviewActivity.this, RC_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(LeaveReviewActivity.this, R.string.toast_server_error);
                    }
                }

            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(LeaveReviewActivity.this);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
        if (requestCode == RC_LOGIN) {
            if (resultCode == RESULT_OK) {
                sendReview();
            }
            if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "Error with login");
                materialDialog.dismiss();
            }
        }
        if (requestCode == RC_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                maxPhotos = maxPhotos - data.getStringArrayListExtra(MultiImageSelectorActivity
                        .EXTRA_RESULT).size();
                imageUris.addAll(data.getStringArrayListExtra(MultiImageSelectorActivity
                        .EXTRA_RESULT));
                photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris,
                        LeaveReviewActivity.this, addPhotoTitle));
            }
        }
    }

    private void pickPhotoFromGallery() {
        if (checkReadStoragePermission()) {
            MultiImageSelector.create(this)
                    .count(maxPhotos)
                    .start(this, RC_PICK_PHOTO);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActivitiesRequestCodes.LEAVE_REVIEW_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE);
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
        if (!helpers.hasConnection(this)) {
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
        photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris,
                LeaveReviewActivity.this, addPhotoTitle));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivitiesRequestCodes.LEAVE_REVIEW_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE: {
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

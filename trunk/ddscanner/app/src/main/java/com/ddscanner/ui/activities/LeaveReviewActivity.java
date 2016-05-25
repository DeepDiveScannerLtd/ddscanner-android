package com.ddscanner.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 12.3.16.
 */
public class LeaveReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_PICK_PHOTO = 9001;

    private static final String TAG = LeaveReviewActivity.class.getSimpleName();
    private static final String ID = "ID";
    private static final String RATING = "RATING";
    private static final int RC_LOGIN = 8001;
    private static final int COMMENT_MAX_LENGTH = 250;

    private Toolbar toolbar;
    private Comment comment = new Comment();
    private String diveSpotId;
    private EditText text;
    private RatingBar ratingBar;
    private float rating;
    private ProgressDialog progressDialog;
    private TextView symbolNumberLeft;
    private ImageButton btnAddPhoto;
    private RecyclerView photos_rc;
    private List<String> imageUris = new ArrayList<String>();
    private TextView addPhotoTitle;
    private AddPhotoToDsListAdapter addPhotoToDsListAdapter;
    private List<String> imagesEncodedList = new ArrayList<>();
    private String imageEncoded;

    private RequestBody requestId, requestComment, requestRating;
    private RequestBody requessToken = null;
    private RequestBody requestSocial = null;
    private RequestBody requestSecret = null;
    private Helpers helpers = new Helpers();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        Bundle bundle = getIntent().getExtras();
        diveSpotId = bundle.getString(Constants.DIVESPOTID);
        rating = getIntent().getExtras().getFloat(RATING);
        findViews();
        toolbarSettings();
        setRcSettings();
        setProgressDialog();

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
        btnAddPhoto.setOnClickListener(this);
    }

    private void setRcSettings() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(LeaveReviewActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New review");
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.pleaseWait));
        progressDialog.setCancelable(false);
    }

    public static void show(Context context, String id, float rating) {
        Intent intent = new Intent(context, LeaveReviewActivity.class);
        intent.putExtra(Constants.DIVESPOTID, id);
        intent.putExtra(RATING, rating);
        context.startActivity(intent);
    }

    private void sendReview() {
        List<MultipartBody.Part> images = new ArrayList<>();
        for (int i = 0; i < imageUris.size(); i++) {
            File image = new File(imageUris.get(i));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData("images[]", image.getName(),
                    requestFile);
            images.add(part);
        }

        requestRating = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(Math.round(ratingBar.getRating())));
        requestId = RequestBody.create(MediaType.parse("multipart/form-data"), diveSpotId);
        requestComment = RequestBody.create(MediaType.parse("multipart/form-data"),
                text.getText().toString().trim());
        if (SharedPreferenceHelper.getIsUserLogined()) {
            requestSocial = RequestBody.create(MediaType.parse("multipart/form-data"),
                    SharedPreferenceHelper.getSn());
            requessToken = RequestBody.create(MediaType.parse("multipart/form-data"),
                    SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                requestSecret = RequestBody.create(MediaType.parse("multipart/form-data"),
                        SharedPreferenceHelper.getSecret());
            }
        }

        Call<ResponseBody> call = RestClient.getServiceInstance().addCommentToDiveSpot(
                requestId,
                requestComment,
                requestRating,
                images,
                requessToken,
                requestSocial,
                requestSecret
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().isSuccessful()) {
                    String responseString = null;
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    comment = new Gson().fromJson(responseString, Comment.class);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("COMMENT", comment);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    int httpResultCode = response.raw().code();
                    Log.i(TAG, response.raw().message());
                    String json = "";
                    try {
                        json = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, json);
                    if (httpResultCode == 400) {
                        Intent intent = new Intent(LeaveReviewActivity.this, SocialNetworks.class);
                        startActivityForResult(intent, RC_LOGIN);
                    } else {
                        String errors = "";
                        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                            System.out.println(entry.getKey());
                            System.out.println(entry.getValue());
                            if (entry.getKey().equals("token") || entry.getKey().equals("social")) {
                                Intent intent = new Intent(LeaveReviewActivity.this, SocialNetworks.class);
                                startActivityForResult(intent, RC_LOGIN);
                                break;
                            } else {
                                LinearLayout header = (LinearLayout) findViewById(R.id.message_layout);
                                header.setBackgroundResource(R.drawable.error_border);
                                errors = errors + entry.getValue() + "\n";
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                        EventTrackerHelper.EVENT_SEND_REVIEW_CLICK, new HashMap<String, Object>() {{
                            put(EventTrackerHelper.PARAM_SEND_REVIEW_CLICK, diveSpotId);
                        }});
                if (checkText(text.getText().toString())) {
                    progressDialog.show();
                    sendReview();
                }
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

    private boolean checkText(String comment) {
        comment = comment.trim();
        if (comment.length() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please leave your feedback", Toast.LENGTH_SHORT);
            LinearLayout header = (LinearLayout) findViewById(R.id.message_layout);
            header.setBackgroundResource(R.drawable.error_border);
            toast.show();
            return false;
        }
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
                progressDialog.dismiss();
            }
        }
        if (requestCode == RC_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        imageUris.add(helpers.getRealPathFromURI(LeaveReviewActivity.this, uri));
                        photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris,
                                LeaveReviewActivity.this, addPhotoTitle));
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_photo:
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(i, RC_PICK_PHOTO);
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

}

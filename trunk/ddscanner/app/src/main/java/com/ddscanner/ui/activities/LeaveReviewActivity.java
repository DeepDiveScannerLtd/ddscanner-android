package com.ddscanner.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.request.SendReviewRequest;
import com.ddscanner.rest.RestClient;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 12.3.16.
 */
public class LeaveReviewActivity extends AppCompatActivity {

    private static final String TAG = LeaveReviewActivity.class.getSimpleName();
    private static final int RC_LOGIN = 8001;
    private static final int COMMENT_MAX_LENGTH = 250;

    private Toolbar toolbar;
    private Comment comment = new Comment();
    private String diveSpotId;
    private EditText text;
    private RatingBar ratingBar;
    private ProgressDialog progressDialog;
    private TextView symbolNumberLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        diveSpotId = getIntent().getStringExtra("id");
        findViews();
        toolbarSettings();
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
        symbolNumberLeft = (TextView) findViewById(R.id.left_number);

    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New review");
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.pleaseWait));
        progressDialog.setCancelable(false);
    }

    private void sendReview() {
        // AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), "Send review clicked", );
        Log.i(TAG, SharedPreferenceHelper.getSn());
        Log.i(TAG, SharedPreferenceHelper.getToken());

        SendReviewRequest sendReviewRequest = new SendReviewRequest();
        sendReviewRequest.setRating(Math.round(ratingBar.getRating()));
        sendReviewRequest.setDiveSpotId(diveSpotId);
        sendReviewRequest.setSocial(SharedPreferenceHelper.getSn());
        if (SharedPreferenceHelper.getSn().equals("tw")) {
            sendReviewRequest.setSecret(SharedPreferenceHelper.getSecret());
        }
        sendReviewRequest.setToken(SharedPreferenceHelper.getToken());
        sendReviewRequest.setComment(text.getText().toString().trim());
        Call<ResponseBody> call = RestClient.getServiceInstance().addCOmmentToDiveSpot(sendReviewRequest);
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
                    Log.i(TAG, "Success leavenig comment\n Response string - " + responseString);

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
                            Toast toast = Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_LONG);
                            if (!errors.equals("")) {
                                errors = errors.replace("[", "");
                                errors = errors.replace("]", "");
                                toast.show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_LOGIN) {
            if (resultCode == RESULT_OK) {
                sendReview();
            }
            if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "Error with login");
                progressDialog.dismiss();
            }
        }
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
}

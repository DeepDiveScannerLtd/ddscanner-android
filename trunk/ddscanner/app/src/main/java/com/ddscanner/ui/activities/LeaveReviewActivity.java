package com.ddscanner.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.request.SendReviewRequest;
import com.ddscanner.rest.RestClient;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by lashket on 12.3.16.
 */
public class LeaveReviewActivity extends AppCompatActivity {

    private static final String TAG = LeaveReviewActivity.class.getSimpleName();

    private Toolbar toolbar;
    private Comment comment = new Comment();
    private String diveSpotId;
    private EditText text;
    private RatingBar ratingBar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        diveSpotId = getIntent().getStringExtra("id");
        findVIews();
        toolbarSettings();
        setProgressDialog();

    }

    private void findVIews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        text = (EditText) findViewById(R.id.review_text);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
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
        sendReviewRequest.setComment(text.getText().toString());
        RestClient.getServiceInstance().addCOmmentToDiveSpot(sendReviewRequest, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                comment = new Gson().fromJson(responseString, Comment.class);
                Log.i(TAG, "Success leavenig comment\n Response string - " + responseString);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("COMMENT", comment);
                progressDialog.dismiss();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Log.i(TAG, error.getMessage());
                String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                Log.i(TAG, json.toString());
                SocialNetworks.show(LeaveReviewActivity.this);
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

    private boolean checkText(String text) {
        text = text.trim();
        if (text.length() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please leave your feedback", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }
}

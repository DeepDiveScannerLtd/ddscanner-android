package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.events.DeleteCommentEvent;
import com.ddscanner.events.IsCommentLikedEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.ShowUserDialogEvent;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.ReviewsListAdapter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int RC_LOGIN = 8001;

    private ArrayList<Comment> comments;
    private RecyclerView commentsRc;
    private ProgressView progressView;

    private Toolbar toolbar;
    private FloatingActionButton leaveReview;

    private String diveSpotId;
    private Helpers helpers = new Helpers();

    private String path;

    private boolean isHasNewComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Bundle bundle = getIntent().getExtras();
        comments = (ArrayList<Comment>) bundle.getSerializable("COMMENTS");
        diveSpotId = bundle.getString(Constants.DIVESPOTID);
        path = bundle.getString("PATH");
        findViews();
        toolbarSettings();
        setContent();
    }

    private void findViews() {
        commentsRc = (RecyclerView) findViewById(R.id.reviews_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        leaveReview = (FloatingActionButton) findViewById(R.id.fab_write_review);
        progressView = (ProgressView) findViewById(R.id.progressBarFull);
        leaveReview.setOnClickListener(this);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.reviews);
    }

    private void setContent() {
        commentsRc.setHasFixedSize(true);
        commentsRc.setLayoutManager(new LinearLayoutManager(this));
        commentsRc.setAdapter(new ReviewsListAdapter(comments, ReviewsActivity.this, path));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 9001) {
            if(resultCode == Activity.RESULT_OK){
//                Comment comment = (Comment)data.getSerializableExtra("COMMENT");
//                if (comments == null) {
//                    comments = new ArrayList<Comment>();
//                }
//                comments.add(0, comment);
//                commentsRc.setAdapter(new ReviewsListAdapter(comments, ReviewsActivity.this, path));
                getComments();
                isHasNewComment = true;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == RC_LOGIN) {
            if (resultCode == RESULT_OK) {
                getComments();
            }
        }
    }

    private void getComments() {
        commentsRc.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        isHasNewComment = true;
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getComments(diveSpotId, helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Comments comments = new Gson().fromJson(responseString, Comments.class);
                    progressView.setVisibility(View.GONE);
                    commentsRc.setVisibility(View.VISIBLE);
                    commentsRc.setAdapter(new ReviewsListAdapter((ArrayList<Comment>) comments.getComments(), ReviewsActivity.this, path));
                }
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(ReviewsActivity.this, RC_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_write_review:
                LeaveReviewActivity.showForResult(this, diveSpotId, 0f, EventsTracker.SendReviewSource.FROM_REVIEWS_LIST, 9001);
                break;
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
    public void showDialog(ShowUserDialogEvent event) {
        helpers.showDialog(event.getUser(), getSupportFragmentManager());
    }

    @Subscribe
    public void isCommentLiked(IsCommentLikedEvent event) {
        isHasNewComment = true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        if (isHasNewComment) {
            setResult(RESULT_OK, returnIntent);
            finish();
        } else {
            setResult(RESULT_CANCELED, returnIntent);
            finish();
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

    @Subscribe
    public void showLoginActivity(ShowLoginActivityIntent event) {
        SocialNetworks.showForResult(ReviewsActivity.this, RC_LOGIN);
    }

    @Subscribe
    public void deleteComment(DeleteCommentEvent event) {
        deleteUsersComment(String.valueOf(event.getCommentId()));
    }

    private void deleteUsersComment(String id) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().deleteComment(id, helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    getComments();
                }
                if (!response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}

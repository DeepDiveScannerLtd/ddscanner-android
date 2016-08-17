package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.events.DeleteCommentEvent;
import com.ddscanner.events.EditCommentEvent;
import com.ddscanner.events.IsCommentLikedEvent;
import com.ddscanner.events.ReportCommentEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.ShowUserDialogEvent;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.ReviewsListAdapter;
import com.ddscanner.ui.adapters.SpinnerItemsAdapter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int RC_LOGIN = 8001;
    private static final int RC_LOGIN_TO_LEAVE_REPORT = 7070;

    private ArrayList<Comment> comments;
    private RecyclerView commentsRc;
    private ProgressView progressView;
    private MaterialDialog materialDialog;

    private Toolbar toolbar;
    private FloatingActionButton leaveReview;

    private String diveSpotId;
    private Helpers helpers = new Helpers();

    private String path;

    private boolean isHasNewComment = false;

    private FiltersResponseEntity filters = new FiltersResponseEntity();

    private List<String> reportItems = new ArrayList<>();

    private String reportCommentId;
    private String reportType;
    private String reportDescription = null;
    private boolean isClickedReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Bundle bundle = getIntent().getExtras();
        comments = (ArrayList<Comment>) bundle.getSerializable("COMMENTS");
        diveSpotId = bundle.getString(Constants.DIVESPOTID);
        path = bundle.getString("PATH");
        getReportsTypes();
        findViews();
        toolbarSettings();
        setContent();
    }

    private void findViews() {
        materialDialog = helpers.getMaterialDialog(this);
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
                onBackPressed();
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
        if (requestCode == 3011) {
            if (resultCode == RESULT_OK) {
                getComments();
            }
        }
        if (requestCode == RC_LOGIN_TO_LEAVE_REPORT) {
            if (resultCode == RESULT_OK) {
                sendReportRequest(reportType, reportDescription);
            }
            if (resultCode == RESULT_CANCELED) {
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

    @Subscribe
    public void editComment(EditCommentEvent editCommentEvent) {
        EditCommentActivity.show(this, editCommentEvent.getComment(), path);
    }

    private void deleteUsersComment(String id) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().deleteComment(id, helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    getComments();
                    isHasNewComment = true;
                }
                if (!response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getReportsTypes() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getFilters();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {

                    }
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(responseString).getAsJsonObject();
                    JsonObject currentsJsonObject = jsonObject.getAsJsonObject(Constants.FILTERS_VALUE_REPORT);
                    for (Map.Entry<String, JsonElement> elementEntry : currentsJsonObject.entrySet()) {
                        filters.getReport().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Subscribe
    public void showReportDialog(ReportCommentEvent event) {
        reportCommentId = event.getCommentId();
        List<String> objects = new ArrayList<String>();
        for (Map.Entry<String, String> entry : filters.getReport().entrySet()) {
            objects.add(entry.getValue());
        }
        new MaterialDialog.Builder(this)
                .title("Report")
                .items(objects)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                        
                        isClickedReport = true;
                        reportType = helpers.getMirrorOfHashMap(filters.getReport()).get(text);
                        if (reportType.equals("other")) {
                            showOtherReportDialog();
                            dialog.dismiss();
                        } else {
                            sendReportRequest(reportType, null);
                        }
                    }
                })
                .show();
    }

    private void showOtherReportDialog() {
        new MaterialDialog.Builder(this)
                .title("Other")
                .widgetColor(getResources().getColor(R.color.primary))
                .input("Write reason", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().trim().length() > 1) {
                            sendReportRequest("other", input.toString());
                            reportDescription = input.toString();
                        } else {
                            Toast.makeText(ReviewsActivity.this, "Write a reason", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }
    
    private void sendReportRequest(String type, String description) {
        materialDialog.show();
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            SocialNetworks.showForResult(ReviewsActivity.this, RC_LOGIN_TO_LEAVE_REPORT);
            return;
        }
        ReportRequest reportRequest = new ReportRequest();
        if (description != null) {
            reportRequest.setDescription(description);
        }
        if (!SharedPreferenceHelper.getToken().isEmpty()) {
            reportRequest.setToken(SharedPreferenceHelper.getToken());
            reportRequest.setSocial(SharedPreferenceHelper.getSn());
        } else {
            SocialNetworks.showForResult(ReviewsActivity.this, RC_LOGIN_TO_LEAVE_REPORT);
            return;
        }
        reportRequest.setType(type);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().reportComment(reportCommentId, reportRequest);
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                materialDialog.dismiss();
                if (response.isSuccessful()) {
                    isClickedReport = false;
                    reportType = null;
                    reportDescription = null;
                    getComments();
                    isHasNewComment = true;
                } else {
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
                        helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
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
                        SocialNetworks.showForResult(ReviewsActivity.this, RC_LOGIN_TO_LEAVE_REPORT);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    }
                }
            }
        });
    }
    
}

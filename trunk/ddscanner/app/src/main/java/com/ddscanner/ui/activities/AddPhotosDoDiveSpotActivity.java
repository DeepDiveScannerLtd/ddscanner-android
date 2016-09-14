package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.AddPhotoToDiveSpotAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lashket on 26.5.16.
 */
public class AddPhotosDoDiveSpotActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private Button button;
    private ArrayList<String> images;
    private Helpers helpers = new Helpers();
    private MaterialDialog materialDialog;
    private Toolbar toolbar;
    private String dsId;
    private RequestBody requestSecret = null;
    private RequestBody requestSocial = null;
    private RequestBody requestToken = null;
    private RequestBody requestType = null;
    private List<String> imagesToShow = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos_to_dive_spot);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_photos_toolbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        materialDialog = helpers.getMaterialDialog(this);
        images = (ArrayList<String>)getIntent().getSerializableExtra(Constants.ADD_PHOTO_ACTIVITY_INTENT_IMAGES);
        dsId = getIntent().getStringExtra(Constants.ADD_PHOTO_ACTIVITY_INTENT_DIVE_SPOT_ID);
        button = (Button) findViewById(R.id.button_share);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        for (int i = 0; i <images.size(); i++) {
            imagesToShow.add("file://" + images.get(i));
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3));
        recyclerView.setAdapter(new AddPhotoToDiveSpotAdapter((ArrayList<String>) imagesToShow,
                AddPhotosDoDiveSpotActivity.this));
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        sendRequest();
    }

    private void sendRequest() {
        materialDialog.show();
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            requestSocial = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    SharedPreferenceHelper.getSn());
            requestToken = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                requestSecret = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                        SharedPreferenceHelper.getSecret());
            }
        }

        requestType = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), "PUT");
        List<MultipartBody.Part> imagesToSend = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            File image = new File(images.get(i));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData(Constants.ADD_DIVE_SPOT_ACTIVITY_IMAGES_ARRAY,
                    image.getName(), requestFile);
            imagesToSend.add(part);
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addImagesToDiveSpot(dsId,
                requestType, imagesToSend, requestToken, requestSocial, requestSecret);
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                materialDialog.dismiss();
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
                        helpers.showToast(AddPhotosDoDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(AddPhotosDoDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(AddPhotosDoDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(AddPhotosDoDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(AddPhotosDoDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(AddPhotosDoDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_PHOTOS_DO_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(AddPhotosDoDiveSpotActivity.this, R.string.toast_server_error);
                    }
                } else {
                    EventsTracker.trackDiveSpotPhotoAdded();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(AddPhotosDoDiveSpotActivity.this);
            }
        });
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;

        public GridSpacingItemDecoration(int spanCount) {
            this.spanCount = spanCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position >= spanCount) {
                outRect.top = Math.round(helpers.convertDpToPixel(Float.valueOf(4),
                        AddPhotosDoDiveSpotActivity.this));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivitiesRequestCodes.REQUEST_CODE_ADD_PHOTOS_DO_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND) {
            if (resultCode == RESULT_OK) {
                sendRequest();
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

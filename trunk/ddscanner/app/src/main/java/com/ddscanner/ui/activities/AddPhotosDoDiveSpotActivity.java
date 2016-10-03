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
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.AddPhotoToDiveSpotAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by lashket on 26.5.16.
 */
public class AddPhotosDoDiveSpotActivity extends AppCompatActivity implements View.OnClickListener, InfoDialogFragment.DialogClosedListener{

    private RecyclerView recyclerView;
    private Button button;
    private ArrayList<String> images;
    private MaterialDialog materialDialog;
    private Toolbar toolbar;
    private String dsId;
    private RequestBody requestSecret = null;
    private RequestBody requestSocial = null;
    private RequestBody requestToken = null;
    private RequestBody requestType = null;
    private List<String> imagesToShow = new ArrayList<>();

    private DDScannerRestClient.ResultListener<Void> addingPhotosToDiveSpotResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackDiveSpotPhotoAdded();
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    LoginActivity.showForResult(AddPhotosDoDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_PHOTOS_DO_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND);
                    break;
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_dive_spot_not_found, DialogsRequestCodes.DRC_ADD_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos_to_dive_spot);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_photos_toolbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        materialDialog = Helpers.getMaterialDialog(this);
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
        DDScannerApplication.getDdScannerRestClient().postAddPhotosToDiveSpot(dsId, imagesToSend, addingPhotosToDiveSpotResultListener, requestType, requestToken, requestSocial);
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
                outRect.top = Math.round(Helpers.convertDpToPixel(Float.valueOf(4),
                        AddPhotosDoDiveSpotActivity.this));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_PHOTOS_DO_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND:
                if (resultCode == RESULT_OK) {
                    sendRequest();
                }
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }
                break;
        }

    }

    public static void showForResult(Activity context, int requestCode, ArrayList<String> path, String diveSpotId) {
        Intent intent = new Intent(context, AddPhotosDoDiveSpotActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("IMAGES", path);
        bundle.putString("id", diveSpotId);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
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

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_ADD_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND:
                finish();
                break;
        }
    }
}

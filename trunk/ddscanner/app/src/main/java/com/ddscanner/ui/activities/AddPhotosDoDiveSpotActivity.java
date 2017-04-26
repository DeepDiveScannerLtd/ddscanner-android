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
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.AddPhotoToDiveSpotAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 26.5.16.
 */
public class AddPhotosDoDiveSpotActivity extends AppCompatActivity implements View.OnClickListener, DialogClosedListener {

    private RecyclerView recyclerView;
    private Button button;
    private ArrayList<String> images;
    private MaterialDialog materialDialog;
    private Toolbar toolbar;
    private String dsId;
    private List<String> imagesToShow = new ArrayList<>();
    private boolean isMap;

    private DDScannerRestClient.ResultListener<Void> photosAddedResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackDiveSpotPhotoAdded();
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
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(AddPhotosDoDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_PHOTOS_DO_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND);
                    break;
                default:
                    UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_ADD_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
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
        isMap = getIntent().getBooleanExtra("isMap", false);
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
        if (isMap) {
            DDScannerApplication.getInstance().getDdScannerRestClient().postMapsToDiveSpot(dsId, images, photosAddedResultListener, this);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postPhotosToDiveSpot(dsId, images, photosAddedResultListener, this);
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
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
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

    public static void showForAddPhotos(boolean isMap, Activity context, ArrayList<String> images, String diveSpotId, int requestcode) {
        Intent intent = new Intent(context, AddPhotosDoDiveSpotActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("IMAGES", images);
        bundle.putString("id", diveSpotId);
        bundle.putBoolean("isMap", isMap);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestcode);
    }

}

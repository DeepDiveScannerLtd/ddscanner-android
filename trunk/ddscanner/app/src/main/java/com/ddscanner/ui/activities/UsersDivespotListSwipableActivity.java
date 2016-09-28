package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.SwipableDiveSpotListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class UsersDivespotListSwipableActivity extends AppCompatActivity implements InfoDialogFragment.DialogClosedListener {

    private static final String BUNDLE_KEY_SPOT_VIEW_SOURCE = "BUNDLE_KEY_SPOT_VIEW_SOURCE";

    private RecyclerView rc;
    private SwipableDiveSpotListAdapter swipableDiveSpotListAdapter;
    private List<DiveSpot> diveSpots = new ArrayList<>();
    private boolean isCheckin = false;
    private ProgressView progressBarFull;
    private EventsTracker.SpotViewSource spotViewSource;

    private DDScannerRestClient.ResultListener<DivespotsWrapper> getDiveSpotsResultListener = new DDScannerRestClient.ResultListener<DivespotsWrapper>() {
        @Override
        public void onSuccess(DivespotsWrapper result) {
            diveSpots = result.getDiveSpots();
            setUi();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USERS_DIVESPOT_LIST_SWIPABLE_ACTIVITY_CONNECTION_ERROR, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    LoginActivity.showForResult(UsersDivespotListSwipableActivity.this, ActivitiesRequestCodes.REQUEST_CODE_USERS_DIVESPOT_LIST_SWIPABLE_ACTIVITY_LOGIN);
                    break;
                default:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_USERS_DIVESPOT_LIST_SWIPABLE_ACTIVITY_UNEXPECTED_ERROR, false);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_users_dive_spots);
        isCheckin = getIntent().getBooleanExtra("ISCHECKIN", false);
        findViews();
        if (isCheckin) {
            DDScannerApplication.getDdScannerRestClient().getUsersCheckins(SharedPreferenceHelper.getUserServerId(), getDiveSpotsResultListener);
        } else {
            DDScannerApplication.getDdScannerRestClient().getUsersFavourites(SharedPreferenceHelper.getUserServerId(), getDiveSpotsResultListener);
        }
        spotViewSource = EventsTracker.SpotViewSource.getByName(getIntent().getStringExtra(BUNDLE_KEY_SPOT_VIEW_SOURCE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_USERS_DIVESPOT_LIST_SWIPABLE_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (isCheckin) {
                        DDScannerApplication.getDdScannerRestClient().getUsersCheckins(SharedPreferenceHelper.getUserServerId(), getDiveSpotsResultListener);
                    } else {
                        DDScannerApplication.getDdScannerRestClient().getUsersFavourites(SharedPreferenceHelper.getUserServerId(), getDiveSpotsResultListener);
                    }
                }
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }
            break;
        }
    }

    private void findViews() {
        rc = (RecyclerView) findViewById(R.id.divespots_rc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBarFull = (ProgressView) findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (isCheckin) {
            getSupportActionBar().setTitle(R.string.checkin);
        } else {
            getSupportActionBar().setTitle(R.string.favorites);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
    }

    private void setUi() {
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
        swipableDiveSpotListAdapter = new SwipableDiveSpotListAdapter((ArrayList<DiveSpot>) diveSpots, this, spotViewSource);
        rc.setAdapter(swipableDiveSpotListAdapter);
        initSwipe();
        progressBarFull.setVisibility(View.GONE);
        rc.setVisibility(View.VISIBLE);
    }

    public static void show(Context context, boolean isCheckins, EventsTracker.SpotViewSource spotViewSource) {
        Intent intent = new Intent(context, UsersDivespotListSwipableActivity.class);
        intent.putExtra("ISCHECKIN", isCheckins);
        intent.putExtra(BUNDLE_KEY_SPOT_VIEW_SOURCE, spotViewSource.getName());
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        swipableDiveSpotListAdapter = null;
        finish();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final DiveSpot diveSpot = diveSpots.get(viewHolder.getAdapterPosition());
                swipableDiveSpotListAdapter.removeItem(viewHolder.getAdapterPosition(), isCheckin);
                Snackbar snackbar = Snackbar
                        .make(rc, "Divespot deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                swipableDiveSpotListAdapter.undoItem(diveSpot,
                                        position, isCheckin);
                            }
                        });
                snackbar.show();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;
                Paint p = new Paint();
                p.setColor(Color.parseColor("#D32F2F"));
                RectF background = new RectF((float) itemView.getRight() + dX,
                        (float) itemView.getTop(), (float) itemView.getRight(),
                        (float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_newsealife);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width,
                        (float) itemView.getTop() + width, (float) itemView.getRight() - width,
                        (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
                super.onChildDraw(c, recyclerView, viewHolder, dX,
                        dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rc);
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
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_USERS_DIVESPOT_LIST_SWIPABLE_ACTIVITY_CONNECTION_ERROR:
            case DialogsRequestCodes.DRC_USERS_DIVESPOT_LIST_SWIPABLE_ACTIVITY_UNEXPECTED_ERROR:
                finish();
        }
    }
}

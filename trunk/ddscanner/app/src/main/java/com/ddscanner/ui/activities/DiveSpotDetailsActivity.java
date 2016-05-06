package com.ddscanner.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DiveSpotFull;
import com.ddscanner.entities.DivespotDetails;
import com.ddscanner.entities.Sealife;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.DiveSpotsPhotosAdapter;
import com.ddscanner.ui.adapters.SealifeListAdapter;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lashket on 26.4.16.
 */
public class DiveSpotDetailsActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private static final String EXTRA_ID = "ID";

    private DivespotDetails divespotDetails;
    private ProgressDialog progressDialog;
    private String productId;
    private LatLng diveSpotCoordinates;
    private boolean isCheckedIn = false;

    /*Ui*/
    private TextView diveSpotName;
    private LinearLayout rating;
    private LinearLayout informationLayout;
    private TextView diveSpotDescription;
    private ImageView diveSpotMainPhoto;
    private ProgressView progressBar;
    private ProgressView progressBarFull;
    private RecyclerView photosRecyclerView;
    private RecyclerView sealifeRecyclerview;
    private MapFragment mapFragment;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private LinearLayout mapLayout;
    private TextView object;
    private TextView level;
    private TextView depth;
    private TextView visibility;
    private TextView currents;
    private RelativeLayout checkInPeoples;
    private TextView showMore;
    private RatingBar ratingBar;
    private FloatingActionButton btnCheckIn;
    private Button showAllReviews;
    private LinearLayout isInfoValidLayout;
    private LinearLayout thanksLayout;
    private Button btnDsDetailsIsValid;
    private Button btnDsDetailsIsInvalid;
    private ImageView thanksClose;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_spot_details);
        findViews();
        toolbarSettings();
        productId = getIntent().getStringExtra(EXTRA_ID);
        requestProductDetails(productId);
    }

    /**
     * Find views in activity
     * @author Andrei Lashkevich
     */

    private void findViews() {
        diveSpotName = (TextView) findViewById(R.id.dive_spot_name);
        rating = (LinearLayout) findViewById(R.id.stars);
        informationLayout = (LinearLayout) findViewById(R.id.informationLayout);
        diveSpotDescription = (TextView) findViewById(R.id.dive_place_description);
        diveSpotMainPhoto = (ImageView) findViewById(R.id.main_photo);
        progressBar = (ProgressView) findViewById(R.id.progressBar);
        progressBarFull = (ProgressView) findViewById(R.id.progressBarFull);
        photosRecyclerView = (RecyclerView) findViewById(R.id.photos_rc);
        sealifeRecyclerview = (RecyclerView) findViewById(R.id.sealife_rc);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_map_fragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mapLayout = (LinearLayout) findViewById(R.id.map_layout);
        object = (TextView) findViewById(R.id.object);
        level = (TextView) findViewById(R.id.level);
        depth = (TextView) findViewById(R.id.depth);
        visibility = (TextView) findViewById(R.id.visibility);
        currents = (TextView) findViewById(R.id.currents);
        checkInPeoples = (RelativeLayout) findViewById(R.id.check_in_peoples);
        showMore = (TextView) findViewById(R.id.showmore);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        btnCheckIn = (FloatingActionButton) findViewById(R.id.fab_checkin);
        showAllReviews = (Button) findViewById(R.id.btn_show_all_reviews);
        isInfoValidLayout = (LinearLayout) findViewById(R.id.is_info_valid_layout);
        thanksLayout = (LinearLayout) findViewById(R.id.thanks_layout);
        btnDsDetailsIsValid = (Button) findViewById(R.id.yes_button);
        btnDsDetailsIsInvalid = (Button) findViewById(R.id.no_button);
        thanksClose = (ImageView) findViewById(R.id.thank_close);
    }

    /**
     * Create toolbar ui
     * @author Andrei Lashkevich
     */

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle("");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(android.R.color.transparent));
      //  collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.transparent));
    }

    private void setDialogUI() {

    }

    /**
     * Set ui data at current activity
     * @author Andrei Lashkevich
     */

    private void setUi() {
        thanksClose.setOnClickListener(this);
        btnDsDetailsIsInvalid.setOnClickListener(this);
        btnDsDetailsIsValid.setOnClickListener(this);
        btnCheckIn.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(this);
        checkInPeoples.setOnClickListener(this);
        showMore.setOnClickListener(this);
        showAllReviews.setOnClickListener(this);
        final DiveSpotFull diveSpot = divespotDetails.getDivespot();
        object.setText(diveSpot.getObject());
        level.setText(diveSpot.getLevel());
        depth.setText(diveSpot.getDepth());
        visibility.setText(diveSpot.getVisibility());
        currents.setText(diveSpot.getCurrents());
        Picasso.with(this).load(diveSpot.getDiveSpotPathMedium() + diveSpot.getImages().get(0)).into(diveSpotMainPhoto, new ImageLoadedCallback(progressBar) {
            @Override
            public void onSuccess() {
                super.onSuccess();
                progressBar.setVisibility(View.GONE);
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(diveSpot.getName());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
        diveSpotName.setText(diveSpot.getName());
        diveSpotDescription.post(new Runnable() {
            @Override
            public void run() {
                diveSpotDescription.setText(diveSpot.getDescription());
                if (diveSpotDescription.getLineCount() > 3) {
                    diveSpotDescription.setMaxLines(3);
                    diveSpotDescription.setEllipsize(TextUtils.TruncateAt.END);
                    showMore.setVisibility(View.VISIBLE);
                }
            }
        });
        for (int k = 0; k < diveSpot.getRating(); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_ds_star_full);
            iv.setPadding(0,0,5,0);
            rating.addView(iv);
        }
        for (int k = 0; k < 5 - diveSpot.getRating(); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_ds_star_empty);
            iv.setPadding(0,0,5,0);
            rating.addView(iv);
        }
         photosRecyclerView.setLayoutManager(new GridLayoutManager(DiveSpotDetailsActivity.this,4));
        photosRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4));
        photosRecyclerView.setAdapter(new DiveSpotsPhotosAdapter((ArrayList<String>) diveSpot.getImages(),
                diveSpot.getDiveSpotPathMedium(), DiveSpotDetailsActivity.this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(DiveSpotDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        sealifeRecyclerview.setNestedScrollingEnabled(false);
        sealifeRecyclerview.setHasFixedSize(false);
        sealifeRecyclerview.setLayoutManager(layoutManager);
        sealifeRecyclerview.setAdapter(new SealifeListAdapter((ArrayList<Sealife>) divespotDetails.getSealifes(), this, diveSpot.getSealifePathSmall(), diveSpot.getSealifePathMedium()));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                workWithMap(googleMap);
            }
        });
        progressBarFull.setVisibility(View.GONE);
        informationLayout.setVisibility(View.VISIBLE);

    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * Handling map click events
     * @author Andrei Lashkevich
     * @param googleMap
     */
    private void workWithMap(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds))
                .position(diveSpotCoordinates));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(diveSpotCoordinates, 7.0f));
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
                intent.putExtra("LATLNG", diveSpotCoordinates);
                startActivity(intent);
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
                intent.putExtra("LATLNG", diveSpotCoordinates);
                startActivity(intent);
                return false;
            }
        });
    }

    /**
     * Request for getting data about dive spot
     * @author Andrei Lashkevich
     * @param productId
     */
    private void requestProductDetails(String productId) {
        Call<ResponseBody> call = RestClient.getServiceInstance().getDiveSpotById(productId);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    divespotDetails = new Gson().fromJson(responseString, DivespotDetails.class);
                    diveSpotCoordinates = new LatLng(divespotDetails.getDivespot().getLat(), divespotDetails.getDivespot().getLng());
                    setUi();
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO Handle errors
            }
        });
    }

    /**
     * Show current activity from another place of app
     * @author Andrei Lashkevich
     * @param context
     * @param id
     */

    public static void show(Context context, String id) {
        Intent intent = new Intent(context, DiveSpotDetailsActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    private class ImageLoadedCallback implements Callback {
        ProgressView progressBar;

        public ImageLoadedCallback(ProgressView progBar) {
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_layout:
                Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
                intent.putExtra("LATLNG", new LatLng(divespotDetails.getDivespot().getLat(), divespotDetails.getDivespot().getLng()));
                startActivity(intent);
                break;
            case R.id.check_in_peoples:
                CheckInPeoplesActivity.show(DiveSpotDetailsActivity.this);
                break;
            case R.id.showmore:
                showMore.setVisibility(View.GONE);
                diveSpotDescription.setMaxLines(10000);
                diveSpotDescription.setEllipsize(null);
                break;
            case R.id.fab_checkin:
                if (isCheckedIn) {
                    checkOut();
                } else {
                    checkIn();
                }
                break;
            case R.id.btn_show_all_reviews:
                Intent reviewsIntent = new Intent(DiveSpotDetailsActivity.this, ReviewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("COMMENTS", (ArrayList<Comment>) divespotDetails.getComments());
                bundle.putString("DIVESPOTID", String.valueOf(divespotDetails.getDivespot().getId()));
                reviewsIntent.putExtras(bundle);
                startActivityForResult(reviewsIntent, 9001);
                break;
            case R.id.yes_button:
                isInfoValidLayout.setVisibility(View.GONE);
                thanksLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.no_button:
                AddDiveSpotActivity.show(this);
                break;
            case R.id.thank_close:
                thanksLayout.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Sending request when try to check in in dive spot and change FAB style
     * @author Andrei Lashkevich
     */

    private void checkIn() {
        Call<ResponseBody> call = RestClient.getServiceInstance().checkIn(
                String.valueOf(divespotDetails.getDivespot().getId()),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        SharedPreferenceHelper.getSn()),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        SharedPreferenceHelper.getToken())
                );
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().code() == 200) {
                    btnCheckIn.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                            DiveSpotDetailsActivity.this, R.drawable.ic_acb_pin_checked));
                    btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
                    isCheckedIn = true;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * Sending request when try to check out in dive spot and change FAB style
     * @author Andrei Lashkevich
     */

    private void checkOut() {
        Call<ResponseBody> call = RestClient.getServiceInstance().checkOut(
                String.valueOf(divespotDetails.getDivespot().getId()),
              /*  RequestBody.create(MediaType.parse("multipart/form-data"),
                        SharedPreferenceHelper.getSn()),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        SharedPreferenceHelper.getToken())*/
                SharedPreferenceHelper.getSn(),
                SharedPreferenceHelper.getToken()
        );
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().code() == 200) {
                    btnCheckIn.setImageDrawable(AppCompatDrawableManager.get().getDrawable(DiveSpotDetailsActivity.this, R.drawable.ic_acb_pin));
                    btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                    isCheckedIn = false;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        LeaveReviewActivity.show(DiveSpotDetailsActivity.this, String.valueOf(divespotDetails.getDivespot().getId()), rating);
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

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;

        public GridSpacingItemDecoration(int spanCount) {
            this.spanCount = spanCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
                if (position >= spanCount) {
                    outRect.top = Math.round(convertDpToPixel(Float.valueOf(10), DiveSpotDetailsActivity.this));
                }
        }
    }

}

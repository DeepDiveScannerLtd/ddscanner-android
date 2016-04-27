package com.ddscanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DiveSpotFull;
import com.ddscanner.entities.DivespotDetails;
import com.ddscanner.entities.Sealife;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.DiveSpotsPhotosAdapter;
import com.ddscanner.ui.adapters.SealifeListAdapter;
import com.ddscanner.utils.LogUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by lashket on 26.4.16.
 */
public class DiveSpotDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_ID = "ID";

    private DivespotDetails divespotDetails;
    private ProgressDialog progressDialog;
    private String productId;

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



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_spot_details);
        findViews();
        toolbarSettings();
        productId = getIntent().getStringExtra(EXTRA_ID);
        requestProductDetails(productId);

    }

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
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
        getSupportActionBar().setTitle("");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(android.R.color.transparent));
      //  collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.transparent));
    }

    private void setUi() {
        final DiveSpotFull diveSpot = divespotDetails.getDivespot();
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
        diveSpotDescription.setText(diveSpot.getDescription());
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

        photosRecyclerView.setLayoutManager(new GridLayoutManager(DiveSpotDetailsActivity.this, 4));
     //   photosRecyclerView.addItemDecoration(new ItemOffsetDecoration(DiveSpotDetailsActivity.this, R.dimen.rc_photos_dimen));
        photosRecyclerView.setAdapter(new DiveSpotsPhotosAdapter((ArrayList<String>) diveSpot.getImages(),
                diveSpot.getDiveSpotPathSmall(), DiveSpotDetailsActivity.this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(DiveSpotDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        sealifeRecyclerview.setNestedScrollingEnabled(false);
        sealifeRecyclerview.setHasFixedSize(false);
        sealifeRecyclerview.setLayoutManager(layoutManager);
        sealifeRecyclerview.setAdapter(new SealifeListAdapter((ArrayList<Sealife>) divespotDetails.getSealifes(), this, diveSpot.getSealifePathSmall(), diveSpot.getSealifePathMedium()));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds))
                        .position(new LatLng(diveSpot.getLat(), diveSpot.getLng())));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(diveSpot.getLat(), diveSpot.getLng()), 7.0f));
            }
        });
        progressBarFull.setVisibility(View.GONE);
        informationLayout.setVisibility(View.VISIBLE);

    }

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

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

}

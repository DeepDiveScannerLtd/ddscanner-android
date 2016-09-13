package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DivespotDetails;
import com.ddscanner.entities.Image;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.PhotosActivityPagerAdapter;
import com.ddscanner.ui.fragments.DiveSpotAllPhotosFragment;
import com.ddscanner.ui.fragments.DiveSpotPhotosFragment;
import com.ddscanner.ui.fragments.DiveSpotReviewsPhoto;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lashket on 11.5.16.
 */
public class DiveSpotPhotosActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DiveSpotPhotosActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager photosViewPager;
    private Toolbar toolbar;
    private ArrayList<Image> diveSpotImages;
    private String path;
    private ArrayList<Image> reviewsImages;
    private ArrayList<Image> allPhotos;
    private Helpers helpers = new Helpers();
    private FloatingActionButton fabAddPhoto;
    private String dsId;
    private PhotosActivityPagerAdapter photosActivityPagerAdapter;
    private DivespotDetails divespotDetails;
    private boolean isDataChanged = false;

    private DiveSpotAllPhotosFragment diveSpotAllPhotosFragment = new DiveSpotAllPhotosFragment();
    private DiveSpotPhotosFragment diveSpotPhotosFragment = new DiveSpotPhotosFragment();
    private DiveSpotReviewsPhoto diveSpotReviewsPhoto = new DiveSpotReviewsPhoto();

    private ProgressView progressView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_photos);
        findViews();
        photosActivityPagerAdapter = new PhotosActivityPagerAdapter(getSupportFragmentManager());
        Bundle bundle = getIntent().getExtras();
        diveSpotImages = bundle.getParcelableArrayList("images");
        reviewsImages =  bundle.getParcelableArrayList("reviewsImages");

        path = getIntent().getStringExtra("path");
        dsId = getIntent().getStringExtra("id");
        if (diveSpotImages != null) {
            diveSpotImages = helpers.appendFullImagesWithPath(diveSpotImages, path);
        }
        diveSpotPhotosFragment.setList(diveSpotImages, path);

        if (reviewsImages != null) {
            reviewsImages = helpers.appendFullImagesWithPath(reviewsImages, path);
        }
        diveSpotReviewsPhoto.setList(reviewsImages, path);

        allPhotos = helpers.compareObjectsArray(reviewsImages, diveSpotImages);

        diveSpotAllPhotosFragment.setList(allPhotos, path);

        setupViewPager();
        setUi();
        setUpTabLayout();
    }

    private void setUpTabLayout() {
        tabLayout.getTabAt(2).setText("Reviews");
        tabLayout.getTabAt(1).setText("Dive Spot");
        tabLayout.getTabAt(0).setText("All");
    }

    private void setUi() {
        tabLayout.setupWithViewPager(photosViewPager);
        photosViewPager.setOffscreenPageLimit(3);
    }

    private void setupViewPager() {
        photosActivityPagerAdapter.addFragment(diveSpotAllPhotosFragment, "all");
        photosActivityPagerAdapter.addFragment(diveSpotPhotosFragment, "divespot");
        photosActivityPagerAdapter.addFragment(diveSpotReviewsPhoto, "reviews");
        photosViewPager.setAdapter(photosActivityPagerAdapter);
    }

    private void findViews() {
        progressView = (ProgressView) findViewById(R.id.progressBar);
        tabLayout = (TabLayout) findViewById(R.id.photos_tab_layout);
        photosViewPager = (ViewPager) findViewById(R.id.photos_view_pager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fabAddPhoto = (FloatingActionButton) findViewById(R.id.fab_add_photo);
        fabAddPhoto.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.photos);
    }

    public static void show(Context context, ArrayList<String> images, String path,
                            ArrayList<String> reviewsImages, String id) {
        Intent intent = new Intent(context, DiveSpotPhotosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("images", images);
        bundle.putString("path", path);
        bundle.putSerializable("reviewsImages", reviewsImages);
        bundle.putString("id", id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                getDiveSpotPhotos();
             //   finish();
            }
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity
                        .EXTRA_RESULT);
                Intent intent = new Intent(this, AddPhotosDoDiveSpotActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("IMAGES", (ArrayList<String>)path);
                bundle.putString("id", dsId);
                intent.putExtras(bundle);
                startActivityForResult(intent, 10);
                for (int i = 0; i < path.size(); i++) {
                    Log.i("ADDRESS", path.get(i));
                }
            }
        }
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                MultiImageSelector.create(this).start(this, 1);
            }
        }
        if (requestCode == ActivitiesRequestCodes.PHOTOS_ACTIVITY_REQUEST_CODE_SLIDER) {
            if (resultCode == RESULT_OK) {
                getDiveSpotPhotos();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_photo:
                if (!SharedPreferenceHelper.isUserLoggedIn()) {
                    Intent intent = new Intent(this, SocialNetworks.class);
                    startActivityForResult(intent, 100);
                } else {
                    MultiImageSelector.create(this).count(3).start(this, 1);
                }
                break;
        }
    }

    private void getDiveSpotPhotos() {
        progressView.setVisibility(View.VISIBLE);
        photosViewPager.setVisibility(View.GONE);
        Map<String, String> map = new HashMap<>();
        map = helpers.getUserQuryMapRequest();
        map.put("isImageAuthor", "true");
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotImages(dsId, map);
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                        divespotDetails = new Gson().fromJson(responseString, DivespotDetails.class);
                        updateFragments(divespotDetails);
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(DiveSpotPhotosActivity.this);
            }
        });
    }

    private void updateFragments(DivespotDetails divespotDetails) {
        isDataChanged = true;

        reviewsImages = (ArrayList<Image>) divespotDetails.getDivespot().getCommentImages();
        diveSpotImages = (ArrayList<Image>)divespotDetails.getDivespot().getImages();
        if (diveSpotImages != null) {
            diveSpotImages = helpers.appendFullImagesWithPath(diveSpotImages, path);
        }
        if (reviewsImages != null) {
            reviewsImages = helpers.appendFullImagesWithPath(reviewsImages, path);
        }

        diveSpotReviewsPhoto.setList(reviewsImages, path);
        allPhotos = new ArrayList<>();
        allPhotos = helpers.compareObjectsArray(reviewsImages, diveSpotImages);
        diveSpotAllPhotosFragment.setList(allPhotos, path);
        diveSpotPhotosFragment.setList(diveSpotImages, path);

        progressView.setVisibility(View.GONE);
        photosViewPager.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isDataChanged) {
            setResult(RESULT_OK);
            finish();
        } else {
            finish();
        }
    }
}

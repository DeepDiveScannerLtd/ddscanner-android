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
import com.ddscanner.ui.adapters.PhotosActivityPagerAdapter;
import com.ddscanner.ui.fragments.DiveSpotAllPhotosFragment;
import com.ddscanner.ui.fragments.DiveSpotPhotosFragment;
import com.ddscanner.ui.fragments.DiveSpotReviewsPhoto;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by lashket on 11.5.16.
 */
public class DiveSpotPhotosActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager photosViewPager;
    private Toolbar toolbar;
    private ArrayList<String> diveSpotImages;
    private String path;
    private ArrayList<String> reviewsImages;
    private ArrayList<String> allPhotos;
    private Helpers helpers = new Helpers();
    private FloatingActionButton fabAddPhoto;
    private String dsId;

    private DiveSpotAllPhotosFragment diveSpotAllPhotosFragment = new DiveSpotAllPhotosFragment();
    private DiveSpotPhotosFragment diveSpotPhotosFragment = new DiveSpotPhotosFragment();
    private DiveSpotReviewsPhoto diveSpotReviewsPhoto = new DiveSpotReviewsPhoto();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_photos);
        findViews();
        diveSpotImages =(ArrayList<String>) getIntent().getSerializableExtra("images");
        reviewsImages = (ArrayList<String>) getIntent().getSerializableExtra("reviewsImages");

        path = getIntent().getStringExtra("path");
        dsId = getIntent().getStringExtra("id");

        diveSpotImages = helpers.appendImagesWithPath(diveSpotImages, path);

        Bundle bundle = new Bundle();
        bundle.putSerializable("diveSpotImages", diveSpotImages);
        diveSpotPhotosFragment.setArguments(bundle);

        if (reviewsImages != null) {
            reviewsImages = helpers.appendImagesWithPath(reviewsImages, path);
        }

        bundle = new Bundle();
        bundle.putSerializable("reviewsImages", reviewsImages);
        diveSpotReviewsPhoto.setArguments(bundle);

        allPhotos = helpers.compareArrays(reviewsImages, diveSpotImages);

        bundle = new Bundle();
        bundle.putSerializable("images", allPhotos);
        diveSpotAllPhotosFragment.setArguments(bundle);

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
        PhotosActivityPagerAdapter photosActivityPagerAdapter = new PhotosActivityPagerAdapter(
                getSupportFragmentManager()
        );
        photosActivityPagerAdapter.addFragment(diveSpotAllPhotosFragment, "all");
        photosActivityPagerAdapter.addFragment(diveSpotPhotosFragment, "divespot");
        photosActivityPagerAdapter.addFragment(diveSpotReviewsPhoto, "reviews");
        photosViewPager.setAdapter(photosActivityPagerAdapter);
    }

    private void findViews() {
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
                finish();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_photo:
                if (!SharedPreferenceHelper.isUserLoggedIn()) {
                    Intent intent = new Intent(this, SocialNetworks.class);
                    startActivityForResult(intent, 100);
                } else {
                    MultiImageSelector.create(this).start(this, 1);
                }
                break;
        }
    }

}

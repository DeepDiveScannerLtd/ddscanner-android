package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ddscanner.R;
import com.ddscanner.ui.adapters.PhotosActivityPagerAdapter;
import com.ddscanner.ui.fragments.DiveSpotAllPhotosFragment;
import com.ddscanner.ui.fragments.DiveSpotPhotosFragment;
import com.ddscanner.ui.fragments.DiveSpotReviewsPhoto;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;

/**
 * Created by lashket on 11.5.16.
 */
public class DiveSpotPhotosActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager photosViewPager;
    private Toolbar toolbar;
    private ArrayList<String> diveSpotImages;
    private String path;
    private ArrayList<String> reviewsImages;
    private ArrayList<String> allPhotos;
    private Helpers helpers = new Helpers();

    private DiveSpotAllPhotosFragment diveSpotAllPhotosFragment = new DiveSpotAllPhotosFragment();
    private DiveSpotPhotosFragment diveSpotPhotosFragment = new DiveSpotPhotosFragment();
    private DiveSpotReviewsPhoto diveSpotReviewsPhoto = new DiveSpotReviewsPhoto();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_photos);
        diveSpotImages =(ArrayList<String>) getIntent().getSerializableExtra("images");
        reviewsImages = (ArrayList<String>) getIntent().getSerializableExtra("reviewsImages");

        path = getIntent().getStringExtra("path");

        diveSpotImages = addPathToAdress(diveSpotImages, path);

        Bundle bundle = new Bundle();
        bundle.putSerializable("diveSpotImages", diveSpotImages);
        diveSpotPhotosFragment.setArguments(bundle);

        if (reviewsImages != null) {
            reviewsImages = addPathToAdress(reviewsImages, path);
        }

        bundle = new Bundle();
        bundle.putSerializable("reviewsImages", reviewsImages);
        diveSpotReviewsPhoto.setArguments(bundle);

        allPhotos = helpers.compareArrays(reviewsImages, diveSpotImages);

        bundle = new Bundle();
        bundle.putSerializable("images", allPhotos);
        diveSpotAllPhotosFragment.setArguments(bundle);

        findViews();
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.photos);
    }

    public static void show(Context context, ArrayList<String> images, String path, ArrayList<String> reviewsImages) {
        Intent intent = new Intent(context, DiveSpotPhotosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("images", images);
        bundle.putString("path", path);
        bundle.putSerializable("reviewsImages", reviewsImages);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private ArrayList<String> addPathToAdress(ArrayList<String> images, String path) {
        for (int i = 0; i < images.size(); i++) {
            images.set(i, path + images.get(i));
        }
        return images;
    }
}

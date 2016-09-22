package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Image;
import com.ddscanner.ui.adapters.ReviewImageSLiderAdapter;
import com.ddscanner.ui.adapters.SliderImagesAdapter;
import com.ddscanner.ui.views.SimpleGestureFilter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ReviewImageSliderActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener , SimpleGestureFilter.SimpleGestureListener{

    private LinearLayout pager_indicator;
    private int dotsCount = 0;
    private ReviewImageSLiderAdapter sliderImagesAdapter;
    private ImageView[] dots;
    private FrameLayout baseLayout;
    private ViewPager viewPager;
    private ImageView close;
    private ArrayList<String> images;
    private Drawable drawable;
    private int position;
    private SimpleGestureFilter detector;
    float x1,x2;
    float y1, y2;
    private boolean isSelfImages;
    private boolean isFromReviews;
    private String reportName;
    private String deleteImageName;
    private ImageView menu;
    private String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        findViews();
        images = (ArrayList<String>) getIntent().getSerializableExtra(Constants.REVIEWS_IMAGES_SLIDE_INTENT_IMAGES);
        isSelfImages = getIntent().getBooleanExtra("isSelf", false);
        isFromReviews = getIntent().getBooleanExtra("isFromReviews", false);
        path = getIntent().getStringExtra("path");
        if (isFromReviews) {
         //   menu.setVisibility(View.VISIBLE);
        }
        detector = new SimpleGestureFilter(this,this);
        position = getIntent().getIntExtra(Constants.REVIEWS_IMAGES_SLIDE_INTENT_POSITION, 0);
        viewPager.addOnPageChangeListener(this);
        sliderImagesAdapter = new ReviewImageSLiderAdapter(getFragmentManager(), images);
        viewPager.setAdapter(sliderImagesAdapter);
        setUi();

    }

    private void showDeleteMenu(View view) {
        deleteImageName = images.get(position).replace(path, "");
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_delete, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemsClickListener(deleteImageName));
        popup.show();
    }

    private void showReportMenu(View view) {
        if (images.size() == 0) {
            position = 0;
            this.position = 0;
        }
        reportName = images.get(position).replace(path, "");
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_report, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemsClickListener(reportName));
        popup.show();
    }

    private void findViews() {
        viewPager = (ViewPager) findViewById(R.id.image_slider);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        close = (ImageView) findViewById(R.id.close_btn);
        baseLayout = (FrameLayout) findViewById(R.id.swipe_layout);
        menu = (ImageView) findViewById(R.id.options);
        close.setOnClickListener(this);
        menu.setOnClickListener(this);
    }

    private void setUi() {
        dotsCount = sliderImagesAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i=0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4,0,4,0);

            pager_indicator.addView(dots[i],  params);
        }
        dots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selecteditem_dot));
        viewPager.setCurrentItem(position);
    }


    private class MenuItemsClickListener implements PopupMenu.OnMenuItemClickListener {

        private String imageName;

        public MenuItemsClickListener(String imageName) {
            this.imageName = imageName;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.photo_report:
                    EventsTracker.trackPhotoReport();
                    break;
                case R.id.photo_delete:
                    break;
            }
            return false;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_btn:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.options:
                if (isSelfImages) {
                    showDeleteMenu(menu);
                    break;
                }
                showReportMenu(menu);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonselecteditem_dot));
        }
        dots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selecteditem_dot));
        this.position = position;
    }


    public static void show(Context context, ArrayList<String> images, int position, boolean isSelfImages, boolean isFromReviews, String path) {
        Intent intent = new Intent(context, ReviewImageSliderActivity.class);
        intent.putExtra(Constants.REVIEWS_IMAGES_SLIDE_INTENT_IMAGES, images);
        intent.putExtra(Constants.REVIEWS_IMAGES_SLIDE_INTENT_POSITION, position);
        intent.putExtra("isSelf", isSelfImages);
        intent.putExtra("isFromReviews", isFromReviews);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }


    @Override
    public void onPageScrollStateChanged(int state) {
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
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                x2 = touchevent.getX();
                y2 = touchevent.getY();
                break;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {
            case SimpleGestureFilter.SWIPE_DOWN:
                // onBackPressed();
                break;
            case SimpleGestureFilter.SWIPE_UP:
                onBackPressed();
                break;

        }
    }

    @Override
    public void onDoubleTap() {

    }

}
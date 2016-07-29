package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Image;
import com.ddscanner.ui.adapters.ReviewImageSLiderAdapter;
import com.ddscanner.ui.adapters.SliderImagesAdapter;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lashket on 4.3.16.
 */
public class ReviewImageSliderActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

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
    private Helpers helpers = new Helpers();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        findViews();
        images = (ArrayList<String>) getIntent().getSerializableExtra("IMAGES");
        position = getIntent().getIntExtra("position", 0);
        viewPager.addOnPageChangeListener(this);
        sliderImagesAdapter = new ReviewImageSLiderAdapter(getFragmentManager(), images);
        viewPager.setAdapter(sliderImagesAdapter);
        close.setOnClickListener(this);
        setUi();

    }

    private void findViews() {
        viewPager = (ViewPager) findViewById(R.id.image_slider);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        close = (ImageView) findViewById(R.id.close_btn);
        baseLayout = (FrameLayout) findViewById(R.id.swipe_layout);
    }

    private void setUi() {
        dotsCount = sliderImagesAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i=0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4,0,4,0);

            pager_indicator.addView(dots[i],  params);
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_btn:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
      /*  Picasso.with(this).load("http://www.trizeri.travel/images/divespots/medium/" +images.get(position)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                drawable = new BitmapDrawable(bitmap);
                drawable.setColorFilter(Color.parseColor("#99000000"), PorterDuff.Mode.SRC_ATOP);
                baseLayout.setBackgroundDrawable(drawable);
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });*/
    }


    public static void show(Context context, ArrayList<String> images, int position) {
        Intent intent = new Intent(context, ReviewImageSliderActivity.class);
        intent.putExtra("IMAGES", images);
        intent.putExtra("position", position);
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
        if (!helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

}
package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.ui.adapters.SliderImagesAdapter;

/**
 * Created by lashket on 4.3.16.
 */
public class ImageSliderActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    private LinearLayout pager_indicator;
    private int dotsCount = 0;
    private SliderImagesAdapter sliderImagesAdapter;
    private ImageView[] dots;
    private FrameLayout baseLayout;
    private ViewPager viewPager;
    private ImageView close;
    private ArrayList<String> images;
    private Drawable drawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        findViews();
        images = (ArrayList<String>) getIntent().getSerializableExtra("IMAGES");
        viewPager.setOnPageChangeListener(this);
        sliderImagesAdapter = new SliderImagesAdapter(getFragmentManager(), images);
        viewPager.setAdapter(sliderImagesAdapter);
        viewPager.setCurrentItem(0);
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
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
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


    public static void show(Context context, ArrayList<String> images) {
        Intent intent = new Intent(context, ImageSliderActivity.class);
        intent.putExtra("IMAGES", images);
        context.startActivity(intent);
    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }


}

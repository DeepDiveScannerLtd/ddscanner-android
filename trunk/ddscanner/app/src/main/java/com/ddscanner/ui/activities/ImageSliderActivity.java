package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.Image;
import com.ddscanner.ui.adapters.SliderImagesAdapter;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

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
    private ArrayList<Image> images;
    private Drawable drawable;
    private int position;
    private ImageView avatar;
    private TextView date;
    private TextView userName;
    private ImageView options;
    private Helpers helpers = new Helpers();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        findViews();
        Bundle bundle = getIntent().getExtras();
        images = bundle.getParcelableArrayList("IMAGES");
        position = getIntent().getIntExtra("position", 0);
        viewPager.addOnPageChangeListener(this);
        sliderImagesAdapter = new SliderImagesAdapter(getFragmentManager(), images);
        viewPager.setAdapter(sliderImagesAdapter);
        close.setOnClickListener(this);
        options.setVisibility(View.VISIBLE);
        changeUiAccrodingPosition(position);
        setUi();

    }

    private void changeUiAccrodingPosition(int position) {
        userName.setText(images.get(position).getAuthor().getName());
        date.setText(helpers.convertDateToImageSliderActivity(images.get(position).getAuthor().getDate()));
        Picasso.with(this)
                .load(images.get(position).getAuthor().getPhoto())
                .resize(Math.round(helpers.convertDpToPixel(35, this)), Math.round(helpers.convertDpToPixel(35, this)))
                .placeholder(R.drawable.avatar_profile_default)
                .transform(new CropCircleTransformation())
                .into(avatar);
        if (images.get(position).isReport()) {
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showReportMenu(options);
                }
            });
        } else {
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteMenu(options);
                }
            });
        }
    }

    private void findViews() {
        viewPager = (ViewPager) findViewById(R.id.image_slider);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        close = (ImageView) findViewById(R.id.close_btn);
        baseLayout = (FrameLayout) findViewById(R.id.swipe_layout);
        avatar = (ImageView) findViewById(R.id.user_avatar);
        date = (TextView) findViewById(R.id.date);
        userName = (TextView) findViewById(R.id.user_name);
        options = (ImageView) findViewById(R.id.options);
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
                setResult(RESULT_OK);
                finish();
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
        changeUiAccrodingPosition(position);
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


    private void showDeleteMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_delete, popup.getMenu());
     //   popup.setOnMenuItemClickListener(new MenuItemClickListener(commentId, comment));
        popup.show();
    }

    private void showReportMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_report, popup.getMenu());
     //   popup.setOnMenuItemClickListener(new MenuItemClickListener(commentId, comment));
        popup.show();
    }

    public static void show(Context context, ArrayList<Image> images, int position) {
        Intent intent = new Intent(context, ImageSliderActivity.class);
        intent.putParcelableArrayListExtra("IMAGES", images);
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

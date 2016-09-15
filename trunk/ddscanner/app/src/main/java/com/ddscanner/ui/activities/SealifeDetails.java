package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Sealife;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class SealifeDetails extends AppCompatActivity {

    private TextView length, weight, depth, scname, order, distribution, scclass, habitat,name;
    private ImageView photo;
    private Sealife sealife;
    private String pathMedium;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private Drawable drawable;
    private Bitmap image;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ImageView backgroundImage;
    private ProgressBar progressBar;

    public static void show(Context context, Sealife sealife, String pathMedium) {
        Intent intent = new Intent(context, SealifeDetails.class);
        intent.putExtra("SEALIFE", sealife);
        intent.putExtra("PATH", pathMedium);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sealife_full);
        findViews();
        sealife = (Sealife) getIntent().getSerializableExtra("SEALIFE");
        pathMedium = getIntent().getStringExtra("PATH");
        Log.i("SEALIFEFULL", pathMedium + sealife.getImage());
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(sealife.getName());
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        Picasso.with(this).load(pathMedium + sealife.getImage()).resize(Math.round(dpWidth), 239).centerCrop().into(photo, new ImageLoadedCallback(progressBar) {
            @Override
            public void onSuccess() {
                if (this.progressBar != null) {
                    this.progressBar.setVisibility(View.GONE);
                }
            }

        });
     //   Picasso.with(this).load(pathMedium + sealife.getImage()).resize(Math.round(dpWidth), 239).centerCrop().into(backgroundImage);
        backgroundImage.setColorFilter(Color.parseColor("#99000000"), PorterDuff.Mode.SRC_ATOP);
        setContent();
    }

    public void findViews() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        name = (TextView) findViewById(R.id.name);
        toolbar = (Toolbar) findViewById(R.id.toolbar_collapse);
        photo = (ImageView) findViewById(R.id.sealife_full_photo);
        length = (TextView) findViewById(R.id.length);
        weight = (TextView) findViewById(R.id.weight);
        depth = (TextView) findViewById(R.id.depth);
        scname = (TextView) findViewById(R.id.scname);
        order = (TextView) findViewById(R.id.order);
        distribution = (TextView) findViewById(R.id.distribution);
        scclass = (TextView) findViewById(R.id.scclass);
        habitat = (TextView) findViewById(R.id.habitat);
        backgroundImage = (ImageView) findViewById(R.id.background_photo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Details");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    private void setContent() {
        name.setText(sealife.getName());
        name.setVisibility(View.VISIBLE);
        if (sealife.getLength() != null) {
            if (!sealife.getLength().equals("")) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.char_length);
                linearLayout.setVisibility(View.VISIBLE);
                length.setText(sealife.getLength());
            }
        }

        if (sealife.getWeight() != null) {
            if (!sealife.getWeight().equals("")) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.char_weight);
                linearLayout.setVisibility(View.VISIBLE);
                weight.setText(sealife.getWeight().trim());
            }
        }

        if (sealife.getDepth() != null) {
            if (!sealife.getDepth().equals("")) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.char_depth);
                linearLayout.setVisibility(View.VISIBLE);
                depth.setText(sealife.getDepth().trim());
            }
        }

        if (sealife.getScName() != null) {
            if (!sealife.getScName().equals("")) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.char_scname);
                linearLayout.setVisibility(View.VISIBLE);
                scname.setText(sealife.getScName().trim());
            }
        }

        if (sealife.getOrder() != null) {
            if (!sealife.getOrder().equals("")) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.char_order);
                linearLayout.setVisibility(View.VISIBLE);
                order.setText(sealife.getOrder().trim());
            }
        }

        if (sealife.getDistribution() != null) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.char_distribution);
            linearLayout.setVisibility(View.VISIBLE);
            distribution.setText(sealife.getDistribution().trim());
        }

        if (sealife.getScCLass() != null) {
            if (!sealife.getScCLass().equals("")) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.char_scclass);
                linearLayout.setVisibility(View.VISIBLE);
                scclass.setText(sealife.getScCLass().trim());
            }
        }

        if (sealife.getHabitat() != null) {
            if (!sealife.getHabitat().equals("")) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.char_habitat);
                linearLayout.setVisibility(View.VISIBLE);
                habitat.setText(sealife.getHabitat().trim());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public ImageLoadedCallback(ProgressBar progBar) {
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }

 /*   public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

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

}

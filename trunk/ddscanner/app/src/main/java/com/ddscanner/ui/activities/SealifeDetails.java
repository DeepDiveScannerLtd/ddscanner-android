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
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Sealife;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by lashket on 17.2.16.
 */
public class SealifeDetails extends AppCompatActivity {

    private TextView length, weight, depth, scname, order, distribution, scclass, habitat;
    private ImageView photo;
    private Sealife sealife;
    private String pathMedium;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private Drawable drawable;
    private Bitmap image;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ImageView backgroundImage;

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
        Picasso.with(this).load(pathMedium + sealife.getImage()).into(photo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(sealife.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;
        Picasso.with(this).load(pathMedium + sealife.getImage()).resize(Math.round(dpWidth), 460).centerCrop().into(backgroundImage);
        backgroundImage.setColorFilter(Color.parseColor("#99000000"), PorterDuff.Mode.SRC_ATOP);
        setContent();
       /* Picasso.with(this).load(pathMedium + sealife.getImage()).resize(Math.round(dpWidth), 460).centerCrop().into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                drawable = new BitmapDrawable(bitmap);
                drawable.setColorFilter(Color.parseColor("#99000000"), PorterDuff.Mode.SRC_ATOP);
                appBarLayout.setBackgroundDrawable(drawable);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });*/
      /*  image = getBitmapFromURL(pathMedium+sealife.getImage());
        if (image != null) {
            drawable = new BitmapDrawable(image);
            drawable.setColorFilter(Color.parseColor("#90000000"), PorterDuff.Mode.SRC_ATOP);
            appBarLayout.setBackgroundDrawable(drawable);
        }*/


    }

    public void findViews() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
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
    }

    private void setContent() {
        if (sealife.getLength() != null) {
            if (!sealife.getLength().equals("")) {
                System.out.println(sealife.getLength());
                length.setText(sealife.getLength());
                findViewById(R.id.char_length).setVisibility(View.VISIBLE);
            }
        }

        if (sealife.getWeight() != null) {
            if (!sealife.getWeight().equals("")) {
                System.out.println(sealife.getWeight());
                weight.setText(sealife.getWeight());
                findViewById(R.id.char_weight).setVisibility(View.VISIBLE);
            }
        }

        if (sealife.getDepth() != null) {
            if (!sealife.getDepth().equals("")) {
                System.out.println(sealife.getDepth());
                depth.setText(sealife.getDepth());
                findViewById(R.id.char_depth).setVisibility(View.VISIBLE);
            }
        }

        if (sealife.getScName() != null) {
            if (!sealife.getScName().equals("")) {
                System.out.println(sealife.getScName());
                scname.setText(sealife.getScName());
                findViewById(R.id.char_scname).setVisibility(View.VISIBLE);
            }
        }

        if (sealife.getOrder() != null) {
            if (!sealife.getOrder().equals("")) {
                System.out.println(sealife.getOrder());
                order.setText(sealife.getOrder());
                findViewById(R.id.char_order).setVisibility(View.VISIBLE);
            }
        }

        if (sealife.getDistribution() != null) {
            System.out.println(sealife.getDistribution());
            distribution.setText(sealife.getDistribution());
            findViewById(R.id.char_distribution).setVisibility(View.VISIBLE);
        }

        if (sealife.getScCLass() != null) {
            if (!sealife.getScCLass().equals("")) {
                System.out.println(sealife.getScCLass());
                scclass.setText(sealife.getScCLass());
                findViewById(R.id.char_scclass).setVisibility(View.VISIBLE);
            }
        }

        if (sealife.getHabitat() != null) {
            if (!sealife.getHabitat().equals("")) {
                System.out.println(sealife.getHabitat());
                habitat.setText(sealife.getHabitat());
                findViewById(R.id.char_habitat).setVisibility(View.VISIBLE);
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

}

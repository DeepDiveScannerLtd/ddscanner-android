package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.ddscanner.R;
import com.squareup.picasso.Picasso;

/**
 * Created by lashket on 28.4.16.
 */
public class ShowImageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String URL = "URL";

    private String imageUrl;

    private ImageView close;
    private ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        imageUrl = getIntent().getStringExtra(URL);
        findViews();
        setUiSetting();
    }

    /**
     * Find views in activity
     * @author Andrei Lashkevich
     */

    private void findViews() {
        close = (ImageView) findViewById(R.id.ic_close);
        image = (ImageView) findViewById(R.id.image);
    }

    /**
     * Set UI settings for views
     * @author Andrei Lashkevich
     */

    private void setUiSetting() {
        close.setOnClickListener(this);
        Picasso.with(this).load(imageUrl).into(image);
    }

    public static void show(Context context, String url) {
        Intent intent = new Intent(context, ShowImageActivity.class);
        intent.putExtra(URL, url);
        context.startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_close:
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
        }
    }
}

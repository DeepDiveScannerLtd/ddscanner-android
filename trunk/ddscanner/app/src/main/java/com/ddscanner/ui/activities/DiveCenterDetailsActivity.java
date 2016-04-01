package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.utils.EventTrackerHelper;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by lashket on 31.3.16.
 */
public class DiveCenterDetailsActivity extends AppCompatActivity {

    private DiveCenter diveCenter;
    private ImageView dc_logo;
    private TextView dc_name;
    private TextView dc_address;
    private TextView dc_phone;
    private TextView dc_email;
    private RelativeLayout addressLayout, phoneLayout, emailLayout;
    private Toolbar toolbar;
    private LinearLayout stars;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dive_center_details);
        diveCenter =getIntent().getParcelableExtra("DC");
        path = getIntent().getStringExtra("PATH");
        findViews();
        toolbarSettings();
        setContent();
    }

    private void setContent() {
        if (diveCenter.getLogo() != null) {
            Picasso.with(this).load(path + diveCenter.getLogo()).into(dc_logo);
        } else {
            dc_logo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.avatar_pr_dc_empty));
        }
        dc_name.setText(diveCenter.getName());

        if (diveCenter.getAddress() != null) {
            addressLayout.setVisibility(View.VISIBLE);
            dc_address.setText(diveCenter.getAddress());
        }

        if (diveCenter.getPhone() != null) {
            phoneLayout.setVisibility(View.VISIBLE);
            dc_phone.setText(diveCenter.getPhone());
            phoneLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                            EventTrackerHelper.EVENT_CALL_NUMBER_CLICK, new HashMap<String, Object>() {{
                                put(EventTrackerHelper.PARAM_CALL_NUMBER_CLICK, diveCenter.getId());
                            }});
                    try {
                        String uri = "tel:" + diveCenter.getPhone();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    } catch (Exception e) {

                    }
                }
            });
        }

        if (diveCenter.getEmail() != null) {
            emailLayout.setVisibility(View.VISIBLE);
            dc_email.setText(diveCenter.getEmail());
            emailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                            EventTrackerHelper.EVENT_WRITE_EMAIL_CLICK, new HashMap<String, Object>() {{
                                put(EventTrackerHelper.PARAM_WRITE_EMAIL_CLICK, diveCenter.getId());
                            }});
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{diveCenter.getEmail()});
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }
            });
        }

        for (int k = 0; k < diveCenter.getRating(); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(3, 0, 0, 0);
            stars.addView(iv);
        }
        for (int k = 0; k < 5 - diveCenter.getRating(); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_flag_empty_small);
            iv.setPadding(3, 0, 0, 0);
            stars.addView(iv);
        }

    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        dc_logo = (ImageView) findViewById(R.id.dc_logo);
        dc_name = (TextView) findViewById(R.id.dc_name);
        dc_address = (TextView) findViewById(R.id.dc_address);
        dc_phone = (TextView) findViewById(R.id.dc_phone);
        dc_email = (TextView) findViewById(R.id.dc_email);
        addressLayout = (RelativeLayout) findViewById(R.id.dc_address_layout);
        phoneLayout = (RelativeLayout) findViewById(R.id.dc_phone_layout);
        emailLayout = (RelativeLayout) findViewById(R.id.dc_email_layout);
        stars = (LinearLayout) findViewById(R.id.stars);
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

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dive center");
    }

    public static void show(Context context, DiveCenter diveCenter, String path) {
        Intent intent = new Intent(context, DiveCenterDetailsActivity.class);
        intent.putExtra("DC", diveCenter);
        intent.putExtra("PATH", path);
        context.startActivity(intent);
    }

}
package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

public class DiveCenterDetailsActivity extends AppCompatActivity {

    private DiveCenter diveCenter;
    private ImageView dc_logo;
    private TextView dc_name;
    private TextView dc_address;
    private TextView dc_phone;
    private TextView dc_email;
    private LinearLayout addressLayout, phoneLayout, emailLayout;
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
//                    EventsTracker.trackContactDiveCenter(EventsTracker.ContactDiveCenterMethod.PHONE_CALL);
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
//                    EventsTracker.trackContactDiveCenter(EventsTracker.ContactDiveCenterMethod.EMAIL);
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{diveCenter.getEmail()});
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }
            });
        }

        for (int k = 0; k < diveCenter.getRating(); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(10, 0, 0, 0);
            stars.addView(iv);
        }
        for (int k = 0; k < 5 - diveCenter.getRating(); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_list_star_empty);
            iv.setPadding(10, 0, 0, 0);
            stars.addView(iv);
        }

    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        dc_logo = findViewById(R.id.dc_logo);
        dc_name = findViewById(R.id.dc_name);
        dc_address = findViewById(R.id.dc_address);
        dc_phone = findViewById(R.id.dc_phone);
        dc_email = findViewById(R.id.dc_email);
        addressLayout = findViewById(R.id.dc_address_layout);
        phoneLayout = findViewById(R.id.dc_phone_layout);
        emailLayout = findViewById(R.id.dc_email_layout);
        stars = findViewById(R.id.stars);
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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.dive_center);
    }

    public static void show(Context context, DiveCenter diveCenter, String path, EventsTracker.SpotViewSource spotViewSource) {
        if (spotViewSource != null) {
//            EventsTracker.trackDiveCenterView(diveCenter.getId(), spotViewSource);
        }
        Intent intent = new Intent(context, DiveCenterDetailsActivity.class);
        intent.putExtra("DC", diveCenter);
        intent.putExtra("PATH", path);
        context.startActivity(intent);
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

}

package com.ddscanner.screens.sealife.details;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.ActivitySealifeFullBinding;
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.entities.Sealife;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;

public class SealifeDetailsActivity extends AppCompatActivity implements DialogClosedListener {

    public static final String EXTRA_SEALIFE = "SEALIFE";
    public static final String EXTRA_PATH = "PATH";
    private float dpWidth;
    private String id;

    private ActivitySealifeFullBinding binding;

    private DDScannerRestClient.ResultListener<Sealife> resultListener = new DDScannerRestClient.ResultListener<Sealife>() {
        @Override
        public void onSuccess(Sealife result) {
            binding.setSealifeViewModel(new SealifeViewModel(result, dpWidth, binding.progressBar));
            binding.progressView.setVisibility(View.GONE);
            binding.mainLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SEALIFE_ACTIVITY_FAILEED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SEALIFE_ACTIVITY_FAILEED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_SEALIFE_ACTIVITY_FAILEED_TO_CONNECT, false);
        }

    };

    public static void show(Context context, String id) {
        Intent intent = new Intent(context, SealifeDetailsActivity.class);
        intent.putExtra(EXTRA_SEALIFE, id);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sealife_full);
        DisplayMetrics outMetrics = new DisplayMetrics();
        id = getIntent().getStringExtra(EXTRA_SEALIFE);
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        dpWidth = outMetrics.widthPixels / density;
        binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.collapsingToolbarLayout.setTitle(getString(R.string.details));
                    isShow = true;
                } else if (isShow) {
                    binding.collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

        setSupportActionBar(binding.toolbar);
        //getSupportActionBar().setTitle(sealife.getName());
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        DDScannerApplication.getInstance().getDdScannerRestClient().getSealifeDetails(id, resultListener);
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

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}
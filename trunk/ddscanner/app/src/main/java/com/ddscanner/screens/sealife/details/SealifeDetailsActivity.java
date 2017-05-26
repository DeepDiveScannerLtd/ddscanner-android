package com.ddscanner.screens.sealife.details;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ActivitySealifeFullBinding;
import com.ddscanner.entities.Sealife;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.sealife.add.AddSealifeActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.google.gson.Gson;

public class SealifeDetailsActivity extends BaseAppCompatActivity implements DialogClosedListener {

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
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SEALIFE_ACTIVITY_FAILEED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SEALIFE_ACTIVITY_FAILEED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_SEALIFE_ACTIVITY_FAILEED_TO_CONNECT, false);
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

        EventsTracker.trackDiveSpotSealifeView();
        setupToolbar(R.string.empty_string, R.id.toolbar, R.menu.menu_sealife_details);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getSealifeDetails(id, resultListener);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.edit_sealife:
                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                    AddSealifeActivity.showForEdit(this, new Gson().toJson(binding.getSealifeViewModel().getSealife()), ActivitiesRequestCodes.REQUEST_CODE_SEALIFE_ACTIVITY_EDIT_SEALIFE);
                    return true;
                }
                LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_LOGINFOR_EDIT_SEALIFE);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SEALIFE_ACTIVITY_EDIT_SEALIFE:
                if (resultCode == RESULT_OK) {
                    binding.progressView.setVisibility(View.VISIBLE);
                    binding.mainLayout.setVisibility(View.GONE);
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).getSealifeDetails(id, resultListener);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_LOGINFOR_EDIT_SEALIFE:
                if (resultCode == RESULT_OK) {
                    AddSealifeActivity.showForEdit(this, new Gson().toJson(binding.getSealifeViewModel().getSealife()), ActivitiesRequestCodes.REQUEST_CODE_SEALIFE_ACTIVITY_EDIT_SEALIFE);
                }
                break;
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
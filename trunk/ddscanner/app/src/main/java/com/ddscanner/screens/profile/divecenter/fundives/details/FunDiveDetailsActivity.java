package com.ddscanner.screens.profile.divecenter.fundives.details;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.ActivityFunDiveDetailsBinding;
import com.ddscanner.entities.FunDiveDetails;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divecenter.request.SendRequestActivity;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;

public class FunDiveDetailsActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private static final String ARG_ID = "id";

    ActivityFunDiveDetailsBinding binding;

    private DDScannerRestClient.ResultListener<FunDiveDetails> resultListener = new DDScannerRestClient.ResultListener<FunDiveDetails>() {
        @Override
        public void onSuccess(FunDiveDetails result) {
            binding.setViewModel(new FunDiveDetailsActivityViewModel(result));
            binding.progressBar.setVisibility(View.GONE);
            binding.informationLayout.setVisibility(View.VISIBLE);
            binding.buttonShowDivecenters.setVisibility(View.VISIBLE);
            checkLines();
            toolbarSettings();
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, 1, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, 1, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, 1, false);
        }
    };

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, FunDiveDetailsActivity.class);
        intent.putExtra(ARG_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fun_dive_details);
        binding.setHandlers(this);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getFunDiveDetails(resultListener, getIntent().getLongExtra(ARG_ID, 0));
    }

    private void toolbarSettings() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle("");
        binding.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        binding.collapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(this, android.R.color.transparent));
        binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = binding.appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.collapsingToolbar.setTitle(binding.getViewModel().getFunDiveDetails().getName());
                    isShow = true;
                } else if (isShow) {
                    binding.collapsingToolbar.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void themeNavAndStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        w.setNavigationBarColor(ContextCompat.getColor(this ,android.R.color.transparent));
        w.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
    }

    public void bookNowClicked(View view) {
        SendRequestActivity.showForFunDive(this, binding.getViewModel().getFunDiveDetails().getId());
    }

    public void showMoreClicked(View view) {
        if (binding.description.isExpanded()) {
            binding.description.collapse();
            binding.showMore.setText(R.string.show_more);
        } else {
            binding.description.expand();
            binding.showMore.setText(R.string.show_less);
        }
    }

    public void showDiveCenter(View view) {
        UserProfileActivity.show(this, binding.getViewModel().getFunDiveDetails().getDiveCenterProfile().getId().toString(), 0);
    }

    private void checkLines() {
        ViewTreeObserver viewTreeObserver = binding.description.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(() -> {
            Layout l = binding.description.getLayout();
            if (l != null) {
                int lines = l.getLineCount();
                if (lines > 0) {
                    if (l.getEllipsisCount(lines - 1) > 0) {
                        binding.showMore.setVisibility(View.VISIBLE);
                    }
                }
            }

        });
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

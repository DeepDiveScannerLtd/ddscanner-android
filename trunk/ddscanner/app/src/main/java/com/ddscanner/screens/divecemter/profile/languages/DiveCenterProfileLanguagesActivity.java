package com.ddscanner.screens.divecemter.profile.languages;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.ActivityListWithProgressBinding;
import com.ddscanner.entities.Language;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;

import java.util.ArrayList;

public class DiveCenterProfileLanguagesActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private ActivityListWithProgressBinding binding;
    private static String ARG_ID = "ID";

    private DDScannerRestClient.ResultListener<ArrayList<Language>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<Language>>() {
        @Override
        public void onSuccess(ArrayList<Language> result) {
            binding.progressBar.setVisibility(View.GONE);
            binding.list.setVisibility(View.VISIBLE);
            binding.list.setLayoutManager(new LinearLayoutManager(DiveCenterProfileLanguagesActivity.this));
            binding.list.setAdapter(new com.ddscanner.screens.divecemter.profile.languages.DiveCenterLanguagesListAdapter(result));
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_LANGUAGES_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(DiveCenterProfileLanguagesActivity.this, ActivitiesRequestCodes.REQUEST_CODE_LANGUAGES_ACTIVITY_LOGIN);
                    break;
                default:
                    UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_LANGUAGES_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_LANGUAGES_ACTIVITY_FAILED_TO_CONNECT, false);
        }
    };

    public static void show(String id, Context context) {
        Intent intent = new Intent(context, DiveCenterProfileLanguagesActivity.class);
        intent.putExtra(ARG_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_with_progress);
        setupToolbar(R.string.languages, R.id.toolbar);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterLanguages(resultListener, getIntent().getStringExtra(ARG_ID));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_LANGUAGES_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterLanguages(resultListener, getIntent().getStringExtra(ARG_ID));
                    break;
                }
                finish();
                break;
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}

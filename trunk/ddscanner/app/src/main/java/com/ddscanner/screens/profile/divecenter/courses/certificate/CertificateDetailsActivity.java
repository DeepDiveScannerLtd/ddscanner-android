package com.ddscanner.screens.profile.divecenter.courses.certificate;


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
import com.ddscanner.databinding.ActivityCertificateDetailsBinding;
import com.ddscanner.entities.Certificate;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;

public class CertificateDetailsActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";

    public static void show(Context context, long id, String name) {
        Intent intent = new Intent(context, CertificateDetailsActivity.class);
        intent.putExtra(ARG_ID, id);
        intent.putExtra(ARG_NAME, name);
        context.startActivity(intent);
    }

    DDScannerRestClient.ResultListener<Certificate> resultListener = new DDScannerRestClient.ResultListener<Certificate>() {
        @Override
        public void onSuccess(Certificate result) {
            binding.setViewModel(new CerificateDetailsActivityViewModel(result));
            setUi();
            binding.progressView.setVisibility(View.GONE);
            binding.content.setVisibility(View.VISIBLE);
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

    ActivityCertificateDetailsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_certificate_details);
        setupToolbar(getIntent().getStringExtra(ARG_NAME), R.id.toolbar);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getCertificate(resultListener, getIntent().getLongExtra(ARG_ID, -1));
    }

    private void setUi() {
        if (binding.getViewModel().getCertificate().getRequiredCertificates() != null) {
            RequiredCertificatesListAdapter requiredCertificatesListAdapter = new RequiredCertificatesListAdapter(item -> CertificateDetailsActivity.show(this, item.getId(), item.getName()));
            requiredCertificatesListAdapter.setCertificates(binding.getViewModel().getCertificate().getRequiredCertificates());
            binding.certificatesList.setLayoutManager(new LinearLayoutManager(this));
            binding.certificatesList.setNestedScrollingEnabled(false);
            binding.certificatesList.setAdapter(requiredCertificatesListAdapter);
        }
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

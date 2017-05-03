package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;

public class ForgotPasswordActivity extends BaseAppCompatActivity implements View.OnClickListener, DialogClosedListener{

    private Toolbar toolbar;
    private EditText email;
    private Button buttonSend;
    private MaterialDialog materialDialog;

    private DDScannerRestClient.ResultListener<Void> resultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            //TODO change text
            materialDialog.dismiss();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.sorry, R.string.success_added, DialogsRequestCodes.DRC_FORGOT_PASSWORD_ACTIVITY_SUCCESS, false);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case BAD_REQUEST_ERROR_400:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.sorry, R.string.success_added, false);
                    break;
                case ENTITY_NOT_FOUND_404:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.sorry, R.string.success_added, false);
                    break;
                default:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    };

    public static void show(Context context) {
        Intent intent = new Intent(context, ForgotPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        findViews();
    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        email = (EditText) findViewById(R.id.email);
        buttonSend = (Button) findViewById(R.id.send);
        buttonSend.setOnClickListener(this);
        setupToolbar(R.string.forgot_password, R.id.toolbar);
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                if (isEmailValid(email.getText().toString())) {
                    materialDialog.show();
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).postForgotPassword(email.getText().toString(), resultListener);
                } else {
                    //TODO change text
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.sorry, R.string.sc_name, false);
                }
                break;
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}

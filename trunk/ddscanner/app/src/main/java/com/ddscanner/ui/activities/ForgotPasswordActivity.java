package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends BaseAppCompatActivity implements View.OnClickListener, DialogClosedListener{

    private Toolbar toolbar;
    private EditText email;
    private Button buttonSend;

    private DDScannerRestClient.ResultListener<Void> resultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            //TODO change text
            UserActionInfoDialogFragment.showForActivityResult(ForgotPasswordActivity.this, R.string.sorry, R.string.success_added, DialogsRequestCodes.DRC_FORGOT_PASSWORD_ACTIVITY_SUCCESS);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case BAD_REQUEST_ERROR_400:
                    UserActionInfoDialogFragment.show(ForgotPasswordActivity.this, R.string.sorry, R.string.success_added);
                    break;
                case ENTITY_NOT_FOUND_404:
                    UserActionInfoDialogFragment.show(ForgotPasswordActivity.this, R.string.sorry, R.string.success_added);
                    break;
                default:
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        email = (EditText) findViewById(R.id.email);
        buttonSend = (Button) findViewById(R.id.send);
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
                    DDScannerApplication.getInstance().getDdScannerRestClient().postForgotPassword(email.getText().toString(), resultListener);
                } else {
                    //TODO change text
                    UserActionInfoDialogFragment.show(this, R.string.sorry, R.string.sc_name);
                }
                break;
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}

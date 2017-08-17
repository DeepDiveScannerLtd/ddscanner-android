package com.ddscanner.screens.profile.edit;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ActivityChangePasswordBinding;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.ForgotPasswordActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.Helpers;

public class ChangePasswordActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private enum InputPasswordType {
        OLD, NEW, NEW_CONFIRM
    }

    private boolean isOldPasswordEntered = false;
    private boolean isNewPasswordEntered = false;
    private boolean isNewConfirmationPasswordEntered = false;
    private MaterialDialog materialDialog;

    private DDScannerRestClient.ResultListener<Void> passwordChangeResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            Toast.makeText(ChangePasswordActivity.this, R.string.password_changed, Toast.LENGTH_SHORT).show();
            finish();
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
                case ENTITY_NOT_FOUND_404:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_dialog_titile, R.string.old_password_incorrect, false);
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

    ActivityChangePasswordBinding binding;

    public static void show(Context context) {
        Intent intent  = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        materialDialog = Helpers.getMaterialDialog(this);
        binding.forgotPassword.setOnClickListener(this);
        updateButton();
        setupToolbar(R.string.change_password_edit, R.id.toolbar);
        binding.currentPassword.addTextChangedListener(new PasswordsTextWatcher(InputPasswordType.OLD));
        binding.newPassword.addTextChangedListener(new PasswordsTextWatcher(InputPasswordType.NEW));
        binding.newPasswordRepeat.addTextChangedListener(new PasswordsTextWatcher(InputPasswordType.NEW_CONFIRM));
    }

    private void updateButton() {
        if (isOldPasswordEntered && isNewConfirmationPasswordEntered && isNewPasswordEntered) {
            binding.save.setTextColor(ContextCompat.getColor(this, R.color.black_text));
            binding.save.setOnClickListener(this);
        } else {
            binding.save.setTextColor(ContextCompat.getColor(this, R.color.empty_login_button_text));
            binding.save.setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                if (checkNewPassword()) {
                    materialDialog.show();
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).postChangeUserPassword(passwordChangeResultListener, binding.currentPassword.getText().toString(), binding.newPassword.getText().toString());
                    break;
                }
                UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_dialog_titile, R.string.passwords_do_not_match, false);
                break;
            case R.id.forgot_password:
                ForgotPasswordActivity.show(this);
                break;
        }
    }

    private boolean checkNewPassword() {
        return binding.newPassword.getText().toString().equals(binding.newPasswordRepeat.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }

    private class PasswordsTextWatcher implements TextWatcher {

        private InputPasswordType type;

        PasswordsTextWatcher(InputPasswordType type) {
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (type) {
                case OLD:
                    if (binding.currentPassword.getText().length() > 3) {
                        isOldPasswordEntered = true;
                        break;
                    }
                    isOldPasswordEntered = false;
                    break;
                case NEW:
                    if (binding.newPassword.getText().length() > 3) {
                        isNewPasswordEntered = true;
                        break;
                    }
                    isNewPasswordEntered = false;
                    break;
                case NEW_CONFIRM:
                    if (binding.newPasswordRepeat.getText().length() > 3) {
                        isNewConfirmationPasswordEntered = true;
                        break;
                    }
                    isNewConfirmationPasswordEntered = false;
                    break;
            }
            updateButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}

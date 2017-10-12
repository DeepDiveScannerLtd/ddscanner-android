package com.ddscanner.screens.divecenter.request;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.MenuItem;

import com.ddscanner.R;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.views.PhoneInputView;
import com.ddscanner.utils.Helpers;

public class SendRequestActivity extends BaseAppCompatActivity {

    DiveCenterRequestInputView nameInputView;
    DiveCenterRequestInputView emailInputView;
    DiveCenterRequestInputView messageInputView;
    PhoneInputView phoneInputView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        setupToolbar(R.string.send_request, R.id.toolbar, R.menu.menu_send_request);
        nameInputView = findViewById(R.id.name_input);
        emailInputView = findViewById(R.id.email_input);
        emailInputView.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        messageInputView = findViewById(R.id.message_input);
        phoneInputView = findViewById(R.id.phone_input);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.send_request:
                sendRequest();
                return true;
        }
        return false;
    }

    private void sendRequest() {
        if (isDataValid()) {

        }
    }

    private boolean isDataValid() {
        nameInputView.hideError();
        emailInputView.hideError();
        messageInputView.hideError();
        phoneInputView.hideError();
        boolean isDataValid = true;
        if (nameInputView.getInputText().trim().isEmpty()) {
            isDataValid = false;
            nameInputView.showError(R.string.name_is_required);
        }
        if (emailInputView.getInputText().trim().isEmpty()) {
            isDataValid = false;
            emailInputView.showError(R.string.email_is_required);
        } else {
            if (!Helpers.checkEmail(emailInputView.getInputText().trim())) {
                isDataValid = false;
                emailInputView.showError(R.string.email_incorrect);
            }
        }
        if (messageInputView.getInputText().trim().isEmpty()) {
            isDataValid = false;
            messageInputView.showError(R.string.message_is_required);
        }
        if (!Helpers.validCellPhone(phoneInputView.getPhoneWithPlus(), phoneInputView.getCountryName())) {
            isDataValid = false;
            phoneInputView.setError();
        }
        return isDataValid;
    }

}

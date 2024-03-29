package com.ddscanner.screens.divecenter.request;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.User;
import com.ddscanner.entities.request.DiveCenterRequestBookingRequest;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.views.PhoneInputView;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;

public class SendRequestActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private static final String ARG_PRODUCT_ID = "product_id";
    private static final String ARG_SOURCE = "source";

    DDScannerRestClient.ResultListener<User> userResultListener = new DDScannerRestClient.ResultListener<User>() {
        @Override
        public void onSuccess(User result) {
            materialDialog.dismiss();
            nameInputView.setText(result.getName());
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
        }
    };

    DDScannerRestClient.ResultListener<Void> resultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            EventsTracker.trackBookingRequestSent();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.success_booking_dialog_title, R.string.success_booking_dialog_text, 1, false);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    };

    DiveCenterRequestInputView nameInputView;
    DiveCenterRequestInputView emailInputView;
    DiveCenterRequestInputView messageInputView;
    PhoneInputView phoneInputView;
    String diveSpotId;
    String diveCenterId;
    long courseId;
    MaterialDialog materialDialog;
    boolean isForProduct = false;
    long productId;
    RequestSource requestSource;
    private long funDiveId;

    enum RequestSource {
        PRODUCT, NONE, FUNDIVE, COURSE
    }

    public static void show(Context context, String diveSpotId, int diveCenterId) {
        Intent intent = new Intent(context, SendRequestActivity.class);
        intent.putExtra("dc_id", String.valueOf(diveCenterId));
        intent.putExtra("ds_id", diveSpotId);
        intent.putExtra(ARG_SOURCE, RequestSource.NONE);
        EventsTracker.trackBookingRequestView();
        context.startActivity(intent);
    }

    public static void showForProduct(Context context, long productId) {
        Intent intent = new Intent(context, SendRequestActivity.class);
        intent.putExtra(ARG_PRODUCT_ID, productId);
        intent.putExtra(ARG_SOURCE, RequestSource.PRODUCT);
        context.startActivity(intent);
    }

    public static void showForFunDive(Context context, long funDiveId) {
        Intent intent = new Intent(context, SendRequestActivity.class);
        intent.putExtra(ARG_PRODUCT_ID, funDiveId);
        intent.putExtra(ARG_SOURCE, RequestSource.FUNDIVE);
        context.startActivity(intent);
    }

    public static void showForCourse(Context context, long funDiveId) {
        Intent intent = new Intent(context, SendRequestActivity.class);
        intent.putExtra(ARG_PRODUCT_ID, funDiveId);
        intent.putExtra(ARG_SOURCE, RequestSource.FUNDIVE);
        context.startActivity(intent);
    }

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
        requestSource = (RequestSource) getIntent().getSerializableExtra(ARG_SOURCE);
        switch (requestSource) {
            case NONE:
                diveCenterId = getIntent().getStringExtra("dc_id");
                diveSpotId = getIntent().getStringExtra("ds_id");
                break;
            case FUNDIVE:
                funDiveId = getIntent().getLongExtra(ARG_PRODUCT_ID, -1);
                break;
            case PRODUCT:
                productId = getIntent().getLongExtra(ARG_PRODUCT_ID, -1);
                break;
            case COURSE:
                courseId = getIntent().getLongExtra(ARG_PRODUCT_ID, -1);
                break;
        }
        materialDialog = Helpers.getMaterialDialog(this);
        materialDialog.show();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserSelfInformation(userResultListener);
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
            materialDialog.show();
            DDScannerApplication.getInstance().getDdScannerRestClient(this).requestBooking(centerRequestBookingRequest(), resultListener);
        }
    }

    private DiveCenterRequestBookingRequest centerRequestBookingRequest() {
        DiveCenterRequestBookingRequest diveCenterRequestBookingRequest = new DiveCenterRequestBookingRequest();
        switch (requestSource) {
            case PRODUCT:
                diveCenterRequestBookingRequest.setProducId(productId);
                break;
            case FUNDIVE:
                diveCenterRequestBookingRequest.setFunDiveId(funDiveId);
                break;
            case NONE:
                diveCenterRequestBookingRequest.setDiveCenterId(diveCenterId);
                diveCenterRequestBookingRequest.setDiveSpotId(diveSpotId);
                break;
            case COURSE:
                diveCenterRequestBookingRequest.setCourseId(courseId);
                break;
        }

        diveCenterRequestBookingRequest.setName(nameInputView.getInputText());
        diveCenterRequestBookingRequest.setPhone(phoneInputView.getPhoneWithPlus());
        diveCenterRequestBookingRequest.setEmail(emailInputView.getInputText());
        diveCenterRequestBookingRequest.setMessage(messageInputView.getInputText());
        diveCenterRequestBookingRequest.setUserId(SharedPreferenceHelper.getActiveUser().getId());
        return diveCenterRequestBookingRequest;
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
        if (messageInputView.getInputText().length() < 10) {
            isDataValid = false;
            messageInputView.showError(R.string.message_error_length);
        }
        return isDataValid;
    }

    @Override
    public void onDialogClosed(int requestCode) {
        Intent intent  = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

package com.ddscanner.screens.user.profile;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.ActivityUserProfileBinding;
import com.ddscanner.entities.User;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.profile.ProfileFragmentViewModel;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;

public class UserProfileActivity extends AppCompatActivity implements InfoDialogFragment.DialogClosedListener{

    private DDScannerRestClient.ResultListener<User> resultListener = new DDScannerRestClient.ResultListener<User>() {
        @Override
        public void onSuccess(User result) {
            binding.setUserProfileViewModel(new ProfileFragmentViewModel(result));
            binding.progressView.setVisibility(View.GONE);
            binding.about.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT, false);
        }
    };

    private ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userId = getIntent().getStringExtra("id");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);
        DDScannerApplication.getInstance().getDdScannerRestClient().getUserProfileInformation(userId, resultListener);
    }

    public static void show(Context context, String userId) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra("id", userId);
        context.startActivity(intent);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}

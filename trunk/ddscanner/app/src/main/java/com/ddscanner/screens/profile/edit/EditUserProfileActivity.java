package com.ddscanner.screens.profile.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ActivityEditProfileBinding;
import com.ddscanner.entities.User;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.profile.edit.divecenter.search.SearchDiveCenterActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.adapters.DiverLevelSpinnerAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.Helpers;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class EditUserProfileActivity extends BaseAppCompatActivity implements BaseAppCompatActivity.PictureTakenListener, ConfirmationDialogClosedListener {

    private static final String ARG_ISLOGOUT = "IS_LOGOUT";
    private static final String ARG_USER = "USER";
    private static final String ARG_DC_ID = "id";
    private static final String ARG_DC_NAME = "name";

    private ActivityEditProfileBinding binding;
    private User user;
    private Gson gson = new Gson();
    private ArrayList<String> levels = new ArrayList<>();
    private ColorStateList colorStateList;
    private MaterialDialog materialDialog;
    private String pathToUploadedPhoto = null;
    private RequestBody requestName, requestAbout, requestSkill;
    private MultipartBody.Part image;
    private Map<String, TextView> errorsMap = new HashMap<>();
    private static final int MAX_LENGTH_NAME = 32;
    private static final int MAX_LENGTH_ABOUT = 255;
    private boolean isAboutChanged = false;
    private boolean isNamChanged = false;
    private AppCompatRadioButton diverRadio;
    private AppCompatRadioButton insructorRadio;
    private RequestBody diveCenterId = null;
    private RequestBody diveCenterTypeRequestBody = null;
    private String dcId;
    private File cameraPhotoToUpload = null;
    private int diveCenterType;


    private DDScannerRestClient.ResultListener<Void> updateProfileInfoResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void v) {
            setResult(RESULT_OK);
            EventsTracker.trackProfileEdited();
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
                case BAD_REQUEST_ERROR_400:
                    Helpers.errorHandling(errorsMap, errorMessage);
                    break;
                default:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.unexcepted_error_title, R.string.unexcepted_error_text, false);
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    public static void showForResult(Activity context, String userData, int requestCode) {
        Intent intent = new Intent(context, EditUserProfileActivity.class);
        intent.putExtra(ARG_USER, userData);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = gson.fromJson(getIntent().getStringExtra(ARG_USER), User.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        binding.setProfileViewModel(new EditProfileActivityViewModel(user));
        binding.setHandlers(this);
        setupToolbar(R.string.edit_profile_activity, R.id.toolbar);

        binding.nameCount.setVisibility(View.GONE);
        createErrorsMap();
        colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        ContextCompat.getColor(this, R.color.radio_button_empty)
                        , ContextCompat.getColor(this, R.color.radio_button_fill),
                }
        );
        setupUi();
    }

    private void setupUi() {
        if (user.getType() == 2) {
            dcId = String.valueOf(user.getDiveCenter().getId());
            diveCenterType = user.getDiveCenter().getType();
        }
        materialDialog = Helpers.getMaterialDialog(this);
        if (user.getUserTypeString().equals("Diver")) {
            setupRadioButtons("Diver", true, false);
            setupRadioButtons("Instructor", false, false);
        } else {
            setupRadioButtons("Diver", false, false);
            setupRadioButtons("Instructor", true, true);
        }
        levels.add("Diver level");
        levels.addAll(Helpers.getDiveLevelTypes());
        binding.levelSpinner.setAdapter(new DiverLevelSpinnerAdapter(this, R.layout.spinner_item, levels, "Diver level"));
        if (user.getDiverLevel() != null) {
            binding.levelSpinner.setSelection(user.getDiverLevel());
        } else {
            binding.levelSpinner.setSelection(1);
        }
        binding.radiogroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (diverRadio.isChecked()) {
                binding.chooseDiveCenterBtn.setVisibility(View.GONE);
                binding.levelLayout.setVisibility(View.VISIBLE);
            } else {
                binding.chooseDiveCenterBtn.setVisibility(View.VISIBLE);
                binding.levelLayout.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void setupRadioButtons(String title, boolean isActive, boolean isShowChoseDiveCenter) {
        AppCompatRadioButton button = new AppCompatRadioButton(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setSupportButtonTintList(colorStateList);
        button.setText(title);
        if (title.equals("Diver")) {
            diverRadio = button;
        } else {
            insructorRadio = button;
        }

        button.setPadding(Math.round(Helpers.convertDpToPixel(16, this)), Math.round(Helpers.convertDpToPixel(10, this)), Math.round(Helpers.convertDpToPixel(22, this)), Math.round(Helpers.convertDpToPixel(10, this)));
        binding.radiogroup.addView(button);
        if (isActive) {
            binding.radiogroup.check(button.getId());
            if (!title.equals("Diver")) {
                diveCenterId = Helpers.createRequestBodyForString(String.valueOf(binding.getProfileViewModel().getUser().getDiveCenter().getId()));
            }
        }
        if (isShowChoseDiveCenter) {
            binding.levelLayout.setVisibility(View.GONE);
            binding.chooseDiveCenterBtn.setVisibility(View.VISIBLE);
        }
    }

    private void sendUpdateRequest() {
        createErrorsMap();
        if (!isDataValid()) {
            return;
        }
        materialDialog.show();
        if (dcId != null) {
            diveCenterId = Helpers.createRequestBodyForString(dcId);
            diveCenterTypeRequestBody = Helpers.createRequestBodyForString(String.valueOf(diveCenterType));
        }
        if (pathToUploadedPhoto != null) {
            File file = new File(pathToUploadedPhoto);
            file = Helpers.compressFile(file, this);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }
        if (cameraPhotoToUpload != null) {
            File file = cameraPhotoToUpload;
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }
        if (diverRadio.isChecked()) {
            diveCenterId = null;
            diveCenterTypeRequestBody = null;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient(this).potUpdateUserProfile(updateProfileInfoResultListener, image, Helpers.createRequestBodyForString(binding.fullName.getText().toString().trim()), Helpers.createRequestBodyForString(binding.aboutEdit.getText().toString().trim()), Helpers.createRequestBodyForString(String.valueOf(levels.indexOf(binding.levelSpinner.getSelectedItem()))), diveCenterId, diveCenterTypeRequestBody);
    }

    private void hideErrorsMap() {
        for (TextView textView : errorsMap.values()) {
            textView.setVisibility(View.GONE);
        }
    }

    private boolean isDataValid() {
        boolean isDataValid = true;
        binding.errorName.setVisibility(View.GONE);
        if (binding.fullName.getText().toString().trim().length() < 1) {
            isDataValid = false;
            binding.errorName.setVisibility(View.VISIBLE);
        }
        return isDataValid;
    }


    /*Called when user clicked save changes button*/
    public void saveChanges(View view) {
        sendUpdateRequest();
    }

    /*Called when user clicked pick photo from gallery button*/
    public void pickPhotoFromGallery(View view) {
        pickSinglePhotoFromGallery();
    }

    /*Called when user try to pick photo from camera*/
    public void capturePhoto(View view) {
        pickPhotoFromCamera();
    }

    public void chooseDiveCenter(View view) {
        SearchDiveCenterActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_PROFILE_ACTIVITY_CHOOSE_DIVE_CENTER, true);
//        SearchDiveCenterActivityOld.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_PROFILE_ACTIVITY_CHOOSE_DIVE_CENTER, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public void onPictureFromCameraTaken(File picture) {
        this.pathToUploadedPhoto = null;
        this.cameraPhotoToUpload = picture;
        Picasso.with(this).load(picture)
                .resize(Math.round(Helpers.convertDpToPixel(80, this)),
                        Math.round(Helpers.convertDpToPixel(80, this))).centerCrop()
                .transform(new CropCircleTransformation()).into(binding.userChosedPhoto);
    }

    @Override
    public void onPicturesTaken(ArrayList<String> pictures) {
        this.cameraPhotoToUpload = null;
        pathToUploadedPhoto = pictures.get(0);
        Picasso.with(this).load("file://" + pictures.get(0))
                .resize(Math.round(Helpers.convertDpToPixel(80, this)),
                        Math.round(Helpers.convertDpToPixel(80, this))).centerCrop()
                .transform(new CropCircleTransformation()).into(binding.userChosedPhoto);
    }

    private void createErrorsMap() {
        errorsMap.put("name", binding.errorName);
        errorsMap.put("about", binding.errorAbout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_PROFILE_ACTIVITY_CHOOSE_DIVE_CENTER:
                if (resultCode == RESULT_OK) {
                    dcId = String.valueOf(data.getIntExtra(Constants.ARG_ID, 0));
                    binding.diveCenterName.setText(data.getStringExtra(Constants.ARG_DC_NAME));
                    diveCenterType = data.getIntExtra(Constants.ARG_DC_TYPE, 0);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DialogHelpers.showDialogAfterChangesInActivity(getSupportFragmentManager());
    }

    @Override
    public void onNegativeDialogClicked() {

    }

    @Override
    public void onPositiveDialogClicked() {
        finish();
    }

    public void changePassword(View view) {
        ChangePasswordActivity.show(this);
    }

}

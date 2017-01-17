package com.ddscanner.screens.profile.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.ActivityEditProfileBinding;
import com.ddscanner.entities.User;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.profile.edit.divecenter.search.SearchDiveCenterActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.adapters.DiverLevelSpinnerAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
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


public class EditUserProfileActivity extends BaseAppCompatActivity implements BaseAppCompatActivity.PictureTakenListener {

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
    private static final int MAX_LENGTH_NAME = 30;
    private static final int MAX_LENGTH_ABOUT = 250;
    private boolean isAboutChanged = false;
    private boolean isNamChanged = false;
    private AppCompatRadioButton diverRadio;
    private AppCompatRadioButton insructorRadio;
    private RequestBody diveCenterId = null;
    private String dcId;
    private File cameraPhotoToUpload = null;


    private DDScannerRestClient.ResultListener<Void> updateProfileInfoResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void v) {
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    public static void showForResult(Activity context, String userData, int requestCode) {
        Intent intent = new Intent(context, EditUserProfileActivity.class);
        intent.putExtra("user", userData);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        binding.setProfileViewModel(new EditProfileActivityViewModel(user));
        binding.setHandlers(this);
        setupToolbar(R.string.edit_profile_activity, R.id.toolbar, R.menu.edit_profile_menu);
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
        binding.aboutEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isAboutChanged) {
                    binding.aboutCount.setVisibility(View.VISIBLE);
                }
                if (MAX_LENGTH_ABOUT - binding.aboutEdit.length() < 10) {
                    binding.aboutCount.setTextColor(ContextCompat.getColor(EditUserProfileActivity.this, R.color.tw__composer_red));
                } else {
                    binding.aboutCount.setTextColor(Color.parseColor("#b2b2b2"));
                }
                binding.aboutCount.setText(String.valueOf(MAX_LENGTH_ABOUT - binding.aboutEdit.length()));
                isAboutChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isNamChanged) {
                    binding.nameCount.setVisibility(View.VISIBLE);
                }
                if (MAX_LENGTH_NAME - binding.fullName.length() < 10) {
                    binding.nameCount.setTextColor(ContextCompat.getColor(EditUserProfileActivity.this, R.color.tw__composer_red));
                } else {
                    binding.nameCount.setTextColor(Color.parseColor("#b2b2b2"));
                }
                binding.nameCount.setText(String.valueOf(MAX_LENGTH_NAME - binding.fullName.length()));
                isNamChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        binding.levelSpinner.setAdapter(new DiverLevelSpinnerAdapter(this, R.layout.spinner_item, levels));
        if (user.getDiverLevel() != null) {
            binding.levelSpinner.setSelection(user.getDiverLevel());
        } else {
            binding.levelSpinner.setSelection(1);
        }
        binding.radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (diverRadio.isChecked()) {
                    binding.chooseDiveCenterBtn.setVisibility(View.GONE);
                } else {
                    binding.chooseDiveCenterBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupRadioButtons(String title, boolean isActive, boolean isShowChoseDiveCenter) {
        AppCompatRadioButton button = new AppCompatRadioButton(this);
        button.setSupportButtonTintList(colorStateList);
        button.setText(title);
        if (title.equals("Diver")) {
            diverRadio = button;
        } else {
            insructorRadio = button;
        }
        button.setPadding(Math.round(Helpers.convertDpToPixel(15, this)), Math.round(Helpers.convertDpToPixel(10, this)), Math.round(Helpers.convertDpToPixel(20, this)), Math.round(Helpers.convertDpToPixel(10, this)));
        binding.radiogroup.addView(button);
        if (isActive) {
            binding.radiogroup.check(button.getId());
        }
        if (isShowChoseDiveCenter) {
            binding.chooseDiveCenterBtn.setVisibility(View.VISIBLE);
        }
    }

    private void sendUpdateRequest() {
        materialDialog.show();
        if (dcId != null) {
            diveCenterId = Helpers.createRequestBodyForString(dcId);
        }
        if (pathToUploadedPhoto != null) {
            File file = new File(pathToUploadedPhoto);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }
        if (cameraPhotoToUpload != null) {
            File file = cameraPhotoToUpload;
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().potUpdateUserProfile(updateProfileInfoResultListener, image, Helpers.createRequestBodyForString(binding.fullName.getText().toString()), Helpers.createRequestBodyForString(binding.aboutEdit.getText().toString()), Helpers.createRequestBodyForString(String.valueOf(levels.indexOf(binding.levelSpinner.getSelectedItem()))), diveCenterId);
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
        SearchDiveCenterActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_PROFILE_ACTIVITY_CHOOSE_DIVE_CENTER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.logout:
                setResult(RESULT_CODE_PROFILE_LOGOUT);
                finish();
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
                    dcId = data.getStringExtra("id");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DialogHelpers.showDialogAfterChanging(R.string.dialog_leave_title, R.string.dialog_leave_review_message, this, this);
    }
}

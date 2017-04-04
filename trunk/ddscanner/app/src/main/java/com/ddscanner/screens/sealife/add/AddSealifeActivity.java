package com.ddscanner.screens.sealife.add;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ActivityAddSealifeBinding;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.SealifeTranslation;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.Helpers;
import com.google.gson.Gson;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddSealifeActivity extends BaseAppCompatActivity implements View.OnClickListener, BaseAppCompatActivity.PictureTakenListener, ConfirmationDialogClosedListener {

    private static final String TAG = AddSealifeActivity.class.getSimpleName();
    private static final String ARG_SEALIFE = "seaife";
    private static final String ARG_IS_EDIT = "is_edit";
    private String filePath = null;

    private ActivityAddSealifeBinding binding;
    private MaterialDialog progressDialogUpload;
    private File fileToSend;
    private boolean isForEdit;

    private Map<String, TextView> errorsMap = new HashMap<>();

    private DDScannerRestClient.ResultListener<SealifeShort> sealifeResultListener = new DDScannerRestClient.ResultListener<SealifeShort>() {
        @Override
        public void onSuccess(SealifeShort result) {
            Intent intent = new Intent();
            intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE, result);
            setResult(RESULT_OK, intent);
            EventsTracker.trackSealifeCreated();
            finish();
        }

        @Override
        public void onConnectionFailure() {
            progressDialogUpload.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            progressDialogUpload.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(AddSealifeActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_LOGIN_TO_SEND);
                    break;
                case BAD_REQUEST_ERROR_400:
                    Helpers.errorHandling(errorsMap, errorMessage);
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    };

    private DDScannerRestClient.ResultListener<Void> updateResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            progressDialogUpload.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            progressDialogUpload.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(AddSealifeActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_LOGIN_TO_SEND);
                    break;
                case BAD_REQUEST_ERROR_400:
                    Helpers.errorHandling(errorsMap, errorMessage);
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_sealife);
        binding.setHandlers(this);
        isForEdit = getIntent().getBooleanExtra(ARG_IS_EDIT, false);
        if (isForEdit) {
            binding.setSealifeViewModel(new EditSealifeActivityViewModel(new Gson().fromJson(getIntent().getStringExtra(ARG_SEALIFE), Sealife.class)));
            if (binding.getSealifeViewModel().getSealife().getImage() != null) {
                setBackImage(binding.getSealifeViewModel().getSealife().getImage(), true);
            }
        }
        makeErrorsMap();
        setupToolbar(R.string.add_sealife, R.id.toolbar);
        EventsTracker.trackSealifeCreation();
        progressDialogUpload = Helpers.getMaterialDialog(this);
    }

    public static void showForEdit(Activity context, String sealife, int requestCode) {
        Intent intent = new Intent(context, AddSealifeActivity.class);
        intent.putExtra(ARG_IS_EDIT, true);
        intent.putExtra(ARG_SEALIFE, sealife);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_LOGIN_TO_SEND:
                if (resultCode == RESULT_OK) {
                    createRequestBody();
                }
                break;
        }
    }

    private void setBackImage(String path, boolean isServerPhoto) {
        if (!isServerPhoto) {
            if (!path.contains(Constants.images) && !path.contains("file:")) {
                path = "file://" + path;
            }
        } else {
            path = DDScannerApplication.getInstance().getString(R.string.base_photo_url, path, "2");
        }
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        binding.sealifePhoto.setVisibility(View.VISIBLE);
        Picasso.with(this).load(path).resize(Math.round(Helpers.convertDpToPixel(dpWidth, this)), Math.round(Helpers.convertDpToPixel(230, this))).centerCrop().into(binding.sealifePhoto);
        binding.addPhotoCenterLayout.setVisibility(View.GONE);
        binding.deletePhoto.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo_layout:
                pickSinglePhotoFromGallery();
                break;
            case R.id.delete_photo:
//                sealifePhoto.setImageDrawable(null);
//                addPhoto.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//                btnDelete.setVisibility(View.GONE);
//                centerLayout.setVisibility(View.VISIBLE);
//                addPhoto.setOnClickListener(this);
                break;
            case R.id.btn_save_sealife:
                createRequestBody();
                break;
        }
    }

    private void createRequestBody() {
        hideErrorsFields();
        if (!validateData()) {
            progressDialogUpload.show();
            ArrayList<SealifeTranslation> sealifeTranslations = new ArrayList<>();
            SealifeTranslation sealifeTranslation = new SealifeTranslation();
            sealifeTranslation.setLangCode("en");
            sealifeTranslation.setDepth(binding.depth.getText().toString().trim());
            sealifeTranslation.setDistribution(binding.distribution.getText().toString().trim());
            sealifeTranslation.setLength(binding.length.getText().toString().trim());
            sealifeTranslation.setWeight(binding.weight.getText().toString().trim());
            sealifeTranslation.setScName(binding.scName.getText().toString().trim());
            sealifeTranslation.setOrder(binding.order.getText().toString().trim());
            sealifeTranslation.setSealifeClass(binding.scClass.getText().toString().trim());
            sealifeTranslation.setHabitat(binding.habitat.getText().toString().trim());
            sealifeTranslation.setName(binding.name.getText().toString().trim());
            sealifeTranslations.add(sealifeTranslation);
            MultipartBody.Part body = null;
            if (filePath != null) {
                fileToSend = new File(filePath);
                fileToSend = Helpers.compressFile(fileToSend, this);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileToSend);
                body = MultipartBody.Part.createFormData("photo", fileToSend.getName(), requestFile);
            }
            if (!isForEdit) {
                DDScannerApplication.getInstance().getDdScannerRestClient().postAddSealife(sealifeResultListener, body, Helpers.createRequestBodyForString(new Gson().toJson(sealifeTranslations)));
            } else {
                DDScannerApplication.getInstance().getDdScannerRestClient().postUpdateSealife(updateResultListener, body, Helpers.createRequestBodyForString(new Gson().toJson(sealifeTranslations)), Helpers.createRequestBodyForString(binding.getSealifeViewModel().getSealife().getId()));
            }
        }
    }

    private boolean validateData() {
        boolean isSomethingWrong = false;

        if (binding.name.length() < 2) {
            isSomethingWrong = true;
            binding.nameError.setVisibility(View.VISIBLE);
        }

        if (filePath == null && !isForEdit) {
            isSomethingWrong = true;
            binding.errorImage.setVisibility(View.VISIBLE);
        }

        if (isSomethingWrong) {
            binding.mainLayout.scrollTo(0,0);
        }

        return isSomethingWrong;
    }

    private void makeErrorsMap() {
        errorsMap.put("name", binding.nameError);
        errorsMap.put("distribution", binding.distributionError);
        errorsMap.put("habitat", binding.habitatError);
        errorsMap.put("image", binding.errorImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DialogHelpers.showDialogAfterChangesInActivity(getSupportFragmentManager());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

    private void hideErrorsFields() {
        for (Map.Entry<String, TextView> entry : errorsMap.entrySet()) {
            entry.getValue().setVisibility(View.GONE);
        }
    }

    @Override
    public void onPictureFromCameraTaken(File picture) {

    }

    @Override
    public void onPicturesTaken(ArrayList<String> pictures) {
        filePath = pictures.get(0);
        setBackImage(filePath, false);
    }

    public void pickPhotoClicked(View view) {
        pickSinglePhotoFromGallery();
    }

    public void deletePhotoClicked(View view) {
        filePath = null;
        binding.sealifePhoto.setImageDrawable(null);
        binding.sealifePhoto.setVisibility(View.GONE);
        binding.addPhotoLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        binding.deletePhoto.setVisibility(View.GONE);
        binding.addPhotoCenterLayout.setVisibility(View.VISIBLE);

    }

    public void saveSealifeClicked(View view) {
        createRequestBody();
    }

    @Override
    public void onNegativeDialogClicked() {

    }

    @Override
    public void onPositiveDialogClicked() {
        finish();
    }

}

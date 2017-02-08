package com.ddscanner.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.SealifeTranslation;
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.google.gson.Gson;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.ddscanner.utils.Constants.MULTIPART_TYPE_TEXT;

public class AddSealifeActivity extends BaseAppCompatActivity implements View.OnClickListener, BaseAppCompatActivity.PictureTakenListener {

    private static final String TAG = AddSealifeActivity.class.getSimpleName();

    private String filePath = null;

    private RelativeLayout addPhoto;
    private RelativeLayout centerLayout;
    private AppCompatImageButton btnDelete;
    private Button btnSaveSealife;
    private Toolbar toolbar;
    private EditText name;
    private EditText habitat;
    private EditText distribution;
    private EditText weight;
    private EditText length;
    private EditText scClass;
    private EditText scName;
    private EditText depth;
    private EditText order;
    private TextView name_error;
    private TextView habitat_error;
    private TextView distribution_error;
    private TextView image_error;
    private ImageView sealifePhoto;
    private MaterialDialog progressDialogUpload;
    private File fileToSend;

    private Map<String, TextView> errorsMap = new HashMap<>();

    private DDScannerRestClient.ResultListener<SealifeShort> sealifeResultListener = new DDScannerRestClient.ResultListener<SealifeShort>() {
        @Override
        public void onSuccess(SealifeShort result) {
//            Intent intent = new Intent();
//            intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE, result);
//            setResult(RESULT_OK, intent);
//            finish();
        }

        @Override
        public void onConnectionFailure() {
            progressDialogUpload.dismiss();
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            progressDialogUpload.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(AddSealifeActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_LOGIN_TO_SEND);
                    break;
                case UNPROCESSABLE_ENTITY_ERROR_422:
//                    Helpers.errorHandling(errorsMap, (ValidationError) errorData);
                    break;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sealife);
        findViews();
        setUi();
        makeErrorsMap();
        EventsTracker.trackSealifeCreation();
    }

    private void findViews() {
        btnDelete = (AppCompatImageButton) findViewById(R.id.delete_photo);
        sealifePhoto = (ImageView) findViewById(R.id.sealife_photo);
        centerLayout = (RelativeLayout) findViewById(R.id.add_photo_center_layout);
        addPhoto = (RelativeLayout) findViewById(R.id.add_photo_layout);
        btnSaveSealife = (Button) findViewById(R.id.btn_save_sealife);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        name = (EditText) findViewById(R.id.name);
        habitat = (EditText) findViewById(R.id.habitat);
        distribution = (EditText) findViewById(R.id.distribution);
        weight = (EditText) findViewById(R.id.weight);
        length = (EditText) findViewById(R.id.length);
        scClass = (EditText) findViewById(R.id.scClass);
        scName = (EditText) findViewById(R.id.scName);
        depth = (EditText) findViewById(R.id.depth);
        order = (EditText) findViewById(R.id.order);
        name_error = (TextView) findViewById(R.id.name_error);
        habitat_error = (TextView) findViewById(R.id.habitat_error);
        distribution_error = (TextView) findViewById(R.id.distribution_error);
        image_error = (TextView) findViewById(R.id.error_image);
    }

    /**
     * Set UI settings
     * @author Andrei Lashkevich
     */
    private void setUi() {
        progressDialogUpload = Helpers.getMaterialDialog(this);
        btnSaveSealife.setOnClickListener(this);
        addPhoto.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_sealife);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    /**
     * Change background image in layout add photo
     *
     * @param path
     * @author Andrei Lashkevich
     */
    private void setBackImage(String path) {
        if (!path.contains(Constants.images) && !path.contains("file:")) {
            path = "file://" + path;
        }
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        Picasso.with(this).load(path).resize(Math.round(Helpers.convertDpToPixel(dpWidth, this)), Math.round(Helpers.convertDpToPixel(230, this))).centerCrop().into(sealifePhoto);
        centerLayout.setVisibility(View.GONE);
        btnDelete.setVisibility(View.VISIBLE);
        addPhoto.setOnClickListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo_layout:
                pickSinglePhotoFromGallery();
                break;
            case R.id.delete_photo:
                sealifePhoto.setImageDrawable(null);
                addPhoto.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                btnDelete.setVisibility(View.GONE);
                centerLayout.setVisibility(View.VISIBLE);
                addPhoto.setOnClickListener(this);
                break;
            case R.id.btn_save_sealife:
                createRequestBody();
                break;
        }
    }


    /**
     * Put data to request body
     *
     * @author Andrei Lashkevich
     */
    private void createRequestBody() {
        progressDialogUpload.show();
        ArrayList<SealifeTranslation> sealifeTranslations = new ArrayList<>();
        SealifeTranslation sealifeTranslation = new SealifeTranslation();
        sealifeTranslation.setLangCode("en");
        sealifeTranslation.setDepth(depth.getText().toString().trim());
        sealifeTranslation.setDistribution(distribution.getText().toString().trim());
        sealifeTranslation.setLength(length.getText().toString().trim());
        sealifeTranslation.setWeight(weight.getText().toString().trim());
        sealifeTranslation.setScName(scName.getText().toString().trim());
        sealifeTranslation.setOrder(order.getText().toString().trim());
        sealifeTranslation.setSealifeClass(scClass.getText().toString().trim());
        sealifeTranslation.setHabitat(habitat.getText().toString().trim());
        sealifeTranslation.setName(name.getText().toString().trim());
        sealifeTranslations.add(sealifeTranslation);
        MultipartBody.Part body = null;
        fileToSend = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileToSend);
        body = MultipartBody.Part.createFormData("photo", fileToSend.getName(), requestFile);
        DDScannerApplication.getInstance().getDdScannerRestClient().postAddSealife(sealifeResultListener, body, Helpers.createRequestBodyForString(new Gson().toJson(sealifeTranslations)));
        hideErrorsFields();
    }

    private boolean validateData() {
        return true;
    }

    /**
     * Create map to store errors textViews with their keys
     * @author Andrei Lashkevich
     */
    private void makeErrorsMap() {
        errorsMap.put("name", name_error);
        errorsMap.put("distribution", distribution_error);
        errorsMap.put("habitat", habitat_error);
        errorsMap.put("image", image_error);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
        setBackImage(filePath);
    }
}

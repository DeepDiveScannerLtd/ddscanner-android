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
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.ddscanner.utils.Constants.MULTIPART_TYPE_TEXT;

public class AddSealifeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddSealifeActivity.class.getSimpleName();

    private Uri filePath;

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

    private RequestBody requestName;
    private RequestBody requestLength;
    private RequestBody requestWeight;
    private RequestBody requestDepth;
    private RequestBody requestScname;
    private RequestBody requestOrder;
    private RequestBody requestClass;
    private RequestBody requestDistribution;
    private RequestBody requestHabitat;

    private RequestBody requestSecret = null;
    private RequestBody requestSocial = null;
    private RequestBody requestToken = null;

    private DDScannerRestClient.ResultListener<Sealife> sealifeResultListener = new DDScannerRestClient.ResultListener<Sealife>() {
        @Override
        public void onSuccess(Sealife result) {
            Intent intent = new Intent();
            intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE, result);
            setResult(RESULT_OK, intent);
            finish();
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
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    String filename = "DDScanner" + String.valueOf(System.currentTimeMillis() / 1232);
                    Uri uri = Uri.parse("");
                    try {
                        uri = data.getData();
                        String mimeType = getContentResolver().getType(uri);
                        String sourcePath = getExternalFilesDir(null).toString();
                        fileToSend = new File(sourcePath + "/" + filename);
                        if (Helpers.isFileImage(uri.getPath()) || mimeType.contains("image")) {
                            try {
                                Helpers.copyFileStream(fileToSend, uri, this);
                                Log.i(TAG, fileToSend.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            filePath = uri;
                            setBackImage(fileToSend.getPath());
                        } else {
                            Toast.makeText(this, "You can choose only images", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
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
                pickPhotoFromGallery();
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

    private void pickPhotoFromGallery() {
        if (checkReadStoragePermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_PICK_PHOTO);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_PERMISSION_READ_STORAGE);
        }
    }

    public boolean checkReadStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }


    /**
     * Put data to request body
     *
     * @author Andrei Lashkevich
     */
    private void createRequestBody() {
        progressDialogUpload.show();
        requestName = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                name.getText().toString());
        requestLength = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                length.getText().toString());
        requestWeight = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                weight.getText().toString());
        requestDepth = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                depth.getText().toString());
        requestScname = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                scName.getText().toString());
        requestOrder = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                order.getText().toString());
        requestClass = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                scClass.getText().toString());
        requestDistribution = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                distribution.getText().toString());
        requestHabitat = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                habitat.getText().toString());
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            requestSocial = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
            requestToken = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn().equals("tw")) {
                requestSecret = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                        DDScannerApplication.getInstance().getSharedPreferenceHelper().getSecret());
            }
        }
        hideErrorsFields();
        sendRequestToAddSealife(filePath);
    }

    /**
     * Make request to server for adding sealife
     * @author Andrei Lashkevich
     */
    private void sendRequestToAddSealife(Uri imageFileUri) {
        MultipartBody.Part body = null;
        if (imageFileUri != null) {
           // File file = new File(Helpers.getRealPathFromURI(this, imageFileUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileToSend);
            body = MultipartBody.Part.createFormData("image", fileToSend.getName(), requestFile);
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postAddSealife(
                sealifeResultListener, body, requestName, requestDistribution, requestHabitat,
                requestScname, requestLength, requestWeight, requestDepth, requestOrder, requestClass,
                requestToken, requestSocial);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhotoFromGallery();
                } else {
                    Toast.makeText(AddSealifeActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private void hideErrorsFields() {
        for (Map.Entry<String, TextView> entry : errorsMap.entrySet()) {
            entry.getValue().setVisibility(View.GONE);
        }
    }
}

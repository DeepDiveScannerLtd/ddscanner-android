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
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.rest.BaseCallbackOld;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

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
        if (requestCode == ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_PICK_PHOTO && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            filePath = uri;
            setBackImage(uri);
        }
        if (requestCode == ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_LOGIN_TO_SEND) {
            if (resultCode == RESULT_OK) {
                sendRequestToAddSealife(filePath);
            }
        }
    }

    /**
     * Change background image in layout add photo
     *
     * @param uri
     * @author Andrei Lashkevich
     */
    private void setBackImage(Uri uri) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        Picasso.with(this).load(uri).resize(Math.round(Helpers.convertDpToPixel(dpWidth, this)), Math.round(Helpers.convertDpToPixel(230, this))).centerCrop().into(sealifePhoto);
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
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_PICK_PHOTO);
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
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            requestSocial = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                    SharedPreferenceHelper.getSn());
            requestToken = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                    SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                requestSecret = RequestBody.create(MediaType.parse(MULTIPART_TYPE_TEXT),
                        SharedPreferenceHelper.getSecret());
            }
        }
        sendRequestToAddSealife(filePath);
    }

    /**
     * Make request to server for adding sealife
     * @author Andrei Lashkevich
     */
    private void sendRequestToAddSealife(Uri imageFileUri) {
        MultipartBody.Part body = null;
        if (imageFileUri != null) {
            File file = new File(Helpers.getRealPathFromURI(this, imageFileUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addSealife(
                requestName, requestDistribution, requestHabitat, body, requestScname,
                requestLength, requestWeight, requestDepth, requestOrder, requestClass,
                requestToken, requestSocial, requestSecret);
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialogUpload.dismiss();
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        Helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        Helpers.errorHandling(AddSealifeActivity.this, errorsMap, responseString);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        LoginActivity.showForResult(AddSealifeActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_SEALIFE_ACTIVITY_LOGIN_TO_SEND);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    }
                }
                if(response.isSuccessful()) {
                    EventsTracker.trackSealifeCreated();
                    try {
                        String responseString = "";
                        responseString = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            responseString = jsonObject.getString(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);
                            Sealife sealife = new Gson().fromJson(responseString, Sealife.class);
                            Intent intent = new Intent();
                            intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE, sealife);
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {

                        }
                        Log.i(TAG, response.body().string());
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(AddSealifeActivity.this);
            }
        });
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
}

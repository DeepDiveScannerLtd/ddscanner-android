package com.ddscanner.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rey.material.widget.EditText;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 8.4.16.
 */
public class AddSealifeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddSealifeActivity.class.getSimpleName();

    private static final int RC_LOGIN = 8001;
    private static final int RC_PICK_PHOTO = 1001;
    private static final int RC_LOGIN_TO_SEND = 4001;
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
    private Helpers helpers = new Helpers();
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
    }

    /**
     * Find views in current activity_add_sealife
     * @author Andrei Lashkevich
     */

    private void findViews() {
        btnDelete = (AppCompatImageButton) findViewById(R.id.delete_photo);
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
        progressDialogUpload = new MaterialDialog.Builder(this)
                .content("Please wait...").progress(true, 0)
                .contentColor(getResources().getColor(R.color.black_text))
                .widgetColor(getResources().getColor(R.color.primary)).build();
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
        if (requestCode == RC_PICK_PHOTO && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            filePath = uri;
            setBackImage(uri);
        }
        if (requestCode == RC_LOGIN_TO_SEND) {
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
        Picasso.with(this).load(uri).memoryPolicy(MemoryPolicy.NO_CACHE).resize(Math.round(dpWidth), 230).centerCrop().into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                centerLayout.setVisibility(View.GONE);
                btnDelete.setVisibility(View.VISIBLE);
                addPhoto.setBackground(ob);
                addPhoto.setOnClickListener(null);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.i(TAG, "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.i(TAG, "onPrepareLoad");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo_layout:
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RC_PICK_PHOTO);
                break;
            case R.id.delete_photo:
                addPhoto.setBackground(null);
                addPhoto.setBackgroundColor(getResources().getColor(R.color.white));
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
        requestName = RequestBody.create(MediaType.parse("multipart/form-data"),
                name.getText().toString());
        requestLength = RequestBody.create(MediaType.parse("multipart/form-data"),
                length.getText().toString());
        requestWeight = RequestBody.create(MediaType.parse("multipart/form-data"),
                weight.getText().toString());
        requestDepth = RequestBody.create(MediaType.parse("multipart/form-data"),
                depth.getText().toString());
        requestScname = RequestBody.create(MediaType.parse("multipart/form-data"),
                scName.getText().toString());
        requestOrder = RequestBody.create(MediaType.parse("multipart/form-data"),
                order.getText().toString());
        requestClass = RequestBody.create(MediaType.parse("multipart/form-data"),
                scClass.getText().toString());
        requestDistribution = RequestBody.create(MediaType.parse("multipart/form-data"),
                distribution.getText().toString());
        requestHabitat = RequestBody.create(MediaType.parse("multipart/form-data"),
                habitat.getText().toString());
        if (SharedPreferenceHelper.getIsUserLogined()) {
            requestSocial = RequestBody.create(MediaType.parse("multipart/form-data"),
                    SharedPreferenceHelper.getSn());
            requestToken = RequestBody.create(MediaType.parse("multipart/form-data"),
                    SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                requestSecret = RequestBody.create(MediaType.parse("multipart/form-data"),
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
            File file = new File(helpers.getRealPathFromURI(this, imageFileUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }
        Call<ResponseBody> call = RestClient.getServiceInstance().addSealife(
                requestName, requestDistribution, requestHabitat, body, requestScname,
                requestLength, requestWeight, requestDepth, requestOrder, requestClass,
                requestToken, requestSocial, requestSecret);
        call.enqueue(new Callback<ResponseBody>() {
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
                        helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        helpers.errorHandling(AddSealifeActivity.this, errorsMap, responseString);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(AddSealifeActivity.this, RC_LOGIN_TO_SEND);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(AddSealifeActivity.this, R.string.toast_server_error);
                    }
                }
                if(response.isSuccessful()) {
                    try {
                        String responseString = "";
                        responseString = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            responseString = jsonObject.getString("sealife");
                            Sealife sealife = new Gson().fromJson(responseString, Sealife.class);
                            Intent intent = new Intent();
                            intent.putExtra("SEALIFE", sealife);
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtils.e(TAG, t.getMessage());
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

    /**
     * Handling errors. Show error message if error goes from server.
     * @author Andrei Lashkevich
     * @param errors JSON string with errors
     */

    private void handleErrors(String errors, int error_number) {
        if (error_number == 400) {
            Intent intent = new Intent(AddSealifeActivity.this, SocialNetworks.class);
            startActivityForResult(intent, RC_LOGIN);
            return;
        }
        JsonObject jsonObject = new JsonParser().parse(errors).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if(!entry.getKey().equals("")) {
                if (entry.getKey().equals("token")) {
                    Intent intent = new Intent(AddSealifeActivity.this, SocialNetworks.class);
                    startActivityForResult(intent, RC_LOGIN);
                    return;
                }
                if (errorsMap.get(entry.getKey()) != null) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    value = value.replace("[\"", "");
                    value = value.replace("\"]", "");
                    errorsMap.get(key).setVisibility(View.VISIBLE);
                    errorsMap.get(key).setText(value);
                }
            }
        }
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
        if (!helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }
}

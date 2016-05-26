package com.ddscanner.ui.activities;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.Sealife;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.ui.adapters.SpinnerItemsAdapter;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDiveSpotActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddDiveSpotActivity.class.getSimpleName();
    private static final int RC_PICK_PHOTO = 9001;
    private static final int RC_PICK_LOCATION = 8001;
    private static final int RC_PICK_SEALIFE = 7001;

    private ProgressDialog progressDialog;

    private ImageButton btnAddPhoto;
    private ImageView btnAddSealife;

    private Toolbar toolbar;
    private LatLng diveSpotLocation;

    private LinearLayout pickLocation;
    private RecyclerView photos_rc;
    private TextView addPhotoTitle;
    private TextView locationTitle;
    private TextView addSealifeTitle;
    private Spinner levelSpinner;
    private Spinner currentsSpinner;
    private Spinner objectSpinner;
    private Spinner visibilitySpinner;
    private EditText name;
    private EditText depth;
    private EditText description;
    private Button btnSave;
    private RecyclerView sealifesRc;
    private SealifeListAddingDiveSpotAdapter sealifeListAddingDiveSpotAdapter = null;
    private AddPhotoToDsListAdapter addPhotoToDsListAdapter = null;
    private ScrollView mainLayout;
    private ProgressView progressView;
    private MaterialDialog progressDialogUpload;
    private TextView error_name;
    private TextView error_location;
    private TextView error_description;
    private TextView error_depth;
    private TextView error_sealife;
    private TextView error_images;


    private Helpers helpers = new Helpers();
    private List<String> imageUris = new ArrayList<>();
    private List<Sealife> sealifes = new ArrayList<>();
    private Map<String, TextView> errorsMap = new HashMap<>();
    private FiltersResponseEntity filters;

    private RequestBody requestName = null, requestLat = null, requestLng = null,
            requestDepth = null, requestVisibility = null, requestCurrents = null,
            requestLevel = null, requestObject = null, requestAccess = null,
            requestDescription = null, requestSocial = null, requestToken = null,
            requestSecret = null;

    public static void show(Context context) {
        Intent intent = new Intent(context, AddDiveSpotActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dive_spot);
        findViews();
        setUi();
        loadFiltersDataRequest();
        makeErrorsMap();
    }


    /**
     * Find views in current activity_add_dive_spot
     *
     * @author Andrei Lashkevich
     */

    private void findViews() {
        name = (EditText) findViewById(R.id.name);
        depth = (EditText) findViewById(R.id.depth);
        description = (EditText) findViewById(R.id.description);
        btnAddSealife = (ImageView) findViewById(R.id.btn_add_sealife);
        addPhotoTitle = (TextView) findViewById(R.id.add_photo_title);
        photos_rc = (RecyclerView) findViewById(R.id.photos_rc);
        btnAddPhoto = (ImageButton) findViewById(R.id.btn_add_photo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        levelSpinner = (Spinner) findViewById(R.id.level_spinner);
        objectSpinner = (Spinner) findViewById(R.id.object_spinner);
        visibilitySpinner = (Spinner) findViewById(R.id.visibility_spinner);
        currentsSpinner = (Spinner) findViewById(R.id.currents_spinner);
        pickLocation = (LinearLayout) findViewById(R.id.location_layout);
        locationTitle = (TextView) findViewById(R.id.location);
        btnSave = (Button) findViewById(R.id.button_create);
        sealifesRc = (RecyclerView) findViewById(R.id.sealifes_rc);
        addSealifeTitle = (TextView) findViewById(R.id.add_sealife_text);
        mainLayout = (ScrollView) findViewById(R.id.main_layout);
        progressView = (ProgressView) findViewById(R.id.progressBarFull);
        error_depth = (TextView) findViewById(R.id.error_depth);
        error_description = (TextView) findViewById(R.id.error_description);
        error_location = (TextView) findViewById(R.id.error_location);
        error_name = (TextView) findViewById(R.id.error_name);
        error_images = (TextView) findViewById(R.id.error_images);
        error_sealife = (TextView) findViewById(R.id.error_sealife);
    }

    /**
     * Set UI settings for activity views
     *
     * @author Andrei Lashkevich
     */

    private void setUi() {
        progressDialogUpload = helpers.getMaterialDialog(this);
        progressDialog = new ProgressDialog(this);
        btnSave.setOnClickListener(this);
        pickLocation.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
        btnAddSealife.setOnClickListener(this);

        /* Recycler view with images settings*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddDiveSpotActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);

        /* Recycler view with sealifes settings*/
        LinearLayoutManager sealifeLayoutManager = new LinearLayoutManager(
                AddDiveSpotActivity.this);
        sealifeLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sealifesRc.setNestedScrollingEnabled(false);
        sealifesRc.setHasFixedSize(false);
        sealifesRc.setLayoutManager(sealifeLayoutManager);

        /*Toolbar settings*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Divespot");

        progressDialog.setMessage("Wait while dive spot adding");
        progressDialog.setCancelable(false);

        progressView.stop();
        progressView.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_LOCATION) {
            if (resultCode == RESULT_OK) {
                this.diveSpotLocation = data.getParcelableExtra("LATLNG");
                locationTitle.setTextColor(getResources().getColor(R.color.black_text));
            }
        }
        if (requestCode == RC_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        imageUris.add(helpers.getRealPathFromURI(AddDiveSpotActivity.this, uri));
                    }
                    photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris, AddDiveSpotActivity.this, addPhotoTitle));
                } else {
                    Uri uri = data.getData();
                    imageUris.add(helpers.getRealPathFromURI(AddDiveSpotActivity.this, uri));
                    photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris, AddDiveSpotActivity.this, addPhotoTitle));
                }
            }
        }
        if (requestCode == RC_PICK_SEALIFE) {
            if (resultCode == RESULT_OK) {
                Sealife sealife =(Sealife) data.getSerializableExtra("SEALIFE");
                sealifes.add(sealife);
                sealifeListAddingDiveSpotAdapter = new SealifeListAddingDiveSpotAdapter(
                        (ArrayList<Sealife>) sealifes, this, addSealifeTitle);
                sealifesRc.setAdapter(sealifeListAddingDiveSpotAdapter);
            }
        }
    }

    private void setSpinnerValues(Spinner spinner, Map<String, String> values, String tag) {
        List<String> objects = new ArrayList<String>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            objects.add(entry.getValue());
            if (entry.getKey().equals(tag)) {

            }
        }
        ArrayAdapter<String> adapter = new SpinnerItemsAdapter(this, R.layout.spinner_item, objects);
        spinner.setAdapter(adapter);
    }

    private void loadFiltersDataRequest() {

        Call<ResponseBody> call = RestClient.getServiceInstance().getFilters();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    filters = new FiltersResponseEntity();

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(responseString).getAsJsonObject();
                    JsonObject currentsJsonObject = jsonObject.getAsJsonObject("currents");
                    for (Map.Entry<String, JsonElement> elementEntry : currentsJsonObject.entrySet()) {
                        filters.getCurrents().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }
                    JsonObject levelJsonObject = jsonObject.getAsJsonObject("level");
                    for (Map.Entry<String, JsonElement> elementEntry : levelJsonObject.entrySet()) {
                        filters.getLevel().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }
                    JsonObject objectJsonObject = jsonObject.getAsJsonObject("object");
                    for (Map.Entry<String, JsonElement> elementEntry : objectJsonObject.entrySet()) {
                        filters.getObject().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }
                    JsonObject visibilityJsonObject = jsonObject.getAsJsonObject("visibility");
                    for (Map.Entry<String, JsonElement> elementEntry : visibilityJsonObject.entrySet()) {
                        filters.getVisibility().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }
                    Gson gson = new Gson();
                    filters.setRating(gson.fromJson(jsonObject.get("rating").getAsJsonArray(), int[].class));

                    Log.i(TAG, responseString);

                    setSpinnerValues(objectSpinner, filters.getObject(), "");
                    setSpinnerValues(levelSpinner, filters.getLevel(), "");
                    setSpinnerValues(currentsSpinner, filters.getCurrents(), "");
                    setSpinnerValues(visibilitySpinner, filters.getVisibility(), "");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO Handle errors
            }
        });
    }

    private void createAddDiveSpotRequest() {
        progressDialogUpload.show();
        List<MultipartBody.Part> sealife = new ArrayList<>();
        if (sealifeListAddingDiveSpotAdapter != null &&
                sealifeListAddingDiveSpotAdapter.getSealifes() != null) {
            sealifes = sealifeListAddingDiveSpotAdapter.getSealifes();
        }
        if (sealifes.size() > 0) {
            for (int i = 0; i < sealifes.size(); i++) {
                sealife.add(MultipartBody.Part.createFormData("sealife[]", sealifes.get(i).getId()));
            }
        } else {
            sealifes = null;
        }
        List<MultipartBody.Part> images = new ArrayList<>();
        if (addPhotoToDsListAdapter != null &&
                addPhotoToDsListAdapter.getNewFilesUrisList()!= null) {
            imageUris = addPhotoToDsListAdapter.getNewFilesUrisList();
        }
        if (imageUris.size() > 0) {
            for (int i = 0; i < imageUris.size(); i++) {
                File image = new File(imageUris.get(i));
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData("images[]",
                        image.getName(), requestFile);
                images.add(part);
            }
        } else {
            images = null;
        }
        Call<ResponseBody> call = RestClient.getServiceInstance().addDiveSpot(
                requestName, requestLat, requestLng, requestDepth, requestVisibility,
                requestCurrents, requestLevel, requestObject, requestAccess, requestDescription,
                sealife, images, requestToken, requestSocial, requestSecret
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "success");
                progressDialogUpload.dismiss();
                if (response.isSuccessful()) {
                    if (response.raw().code() == 200) {
                        String responseString = "";
                        try {
                            responseString = response.body().string();
                            DivespotsWrapper divespotsWrapper = new DivespotsWrapper();
                            divespotsWrapper = new Gson().fromJson(responseString,
                                    DivespotsWrapper.class);
                            DiveSpotDetailsActivity.show(AddDiveSpotActivity.this, String
                                    .valueOf(divespotsWrapper.getDiveSpots().get(0).getId()));
                            finish();
                        } catch (IOException e) {

                        }

                    }
                }
                if (response.errorBody() != null) {
                    try {
                        String error = response.errorBody().string();
                        helpers.errorHandling(AddDiveSpotActivity.this, errorsMap,error);
                        Log.i(TAG, response.errorBody().string());
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialogUpload.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_photo:
                // Method 1
                // Pros: works fine
                // Cons: does not work if opened app does not support EXTRA_ALLOW_MULTIPLE; api 18+
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(i, RC_PICK_PHOTO);

                // Method 2
                // Pros: works on any device (?)
                // Cons: returned uri may appear non-convertable to absolute path depending on the app that user will use for choosing. For example Google Draive may return uri for image that is not yet downloaded to device
//                Intent i = new Intent();
//                i.setType("image/*");
//                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                i.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(i,"Select Picture"), RC_PICK_PHOTO);
                break;
            case R.id.location_layout:
                Intent intent = new Intent(AddDiveSpotActivity.this,
                        PickLocationActivity.class);
                startActivityForResult(intent, RC_PICK_LOCATION);
                break;
            case R.id.btn_add_sealife:
                Intent sealifeIntent = new Intent(AddDiveSpotActivity.this,
                        SearchSealifeActivity.class);
                startActivityForResult(sealifeIntent, RC_PICK_SEALIFE);
                break;
            case R.id.button_create:
                createRequestBodyies();
                break;
        }
    }

    private void createRequestBodyies() {
        requestName = RequestBody.create(MediaType.parse("multipart/form-data"),
                name.getText().toString());
        requestDepth = RequestBody.create(MediaType.parse("multipart/form-data"),
                depth.getText().toString());
        if (diveSpotLocation != null) {
            requestLat = RequestBody.create(MediaType.parse("multipart/form-data"),
                    String.valueOf(diveSpotLocation.latitude));
            requestLng = RequestBody.create(MediaType.parse("multipart/form-data"),
                    String.valueOf(diveSpotLocation.longitude));
        }
        requestAccess = RequestBody.create(MediaType.parse("multipart/form-data"), "boat");
        requestObject = RequestBody.create(MediaType.parse("multipart/form-data"),
                helpers.getMirrorOfHashMap(filters.getObject())
                        .get(objectSpinner.getSelectedItem().toString()));
        Log.i("Selected", helpers.getMirrorOfHashMap(filters.getVisibility())
                .get(visibilitySpinner.getSelectedItem().toString()));
        requestVisibility = RequestBody.create(MediaType.parse("multipart/form-data"),
                helpers.getMirrorOfHashMap(filters.getVisibility())
                        .get(visibilitySpinner.getSelectedItem().toString()));
        requestCurrents = RequestBody.create(MediaType.parse("multipart/form-data"),
                helpers.getMirrorOfHashMap(filters.getCurrents())
                        .get(currentsSpinner.getSelectedItem().toString()));
        requestLevel = RequestBody.create(MediaType.parse("multipart/form-data"),
                helpers.getMirrorOfHashMap(filters.getLevel())
                        .get(levelSpinner.getSelectedItem().toString()));
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
        requestDescription = RequestBody.create(MediaType.parse("multipart/form-data"),
                description.getText().toString());
        createAddDiveSpotRequest();
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

    private void makeErrorsMap() {
        errorsMap.put("depth", error_depth);
        errorsMap.put("name", error_name);
        errorsMap.put("description", error_description);
        errorsMap.put("location", error_location);
        errorsMap.put("images", error_images);
        errorsMap.put("sealife", error_sealife);
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

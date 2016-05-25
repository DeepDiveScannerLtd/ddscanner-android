package com.ddscanner.ui.activities;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.ddscanner.entities.DiveSpotFull;
import com.ddscanner.entities.DivespotDetails;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.Sealife;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
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
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditDiveSpotActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = EditDiveSpotActivity.class.getSimpleName();


    private static final int RC_PICK_PHOTO = 9001;
    private static final int RC_PICK_LOCATION = 8001;
    private static final int RC_PICK_SEALIFE = 7001;

    private String diveSpotId;

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
    private SealifeListAddingDiveSpotAdapter sealifeListAddingDiveSpotAdapter;
    private ScrollView mainLayout;
    private ProgressView progressView;
    private MaterialDialog progressDialogUpload;

    private List<String> imageUris = new ArrayList<String>();
    private List<Sealife> sealifes = new ArrayList<>();
    private List<String> images_del = new ArrayList<>();
    private List<MultipartBody.Part> sealifeRequest = new ArrayList<>();
    private List<MultipartBody.Part> deletedImages = new ArrayList<>();
    private List<MultipartBody.Part> newImages = new ArrayList<>();
    private FiltersResponseEntity filters;
    private DivespotDetails divespotDetails;
    private DiveSpotFull diveSpot;
    private AddPhotoToDsListAdapter addPhotoToDsListAdapter;
    private ProgressDialog progressDialog;
    private Helpers helpers = new Helpers();

    private RequestBody requestName, requestLat, requestLng, requestDepth, requestVisibility,
            requestCurrents, requestLevel, requestObject, requestAccess,
            requestDescription, requestType;
    private RequestBody requestSecret = null;
    private RequestBody requestSocial = null;
    private RequestBody requestToken = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dive_spot);
        diveSpotId = getIntent().getStringExtra(Constants.DIVESPOTID);
        findViews();
        toolbarSettings();
        getDsInfoRequest();
        loadFiltersDataRequest();
    }

    /**
     * Find views in current activity_add_dive_spot
     * @author Andrei Lashkevich
     */

    private void findViews() {
        progressDialogUpload = helpers.getMaterialDialog(this);
        progressDialog = new ProgressDialog(this);
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
    }

    /**
     * Change data and settings of current activity views
     * @author Andrei Lashkevich
     */

    private void setUi() {
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);

        btnSave.setOnClickListener(this);
        pickLocation.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
        btnAddSealife.setOnClickListener(this);

        name.setText(diveSpot.getName());
        depth.setText(diveSpot.getDepth());
        description.setText(diveSpot.getDescription());
        locationTitle.setTextColor(getResources().getColor(R.color.black_text));

        /* Recycler view with images settings*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditDiveSpotActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);
        photos_rc.setAdapter(addPhotoToDsListAdapter);

        /* Recycler view with sealifes settings*/
        LinearLayoutManager sealifeLayoutManager = new LinearLayoutManager(
                EditDiveSpotActivity.this);
        sealifeLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sealifesRc.setNestedScrollingEnabled(false);
        sealifesRc.setHasFixedSize(false);
        sealifesRc.setLayoutManager(sealifeLayoutManager);
        sealifeListAddingDiveSpotAdapter = new SealifeListAddingDiveSpotAdapter((ArrayList<Sealife>) sealifes,
                EditDiveSpotActivity.this, addSealifeTitle);
        sealifesRc.setAdapter(sealifeListAddingDiveSpotAdapter);
        progressView.stop();
        progressView.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Change title and adding back icon to toolbar
     * @author Andrei Lashkevich
     */

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_dive_spot);
    }

    public static void show(Context context, String id) {
        Intent intent = new Intent(context, EditDiveSpotActivity.class);
        intent.putExtra("ID", id);
        context.startActivity(intent);
    }

    /**
     * Get dive spot information according last modification by current user
     * @author Andrei Lashkevich
     */

    private void getDsInfoRequest() {

        Call<ResponseBody> call = RestClient.getServiceInstance().getDiveSpotForEdit(
                diveSpotId, helpers.getUserQuryMapRequest());
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
                    LogUtils.i("response body is " + responseString);
                    if (response.raw().code() == 200) {
                        divespotDetails = new Gson().fromJson(responseString, DivespotDetails.class);
                        diveSpot = divespotDetails.getDivespot();
                        sealifes = divespotDetails.getSealifes();
                        imageUris = changeImageAddresses(diveSpot.getImages());
                        addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris, EditDiveSpotActivity.this, addPhotoTitle);
                        diveSpotLocation = new LatLng(divespotDetails.getDivespot().getLat(),
                                divespotDetails.getDivespot().getLng());
                        setUi();
                    } else {

                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * Add path to images to name to have a full address of image for future work with this
     * @author Andrei Lashkevich
     * @param images
     * @return images
     */

    private List<String> changeImageAddresses(List<String> images) {
        for (int i = 0; i <images.size(); i++) {
            images.set(i, diveSpot.getDiveSpotPathSmall() + images.get(i));
        }
        return images;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_photo:
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(i, RC_PICK_PHOTO);
                break;
            case R.id.location_layout:
                Intent intent = new Intent(EditDiveSpotActivity.this,
                        PickLocationActivity.class);
                intent.putExtra("LATLNG", diveSpotLocation);
                startActivityForResult(intent, RC_PICK_LOCATION);
                break;
            case R.id.btn_add_sealife:
                Intent sealifeIntent = new Intent(EditDiveSpotActivity.this,
                        SearchSealifeActivity.class);
                startActivityForResult(sealifeIntent, RC_PICK_SEALIFE);
                break;
            case R.id.button_create:
                progressDialogUpload.show();
                createRequestBodyies();
                break;
        }
    }

    /**
     * Remove adress part from image URL for creating list of deleted images names
     * @author Andrei Lashkevich
     * @param deleted
     * @return
     */

    private ArrayList<String> removeAdressPart(ArrayList<String> deleted) {

        for (int i = 0; i <deleted.size(); i++ ) {
            deleted.set(i, deleted.get(i).replace(diveSpot.getDiveSpotPathSmall(), ""
            ));
        }

        return deleted;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_LOCATION) {
            if (resultCode == RESULT_OK) {
                this.diveSpotLocation = data.getParcelableExtra("LATLNG");
            }
        }
        if (requestCode == RC_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        imageUris.add(helpers.getRealPathFromURI(EditDiveSpotActivity.this, uri));
                        photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris,
                                EditDiveSpotActivity.this, addPhotoTitle));
                        Log.i(TAG, helpers.getRealPathFromURI(EditDiveSpotActivity.this, uri));
                    }
                }
            }
        }
        if (requestCode == RC_PICK_SEALIFE) {
            if (resultCode == RESULT_OK) {
                Sealife sealife =(Sealife) data.getSerializableExtra("SEALIFE");
                sealifeListAddingDiveSpotAdapter.add(sealife);
                Log.i(TAG, sealifeListAddingDiveSpotAdapter.getSealifes().get(0).getName());
            }
        }
    }

    private void createRequestBodyies() {
        requestName = RequestBody.create(MediaType.parse("multipart/form-data"),
                name.getText().toString());
        requestDepth = RequestBody.create(MediaType.parse("multipart/form-data"),
                depth.getText().toString());
        requestLat = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(diveSpotLocation.latitude));
        requestLng = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(diveSpotLocation.longitude));
        requestAccess = RequestBody.create(MediaType.parse("multipart/form-data"), "boat");
        requestObject = RequestBody.create(MediaType.parse("multipart/form-data"), "reef");
        requestVisibility = RequestBody.create(MediaType.parse("multipart/form-data"), "good");
        requestCurrents = RequestBody.create(MediaType.parse("multipart/form-data"), "strong");
        requestLevel = RequestBody.create(MediaType.parse("multipart/form-data"), "master");
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
        requestType = RequestBody.create(MediaType.parse("multipart/form-data"), "PUT");



        sealifeRequest = new ArrayList<>();
        for (int i = 0; i < sealifes.size(); i++) {
            sealifeRequest.add(MultipartBody.Part.createFormData("sealife[]", sealifes.get(i).getId()));
        }

        if (addPhotoToDsListAdapter.getNewFilesUrisList() == null) {
            newImages = null;
        } else {
            for (int i = 0; i < addPhotoToDsListAdapter.getNewFilesUrisList().size(); i++) {
                File image = new File(addPhotoToDsListAdapter.getNewFilesUrisList().get(i));
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData("images_new[]", image.getName(),
                        requestFile);
                newImages.add(part);
            }
        }

        if (addPhotoToDsListAdapter.getListOfDeletedImages() == null) {
            deletedImages = null;
        } else {
            ArrayList<String> deleted = new ArrayList<>();
            deleted = (ArrayList<String>)addPhotoToDsListAdapter.getListOfDeletedImages();
            deleted = removeAdressPart(deleted);
            for (int i = 0; i < deleted.size(); i++) {
                deletedImages.add(MultipartBody.Part.createFormData("images_del[]", deleted.get(i)));
            }
        }

        createAddDiveSpotRequest();

    }

    private void createAddDiveSpotRequest() {
        Call<ResponseBody> call = RestClient.getServiceInstance().updateDiveSpot(
                diveSpotId, requestType, requestName, requestLat, requestLng, requestDepth,
                requestVisibility, requestCurrents, requestLevel, requestObject, requestAccess,
                requestDescription, sealifeRequest, newImages, deletedImages, requestToken,
                requestSocial, requestSecret
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().code() == 200) {
                    addPhotoToDsListAdapter.clearNewFilesUrisList();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
                progressDialogUpload.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialogUpload.dismiss();
            }
        });
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

    private void setSpinnerValues(Spinner spinner, Map<String, String> values, String tag) {
        List<String> objects = new ArrayList<String>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            objects.add(entry.getValue());
            if (entry.getKey().equals(tag)) {

            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, objects);
        spinner.setAdapter(adapter);
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

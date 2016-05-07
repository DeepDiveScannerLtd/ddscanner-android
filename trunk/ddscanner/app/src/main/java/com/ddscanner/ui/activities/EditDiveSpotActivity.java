package com.ddscanner.ui.activities;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DiveSpotFull;
import com.ddscanner.entities.DivespotDetails;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.Sealife;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private Helpers helpers = new Helpers();
    private List<String> imageUris = new ArrayList<String>();
    private List<Sealife> sealifes = new ArrayList<>();
    private List<String> images_del = new ArrayList<>();
    private FiltersResponseEntity filters;
    private DivespotDetails divespotDetails;
    private DiveSpotFull diveSpot;

    private RequestBody requestName, requestLat, requestLng, requestDepth, requestVisibility,
            requestCurrents, requestLevel, requestObject, requestAccess,
            requestDescription, requestSocial, requestToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dive_spot);
        diveSpotId = getIntent().getStringExtra("ID");
        findViews();
        toolbarSettings();
        getDsInfoRequest();
    }

    /**
     * Find views in current activity_add_dive_spot
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
    }

    private void setUi() {
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

    private void getDsInfoRequest() {
        Call<ResponseBody> call = RestClient.getServiceInstance().getDiveSpotForEdit(diveSpotId);
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
                    divespotDetails = new Gson().fromJson(responseString, DivespotDetails.class);
                    diveSpot = divespotDetails.getDivespot();
                    sealifes = divespotDetails.getSealifes();
                    imageUris = diveSpot.getImages();
                    diveSpotLocation = new LatLng(divespotDetails.getDivespot().getLat(),
                            divespotDetails.getDivespot().getLng());
                    setUi();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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
              //  createRequestBodyies();
                break;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_LOCATION) {
            if (resultCode == RESULT_OK) {
                this.diveSpotLocation = data.getParcelableExtra("LATLNG");
            }
        }
        if (requestCode == RC_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                imageUris.add(helpers.getRealPathFromURI(EditDiveSpotActivity.this, uri));
                photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris,
                        EditDiveSpotActivity.this, addPhotoTitle));
                Log.i(TAG, helpers.getRealPathFromURI(EditDiveSpotActivity.this, uri));
            }
        }
        if (requestCode == RC_PICK_SEALIFE) {
            if (resultCode == RESULT_OK) {
                Sealife sealife =(Sealife) data.getSerializableExtra("SEALIFE");
                sealifeListAddingDiveSpotAdapter.add(sealife);
                /*sealifeListAddingDiveSpotAdapter = new SealifeListAddingDiveSpotAdapter(
                        (ArrayList<Sealife>) sealifes, this, addSealifeTitle);
                sealifesRc.setAdapter(sealifeListAddingDiveSpotAdapter);*/
            }
        }
    }

}

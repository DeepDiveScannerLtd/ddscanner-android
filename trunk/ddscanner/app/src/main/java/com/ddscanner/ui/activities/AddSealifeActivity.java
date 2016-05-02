package com.ddscanner.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ddscanner.R;
import com.ddscanner.entities.request.CreateSealifeRequest;
import com.ddscanner.rest.RestClient;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 8.4.16.
 */
public class AddSealifeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = AddSealifeActivity.class.getSimpleName();

    private static final int RC_PICK_PHOTO = 1001;
    private Helpers helpers = new Helpers();
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


    private Map<String, View> errorsMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sealife);
        findViews();
        setUi();
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
    }

    /**
     * Set UI settings
     * @author Andrei Lashkevich
     */


    private void setUi() {
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
            setBackImage(helpers.getRealPathFromURI(this, uri));
        }
    }

    /**
     * Change background image in layout add photo
     * @author Andrei Lashkevich
     * @param path
     */

    private void setBackImage(String path) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        path = "file://" + path;
        Picasso.with(this).load(path).resize(Math.round(dpWidth), 230).centerCrop().into(new Target() {
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

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo_layout:
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
     * @author Andrei Lashkevich
     */

    private void createRequestBody() {
        TypedFile file = new TypedFile("image/png", new File(filePath.getPath()));
        CreateSealifeRequest createSealifeRequest = new CreateSealifeRequest();
        createSealifeRequest.setDepth(depth.getText().toString());
        createSealifeRequest.setDistribution(distribution.getText().toString());
        createSealifeRequest.setHabitat(habitat.getText().toString());
        createSealifeRequest.setImage(file);
        createSealifeRequest.setLength(length.getText().toString());
       // createSealifeRequest.setScClass("ff");
        createSealifeRequest.setOrder(order.getText().toString());
        createSealifeRequest.setWeight(weight.getText().toString());
        createSealifeRequest.setName(name.getText().toString());
        createSealifeRequest.setScName(scName.getText().toString());
        createSealifeRequest.setToken(SharedPreferenceHelper.getToken());
        createSealifeRequest.setSocial(SharedPreferenceHelper.getSn());
        sendRequestToAddSealife(createSealifeRequest);
    }

    /**
     * Make request to server for adding sealife
     * @author Andrei Lashkevich
     * @param createSealifeRequest
     */

    private void sendRequestToAddSealife(final CreateSealifeRequest createSealifeRequest) {
        Call<ResponseBody> call = RestClient.getServiceInstance().addSealife(createSealifeRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String error = response.errorBody().string();
                    Log.i("TAG", error);
                } catch (IOException e) {

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
}

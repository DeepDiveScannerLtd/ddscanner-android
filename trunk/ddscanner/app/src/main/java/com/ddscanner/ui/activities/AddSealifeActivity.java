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



    private static final int RC_PICK_PHOTO = 1001;
    private Helpers helpers = new Helpers();
    private Uri filePath;


    private RelativeLayout addPhoto;
    private RelativeLayout centerLayout;
    private AppCompatImageButton btnDelete;
    private Button btnSaveSealife;

    private Map<String, View> errorsMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sealife);
        findViews();
    }

    private void findViews() {
        btnDelete = (AppCompatImageButton) findViewById(R.id.delete_photo);
        centerLayout = (RelativeLayout) findViewById(R.id.add_photo_center_layout);
        addPhoto = (RelativeLayout) findViewById(R.id.add_photo_layout);
        btnSaveSealife = (Button) findViewById(R.id.btn_save_sealife);
        btnSaveSealife.setOnClickListener(this);
        addPhoto.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
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

    private void createRequestBody() {
        TypedFile file = new TypedFile("image/jpeg", new File(helpers.getRealPathFromURI(this, filePath)));
        CreateSealifeRequest createSealifeRequest = new CreateSealifeRequest();
        createSealifeRequest.setDepth("1");
        createSealifeRequest.setDistribution("21");
        createSealifeRequest.setHabitat("43242300");
        createSealifeRequest.setImage(file);
        createSealifeRequest.setLength("123");
       // createSealifeRequest.setScClass("ff");
        createSealifeRequest.setOrder("fdsf");
        createSealifeRequest.setWeight("321");
        createSealifeRequest.setName("evvgeniy");
        createSealifeRequest.setScName("zhukovets");
        createSealifeRequest.setToken(SharedPreferenceHelper.getToken());
        createSealifeRequest.setSocial(SharedPreferenceHelper.getSn());
        sendRequestToAddSealife(createSealifeRequest);
    }

    private void sendRequestToAddSealife(final CreateSealifeRequest createSealifeRequest) {
        Call<ResponseBody> call = RestClient.getServiceInstance().addSealife(createSealifeRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                try {
                    String error = response.errorBody().string();
                    Log.i("TAG", error);
                    Log.i("TAG", createSealifeRequest.getToken());
                    Log.i("TAG", createSealifeRequest.getSocial());
                } catch (IOException e) {

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("ERROR", "dsa");
            }
        });
    }
}

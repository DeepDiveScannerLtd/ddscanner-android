package com.ddscanner.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.User;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 20.4.16.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private LinearLayout editProfile;
    private ScrollView aboutLayout;
    private LinearLayout editLayout;
    private LinearLayout logout;
    private ImageView capturePhoto;
    private ImageView newPhoto;
    private com.rey.material.widget.Button cancelButton;
    private Helpers helpers = new Helpers();
    private User user;
    private TextView userCommentsCount;
    private TextView userLikesCount;
    private TextView userDislikesCount;
    private ImageView avatar;
    private EditText aboutEdit;
    private EditText fullNameEdit;
    private Button saveChanges;
    private TextView userFullName;
    private TextView userAbout;
    private TextView checkInCount;
    private TextView favouriteCount;
    private TextView addedCount;
    private TextView editedCount;
    private ImageView pickPhotoFromGallery;

    private RequestBody requestSecret = null;
    private RequestBody requestSocial = null;
    private RequestBody requestToken = null;
    private RequestBody about = null;
    private RequestBody username = null;
    private RequestBody name = null;
    private RequestBody requestType = null;
    private MultipartBody.Part image = null;

    private Uri uri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(v);
        editProfile.setOnClickListener(this);
        capturePhoto.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        saveChanges.setOnClickListener(this);
        pickPhotoFromGallery.setOnClickListener(this);
        logout.setOnClickListener(this);
        if (SharedPreferenceHelper.getIsUserLogined()) {
            getUserDataRequest(SharedPreferenceHelper.getUserServerId());
        }
        return v;
    }

    private void findViews(View v) {
        checkInCount = (TextView) v.findViewById(R.id.checkin_count);
        addedCount = (TextView) v.findViewById(R.id.added_count);
        favouriteCount = (TextView) v.findViewById(R.id.favourites_count);
        editedCount = (TextView) v.findViewById(R.id.edited_count);
        aboutLayout = (ScrollView) v.findViewById(R.id.about);
        editLayout = (LinearLayout) v.findViewById(R.id.editProfile);
        editProfile = (LinearLayout) v.findViewById(R.id.edit_profile);
        capturePhoto = (ImageView) v.findViewById(R.id.capture_photo);
        newPhoto = (ImageView) v.findViewById(R.id.user_chosed_photo);
        cancelButton = (com.rey.material.widget.Button) v.findViewById(R.id.cancel_button);
        userCommentsCount = (TextView) v.findViewById(R.id.user_comments);
        userLikesCount = (TextView) v.findViewById(R.id.user_likes);
        userDislikesCount = (TextView) v.findViewById(R.id.user_dislikes);
        avatar = (ImageView) v.findViewById(R.id.user_avatar);
        fullNameEdit = (EditText) v.findViewById(R.id.fullName);
        aboutEdit = (EditText) v.findViewById(R.id.aboutEdit);
        saveChanges = (Button) v.findViewById(R.id.button_save);
        userFullName = (TextView) v.findViewById(R.id.user_name);
        userAbout = (TextView) v.findViewById(R.id.user_about);
        pickPhotoFromGallery = (ImageView) v.findViewById(R.id.pick_photo_from_gallery);
        logout = (LinearLayout) v.findViewById(R.id.logout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile:
                aboutLayout.setVisibility(View.GONE);
                editLayout.setVisibility(View.VISIBLE);
                fullNameEdit.setText(user.getName());
                if (user.getAbout() != null) {
                    aboutEdit.setText(user.getAbout());
                }
                break;
            case R.id.capture_photo:
                DDScannerApplication.bus.post(new TakePhotoFromCameraEvent());
                break;
            case R.id.cancel_button:
                aboutLayout.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.GONE);
                break;
            case R.id.button_save:
                createUpdateRequest();
                break;
            case R.id.pick_photo_from_gallery:
                DDScannerApplication.bus.post(new PickPhotoFromGallery());
                break;
            case R.id.logout:
                SharedPreferenceHelper.logout();
                DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DDScannerApplication.bus.register(this);
        if (!getUserVisibleHint())
        {
            return;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    public void setImage(Uri uri) {
        Picasso.with(getContext()).load(uri)
                .resize(Math.round(helpers.convertDpToPixel(80,getContext())),
                        Math.round(helpers.convertDpToPixel(80,getContext()))).centerCrop()
                .transform(new TransformationRoundImage(100,0)).into(newPhoto);
        this.uri = uri;
    }

    private void getUserDataRequest(String id) {
        Call<ResponseBody> call = RestClient.getServiceInstance().getUserInfo(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    if (response.raw().code() == 200) {
                        try {
                            responseString = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            responseString = jsonObject.getString("user");
                            user = new Gson().fromJson(responseString, User.class);
                            changeUi(user);
                        } catch (IOException e) {

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void changeUi(User user) {
        Picasso.with(getContext()).load(user.getPicture())
                .resize(Math.round(helpers.convertDpToPixel(100,getContext())),
                        Math.round(helpers.convertDpToPixel(100,getContext()))).centerCrop()
                .transform(new TransformationRoundImage(100,0)).into(avatar);
        userCommentsCount.setText(user.getCountComment());
        userLikesCount.setText(user.getCountLike());
        userDislikesCount.setText(user.getCountDislike());
        Picasso.with(getContext()).load(user.getPicture())
                .resize(Math.round(helpers.convertDpToPixel(80,getContext())),
                        Math.round(helpers.convertDpToPixel(80,getContext()))).centerCrop()
                .transform(new TransformationRoundImage(100,0)).into(newPhoto);
        if (user.getAbout() != null) {
            userAbout.setVisibility(View.VISIBLE);
            userAbout.setText(user.getAbout());
        }
        userFullName.setText(user.getName());
        addedCount.setText(user.getCountAdd());
        editedCount.setText(user.getCountEdit());
        favouriteCount.setText(user.getCountFavorite());
        checkInCount.setText(user.getCountCheckin());
    }

    @Override
    public void setUserVisibleHint(final boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible) {
            if (SharedPreferenceHelper.getIsUserLogined()) {
                getUserDataRequest(SharedPreferenceHelper.getUserServerId());
            }
        }
    }

    private void createUpdateRequest() {
        if (!aboutEdit.equals(user.getAbout()) && !aboutEdit.getText().toString().equals("")) {
            about = RequestBody.create(MediaType.parse("multipart/form-data"),
                    aboutEdit.getText().toString());
        }
        if (!fullNameEdit.equals(user.getName()) && !fullNameEdit.getText().toString().equals("")) {
            name = RequestBody.create(MediaType.parse("multipart/form-data"),
                    fullNameEdit.getText().toString());
        }

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

        if (uri != null) {
            File file;
            if (!uri.toString().contains("file:")) {
                file = new File(helpers.getRealPathFromURI(getContext(), uri));
            } else {
                file = new File(uri.getPath());
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        requestType = RequestBody.create(MediaType.parse("multipart/form-data"), "PUT");

        Call<ResponseBody> call = RestClient.getServiceInstance().updateUserById(
                SharedPreferenceHelper.getUserServerId(),
                requestType, image, name, username, about, requestToken, requestSocial,
                requestSecret
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    if (response.raw().code() == 200) {
                        try {
                            responseString = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            responseString = jsonObject.getString("user");
                            user = new Gson().fromJson(responseString, User.class);
                            uri = null;
                            changeUi(user);
                            aboutLayout.setVisibility(View.VISIBLE);
                            editLayout.setVisibility(View.GONE);
                        } catch (IOException e) {

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

}

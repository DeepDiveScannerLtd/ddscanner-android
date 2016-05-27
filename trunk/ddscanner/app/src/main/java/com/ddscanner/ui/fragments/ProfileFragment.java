package com.ddscanner.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.User;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.activities.DiveSpotsListActivity;
import com.ddscanner.ui.activities.UsersDivespotListSwipableActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
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
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 20.4.16.
 */
public class ProfileFragment extends Fragment
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

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
    private LinearLayout showAllCheckins;
    private LinearLayout showAllFavorites;
    private LinearLayout showAllAdded;
    private LinearLayout showAllEdited;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView error_name;
    private TextView error_about;
    private boolean isClickedChosingPhotoButton = false;

    private RequestBody requestSecret = null;
    private RequestBody requestSocial = null;
    private RequestBody requestToken = null;
    private RequestBody about = null;
    private RequestBody username = null;
    private RequestBody name = null;
    private RequestBody requestType = null;
    private MultipartBody.Part image = null;
    private MaterialDialog materialDialog;
    private Map<String, TextView> errorsMap = new HashMap<>();

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
        materialDialog = helpers.getMaterialDialog(getContext());
        createErrorsMap();
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
        showAllCheckins = (LinearLayout) v.findViewById(R.id.checkins_activity);
        showAllFavorites = (LinearLayout) v.findViewById(R.id.favorites_activity);
        showAllAdded = (LinearLayout) v.findViewById(R.id.created_activity);
        showAllEdited = (LinearLayout) v.findViewById(R.id.edited_activity);
        error_about = (TextView) v.findViewById(R.id.error_about);
        error_name = (TextView) v.findViewById(R.id.error_name);

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
                isClickedChosingPhotoButton = true;
                DDScannerApplication.bus.post(new TakePhotoFromCameraEvent());
                break;
            case R.id.cancel_button:
                isClickedChosingPhotoButton = false;
                aboutLayout.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.GONE);
                break;
            case R.id.button_save:
                createUpdateRequest();
                break;
            case R.id.pick_photo_from_gallery:
                isClickedChosingPhotoButton = true;
                DDScannerApplication.bus.post(new PickPhotoFromGallery());
                break;
            case R.id.logout:
                materialDialog.show();
                logout();
                break;
            case R.id.checkins_activity:
                Intent intent = new Intent(getContext(), UsersDivespotListSwipableActivity.class);
                UsersDivespotListSwipableActivity.show(getContext(), true);
                break;
            case R.id.favorites_activity:
                UsersDivespotListSwipableActivity.show(getContext(), false);
                break;
            case R.id.edited_activity:
                DiveSpotsListActivity.show(getContext(), false);
                break;
            case R.id.created_activity:
                DiveSpotsListActivity.show(getContext(), true);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SharedPreferenceHelper.getIsUserLogined()) {
            if (!isClickedChosingPhotoButton) {
                getUserDataRequest(SharedPreferenceHelper.getUserServerId());
            }
        }
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
                            aboutLayout.setVisibility(View.VISIBLE);
                            responseString = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            responseString = jsonObject.getString("user");
                            user = new Gson().fromJson(responseString, User.class);
                            // TODO Server may respond error. For example {"message":"Social user not found","status_code":404}. In This case user will be null.
                            changeUi(user);
                        } catch (IOException e) {

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (response.raw().code() == 422 || response.raw().code() == 404) {
                    SharedPreferenceHelper.logout();
                  //  DDScannerApplication.bus.post(new ShowLoginActivityIntent());
                }
                if (response.errorBody() != null) {
                    try {
                    if (helpers.checkIsErrorByLogin(response.errorBody().string())) {
                            SharedPreferenceHelper.logout();
                        }
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void changeUi(User user) {
        if (getContext() == null) {
            return;
        }
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
        showAllCheckins.setOnClickListener(this);
        if (Integer.parseInt(user.getCountCheckin()) == 0) {
            showAllCheckins.setOnClickListener(null);
        }
        showAllFavorites.setOnClickListener(this);

        if (user.getCountFavorite() == null ||Integer.parseInt(user.getCountFavorite()) == 0) {
            showAllFavorites.setOnClickListener(null);
        }
        showAllEdited.setOnClickListener(this);
        if (Integer.parseInt(user.getCountEdit()) == 0) {
            showAllEdited.setOnClickListener(null);
        }
        showAllAdded.setOnClickListener(this);
        if (Integer.parseInt(user.getCountAdd()) == 0) {
            showAllAdded.setOnClickListener(null);
        }
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
        isClickedChosingPhotoButton = false;
        materialDialog.show();
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
                    materialDialog.dismiss();
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
                if (response.errorBody() != null) {
                    if (response.raw().code() == 404) {
                        SharedPreferenceHelper.logout();
                        DDScannerApplication.bus.post(new ShowLoginActivityIntent());
                    }
                    if (response.raw().code() == 422) {
                        try {
                            if (helpers.checkIsErrorByLogin(response.errorBody().string())) {
                                SharedPreferenceHelper.logout();
                                DDScannerApplication.bus.post(new ShowLoginActivityIntent());
                            }else {
                                    try {
                                        String error = response.errorBody().string();
                                        helpers.errorHandling(getContext(), errorsMap,error);
                                    } catch (IOException e) {

                                    }
                            }
                        } catch (IOException e) {

                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                materialDialog.dismiss();
            }
        });

    }

    @Override
    public void onRefresh() {
        getUserDataRequest(SharedPreferenceHelper.getUserServerId());
        swipeRefreshLayout.setRefreshing(false);
    }

    private void logout() {
        Call<ResponseBody> call = RestClient.getServiceInstance()
                .logout(helpers.getRegisterRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().code() == 200) {
                    aboutLayout.setVisibility(View.GONE);
                    SharedPreferenceHelper.logout();
                    DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
                    materialDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                    materialDialog.dismiss();
            }
        });
    }

    private void createErrorsMap() {
        errorsMap.put("name", error_name);
        errorsMap.put("about",error_about);
    }

}

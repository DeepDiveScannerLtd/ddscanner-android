package com.ddscanner.ui.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.User;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.activities.AboutActivity;
import com.ddscanner.ui.activities.DiveSpotsListActivity;
import com.ddscanner.ui.activities.ForeignUserLikesDislikesActivity;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.activities.UsersDivespotListSwipableActivity;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lashket on 20.4.16.
 */
public class ProfileFragment extends Fragment
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, LoginView.LoginStateChangeListener {

    private static final String TAG = ProfileFragment.class.getName();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MAX_LENGTH_NAME = 30;
    private static final int MAX_LENGTH_ABOUT = 250;

    private LinearLayout editProfile;
    private ScrollView aboutLayout;
    private ScrollView editLayout;
    private LinearLayout logout;
    private ImageView capturePhoto;
    private ImageView newPhoto;
    private com.rey.material.widget.Button cancelButton;
    private Helpers helpers = new Helpers();
    private User user;
    private TextView userCommentsCount;
    private TextView userLikesCount;
    private TextView userDislikesCount;
    private TextView nameLeftSymbols;
    private TextView aboutLeftSymbols;
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
    private LinearLayout aboutDDsLayout;
    private LinearLayout likeLayout;
    private LinearLayout dislikeLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView error_name;
    private TextView error_about;
    private RelativeLayout loginView;
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
        Log.i(TAG, "ProfileFragment onCreateView, this = " + this);
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(v);
        editProfile.setOnClickListener(this);
        capturePhoto.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        saveChanges.setOnClickListener(this);
        pickPhotoFromGallery.setOnClickListener(this);
        logout.setOnClickListener(this);
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            getUserDataRequest(SharedPreferenceHelper.getUserServerId());
        }
        materialDialog = helpers.getMaterialDialog(getContext());
        createErrorsMap();
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            onLoggedIn();
        } else {
            onLoggedOut();
        }
        return v;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.i(TAG, "onAttach(Context context)");
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

        Log.i(TAG, "onAttach(Activity context)");
        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(context);
        }
    }

    protected void onAttachToContext(Context context) {
        try {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.setProfileFragment(this);
        } catch (ClassCastException e) {
            // waaat?
            e.printStackTrace();
        }
    }

    private void findViews(View v) {
        checkInCount = (TextView) v.findViewById(R.id.checkin_count);
        addedCount = (TextView) v.findViewById(R.id.added_count);
        favouriteCount = (TextView) v.findViewById(R.id.favourites_count);
        editedCount = (TextView) v.findViewById(R.id.edited_count);
        aboutLayout = (ScrollView) v.findViewById(R.id.about);
        editLayout = (ScrollView) v.findViewById(R.id.editProfile);
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
        nameLeftSymbols = (TextView) v.findViewById(R.id.name_count);
        aboutLeftSymbols = (TextView) v.findViewById(R.id.about_count);
        loginView = (RelativeLayout) v.findViewById(R.id.login_view_root);
        aboutDDsLayout = (LinearLayout) v.findViewById(R.id.about_dss_layout);
        aboutDDsLayout.setOnClickListener(this);
        likeLayout = (LinearLayout) v.findViewById(R.id.likeLayout);
        dislikeLayout = (LinearLayout) v.findViewById(R.id.dislikeLayout);

        aboutEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (MAX_LENGTH_ABOUT - aboutEdit.length() < 10) {
                    aboutLeftSymbols.setTextColor(getResources().getColor(R.color.tw__composer_red));
                } else {
                    aboutLeftSymbols.setTextColor(Color.parseColor("#b2b2b2"));
                }
                aboutLeftSymbols.setText(String.valueOf(MAX_LENGTH_ABOUT - aboutEdit.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fullNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (MAX_LENGTH_NAME - fullNameEdit.length() < 10) {
                    nameLeftSymbols.setTextColor(getResources().getColor(R.color.tw__composer_red));
                } else {
                    nameLeftSymbols.setTextColor(Color.parseColor("#b2b2b2"));
                }
                nameLeftSymbols.setText(String.valueOf(MAX_LENGTH_NAME - fullNameEdit.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                aboutLayout.scrollTo(0,0);
                editLayout.setVisibility(View.GONE);
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
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
                SharedPreferenceHelper.setLastShowingNotificationTime(0);
                logout();
                break;
            case R.id.checkins_activity:
                EventsTracker.trackUserCheckinsView();
                UsersDivespotListSwipableActivity.show(getContext(), true, EventsTracker.SpotViewSource.FROM_PROFILE_CHECKINS);
                break;
            case R.id.favorites_activity:
                EventsTracker.trackUserFavoritesView();
                UsersDivespotListSwipableActivity.show(getContext(), false, EventsTracker.SpotViewSource.FROM_PROFILE_FAVOURITES);
                break;
            case R.id.edited_activity:
                EventsTracker.trackUserEditedView();
                DiveSpotsListActivity.show(getContext(), false, EventsTracker.SpotViewSource.FROM_PROFILE_EDITED);
                break;
            case R.id.created_activity:
                EventsTracker.trackUserCreatedView();
                DiveSpotsListActivity.show(getContext(), true, EventsTracker.SpotViewSource.FROM_PROFILE_CREATED);
                break;
            case R.id.about_dss_layout:
                AboutActivity.show(getContext());
                break;
            case R.id.likeLayout:
                ForeignUserLikesDislikesActivity.show(getActivity(), true, user.getId(), Constants.FOREIGN_USER_REQUEST_CODE_SHOW_LIKES_LIST);
                break;
            case R.id.dislikeLayout:
                ForeignUserLikesDislikesActivity.show(getActivity(), false, user.getId(), Constants.FOREIGN_USER_REQUEST_CODE_SHOW_LIKES_LIST);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ProfileFragment onCreateView, this = " + this);
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            if (!isClickedChosingPhotoButton) {
                getUserDataRequest(SharedPreferenceHelper.getUserServerId());
            }
        }
        if (!getUserVisibleHint()) {
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ProfileFragment onStart, this = " + this);
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ProfileFragment onStop, this = " + this);
        DDScannerApplication.bus.unregister(this);
    }

    public void setImage(Uri uri) {
        Picasso.with(getContext()).load(uri)
                .resize(Math.round(helpers.convertDpToPixel(80, getContext())),
                        Math.round(helpers.convertDpToPixel(80, getContext()))).centerCrop()
                .transform(new CropCircleTransformation()).into(newPhoto);
        this.uri = uri;
    }

    private void getUserDataRequest(String id) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserInfo(id);
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    if (response.raw().code() == 200) {
                        try {
                            if (editLayout.getVisibility() != View.VISIBLE) {
                                aboutLayout.setVisibility(View.VISIBLE);
                            }
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
                if (response.raw().code() == 422 || response.raw().code() == 404 ||
                        response.raw().code() == 400) {
                    SharedPreferenceHelper.logout();
                    DDScannerApplication.bus.post(new LoggedOutEvent());
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
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(getActivity());
            }
        });
    }

    private void changeUi(User user) {
        if (getContext() == null) {
            return;
        }
        if (user != null) {
            if (!user.getCountLike().equals("0")) {
                likeLayout.setOnClickListener(this);
            }
            if (!user.getCountDislike().equals("0")) {
                dislikeLayout.setOnClickListener(this);
            }
            if (user.getPicture() == null) {
                Picasso.with(getContext()).load(R.drawable.avatar_profile_default)
                        .resize(Math.round(helpers.convertDpToPixel(100, getContext())),
                                Math.round(helpers.convertDpToPixel(100, getContext()))).centerCrop()
                        .placeholder(R.drawable.avatar_profile_default)
                        .transform(new CropCircleTransformation()).into(avatar);
            } else {
                Picasso.with(getContext()).load(user.getPicture())
                        .resize(Math.round(helpers.convertDpToPixel(80, getContext())),
                                Math.round(helpers.convertDpToPixel(80, getContext()))).centerCrop()
                        .placeholder(R.drawable.avatar_profile_default)
                        .error(R.drawable.avatar_profile_default)
                        .transform(new CropCircleTransformation()).into(avatar);
            }
            userCommentsCount.setText(user.getCountComment());
            userLikesCount.setText(user.getCountLike());
            userDislikesCount.setText(user.getCountDislike());
            Picasso.with(getContext()).load(user.getPicture())
                    .resize(Math.round(helpers.convertDpToPixel(80, getContext())),
                            Math.round(helpers.convertDpToPixel(80, getContext()))).centerCrop()
                    .placeholder(R.drawable.avatar_profile_default)
                    .error(R.drawable.avatar_profile_default)
                    .transform(new CropCircleTransformation()).into(newPhoto);
            if (user.getAbout() != null) {
                userAbout.setVisibility(View.VISIBLE);
                userAbout.setText(user.getAbout());
            } else {
                userAbout.setVisibility(View.GONE);
            }
            userFullName.setText(user.getName());
            addedCount.setText(user.getCountAdd() + getDiveSpotString(Integer.parseInt(user.getCountAdd())));
            editedCount.setText(user.getCountEdit() + getDiveSpotString(Integer.parseInt(user.getCountEdit())));
            favouriteCount.setText(user.getCountFavorite() + getDiveSpotString(Integer.parseInt(user.getCountFavorite())));
            checkInCount.setText(user.getCountCheckin() + getDiveSpotString(Integer.parseInt(user.getCountFavorite())));
            showAllCheckins.setOnClickListener(this);
            if (Integer.parseInt(user.getCountCheckin()) == 0) {
                showAllCheckins.setOnClickListener(null);
            }
            showAllFavorites.setOnClickListener(this);

            if (user.getCountFavorite() == null || Integer.parseInt(user.getCountFavorite()) == 0) {
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
        } else {
            SharedPreferenceHelper.logout();
        }
    }

    @Override
    public void setUserVisibleHint(final boolean visible) {
        super.setUserVisibleHint(visible);
        Log.i(TAG, "ProfileFragment setUserVisibleHint, this = " + this);
        if (visible) {
            if (SharedPreferenceHelper.isUserLoggedIn()) {
                getUserDataRequest(SharedPreferenceHelper.getUserServerId());
            }
        }
    }

    private String getDiveSpotString(int count) {
        if (count > 1 || count == 0) {
            return getString(R.string.dive_spos);
        }
        if (count == 1) {
            return getString(R.string.one_dive_spot);
        }
        return "";
    }

    private void createUpdateRequest() {
        isClickedChosingPhotoButton = false;
        materialDialog.show();
        if (!aboutEdit.equals(user.getAbout()) && !aboutEdit.getText().toString().equals("")) {
            about = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    aboutEdit.getText().toString());
        }
        if (!fullNameEdit.equals(user.getName()) && !fullNameEdit.getText().toString().equals("")) {
            name = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    fullNameEdit.getText().toString());
        }

        if (SharedPreferenceHelper.isUserLoggedIn()) {
            requestSocial = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    SharedPreferenceHelper.getSn());
            requestToken = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                requestSecret = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
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

        requestType = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), "PUT");

        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().updateUserById(
                SharedPreferenceHelper.getUserServerId(),
                requestType, image, name, username, about, requestToken, requestSocial,
                requestSecret
        );

        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                materialDialog.dismiss();
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
                            aboutLayout.scrollTo(0,0);
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
                            } else {
                                try {
                                    String error = response.errorBody().string();
                                    helpers.errorHandling(getContext(), errorsMap, error);
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
                super.onFailure(call, t);
                materialDialog.dismiss();
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(getActivity());
            }
        });

    }

    @Override
    public void onRefresh() {
        getUserDataRequest(SharedPreferenceHelper.getUserServerId());
        swipeRefreshLayout.setRefreshing(false);
    }

    private void logout() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance()
                .logout(helpers.getRegisterRequest());
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                materialDialog.dismiss();
                if (response.raw().code() == 200) {
                    aboutLayout.setVisibility(View.GONE);
                    SharedPreferenceHelper.logout();
                    DDScannerApplication.bus.post(new LoggedOutEvent());
                    DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
                }
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
                        helpers.showToast(getContext(), R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(getContext(), R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                      // helpers.showToast(getContext(), R.string.toast_server_error);
                        SharedPreferenceHelper.logout();
                        DDScannerApplication.bus.post(new LoggedOutEvent());
                        DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(getContext(), R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(getContext(), R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        DDScannerApplication.bus.post(new LoggedOutEvent());
                        DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(getContext(), R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                super.onFailure(call, t);
                materialDialog.dismiss();
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(getActivity());
            }
        });
    }

    private void createErrorsMap() {
        errorsMap.put("name", error_name);
        errorsMap.put("about", error_about);
    }

    @Override
    public void onLoggedIn() {
        Log.i(TAG, "ProfileFragment onLoggedIn, this = " + this);
        if (loginView != null && aboutLayout != null) {
            loginView.setVisibility(View.GONE);
            if (editLayout.getVisibility() != View.VISIBLE) {
                aboutLayout.setVisibility(View.VISIBLE);
            } else {
                aboutLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoggedOut() {
        if (loginView != null && aboutLayout != null) {
            loginView.setVisibility(View.VISIBLE);
            aboutLayout.setVisibility(View.GONE);
        }
    }
}

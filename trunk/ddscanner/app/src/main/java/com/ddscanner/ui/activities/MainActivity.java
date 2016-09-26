package com.ddscanner.ui.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.RegisterResponse;
import com.ddscanner.entities.SignInType;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.CloseInfoWindowEvent;
import com.ddscanner.events.CloseListEvent;
import com.ddscanner.events.InfowWindowOpenedEvent;
import com.ddscanner.events.InternetConnectionClosedEvent;
import com.ddscanner.events.ListOpenedEvent;
import com.ddscanner.events.LocationReadyEvent;
import com.ddscanner.events.LoggedInEvent;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.events.LoginViaFacebookClickEvent;
import com.ddscanner.events.LoginViaGoogleClickEvent;
import com.ddscanner.events.NewDiveSpotAddedEvent;
import com.ddscanner.events.OpenAddDiveSpotActivity;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.PlaceChoosedEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.BaseCallbackOld;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.MainActivityPagerAdapter;
import com.ddscanner.ui.fragments.ActivityNotificationsFragment;
import com.ddscanner.ui.fragments.AllNotificationsFragment;
import com.ddscanner.ui.fragments.NotificationsFragment;
import com.ddscanner.ui.fragments.ProfileFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends BaseAppCompatActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getName();

    private Uri capturedImageUri;

    private TabLayout toolbarTabLayout;
    private ViewPager mainViewPager;
    private PercentRelativeLayout menuItemsLayout;
    private ImageView searchLocationBtn;
    private ImageView btnFilter;
    private MainActivityPagerAdapter mainViewPagerAdapter;
    private ProfileFragment profileFragment;
    private NotificationsFragment notificationsFragment;
    private ActivityNotificationsFragment activityNotificationsFragment;
    private AllNotificationsFragment allNotificationsFragment;
    private ImageView imageView;
    private boolean isHasInternetConnection;
    private boolean isHasLocation;
    private MaterialDialog materialDialog;
    private boolean isTryToOpenAddDiveSpotActivity = false;
    private boolean isDiveSpotInfoWindowShown = false;
    private boolean isDiveSpotListIsShown = false;
    private int positionToScroll;

    private CallbackManager facebookCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private com.ddscanner.entities.User selfProfile;
    private RegisterResponse registerResponse = new RegisterResponse();

    private boolean loggedInDuringLastOnStart;
    private boolean needToClearDefaultAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG, "onCreate");
        isHasInternetConnection = getIntent().getBooleanExtra(Constants.IS_HAS_INTERNET, false);
        clearFilterSharedPrefences();
        startActivity();
        if (!isHasInternetConnection) {
            LogUtils.i(TAG, "internetConnectionClosed 2");
            InternetClosedActivity.show(this);
        }
        loggedInDuringLastOnStart = SharedPreferenceHelper.isUserLoggedIn();
    }

    private void startActivity() {
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);
        findViews();
        setUi();
        searchLocationBtn.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        setupTabLayout();
        EventsTracker.trackDiveSpotMapView();
    }

    private void initGoogleLoginManager() {
        needToClearDefaultAccount = true;
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("195706914618-ist9f8ins485k2gglbomgdp4l2pn57iq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (needToClearDefaultAccount) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            mGoogleApiClient.clearDefaultAccountAndReconnect();
                            needToClearDefaultAccount = false;
                            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                            startActivityForResult(signInIntent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_CHOSE_GOOGLE_ACCOUNT);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
    }

    private void initFBLoginManager() {
        facebookCallbackManager = CallbackManager.Factory.create();
    }

    /*Google*/
    private void googleSignIn() {
        if (mGoogleApiClient == null) {
            initGoogleLoginManager();
        } else if (mGoogleApiClient.isConnected()) {
            needToClearDefaultAccount = true;
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        }
    }

    private void fbLogin() {
        if (facebookCallbackManager == null) {
            initFBLoginManager();
        }
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                sendRegisterRequest(putTokensToMap(SharedPreferenceHelper.getUserAppId(), "fb", loginResult.getAccessToken().getToken()), SignInType.FACEBOOK);

                            }
                        }).executeAsync();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
    }

    private void setUi() {
        mainViewPagerAdapter = new MainActivityPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(mainViewPagerAdapter);
        toolbarTabLayout.setupWithViewPager(mainViewPager);
        mainViewPager.setOffscreenPageLimit(3);
        mainViewPager.addOnPageChangeListener(this);
        if (profileFragment != null) {
            mainViewPagerAdapter.setProfileFragment(profileFragment);
        }
        if (notificationsFragment != null) {
            mainViewPagerAdapter.setNotificationsFragment(notificationsFragment);
        }
        if (activityNotificationsFragment != null) {
            mainViewPagerAdapter.setActivityNotificationsFragment(activityNotificationsFragment);
        }
        if (allNotificationsFragment != null) {
            mainViewPagerAdapter.setAllNotificationsFragment(allNotificationsFragment);
        }
    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        toolbarTabLayout = (TabLayout) findViewById(R.id.toolbar_tablayout);
        mainViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        menuItemsLayout = (PercentRelativeLayout) findViewById(R.id.menu_items_layout);
        searchLocationBtn = (ImageView) findViewById(R.id.search_location_menu_button);
        btnFilter = (ImageView) findViewById(R.id.filter_menu_button);
    }

    private void setupTabLayout() {
        toolbarTabLayout.getTabAt(2).setCustomView(R.layout.tab_profile_item);
        toolbarTabLayout.getTabAt(1).setCustomView(R.layout.tab_notification_item);
        toolbarTabLayout.getTabAt(0).setCustomView(R.layout.tab_map_item);
        toolbarTabLayout.getTabAt(0).getCustomView().setSelected(true);
        mainViewPager.setCurrentItem(0);
    }

    public static void show(Context context, boolean isHasInternet) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.IS_HAS_INTERNET, isHasInternet);
        context.startActivity(intent);
    }

    @Override
    public void onPageSelected(int position) {
        if (position != 2) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        if (position != 0) {
            menuItemsLayout.animate()
                    .translationX(menuItemsLayout.getWidth())
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            menuItemsLayout.setVisibility(View.GONE);
                        }
                    });
        } else {
            menuItemsLayout.animate()
                    .translationX(0)
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            menuItemsLayout.setVisibility(View.VISIBLE);
                        }
                    });
        }
        if (position == 2) {
            EventsTracker.trackUserProfileView();
        }
//        if ((position == 2 || position == 1) && !SharedPreferenceHelper.isUserLoggedIn()) {
//            positionToScroll = position;
//            Intent intent = new Intent(MainActivity.this, SocialNetworks.class);
//            startActivityForResult(intent, REQUEST_CODE_MAIN_ACTIVITY_LOGIN);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_location_menu_button:
//                materialDialog.show();
//                openSearchLocationWindow();

                SearchSpotOrLocationActivity.showForResult(MainActivity.this, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PLACE_AUTOCOMPLETE);
        //        EventsTracker.trackSearchActivityOpened();
                break;
            case R.id.filter_menu_button:
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intent);
        //        EventsTracker.trackFiltersActivityOpened();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PLACE_AUTOCOMPLETE:
                materialDialog.dismiss();
                switch (resultCode) {
                    case RESULT_OK:
                        final LatLngBounds place = data.getParcelableExtra(Constants.SEARCH_ACTIVITY_INTENT_KEY);
                        DDScannerApplication.bus.post(new PlaceChoosedEvent(place));
//                    if (place.getViewport() != null) {
//                        DDScannerApplication.bus.post(new PlaceChoosedEvent(place.getViewport()));
//                    } else {
//                        LatLngBounds latLngBounds = new LatLngBounds(new LatLng(place.getLatLng().latitude - 0.2, place.getLatLng().longitude - 0.2), new LatLng(place.getLatLng().latitude + 0.2, place.getLatLng().longitude + 0.2) );
//                        DDScannerApplication.bus.post(new PlaceChoosedEvent(latLngBounds));
//                    }
                        break;
                    case ActivitiesRequestCodes.RESULT_CODE_SEARCH_ACTIVITY_MY_LOCATION:
                        Log.i(TAG, "MainActivity getLocation 2");
                        getLocation(ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_GO_TO_MY_LOCATION);
                        break;
                }

                break;
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    mainViewPagerAdapter.setProfileImageFromCamera(capturedImageUri);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity
                            .EXTRA_RESULT);
                    mainViewPagerAdapter.setProfileImage(path.get(0));
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (isTryToOpenAddDiveSpotActivity) {
                        AddDiveSpotActivity.showForResult(this, Constants.MAIN_ACTIVITY_ACTVITY_REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY, true);
                        isTryToOpenAddDiveSpotActivity = false;
                        return;
                    }
                    mainViewPager.setCurrentItem(positionToScroll, false);
                } else {
                    isTryToOpenAddDiveSpotActivity = false;
                }
                if (resultCode == RESULT_CANCELED) {
                    mainViewPager.setCurrentItem(0, false);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_CHOSE_GOOGLE_ACCOUNT:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());
                if (result.isSuccess()) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    String idToken = acct.getIdToken();
                    sendRegisterRequest(putTokensToMap(SharedPreferenceHelper.getUserAppId(), "go", idToken), SignInType.GOOGLE);
                }
                break;
            case Constants.MAIN_ACTIVITY_ACTVITY_REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    LatLng latLng = data.getParcelableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_RESULT_LAT_LNG);
                    String diveSpotId = data.getStringExtra(Constants.ADD_DIVE_SPOT_INTENT_DIVESPOT_ID);
                    if (latLng != null) {
                        DDScannerApplication.bus.post(new NewDiveSpotAddedEvent(latLng, diveSpotId));
                    }
                }
                break;
            default:
                if (facebookCallbackManager != null) {
                    facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.i(TAG, "onStart");
        DDScannerApplication.bus.register(this);
        if (loggedInDuringLastOnStart != SharedPreferenceHelper.isUserLoggedIn()) {
            mainViewPagerAdapter.notifyDataSetChanged();
            setupTabLayout();
            loggedInDuringLastOnStart = SharedPreferenceHelper.isUserLoggedIn();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.i(TAG, "onStop");
        DDScannerApplication.bus.unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i(TAG, "onPause");
        AppEventsLogger.deactivateApp(this);
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i(TAG, "onResume");
        AppEventsLogger.activateApp(this);
        DDScannerApplication.activityResumed();
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            mainViewPagerAdapter.onLoggedIn();
        } else {
            mainViewPagerAdapter.onLoggedOut();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DDScanner");
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, "DDS_" + timeStamp + ".png");
        capturedImageUri = Uri.fromFile(image);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_IMAGE_CAPTURE);
        }
    }

    private RegisterRequest putTokensToMap(String... args) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAppId(args[0]);
        registerRequest.setSocial(args[1]);
        registerRequest.setToken(args[2]);
        if (args.length == 4) {
            registerRequest.setSecret(args[3]);
        }
        return registerRequest;
    }

    private void sendRegisterRequest(final RegisterRequest userData, final SignInType signInType) {
        materialDialog.show();
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().registerUser(userData);
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                materialDialog.dismiss();
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, responseString);
                    SharedPreferenceHelper.setToken(userData.getToken());
                    SharedPreferenceHelper.setSn(userData.getSocial());
                    if (userData.getSocial().equals("tw")) {
                        SharedPreferenceHelper.setSecret(userData.getSecret());
                    }
                    registerResponse = new Gson().fromJson(responseString, RegisterResponse.class);
                    selfProfile = registerResponse.getUser();
                    SharedPreferenceHelper.setUserServerId(selfProfile.getId());
                    SharedPreferenceHelper.setIsUserSignedIn(true, signInType);
                    DDScannerApplication.bus.post(new LoggedInEvent());
                } else {
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
                        Helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(MainActivity.this, R.string.toast_server_error);
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
                DialogUtils.showConnectionErrorDialog(MainActivity.this);
            }
        });
    }

    private void validateIdToken() {
        Call<ResponseBody> call = RestClient.getGoogleApisServiceInstance().getTokenInfo("eyJhbGciOiJSUzI1NiIsImtpZCI6IjIyZjJiN2RjMzI5ZWIxMWU0ZTA1MjEzMjRjNjZiZGJmNjNiYzNhNzIifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdWQiOiIxOTU3MDY5MTQ2MTgtaXN0OWY4aW5zNDg1azJnZ2xib21nZHA0bDJwbjU3aXEuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDA4MjExMDgxMDk2NzM2OTc1NjMiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXpwIjoiMTk1NzA2OTE0NjE4LXUydGlsdTZ0cGU3bDA2bzNjZzcwNWJlZzB0bmdqMmdpLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiZW1haWwiOiJteWxhbmd2dWlAZ21haWwuY29tIiwiaWF0IjoxNDY4MTYxMTk0LCJleHAiOjE0NjgxNjQ3OTQsIm5hbWUiOiJMYW5nIFZ1aSIsImdpdmVuX25hbWUiOiJMYW5nIiwiZmFtaWx5X25hbWUiOiJWdWkiLCJsb2NhbGUiOiJlbiJ9.PcDkSOYFFv8TvkPM9LfZ2F5TaNOk6aLy0x8kqci4RLmisWAbomnnBtlPhZ-KVgsmjvTevwKb8DDCkJysxLkngh8ZjuPj-wDbNrPaHQMiawFW1pABorygWLU7fbd2ddnj6lY7DabeI1YW_fnux1Ep_36WUULGyz5YPstA0zsZNUWC9ndu_m-kTnlL-di5WXaqLadD9YMisZMStgYrTzr7LCwtg_x1A5xo2zCr5wISjI7eQN4xoRX9kff7vCoJMCUPmK-jyBnM62DRelxEELvhVppUl5ypqY6GHkajA8t8viug7ZdPbUh9i8OlGY3hCvCFilNALVDVRLZFbjXqTZPQ6Q");
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "createAddDiveSpotRequest success");
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("createAddDiveSpotRequest response body is " + responseString);
                } else {
                    if (response.raw().code() == 200) {
                        String responseString = "";
                        try {
                            responseString = response.body().string();
                            LogUtils.i("createAddDiveSpotRequest response body is " + responseString);
                        } catch (IOException e) {

                        }

                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(MainActivity.this);
            }
        });
    }

    private void refreshIdTokenSilently() {
        if (mGoogleApiClient == null) {
            Log.i(TAG, "refreshIdTokenSilently initGoogleLoginManager");
            initGoogleLoginManager();
        }
        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            // There's immediate result available.
            GoogleSignInAccount acct = pendingResult.get().getSignInAccount();
            String idToken = acct.getIdToken();
            Log.i(TAG, "refreshIdTokenSilently pendingResult.isDone idToken = " + idToken);
        } else {
            // There's no immediate result ready, displays some progress indicator and waits for the
            // async callback.
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    Log.i(TAG, "refreshIdTokenSilently onResult result.isSuccess = " + result.isSuccess());
                    if (result.isSuccess()) {
                        GoogleSignInAccount acct = result.getSignInAccount();
                        String idToken = acct.getIdToken();
                        Log.i(TAG, "refreshIdTokenSilently onResult idToken = " + idToken);
                    }
                }
            });
        }

    }

    @Subscribe
    public void onLoginViaFacebookClick(LoginViaFacebookClickEvent event) {
        if (AccessToken.getCurrentAccessToken() == null) {
            fbLogin();
            Log.i(TAG, "LOGGED IN");
        } else {
            LoginManager.getInstance().logOut();
            fbLogin();
            Log.i(TAG, "LOGGED OUT");
        }
    }

    @Subscribe
    public void onLoginViaGoogleClick(LoginViaGoogleClickEvent event) {
        googleSignIn();
    }

    @Subscribe
    public void onLocationReady(LocationReadyEvent event) {
        LogUtils.i(TAG, "location check: onLocationReady request codes = " + event.getRequestCodes());
        for (Integer code : event.getRequestCodes()) {
            switch (code) {
                case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_GO_TO_MY_LOCATION:
                    if (SharedPreferenceHelper.isUserAppIdReceived()) {
                        identifyUser(String.valueOf(event.getLocation().getLatitude()), String.valueOf(event.getLocation().getLongitude()));
                    }
                    DDScannerApplication.bus.post(new PlaceChoosedEvent(new LatLngBounds(new LatLng(event.getLocation().getLatitude() - 1, event.getLocation().getLongitude() - 1), new LatLng(event.getLocation().getLatitude() + 1, event.getLocation().getLongitude() + 1))));
                    break;
            }
        }
    }

    @Subscribe
    public void changeProfileFragmentView(TakePhotoFromCameraEvent event) {
        if (checkWriteStoragePermision(this)) {
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_CAMERA_AND_WRITE_STORAGE);

        }
    }

    @Subscribe
    public void pickPhotoFromGallery(PickPhotoFromGallery event) {
        if (checkReadStoragePermission(this)) {
            pickphotoFromGallery();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_READ_STORAGE);
        }
    }

    private void pickphotoFromGallery() {
        MultiImageSelector.create(this).count(1).start(this, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PICK_PHOTO);
    }

    private boolean checkPermissionReadStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Subscribe
    public void userIslogouted(ChangePageOfMainViewPagerEvent event) {
        mainViewPager.setCurrentItem(event.getPage());
    }

    @Subscribe
    public void internetConnectionClosed(InternetConnectionClosedEvent event) {
        LogUtils.i(TAG, "internetConnectionClosed 1");
        InternetClosedActivity.show(this);
    }

    @Subscribe
    public void showLoginActivity(ShowLoginActivityIntent event) {
        Intent intent = new Intent(MainActivity.this, SocialNetworks.class);
        startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN);
    }

    @Subscribe
    public void openLoginWindowToAdd(OpenAddDsActivityAfterLogin event) {
        isTryToOpenAddDiveSpotActivity = true;
        Intent intent = new Intent(MainActivity.this, SocialNetworks.class);
        startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN);
    }

    @Subscribe
    public void onLoggedIn(LoggedInEvent event) {
        mainViewPagerAdapter.notifyDataSetChanged();
        setupTabLayout();

        mainViewPagerAdapter.onLoggedIn();
    }

    @Subscribe
    public void onLoggedOut(LoggedOutEvent event) {
        mainViewPagerAdapter.notifyDataSetChanged();
        setupTabLayout();

        mainViewPagerAdapter.onLoggedOut();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Subscribe
    public void infowindowShowed(InfowWindowOpenedEvent event) {
        isDiveSpotInfoWindowShown = true;
    }

    @Subscribe
    public void listOpenedEvent(ListOpenedEvent event) {
        isDiveSpotListIsShown = true;
    }

    @Override
    public void onBackPressed() {
        if (mainViewPager.getCurrentItem() != 0) {
            DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
            return;
        }
        if (isDiveSpotListIsShown) {
            DDScannerApplication.bus.post(new CloseListEvent());
            isDiveSpotListIsShown = false;
            return;
        }
        if (isDiveSpotInfoWindowShown) {
            DDScannerApplication.bus.post(new CloseInfoWindowEvent());
            isDiveSpotInfoWindowShown = false;
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

    }

    public void setProfileFragment(ProfileFragment profileFragment) {
        if (mainViewPagerAdapter != null) {
            mainViewPagerAdapter.setProfileFragment(profileFragment);
        } else {
            this.profileFragment = profileFragment;
        }
    }

    public void setNotificationsFragment(NotificationsFragment notificationsFragment) {
        if (mainViewPagerAdapter != null) {
            mainViewPagerAdapter.setNotificationsFragment(notificationsFragment);
        } else {
            this.notificationsFragment = notificationsFragment;
        }
    }

    public void setActivityNotificationsFragment(ActivityNotificationsFragment activityNotificationsFragment) {
        if (mainViewPagerAdapter != null) {
            mainViewPagerAdapter.setActivityNotificationsFragment(activityNotificationsFragment);
        } else {
            this.activityNotificationsFragment = activityNotificationsFragment;
        }
    }

    public void setAllNotificationsFragment(AllNotificationsFragment allNotificationsFragment) {
        if (mainViewPagerAdapter != null) {
            mainViewPagerAdapter.setAllNotificationsFragment(allNotificationsFragment);
        } else {
            this.allNotificationsFragment = allNotificationsFragment;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickphotoFromGallery();
                } else {
                    Toast.makeText(MainActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_CAMERA:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(MainActivity.this, "Grand permission to pick photo from camera!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_CAMERA_AND_WRITE_STORAGE:{
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(MainActivity.this, "Grand permissions to pick photo from camera!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_WRITE_STORAGE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(MainActivity.this, "Grand permission to pick photo from camera!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    public boolean checkReadStoragePermission(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public boolean checkCameraPermission(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public boolean checkCameraPermissions(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public boolean checkWriteStoragePermision(Activity context) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Subscribe
    public void openAddDiveSpotActivity(OpenAddDiveSpotActivity event) {
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            AddDiveSpotActivity.showForResult(this, Constants.MAIN_ACTIVITY_ACTVITY_REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY, true);
        } else {
            isTryToOpenAddDiveSpotActivity = true;
            Intent intent = new Intent(MainActivity.this, SocialNetworks.class);
            startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN);
        }
    }

    private void clearFilterSharedPrefences() {
        SharedPreferenceHelper.setObject("");
        SharedPreferenceHelper.setLevel("");
    }
}

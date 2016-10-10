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
import com.crashlytics.android.Crashlytics;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.RegisterResponse;
import com.ddscanner.entities.SignInType;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.CloseInfoWindowEvent;
import com.ddscanner.events.CloseListEvent;
import com.ddscanner.events.GetNotificationsEvent;
import com.ddscanner.events.InfowWindowOpenedEvent;
import com.ddscanner.events.InternetConnectionClosedEvent;
import com.ddscanner.events.ListOpenedEvent;
import com.ddscanner.events.LoadUserProfileInfoEvent;
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
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.MainActivityPagerAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.fragments.ActivityNotificationsFragment;
import com.ddscanner.ui.fragments.AllNotificationsFragment;
import com.ddscanner.ui.fragments.NotificationsFragment;
import com.ddscanner.ui.fragments.ProfileFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

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

    private boolean loggedInDuringLastOnStart;
    private boolean needToClearDefaultAccount;

    private LoginResultListener loginResultListener = new LoginResultListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG, "onCreate");
        isHasInternetConnection = getIntent().getBooleanExtra(Constants.IS_HAS_INTERNET, false);
        clearFilterSharedPreferences();
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
        DDScannerApplication.bus.post(new LoadUserProfileInfoEvent());
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
                        // TODO Implement
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
                                sendLoginRequest(SharedPreferenceHelper.getUserAppId(), SignInType.FACEBOOK, loginResult.getAccessToken().getToken());
                            }
                        }).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // TODO Implement
                    }

                    @Override
                    public void onError(FacebookException error) {
                        // TODO Implement
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
        switch (position) {
            case 0:
                showSearchFilterMenuItems();
                Helpers.hideKeyboard(this);
                break;
            case 1:
                DDScannerApplication.bus.post(new GetNotificationsEvent());
                hideSearchFilterMenuItems();
                Helpers.hideKeyboard(this);
                break;
            case 2:
                DDScannerApplication.bus.post(new LoadUserProfileInfoEvent());
                EventsTracker.trackUserProfileView();
                hideSearchFilterMenuItems();
                break;
        }
    }

    private void showSearchFilterMenuItems() {
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

    private void hideSearchFilterMenuItems() {
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
                    if (acct == null) {
                        Helpers.handleUnexpectedServerError(getSupportFragmentManager(), "google_login", "result.getSignInAccount() returned null");
                    } else {
                        String idToken = acct.getIdToken();
                        sendLoginRequest(SharedPreferenceHelper.getUserAppId(), SignInType.GOOGLE, idToken);
                    }
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

    private void sendLoginRequest(String appId, SignInType signInType, String token) {
        loginResultListener.setToken(token);
        loginResultListener.setSocialNetwork(signInType);
        materialDialog.show();
        DDScannerApplication.getDdScannerRestClient().postLogin(appId, signInType, token, loginResultListener);
    }

//    private void validateIdToken() {
//        Call<ResponseBody> call = RestClient.getGoogleApisServiceInstance().getTokenInfo("eyJhbGciOiJSUzI1NiIsImtpZCI6IjIyZjJiN2RjMzI5ZWIxMWU0ZTA1MjEzMjRjNjZiZGJmNjNiYzNhNzIifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdWQiOiIxOTU3MDY5MTQ2MTgtaXN0OWY4aW5zNDg1azJnZ2xib21nZHA0bDJwbjU3aXEuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDA4MjExMDgxMDk2NzM2OTc1NjMiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXpwIjoiMTk1NzA2OTE0NjE4LXUydGlsdTZ0cGU3bDA2bzNjZzcwNWJlZzB0bmdqMmdpLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiZW1haWwiOiJteWxhbmd2dWlAZ21haWwuY29tIiwiaWF0IjoxNDY4MTYxMTk0LCJleHAiOjE0NjgxNjQ3OTQsIm5hbWUiOiJMYW5nIFZ1aSIsImdpdmVuX25hbWUiOiJMYW5nIiwiZmFtaWx5X25hbWUiOiJWdWkiLCJsb2NhbGUiOiJlbiJ9.PcDkSOYFFv8TvkPM9LfZ2F5TaNOk6aLy0x8kqci4RLmisWAbomnnBtlPhZ-KVgsmjvTevwKb8DDCkJysxLkngh8ZjuPj-wDbNrPaHQMiawFW1pABorygWLU7fbd2ddnj6lY7DabeI1YW_fnux1Ep_36WUULGyz5YPstA0zsZNUWC9ndu_m-kTnlL-di5WXaqLadD9YMisZMStgYrTzr7LCwtg_x1A5xo2zCr5wISjI7eQN4xoRX9kff7vCoJMCUPmK-jyBnM62DRelxEELvhVppUl5ypqY6GHkajA8t8viug7ZdPbUh9i8OlGY3hCvCFilNALVDVRLZFbjXqTZPQ6Q");
//        call.enqueue(new BaseCallbackOld() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.i(TAG, "createAddDiveSpotRequest success");
//                if (!response.isSuccessful()) {
//                    String responseString = "";
//                    try {
//                        responseString = response.errorBody().string();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    LogUtils.i("createAddDiveSpotRequest response body is " + responseString);
//                } else {
//                    if (response.raw().code() == 200) {
//                        String responseString = "";
//                        try {
//                            responseString = response.body().string();
//                            LogUtils.i("createAddDiveSpotRequest response body is " + responseString);
//                        } catch (IOException e) {
//
//                        }
//
//                    }
//                }
//            }
//
//            @Override
//            public void onConnectionFailure() {
//                DialogUtils.showConnectionErrorDialog(MainActivity.this);
//            }
//        });
//    }
//
//    private void refreshIdTokenSilently() {
//        if (mGoogleApiClient == null) {
//            Log.i(TAG, "refreshIdTokenSilently initGoogleLoginManager");
//            initGoogleLoginManager();
//        }
//        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (pendingResult.isDone()) {
//            // There's immediate result available.
//            GoogleSignInAccount acct = pendingResult.get().getSignInAccount();
//            String idToken = acct.getIdToken();
//            Log.i(TAG, "refreshIdTokenSilently pendingResult.isDone idToken = " + idToken);
//        } else {
//            // There's no immediate result ready, displays some progress indicator and waits for the
//            // async callback.
//            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(@NonNull GoogleSignInResult result) {
//                    Log.i(TAG, "refreshIdTokenSilently onResult result.isSuccess = " + result.isSuccess());
//                    if (result.isSuccess()) {
//                        GoogleSignInAccount acct = result.getSignInAccount();
//                        String idToken = acct.getIdToken();
//                        Log.i(TAG, "refreshIdTokenSilently onResult idToken = " + idToken);
//                    }
//                }
//            });
//        }
//
//    }

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
                    DDScannerApplication.bus.post(new PlaceChoosedEvent(new LatLngBounds(new LatLng(event.getLocation().getLatitude() - 1, event.getLocation().getLongitude() - 1), new LatLng(event.getLocation().getLatitude() + 1, event.getLocation().getLongitude() + 1))));
                    break;
            }
        }
    }

    @Subscribe
    public void changeProfileFragmentView(TakePhotoFromCameraEvent event) {
        if (checkWriteStoragePermission()) {
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
        MultiImageSelector.create().showCamera(false).multi().count(1).start(this, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PICK_PHOTO);
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN);
    }

    @Subscribe
    public void openLoginWindowToAdd(OpenAddDsActivityAfterLogin event) {
        isTryToOpenAddDiveSpotActivity = true;
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(MainActivity.this, "Grand permission to pick photo from camera!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_CAMERA_AND_WRITE_STORAGE: {
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(MainActivity.this, "Grand permissions to pick photo from camera!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_WRITE_STORAGE: {
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

    public boolean checkWriteStoragePermission() {
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN);
        }
    }

    private void clearFilterSharedPreferences() {
        SharedPreferenceHelper.setObject("");
        SharedPreferenceHelper.setLevel("");
    }

    private class LoginResultListener implements DDScannerRestClient.ResultListener<RegisterResponse> {

        private String token;
        private SignInType socialNetwork;

        public void setToken(String token) {
            this.token = token;
        }

        void setSocialNetwork(SignInType socialNetwork) {
            this.socialNetwork = socialNetwork;
        }

        @Override
        public void onSuccess(RegisterResponse result) {
            materialDialog.dismiss();
            SharedPreferenceHelper.setToken(token);
            SharedPreferenceHelper.setSn(socialNetwork.getName());
            SharedPreferenceHelper.setIsUserSignedIn(true, socialNetwork);
            SharedPreferenceHelper.setUserServerId(result.getUser().getId());
            DDScannerApplication.bus.post(new LoggedInEvent());
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    Crashlytics.log("801 error on identify");
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
            }
        }
    }
}

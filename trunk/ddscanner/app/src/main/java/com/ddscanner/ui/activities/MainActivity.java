package com.ddscanner.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
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
import com.ddscanner.entities.request.IdentifyRequest;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.CloseInfoWindowEvent;
import com.ddscanner.events.CloseListEvent;
import com.ddscanner.events.InfowWindowOpenedEvent;
import com.ddscanner.events.InstanceIDReceivedEvent;
import com.ddscanner.events.InternetConnectionClosedEvent;
import com.ddscanner.events.ListOpenedEvent;
import com.ddscanner.events.LocationReadyEvent;
import com.ddscanner.events.LoggedInEvent;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.events.LoginViaFacebookClickEvent;
import com.ddscanner.events.LoginViaGoogleClickEvent;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.PlaceChoosedEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.services.RegistrationIntentService;
import com.ddscanner.ui.adapters.MainActivityPagerAdapter;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
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

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by lashket on 20.4.16.
 */
public class MainActivity extends BaseAppCompatActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int REQUEST_CODE_PLACE_AUTOCOMPLETE = 1000;
    private static final int REQUEST_CODE_PLAY_SERVICES_RESOLUTION = 9000;
    private static final int REQUEST_CODE_PICK_PHOTO = 8000;
    private static final int REQUEST_CODE_LOGIN = 7000;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 6000;
    private static final int REQUEST_CODE_TURN_ON_LOCATION_PROVIDERS = 5000;

    private Uri capturedImageUri;

    private TabLayout toolbarTabLayout;
    private ViewPager mainViewPager;
    private PercentRelativeLayout menuItemsLayout;
    private ImageView searchLocationBtn;
    private ImageView btnFilter;
    private MainActivityPagerAdapter mainViewPagerAdapter;
    private ImageView imageView;
    private Helpers helpers = new Helpers();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isHasInternetConnection = getIntent().getBooleanExtra(Constants.IS_HAS_INTERNET, false);
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
        playServices();
        getLocation(Constants.REQUEST_CODE_MAIN_ACTIVITY_GET_LOCATION_ON_ACTIVITY_START);
        if (SharedPreferenceHelper.isFirstLaunch()) {
            identifyUser("", "");
            SharedPreferenceHelper.setIsFirstLaunch(false);
        }
    }

    private void initGoogleLoginManager() {
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
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        mGoogleApiClient.clearDefaultAccountAndReconnect();
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

    /*Google plus*/
    private void googleSignIn() {
        if (mGoogleApiClient == null) {
            initGoogleLoginManager();
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Constants.REQUEST_CODE_NEED_TO_LOGIN);
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
    }

    private void findViews() {
        materialDialog = helpers.getMaterialDialog(this);
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

    public static void show(Context context, boolean isHasInternet, boolean isHasLocation) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.IS_HAS_INTERNET, isHasInternet);
        intent.putExtra(Constants.IS_LOCATION, isHasLocation);
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
//        if ((position == 2 || position == 1) && !SharedPreferenceHelper.isUserLoggedIn()) {
//            positionToScroll = position;
//            Intent intent = new Intent(MainActivity.this, SocialNetworks.class);
//            startActivityForResult(intent, REQUEST_CODE_LOGIN);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_location_menu_button:
//                materialDialog.show();
//                openSearchLocationWindow();

                SearchSpotOrLocationActivity.showForResult(MainActivity.this, REQUEST_CODE_PLACE_AUTOCOMPLETE);
                break;
            case R.id.filter_menu_button:
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intent);
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
            case REQUEST_CODE_PLACE_AUTOCOMPLETE:
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
                    case Constants.SEARCH_ACTIVITY_RESULT_CODE_MY_LOCATION:
                        getLocation(Constants.MAIN_ACTIVITY_REQUEST_CODE_GO_TO_MY_LOCATION);
                        break;
                }

                break;
            case REQUEST_CODE_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    mainViewPagerAdapter.setProfileImage(capturedImageUri);
                }
                break;
            case REQUEST_CODE_PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    mainViewPagerAdapter.setProfileImage(uri);
                }
                break;
            case REQUEST_CODE_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (isTryToOpenAddDiveSpotActivity) {
                        AddDiveSpotActivity.show(this);
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
            case Constants.REQUEST_CODE_NEED_TO_LOGIN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());
                if (result.isSuccess()) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    String idToken = acct.getIdToken();
                    sendRegisterRequest(putTokensToMap(SharedPreferenceHelper.getUserAppId(), "go", idToken), SignInType.GOOGLE);
                }
                break;
            default:
                if (facebookCallbackManager != null) {
                    facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    public void identifyUser(String lat, String lng) {
        Call<ResponseBody> call = RestClient.getServiceInstance().identify(getUserIdentifyData(lat, lng));
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                        Log.i(TAG, "identifyUser responseString = " + responseString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });
    }

    private IdentifyRequest getUserIdentifyData(String lat, String lng) {
        IdentifyRequest identifyRequest = new IdentifyRequest();
        identifyRequest.setAppId(SharedPreferenceHelper.getUserAppId());
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            identifyRequest.setSocial(SharedPreferenceHelper.getSn());
            identifyRequest.setToken(SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                identifyRequest.setSecret(SharedPreferenceHelper.getSecret());
            }
        }
        identifyRequest.setpush(SharedPreferenceHelper.getGcmId());
        if (lat != null && lng != null) {
            identifyRequest.setLat(lat);
            identifyRequest.setLng(lng);
        }
        identifyRequest.setType("android");
        return identifyRequest;
    }

    public void playServices() {
        if (!SharedPreferenceHelper.isUserAppIdReceived() && checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, REQUEST_CODE_PLAY_SERVICES_RESOLUTION).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
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
        DDScannerApplication.bus.unregister(this);
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DDScanner");
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, "DDS_" + timeStamp + ".png");
        capturedImageUri = Uri.fromFile(image);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE);
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
        Call<ResponseBody> call = RestClient.getServiceInstance().registerUser(userData);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
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
                        helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(MainActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO Handle errors
                materialDialog.dismiss();
            }
        });
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
                case Constants.REQUEST_CODE_MAIN_ACTIVITY_GET_LOCATION_ON_ACTIVITY_START:
                    if (SharedPreferenceHelper.isUserAppIdReceived()) {
                        identifyUser(String.valueOf(event.getLocation().getLatitude()), String.valueOf(event.getLocation().getLongitude()));
                        DDScannerApplication.bus.post(new PlaceChoosedEvent(new LatLngBounds(new LatLng(event.getLocation().getLatitude() - 1, event.getLocation().getLongitude() - 1), new LatLng(event.getLocation().getLatitude() + 1, event.getLocation().getLongitude() + 1))));
                    }
                    break;
                case Constants.MAIN_ACTIVITY_REQUEST_CODE_GO_TO_MY_LOCATION:
                    DDScannerApplication.bus.post(new PlaceChoosedEvent(new LatLngBounds(new LatLng(event.getLocation().getLatitude() - 1, event.getLocation().getLongitude() - 1), new LatLng(event.getLocation().getLatitude() + 1, event.getLocation().getLongitude() + 1))));
                    break;
            }
        }
    }

    @Subscribe
    public void onAppInstanceIdReceived(InstanceIDReceivedEvent event) {
        getLocation(Constants.REQUEST_CODE_MAIN_ACTIVITY_GET_LOCATION_ON_ACTIVITY_START);
    }

    @Subscribe
    public void changeProfileFragmentView(TakePhotoFromCameraEvent event) {
        dispatchTakePictureIntent();
    }

    @Subscribe
    public void pickPhotoFromGallery(PickPhotoFromGallery event) {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_CODE_PICK_PHOTO);
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
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    @Subscribe
    public void openLoginWindowToAdd(OpenAddDsActivityAfterLogin event) {
        isTryToOpenAddDiveSpotActivity = true;
        Intent intent = new Intent(MainActivity.this, SocialNetworks.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    @Subscribe
    public void onLoggedIn(LoggedInEvent event) {
        mainViewPagerAdapter.notifyDataSetChanged();
        setupTabLayout();
    }

    @Subscribe
    public void onLoggedOut(LoggedOutEvent event) {
        mainViewPagerAdapter.notifyDataSetChanged();
        setupTabLayout();
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
}

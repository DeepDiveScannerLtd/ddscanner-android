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
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.SignInType;
import com.ddscanner.entities.SignUpResponseEntity;
import com.ddscanner.events.ChangeAccountEvent;
import com.ddscanner.events.ChangeLoginViewEvent;
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
import com.ddscanner.events.LoginSignUpViaEmailEvent;
import com.ddscanner.events.LoginViaFacebookClickEvent;
import com.ddscanner.events.LoginViaGoogleClickEvent;
import com.ddscanner.events.NewDiveSpotAddedEvent;
import com.ddscanner.events.OpenAddDiveSpotActivity;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.PlaceChoosedEvent;
import com.ddscanner.events.ShowLoginActivityForAddAccount;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.SignupLoginButtonClicked;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.add.AddDiveSpotActivity;
import com.ddscanner.screens.profile.DiveCenterProfileFragment;
import com.ddscanner.ui.adapters.MainActivityPagerAdapter;
import com.ddscanner.ui.dialogs.ActionSuccessDialogFragment;
import com.ddscanner.ui.dialogs.ChangeAccountBottomDialog;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.fragments.ActivityNotificationsFragment;
import com.ddscanner.ui.fragments.AllNotificationsFragment;
import com.ddscanner.ui.fragments.NotificationsFragment;
import com.ddscanner.screens.profile.ProfileFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends BaseAppCompatActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getName();

    private Uri capturedImageUri;

    private TabLayout toolbarTabLayout;
    private ViewPager mainViewPager;
    private PercentRelativeLayout menuItemsLayout;
    private ImageView searchLocationBtn;
    private ImageView btnFilter;
    private ImageView changeAccountButton;
    private MainActivityPagerAdapter mainViewPagerAdapter;
    private ProfileFragment profileFragment;
    private NotificationsFragment notificationsFragment;
    private ActivityNotificationsFragment activityNotificationsFragment;
    private AllNotificationsFragment allNotificationsFragment;
    private DiveCenterProfileFragment diveCenterProfileFragment;
    private ImageView imageView;
    private boolean isHasInternetConnection;
    private boolean isHasLocation;
    private MaterialDialog materialDialog;
    private boolean isTryToOpenAddDiveSpotActivity = false;
    private boolean isDiveSpotInfoWindowShown = false;
    private boolean isDiveSpotListIsShown = false;
    private boolean isSignupClicked = false;
    private int positionToScroll;
    private PercentRelativeLayout acountChangeLayout;
    private ChangeAccountBottomDialog changeAccountBottomDialog;

    private CallbackManager facebookCallbackManager;
    private GoogleApiClient mGoogleApiClient;

    private boolean loggedInDuringLastOnStart;
    private boolean needToClearDefaultAccount;

    private DDScannerRestClient.ResultListener<SignUpResponseEntity> signUpLoginResultListener = new DDScannerRestClient.ResultListener<SignUpResponseEntity>() {
        @Override
        public void onSuccess(SignUpResponseEntity result) {
            materialDialog.dismiss();
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setToken(result.getToken());
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsUserSignedIn(true, SignInType.EMAIL);
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setUserServerId(result.getId());
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setActiveUserType(result.getType());
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
                case ENTITY_NOT_FOUND_404:
                    ActionSuccessDialogFragment.show(MainActivity.this, R.string.title_pass_incorrect, R.string.pass_incorrect);
                    break;
                default:
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        isHasInternetConnection = getIntent().getBooleanExtra(Constants.IS_HAS_INTERNET, false);
        clearFilterSharedPreferences();
        startActivity();
        Log.i(TAG, FirebaseInstanceId.getInstance().getToken());
       // DDScannerApplication.getInstance().getSharedPreferenceHelper().clear();
        if (!isHasInternetConnection) {
            Log.i(TAG, "internetConnectionClosed 2");
            InternetClosedActivity.show(this);
        }
        loggedInDuringLastOnStart = DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn();
    }

    private void startActivity() {
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);
        findViews();
        setUi();
        searchLocationBtn.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        setupTabLayout();
        mainViewPager.setCurrentItem(0);
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
                                sendLoginRequest(SignInType.FACEBOOK, loginResult.getAccessToken().getToken());
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
        acountChangeLayout = (PercentRelativeLayout) findViewById(R.id.account_change_layout);
        changeAccountButton = (ImageView) findViewById(R.id.change_account);
        changeAccountButton.setOnClickListener(this);
    }

    private void setupTabLayout() {
        toolbarTabLayout.getTabAt(2).setCustomView(R.layout.tab_profile_item);
        toolbarTabLayout.getTabAt(1).setCustomView(R.layout.tab_notification_item);
        toolbarTabLayout.getTabAt(0).setCustomView(R.layout.tab_map_item);
    }

    public static void show(Context context, boolean isHasInternet) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.IS_HAS_INTERNET, isHasInternet);
        context.startActivity(intent);
    }

    @Override
    public void onPageSelected(int position) {
        if (position != 0 && isSignupClicked) {
            DDScannerApplication.bus.post(new ChangeLoginViewEvent());
            isSignupClicked = !isSignupClicked;
        }
        switch (position) {
            case 0:
                showSearchFilterMenuItems();
                changeVisibilityChangeAccountLayout(View.GONE);
                Helpers.hideKeyboard(this);
                break;
            case 1:
                DDScannerApplication.bus.post(new GetNotificationsEvent());
                hideSearchFilterMenuItems();
                changeVisibilityChangeAccountLayout(View.GONE);
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
                        if (mainViewPager.getCurrentItem() == 2 && DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
                            changeVisibilityChangeAccountLayout(View.VISIBLE);
                        }
                    }
                });
    }

    private void changeVisibilityChangeAccountLayout(int visibility) {
        acountChangeLayout.setVisibility(visibility);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_location_menu_button:
//                materialDialog.showForResult();
//                openSearchLocationWindow();

                SearchSpotOrLocationActivity.showForResult(MainActivity.this, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PLACE_AUTOCOMPLETE);
                //        EventsTracker.trackSearchActivityOpened();
                break;
            case R.id.filter_menu_button:
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intent);
                //        EventsTracker.trackFiltersActivityOpened();
                break;
            case R.id.change_account:
                changeAccountBottomDialog = new ChangeAccountBottomDialog();
                changeAccountBottomDialog.show(getSupportFragmentManager(), "");
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
                    mainViewPagerAdapter.setProfileImage(Helpers.getPhotosFromIntent(data, this).get(0));
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
                        sendLoginRequest(SignInType.GOOGLE, idToken);
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
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN_TO_ADD_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    mainViewPagerAdapter.notifyDataSetChanged();
                    mainViewPager.destroyDrawingCache();
                    setupTabLayout();
                    DDScannerApplication.bus.post(new LoadUserProfileInfoEvent());
                    changeAccountBottomDialog.dismiss();
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
        Log.i(TAG, "onStart");
        DDScannerApplication.bus.register(this);
        if (loggedInDuringLastOnStart != DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            mainViewPagerAdapter.notifyDataSetChanged();
            setupTabLayout();
            loggedInDuringLastOnStart = DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        DDScannerApplication.bus.unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        AppEventsLogger.deactivateApp(this);
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        AppEventsLogger.activateApp(this);
        DDScannerApplication.activityResumed();
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
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

    private void sendLoginRequest(SignInType signInType, String token) {
        materialDialog.show();
        // TODO Debug: switch calls after tests
        DDScannerApplication.getInstance().getDdScannerRestClient().postUserLogin(null, null, "21", "32", signInType, token, signUpLoginResultListener);
//        DDScannerApplication.getDdScannerRestClient().postLogin(FirebaseInstanceId.getInstance().getId(), signInType, token, loginResultListener);
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
        Log.i(TAG, "location check: onLocationReady request codes = " + event.getRequestCodes());
        for (Integer code : event.getRequestCodes()) {
            switch (code) {
                case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_GO_TO_MY_LOCATION:
                    DDScannerApplication.bus.post(new PlaceChoosedEvent(new LatLngBounds(new LatLng(event.getLocation().getLatitude() - 1, event.getLocation().getLongitude() - 1), new LatLng(event.getLocation().getLatitude() + 1, event.getLocation().getLongitude() + 1))));
                    break;
            }
        }
    }

    @Subscribe
    public void onLoginViaEmail(LoginSignUpViaEmailEvent event) {
        //TODO remove hardcoded coordinates
        materialDialog.show();
        if (event.isSignUp()) {
            DDScannerApplication.getInstance().getDdScannerRestClient().postUserSignUp(event.getEmail(), event.getPassword(), event.getUserType(), "23", "22", signUpLoginResultListener);
        } else {
            DDScannerApplication.getInstance().getDdScannerRestClient().postUserLogin(event.getEmail(), event.getPassword(), "24", "25", null, null, signUpLoginResultListener);
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PICK_PHOTO);
//        MultiImageSelector.create().showCamera(false).multi().count(1).start(this, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PICK_PHOTO);
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
        Log.i(TAG, "internetConnectionClosed 1");
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
            if (!isSignupClicked) {
                DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
                return;
            }
            DDScannerApplication.bus.post(new ChangeLoginViewEvent());
            isSignupClicked = !isSignupClicked;
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

    public void setDiveCenterProfileFragment(DiveCenterProfileFragment diveCenterProfileFragment) {
        if (mainViewPagerAdapter != null) {
            mainViewPagerAdapter.setDiveCenterProfileFragment(diveCenterProfileFragment);
        } else {
            this.diveCenterProfileFragment = diveCenterProfileFragment;
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
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            AddDiveSpotActivity.showForResult(this, Constants.MAIN_ACTIVITY_ACTVITY_REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY, true);
        } else {
            isTryToOpenAddDiveSpotActivity = true;
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN);
        }
    }

    private void clearFilterSharedPreferences() {
        DDScannerApplication.getInstance().getSharedPreferenceHelper().setObject("");
        DDScannerApplication.getInstance().getSharedPreferenceHelper().setLevel("");
    }


    @Subscribe
    public void chengeLoginView(SignupLoginButtonClicked event) {
        isSignupClicked = event.isShowing();
    }

    @Subscribe
    public void showLoginActivtyToAddUser(ShowLoginActivityForAddAccount event) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN_TO_ADD_ACCOUNT);
    }

    @Subscribe
    public void changeActiveAccount(ChangeAccountEvent event) {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == 0) {
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setActiveUserType(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUser().getType());
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setToken(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUser().getToken());
        } else {
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setActiveUserType(0);
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setToken(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLoggedDiveCenter().getToken());
        }
        mainViewPagerAdapter.notifyDataSetChanged();
        mainViewPager.destroyDrawingCache();
        setupTabLayout();
        DDScannerApplication.bus.post(new LoadUserProfileInfoEvent());
        changeAccountBottomDialog.dismiss();
    }

}

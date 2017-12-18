package com.ddscanner.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.BaseUser;
import com.ddscanner.entities.NotificationsCountEntity;
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
import com.ddscanner.events.LoginSignUpViaEmailEvent;
import com.ddscanner.events.LoginViaFacebookClickEvent;
import com.ddscanner.events.LoginViaGoogleClickEvent;
import com.ddscanner.events.LogoutEvent;
import com.ddscanner.events.NewDiveSpotAddedEvent;
import com.ddscanner.events.OpenAddDiveSpotActivity;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.events.OpenDiveSpotDetailsActivityEvent;
import com.ddscanner.events.OpenUserProfileActivityFromNotifications;
import com.ddscanner.events.PlaceChoosedEvent;
import com.ddscanner.events.ShowLoginActivityForAddAccount;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.SignupLoginButtonClicked;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.add.AddDiveSpotActivity;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.screens.notifications.ActivityNotificationsFragment;
import com.ddscanner.screens.notifications.DiveCenterNotificationsFragment;
import com.ddscanner.screens.notifications.DiverNotificationsFragment;
import com.ddscanner.screens.notifications.PersonalNotificationsFragment;
import com.ddscanner.screens.profile.divecenter.DiveCenterProfileFragment;
import com.ddscanner.screens.profile.edit.divecenter.search.SearchDiveCenterActivity;
import com.ddscanner.screens.profile.user.ProfileFragment;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.ui.adapters.MainActivityPagerAdapter;
import com.ddscanner.ui.dialogs.ChangeAccountBottomDialog;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.NotificationHelper;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
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

import java.util.Arrays;

import me.toptas.fancyshowcase.DismissListener;

@SuppressWarnings("deprecation")
public class MainActivity extends BaseAppCompatActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, ConfirmationDialogClosedListener{

    private static final String TAG = MainActivity.class.getName();

    private TabLayout toolbarTabLayout;
    private ViewPager mainViewPager;
    private PercentRelativeLayout menuItemsLayout;
    private RelativeLayout searchLocationBtn;
    private RelativeLayout btnFilter;
    private ImageView iconFilter;
    private ImageView iconSearch;
    private MainActivityPagerAdapter mainViewPagerAdapter;
    private ProfileFragment profileFragment;
    private DiverNotificationsFragment diverNotificationsFragment;
    private ActivityNotificationsFragment activityNotificationsFragment;
    private PersonalNotificationsFragment allNotificationsFragment;
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

    private DismissListener searchDismissListener = new DismissListener() {
        @Override
        public void onDismiss(String id) {
            DDScannerApplication.getInstance().getTutorialHelper().showFilterTutorial(MainActivity.this, iconFilter, filterDismissListener);
        }

        @Override
        public void onSkipped(String id) {

        }
    };

    private DismissListener filterDismissListener = new DismissListener() {
        @Override
        public void onDismiss(String id) {
            DDScannerApplication.getInstance().getTutorialHelper().showMapListTutorial(MainActivity.this, mainViewPagerAdapter.getMapListFragment().getMapListFAB(), mapDismissListener);
        }

        @Override
        public void onSkipped(String id) {

        }
    };

    private DismissListener mapDismissListener = new DismissListener() {
        @Override
        public void onDismiss(String id) {
            DDScannerApplication.getInstance().getTutorialHelper().showGoToPhuketTitle(MainActivity.this, goToPhuketDismissListener);
        }

        @Override
        public void onSkipped(String id) {

        }
    };

    private DismissListener goToPhuketDismissListener = new DismissListener() {
        @Override
        public void onDismiss(String id) {
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustShowSelectAPin(true);
            mainViewPagerAdapter.getMapListFragment().moveCameraToPhuket();
        }

        @Override
        public void onSkipped(String id) {

        }
    };

    private SigningUserResultListener signUpResultListener = new SigningUserResultListener(true);
    private SigningUserResultListener signInResultListener = new SigningUserResultListener(false);

    private DDScannerRestClient.ResultListener<Void> notificationReadResultListner = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            DDScannerApplication.getInstance().clearNotificationsContainer();
            getIsHasNewotifications();
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {

        }
    };

    private DDScannerRestClient.ResultListener<NotificationsCountEntity> newotificationsCountEntity = new DDScannerRestClient.ResultListener<NotificationsCountEntity>() {
        @Override
        public void onSuccess(NotificationsCountEntity result) {
            if (result.getYou() > 0) {
                toolbarTabLayout.getTabAt(1).setCustomView(null);
                toolbarTabLayout.getTabAt(1).setCustomView(R.layout.tab_notification_item_new);
                if (mainViewPagerAdapter.getDiverNotificationsFragment() != null) {
                    mainViewPagerAdapter.getDiverNotificationsFragment().showPersonalNotificationsFragment();
                }
            } else {
                toolbarTabLayout.getTabAt(1).setCustomView(null);
                toolbarTabLayout.getTabAt(1).setCustomView(R.layout.tab_notification_item);
            }
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {

        }
    };

    private DDScannerRestClient.ResultListener<Void> instructorsResultListene = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {

        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        NotificationHelper.setupAlarmManager(this);
        DDScannerApplication.getInstance().getSharedPreferenceHelper().clearFilters();
        setContentView(R.layout.activity_main);
        findViews();
        setUi();
        searchLocationBtn.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        mainViewPager.setCurrentItem(0);
        DDScannerApplication.bus.post(new LoadUserProfileInfoEvent());
        loggedInDuringLastOnStart = SharedPreferenceHelper.getIsUserSignedIn();
    }

    private void initGoogleLoginManager() {
        needToClearDefaultAccount = true;
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_login_manager_key))
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
                        GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> sendLoginRequest(SignInType.FACEBOOK, loginResult.getAccessToken().getToken())).executeAsync();
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
        if (diverNotificationsFragment != null) {
            mainViewPagerAdapter.setDiverNotificationsFragment(diverNotificationsFragment);
        }
        if (activityNotificationsFragment != null) {
            mainViewPagerAdapter.setActivityNotificationsFragment(activityNotificationsFragment);
        }
        if (allNotificationsFragment != null) {
            mainViewPagerAdapter.setAllNotificationsFragment(allNotificationsFragment);
        }
        setupTabLayout();
    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        toolbarTabLayout = findViewById(R.id.toolbar_tablayout);
        mainViewPager = findViewById(R.id.main_viewpager);
        menuItemsLayout = findViewById(R.id.menu_items_layout);
        searchLocationBtn = findViewById(R.id.search_location_menu_button);
        btnFilter = findViewById(R.id.filter_menu_button);
        acountChangeLayout = findViewById(R.id.account_change_layout);
        iconFilter = findViewById(R.id.icon_filter);
        iconSearch = findViewById(R.id.icon_search);
        ImageView changeAccountButton = findViewById(R.id.change_account);
        changeAccountButton.setOnClickListener(this);
    }

    private void setupTabLayout() {
        toolbarTabLayout.getTabAt(2).setCustomView(R.layout.tab_profile_item);
        toolbarTabLayout.getTabAt(1).setCustomView(R.layout.tab_notification_item);
        toolbarTabLayout.getTabAt(0).setCustomView(R.layout.tab_map_item);
        if (SharedPreferenceHelper.getIsNeedToShowTutorial() && SharedPreferenceHelper.getActiveUserType() != SharedPreferenceHelper.UserType.DIVECENTER) {
            SharedPreferenceHelper.setIsNeedToShowTutorial();
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustToShowDiveSpotDetailsTutorial(true);
        new Handler().postDelayed(() ->  DDScannerApplication.getInstance().getTutorialHelper().showSearchTutorial(this, iconSearch, searchDismissListener), 1000);
//            new Handler().postDelayed(() -> DDScannerApplication.getInstance().getTutorialHelper().showNotificationTutorial(this, toolbarTabLayout.getTabAt(1).getCustomView().findViewById(R.id.notification_image_view), notificationsTutorialDismissListener), 3500);
            return;
        }
        getIsHasNewotifications();
    }

    private void getIsHasNewotifications() {
        if (SharedPreferenceHelper.getIsUserSignedIn()) {
            DDScannerApplication.getInstance().getDdScannerRestClient(this).getNewNotificationsCount(newotificationsCountEntity);
            return;
        }
        toolbarTabLayout.getTabAt(1).setCustomView(null);
        toolbarTabLayout.getTabAt(1).setCustomView(R.layout.tab_notification_item);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
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
                EventsTracker.trackDiveSpotMapView();
                showSearchFilterMenuItems();
                changeVisibilityChangeAccountLayout(View.GONE);
                Helpers.hideKeyboard(this);
                if (DDScannerApplication.getInstance().getNotificationsContainer().size() > 0) {
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).postNotificationsRead(notificationReadResultListner, DDScannerApplication.getInstance().getNotificationsContainer());
                }
                break;
            case 1:
                DDScannerApplication.bus.post(new GetNotificationsEvent());
                hideSearchFilterMenuItems();
                changeVisibilityChangeAccountLayout(View.GONE);
                Helpers.hideKeyboard(this);
                break;
            case 2:
                DDScannerApplication.bus.post(new LoadUserProfileInfoEvent());
                EventsTracker.trackMyProfileView();
                hideSearchFilterMenuItems();
                if (DDScannerApplication.getInstance().getNotificationsContainer().size() > 0) {
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).postNotificationsRead(notificationReadResultListner, DDScannerApplication.getInstance().getNotificationsContainer());
                }
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
                        if (mainViewPager.getCurrentItem() == 2 && SharedPreferenceHelper.getIsUserSignedIn()) {
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

                SearchSpotOrLocationActivity.showForResult(MainActivity.this, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PLACE_AUTOCOMPLETE, false);
                //        EventsTracker.trackSearchActivityOpened();
                break;
            case R.id.filter_menu_button:
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_FILTERS);
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
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_FILTERS:
                EventsTracker.trackDiveSpotMapView();
                if (resultCode == RESULT_OK) {
                    iconFilter.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_ac_filter_full));
                }
                if (resultCode == RESULT_CODE_FILTERS_RESETED) {
                    iconFilter.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_ac_filter));
                }
                mainViewPagerAdapter.getMapListFragment().reloadDataAfterFilters();
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PLACE_AUTOCOMPLETE:
                materialDialog.dismiss();
                EventsTracker.trackDiveSpotMapView();
                switch (resultCode) {
                    case RESULT_OK:
                        final LatLngBounds place = data.getParcelableExtra(Constants.SEARCH_ACTIVITY_INTENT_KEY);
                        mainViewPagerAdapter.getMapListFragment().goToLatLngBounds(place);
                        break;
                    case ActivitiesRequestCodes.RESULT_CODE_SEARCH_ACTIVITY_MY_LOCATION:
                        Log.i(TAG, "MainActivity getLocation 2");
                        getLocation(ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_GO_TO_MY_LOCATION);
                        break;
                    case RESULT_CODE_DIVE_SPOT_ADDED:
                        diveSpotAdded(data);
                        break;
                }

                break;
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_SHOW_EDIT_PROFILE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    switch (SharedPreferenceHelper.getActiveUserType()) {
                        case DIVECENTER:
                            mainViewPagerAdapter.getDiveCenterProfileFragment().reloadData();
                            break;
                        case DIVER:
                        case INSTRUCTOR:
                            mainViewPagerAdapter.getProfileFragment().reloadData();
                            break;
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_SHOW_INSTRUCTORS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).postInstructorsSee(instructorsResultListene, data.getStringArrayListExtra("ids"));
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
            case ActivitiesRequestCodes.REQUEST_CODE_SHOW_USER_PROFILE_PHOTOS:
                if (resultCode == RESULT_OK) {
                    switch (SharedPreferenceHelper.getActiveUserType()) {
                        case DIVECENTER:
                            if (mainViewPagerAdapter.getDiveCenterProfileFragment() != null) {
                                mainViewPagerAdapter.getDiveCenterProfileFragment().reloadData();
                            }
                            break;
                        case DIVER:
                        case INSTRUCTOR:
                            if (mainViewPagerAdapter.getProfileFragment() != null) {
                                mainViewPagerAdapter.getProfileFragment().reloadData();
                            }
                            break;
                    }
//                    DDScannerApplication.bus.post(new LoadUserProfileInfoEvent());
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_CHOSE_GOOGLE_ACCOUNT:
                materialDialog.dismiss();
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
                    diveSpotAdded(data);
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
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PICK_DIVE_CENTER_FOR_INSTRUCTOR:
                if (resultCode == RESULT_OK) {
                    mainViewPagerAdapter.getProfileFragment().reloadData();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN_FOR_PROFILE_OR_NOTIFICATIONS:
                if (resultCode == RESULT_OK) {
                    if (mainViewPager != null && mainViewPagerAdapter != null) {
                        mainViewPagerAdapter.notifyDataSetChanged();
                        setupTabLayout();
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

    private void diveSpotAdded(Intent data) {
        UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.thank_you_title, R.string.success_added, false);
        LatLng latLng = data.getParcelableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_RESULT_LAT_LNG);
        String diveSpotId = data.getStringExtra(Constants.ADD_DIVE_SPOT_INTENT_DIVESPOT_ID);
        if (latLng != null) {
            DDScannerApplication.bus.post(new NewDiveSpotAddedEvent(latLng, diveSpotId));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
//        DDScannerApplication.bus.register(this);
        if (loggedInDuringLastOnStart != SharedPreferenceHelper.getIsUserSignedIn()) {
            mainViewPagerAdapter.notifyDataSetChanged();
            setupTabLayout();
            loggedInDuringLastOnStart = SharedPreferenceHelper.getIsUserSignedIn();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        newotificationsCountEntity.setCancelled(true);
//        DDScannerApplication.bus.unregister(this);
        if (mainViewPager.getCurrentItem() == 1) {
            DDScannerApplication.getInstance().getDdScannerRestClient(this).postNotificationsRead(notificationReadResultListner, DDScannerApplication.getInstance().getNotificationsContainer());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        AppEventsLogger.deactivateApp(this);
        DDScannerApplication.activityPaused();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        newotificationsCountEntity.setCancelled(false);
        AppEventsLogger.activateApp(this);
        DDScannerApplication.activityResumed();
        getIsHasNewotifications();
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isFiltersApplyied()) {
            iconFilter.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_ac_filter_full));
        } else {
            iconFilter.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_ac_filter));
        }
        if (SharedPreferenceHelper.getIsUserSignedIn()) {
            mainViewPagerAdapter.onLoggedIn();
        } else {
            mainViewPagerAdapter.onLoggedOut();
        }
    }

    private void sendLoginRequest(SignInType signInType, String token) {
        materialDialog.show();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postUserLogin(null, null, null, null, signInType, token, signInResultListener);
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
        materialDialog.show();
        if (event.isSignUp()) {
            DDScannerApplication.getInstance().getDdScannerRestClient(this).postUserSignUp(event.getEmail(), event.getPassword(), event.getUserType(), null, null, event.getName(), signUpResultListener);
        } else {
            DDScannerApplication.getInstance().getDdScannerRestClient(this).postUserLogin(event.getEmail(), event.getPassword(), null, null, null, null, signInResultListener);
        }
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

    public void setDiverNotificationsFragment(DiverNotificationsFragment diverNotificationsFragment) {
        if (mainViewPagerAdapter != null) {
            mainViewPagerAdapter.setDiverNotificationsFragment(diverNotificationsFragment);
        } else {
            this.diverNotificationsFragment = diverNotificationsFragment;
        }
    }

    public void setActivityNotificationsFragment(ActivityNotificationsFragment activityNotificationsFragment) {
        if (mainViewPagerAdapter != null) {
            mainViewPagerAdapter.setActivityNotificationsFragment(activityNotificationsFragment);
        } else {
            this.activityNotificationsFragment = activityNotificationsFragment;
        }
    }

    public void setAllNotificationsFragment(PersonalNotificationsFragment allNotificationsFragment) {
        if (mainViewPagerAdapter != null) {
            mainViewPagerAdapter.setAllNotificationsFragment(allNotificationsFragment);
        } else {
            this.allNotificationsFragment = allNotificationsFragment;
        }
    }

    public void setDiveCenterProfileFragment(DiveCenterProfileFragment diveCenterProfileFragment) {
        if (mainViewPagerAdapter != null) {
            mainViewPagerAdapter.setDiveCenterProfileFragment(diveCenterProfileFragment);
        }
    }

    public void setDiveCenterNotificationsFragment(DiveCenterNotificationsFragment diveCenterNotificationsFragment) {
        if (mainViewPagerAdapter != null) {
            mainViewPagerAdapter.setDiveCenterNotificationsFragment(diveCenterNotificationsFragment);
        }
    }

    @Subscribe
    public void openAddDiveSpotActivity(OpenAddDiveSpotActivity event) {
        if (SharedPreferenceHelper.getIsUserSignedIn()) {
            AddDiveSpotActivity.showForResult(this, Constants.MAIN_ACTIVITY_ACTVITY_REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY, true);
        } else {
            isTryToOpenAddDiveSpotActivity = true;
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN);
        }
    }

    @Subscribe
    public void chengeLoginView(SignupLoginButtonClicked event) {
        isSignupClicked = event.isShowing();
    }

    @Subscribe
    public void showLoginActivtyToAddUser(ShowLoginActivityForAddAccount event) {
        LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN_TO_ADD_ACCOUNT, true);
    }

    @Subscribe
    public void changeActiveAccount(ChangeAccountEvent event) {
        SharedPreferenceHelper.changeActiveUser(event.getId());
        mainViewPagerAdapter.notifyDataSetChanged();
        mainViewPager.destroyDrawingCache();
        setupTabLayout();
        DDScannerApplication.bus.post(new LoadUserProfileInfoEvent());
        changeAccountBottomDialog.dismiss();
    }

    @Override
    public void onNegativeDialogClicked() {

    }

    @Override
    public void onPositiveDialogClicked() {
        EventsTracker.trackYesInstructorClicked();
        SearchDiveCenterActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_PICK_DIVE_CENTER_FOR_INSTRUCTOR, false);
    }

    @Subscribe
    public void openUserProfileActivity(OpenUserProfileActivityFromNotifications event) {
        UserProfileActivity.show(this, event.getId(), event.getType());
    }

    class SigningUserResultListener extends DDScannerRestClient.ResultListener<SignUpResponseEntity> {

        private boolean isSignUp;

        SigningUserResultListener(boolean isSignUp) {
            this.isSignUp = isSignUp;
        }

        @Override
        public void onSuccess(SignUpResponseEntity result) {
            materialDialog.dismiss();
            materialDialog.dismiss();
            Log.i(TAG, "onSuccess: ");
            BaseUser baseUser = new BaseUser();
            baseUser.setActive(true);
            baseUser.setType(result.getType());
            baseUser.setToken(result.getToken());
            baseUser.setId(result.getId());
            DDScannerApplication.getInstance().getSharedPreferenceHelper().addUserToList(baseUser);
            DDScannerApplication.bus.post(new LoggedInEvent());
            DDScannerApplication.bus.post(new LoadUserProfileInfoEvent());
            if (mainViewPagerAdapter.getDiverNotificationsFragment() != null) {
                mainViewPagerAdapter.getDiverNotificationsFragment().getUserNotifications(false);
            }
            if (isSignUp && result.getType() != 0) {
                DialogHelpers.showInstructorConfirmationDialog(getSupportFragmentManager());
            }
            EventsTracker.trackRegistration(result.getType());
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case ENTITY_NOT_FOUND_404:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.title_pass_incorrect, R.string.pass_incorrect, false);
                    break;
                default:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);

            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    }

    @Subscribe
    public void openDiveSpotDetails(OpenDiveSpotDetailsActivityEvent event) {
        DiveSpotDetailsActivity.show(this, event.getId(), EventsTracker.SpotViewSource.FROM_ACTIVITIES);
    }

    @Subscribe
    public void logoutUser(LogoutEvent event) {
        SharedPreferenceHelper.UserType currentUserType = SharedPreferenceHelper.getActiveUserType();
        SharedPreferenceHelper.removeUserFromList(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId());
        if (SharedPreferenceHelper.getIsUserSignedIn()) {
            mainViewPagerAdapter.notifyDataSetChanged();
            mainViewPager.destroyDrawingCache();
            if (mainViewPagerAdapter.getDiverNotificationsFragment() != null) {
                mainViewPagerAdapter.getDiverNotificationsFragment().getUserNotifications(false);
            }
            setupTabLayout();
            DDScannerApplication.bus.post(new LoadUserProfileInfoEvent());
        } else {
//            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_LOGIN_FOR_PROFILE_OR_NOTIFICATIONS);
            changeVisibilityChangeAccountLayout(View.GONE);
            mainViewPagerAdapter.notifyDataSetChanged();
            mainViewPager.destroyDrawingCache();
            setupTabLayout();
        }
    }

}

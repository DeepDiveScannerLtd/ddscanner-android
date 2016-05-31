package com.ddscanner.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.request.IdentifyRequest;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.InternetConnectionClosedEvent;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.PlaceChoosedEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.RestClient;
import com.ddscanner.services.GPSTracker;
import com.ddscanner.services.RegistrationIntentService;
import com.ddscanner.ui.adapters.MainActivityPagerAdapter;
import com.ddscanner.ui.fragments.EditProfileFragment;
import com.ddscanner.ui.fragments.MapListFragment;
import com.ddscanner.ui.fragments.NotificationsFragment;
import com.ddscanner.ui.fragments.ProfileFragment;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by lashket on 20.4.16.
 */
public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int REQUEST_CODE_PLACE_AUTOCOMPLETE = 1000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int RC_PICK_PHOTO = 8000;
    private static final int RC_LOGIN = 7000;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri capturedImageUri;
    private LocationManager locationManager;

    private TabLayout toolbarTabLayout;
    private ViewPager mainViewPager;
    private PercentRelativeLayout menuItemsLayout;
    private ImageView searchLocationBtn;
    private ImageView btnFilter;
    private MainActivityPagerAdapter adapter;
    private ImageView imageView;
    private Helpers helpers = new Helpers();
    private boolean isHasInternetConnection;
    private boolean isHasLocation;
    private MaterialDialog materialDialog;
    private boolean isTryToOpenAddDiveSpotActivity = false;
    private int positionToScroll;

    private MapListFragment mapListFragment = new MapListFragment();
    private NotificationsFragment notificationsFragment = new NotificationsFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private EditProfileFragment editProfileFragment = new EditProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isHasInternetConnection = getIntent().getBooleanExtra(Constants.IS_HAS_INTERNET, false);
        startActivity();
        if (!isHasInternetConnection) {
            InternetClosedActivity.show(this);
        }
    }

    private void startActivity() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);
        findViews();
        setupViewPager(mainViewPager);
        setUi();
        setupTabLayout();
        playServices();
        if (checkIsProvidersEnabled()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    GPSTracker tracker = new GPSTracker(MainActivity.this);
                    identifyUser(String.valueOf(tracker.getLatitude()),
                            String.valueOf(tracker.getLongitude()));
                }
            }, 1000);
        } else {
            showAlertDialogLocationSettings();
        }

    }

    private void setUi() {
        toolbarTabLayout.setupWithViewPager(mainViewPager);
        mainViewPager.setOffscreenPageLimit(3);
        mainViewPager.addOnPageChangeListener(this);
        searchLocationBtn.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
    }

    private void findViews() {
        materialDialog = helpers.getMaterialDialog(this);
        toolbarTabLayout = (TabLayout) findViewById(R.id.toolbar_tablayout);
        mainViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        menuItemsLayout = (PercentRelativeLayout) findViewById(R.id.menu_items_layout);
        searchLocationBtn = (ImageView) findViewById(R.id.search_location_menu_button);
        btnFilter = (ImageView) findViewById(R.id.filter_menu_button);
    }

    private String getUserUniqueId() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        SharedPreferenceHelper.setUserAppId(deviceId);
        return deviceId;
    }

    private void setupTabLayout() {
        toolbarTabLayout.getTabAt(2).setCustomView(R.layout.tab_profile_item);
        toolbarTabLayout.getTabAt(1).setCustomView(R.layout.tab_notification_item);
        toolbarTabLayout.getTabAt(0).setCustomView(R.layout.tab_map_item);
        toolbarTabLayout.getTabAt(0).getCustomView().setSelected(true);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new MainActivityPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapListFragment(), "mapl/list");
        adapter.addFragment(new NotificationsFragment(), "notifications");
        adapter.addFragment(profileFragment, "profile");
        viewPager.setAdapter(adapter);
    }

    public static void show(Context context, boolean isHasInternet, boolean isHasLocation) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.IS_HAS_INTERNET, isHasInternet);
        intent.putExtra(Constants.IS_LOCATION, isHasLocation);
        context.startActivity(intent);
    }

    @Override
    public void onPageSelected(int position) {
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
        Log.i(TAG,String.valueOf(SharedPreferenceHelper.getIsUserLogined()));
        Log.i(TAG,String.valueOf(SharedPreferenceHelper.getToken()));
        if ((position == 2 || position == 1) && !SharedPreferenceHelper.getIsUserLogined()) {
            positionToScroll = position;
            Intent intent = new Intent(MainActivity.this, SocialNetworks.class);
            startActivityForResult(intent, RC_LOGIN);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_location_menu_button:
                materialDialog.show();
                openSearchLocationWindow();
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
        profileFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2001) {
            if (resultCode == RESULT_OK) {

            }
        }
        if (requestCode == REQUEST_CODE_PLACE_AUTOCOMPLETE) {
            materialDialog.dismiss();
            if (resultCode == RESULT_OK) {
                final Place place = PlaceAutocomplete.getPlace(this, data);
                DDScannerApplication.bus.post(new PlaceChoosedEvent(place.getViewport()));
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            profileFragment.setImage(capturedImageUri);
        }
        if (requestCode == RC_PICK_PHOTO && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            profileFragment.setImage(uri);
        }
        if (requestCode == RC_LOGIN) {
            if (resultCode == RESULT_OK) {
                if (isTryToOpenAddDiveSpotActivity) {
                    AddDiveSpotActivity.show(this);
                    isTryToOpenAddDiveSpotActivity = false;
                    return;
                }
                mainViewPager.setCurrentItem(positionToScroll);
            } else {
                isTryToOpenAddDiveSpotActivity = false;
            }
            if (resultCode == RESULT_CANCELED) {
                mainViewPager.setCurrentItem(0);
            }
        }

        if (requestCode == 1) {
            if (checkIsProvidersEnabled()) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GPSTracker tracker = new GPSTracker(MainActivity.this);
                        identifyUser(String.valueOf(tracker.getLatitude()),
                                String.valueOf(tracker.getLongitude()));
                        DDScannerApplication.bus.post(new PlaceChoosedEvent(new LatLngBounds(
                                new LatLng(tracker.getLatitude() - 1, tracker.getLongitude() - 1),
                                new LatLng(tracker.getLatitude() + 1, tracker.getLongitude() + 1)
                        )));
                    }
                }, 1000);
            } else {
                showAlertDialogLocationSettings();
            }
        }
    }

    private void openSearchLocationWindow() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, REQUEST_CODE_PLACE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }

    public void identifyUser(String lat, String lng) {
        Call<ResponseBody> call = RestClient.getServiceInstance()
                .identify(getUserIdentifyData(lat, lng));
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });
    }

    private IdentifyRequest getUserIdentifyData(String lat, String lng) {
        IdentifyRequest identifyRequest = new IdentifyRequest();
        identifyRequest.setAppId(getUserUniqueId());
        if(SharedPreferenceHelper.getIsUserLogined()) {
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
        return  identifyRequest;
    }

    public void playServices() {
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
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
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void showAlertDialogLocationSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "To work with ddscanner you must enable location service. Do you want to do this?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 1);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean checkIsProvidersEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    @Subscribe
    public void changeProfileFragmentView(TakePhotoFromCameraEvent event) {
        dispatchTakePictureIntent();
    }

    @Subscribe
    public void pickPhotoFromGallery(PickPhotoFromGallery event) {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_PICK_PHOTO);
    }

    @Subscribe
    public void userIslogouted(ChangePageOfMainViewPagerEvent event) {
        mainViewPager.setCurrentItem(event.getPage());
    }

    @Subscribe
    public void internetConnectionClosed(InternetConnectionClosedEvent event) {
        InternetClosedActivity.show(this);
    }

    @Subscribe
    public void showLoginActivity(ShowLoginActivityIntent event) {
        Intent intent = new Intent(MainActivity.this, SocialNetworks.class);
        startActivityForResult(intent, RC_LOGIN);
    }

    @Subscribe
    public void openLoginWindowToAdd(OpenAddDsActivityAfterLogin event) {
        isTryToOpenAddDiveSpotActivity = true;
        Intent intent = new Intent(MainActivity.this, SocialNetworks.class);
        startActivityForResult(intent, RC_LOGIN);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


}

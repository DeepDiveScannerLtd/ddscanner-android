package com.ddscanner.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.PlaceChoosedEvent;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.RestClient;
import com.ddscanner.services.RegistrationIntentService;
import com.ddscanner.ui.adapters.MainActivityPagerAdapter;
import com.ddscanner.ui.fragments.EditProfileFragment;
import com.ddscanner.ui.fragments.MapListFragment;
import com.ddscanner.ui.fragments.NotificationsFragment;
import com.ddscanner.ui.fragments.ProfileFragment;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private TabLayout toolbarTabLayout;
    private ViewPager mainViewPager;
    private PercentRelativeLayout menuItemsLayout;
    private ImageView searchLocationBtn;
    private ImageView btnFilter;
    private MainActivityPagerAdapter adapter;
    private ImageView imageView;
    private Helpers helpers = new Helpers();

    private MapListFragment mapListFragment = new MapListFragment();
    private NotificationsFragment notificationsFragment = new NotificationsFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private EditProfileFragment editProfileFragment = new EditProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);
        findViews();
        setupViewPager(mainViewPager);
        setUi();
        setupTabLayout();
        playServices();
    }

    private void setUi() {
        toolbarTabLayout.setupWithViewPager(mainViewPager);
        mainViewPager.setOffscreenPageLimit(3);
        mainViewPager.setOnPageChangeListener(this);
        searchLocationBtn.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
    }

    private void findViews() {
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
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new MainActivityPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapListFragment(), "mapl/list");
        adapter.addFragment(new NotificationsFragment(), "notifications");
        adapter.addFragment(profileFragment, "profile");
        viewPager.setAdapter(adapter);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
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
        if (position == 2 && !SharedPreferenceHelper.getIsUserLogined()) {
            Intent intent = new Intent(MainActivity.this, SocialNetworks.class);
            startActivityForResult(intent, RC_LOGIN);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_location_menu_button:
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
        if (requestCode == REQUEST_CODE_PLACE_AUTOCOMPLETE) {
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
        if (requestCode == RC_LOGIN && resultCode == RESULT_OK) {
            mainViewPager.setCurrentItem(2);
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

    public static void checkGcm() {
        Call<ResponseBody> call = RestClient.getServiceInstance().identifyGcmToken(SharedPreferenceHelper.getGcmId());
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });
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

    @Subscribe
    public void changeProfileFragmentView(TakePhotoFromCameraEvent event) {
        dispatchTakePictureIntent();
    }

    @Subscribe
    public void pickPhotoFromGallery(PickPhotoFromGallery event) {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(i, RC_PICK_PHOTO);
    }

    @Subscribe
    public void userIslogouted(ChangePageOfMainViewPagerEvent event) {
        mainViewPager.setCurrentItem(event.getPage());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

//folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        imagesFolder.mkdirs();

        File image = new File(imagesFolder, "QR_" + timeStamp + ".png");
        capturedImageUri = Uri.fromFile(image);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}

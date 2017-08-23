package com.ddscanner.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.events.InstanceIDReceivedEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.screens.tutorial.TutorialActivity;
import com.ddscanner.services.CheckResentRunService;
import com.ddscanner.ui.views.DDProgressBarView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;

public class SplashActivity extends BaseAppCompatActivity implements DialogClosedListener, View.OnClickListener {

    private static final String TAG = SplashActivity.class.getName();

    private Handler handler = new Handler();
    private Runnable showMainActivityRunnable;
    private long activityShowTimestamp;

    private TextView progressMessage;
    private TextView skip;
    private Button signUpButton;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsFirstLaunch()) {
            DDScannerApplication.getInstance().getSharedPreferenceHelper().clear();
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsFirstLaunch(false);
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_splash);
        //TODO uncomment in future versions
        activityShowTimestamp = System.currentTimeMillis();

        progressMessage = findViewById(R.id.message);
        skip = findViewById(R.id.skip);
        loginButton = findViewById(R.id.login);
        signUpButton = findViewById(R.id.sign_up);

        skip.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

//        if (SharedPreferenceHelper.getIsUserSignedIn()) {
//            skip.setVisibility(View.GONE);
//            loginButton.setVisibility(View.GONE);
//            signUpButton.setVisibility(View.GONE);
//            showMainActivity();
//        }

//        if (SharedPreferenceHelper.getIsNeedToShowTutorial()) {
//            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsMustTrackFbEvent()) {
//                DDScannerApplication.getInstance().getSharedPreferenceHelper().setFbTracked();
//                EventsTracker.trackFirstLaunchForFacebookAnalytics();
//            }
//            loadTutorial();
//            SharedPreferenceHelper.setIsNeedToShowTutorial();
//        }
        if (SharedPreferenceHelper.getIsUserSignedIn()) {
            skip.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            showMainActivity();
        }

        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isMustToEnableNotification()) {
            DDScannerApplication.getInstance().getSharedPreferenceHelper().enableNotification();
        } else {
            DDScannerApplication.getInstance().getSharedPreferenceHelper().recordRunTime();
        }
        LinearLayout mainLayout = findViewById(R.id.main);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        mainLayout.startAnimation(fadeInAnimation);

        startService(new Intent(this, CheckResentRunService.class));

    }

    public void loadTutorial() {
        Intent mainAct = new Intent(this, TutorialActivity.class);
        mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems());
        startActivityForResult(mainAct, 15);

    }

    private ArrayList<TutorialItem> getTutorialItems() {
        TutorialItem createItem = new TutorialItem(getString(R.string.tutorial_item_title_create), getString(R.string.tutorial_item_create),
                R.color.white, R.drawable.ic_create);

        TutorialItem earnItem = new TutorialItem(getString(R.string.tutorial_item_title_earn), getString(R.string.tutorial_item_earn),
                R.color.white, R.drawable.ic_earn);

        TutorialItem discoverItem = new TutorialItem(getString(R.string.tutorial_item_title_discover), getString(R.string.tutorial_item_discover),
                R.color.white, R.drawable.ic_discover);

        TutorialItem exploreItem = new TutorialItem(getString(R.string.tutorial_item_explore_title), getString(R.string.tutorial_item_explore),
                R.color.white, R.drawable.ic_explore);

        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(createItem);
        tutorialItems.add(earnItem);
        tutorialItems.add(discoverItem);
        tutorialItems.add(exploreItem);


        return tutorialItems;
    }

    private void showMainActivity() {
        Log.i(TAG, "showMainActivity");
        showMainActivityRunnable = () -> {
//            ExampleActivityCountry.show(this);
            MainActivity.show(SplashActivity.this);
            SplashActivity.this.finish();
        };
        if (System.currentTimeMillis() - activityShowTimestamp < DDProgressBarView.ANIMATION_DURATION) {
            handler.postDelayed(showMainActivityRunnable, DDProgressBarView.ANIMATION_DURATION - (System.currentTimeMillis() - activityShowTimestamp));
        } else {
            handler.post(showMainActivityRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
       // handler.removeCallbacks(showMainActivityRunnable);
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DDScannerApplication.activityResumed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Subscribe
    public void onAppInstanceIdReceived(InstanceIDReceivedEvent event) {
        progressMessage.setText(R.string.start_process_register_for_ddscanner);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(showMainActivityRunnable);
        finish();
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_SPLASH_ACTIVITY_UNEXPECTED_ERROR:
            case DialogsRequestCodes.DRC_SPLASH_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip:
                EventsTracker.trackSkipRegistration();
                MainActivity.show(SplashActivity.this);
                SplashActivity.this.finish();
                break;
            case R.id.login:
                SignUpActivity.showForResult(this, false, ActivitiesRequestCodes.REQUEST_CODE_SPLASH_ACTIVITY_LOGIN);
                break;
            case R.id.sign_up:
                SignUpActivity.showForResult(this, true, ActivitiesRequestCodes.REQUEST_CODE_SPLASH_ACTIVITY_SIGN_UP);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SPLASH_ACTIVITY_SIGN_UP:
            case ActivitiesRequestCodes.REQUEST_CODE_SPLASH_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    showMainActivity();
                }
                break;
            case 15:
                showMainActivity();
//                if (SharedPreferenceHelper.getIsUserSignedIn()) {
//                    skip.setVisibility(View.GONE);
//                    loginButton.setVisibility(View.GONE);
//                    signUpButton.setVisibility(View.GONE);
//                    showMainActivity();
//                }
                break;
        }
    }
}
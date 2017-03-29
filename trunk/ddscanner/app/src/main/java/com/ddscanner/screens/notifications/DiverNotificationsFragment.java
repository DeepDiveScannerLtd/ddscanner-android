package com.ddscanner.screens.notifications;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Activity;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.entities.NotificationOld;
import com.ddscanner.entities.Notifications;
import com.ddscanner.events.ChangeLoginViewEvent;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.GetNotificationsEvent;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.adapters.NotificationsPagerAdapter;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiverNotificationsFragment extends Fragment implements ViewPager.OnPageChangeListener, LoginView.LoginStateChangeListener, DialogClosedListener {

    private static final String TAG = DiverNotificationsFragment.class.getName();

    private List<Activity> activities = new ArrayList<>();
    private List<NotificationOld> notificationOldList = new ArrayList<>();
    private Notifications notifications = new Notifications();
    private TabLayout tabLayout;
    private ViewPager notificationsViewPager;
    private RelativeLayout loginView;
    private ProgressView progressView;
    private LoginView customLoginView;
    private PersonalNotificationsFragment personalNotificationsFragment = new PersonalNotificationsFragment();
    private ActivityNotificationsFragment activityNotificationsFragment = new ActivityNotificationsFragment();
    private boolean isViewNull = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        findViews(view);
        setupViewPager();
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            onLoggedIn();
        } else {
            onLoggedOut();
        }
        activityNotificationsFragment.loadNotifications();
        return view;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(android.app.Activity context) {
        super.onAttach(context);

        Log.i(TAG, "onAttach(Activity context)");
        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(context);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        notificationsResultListener.setCancelled(true);
    }

    protected void onAttachToContext(Context context) {
        try {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.setDiverNotificationsFragment(this);
        } catch (ClassCastException e) {
            // waaat?
            e.printStackTrace();
        }
    }

    private void findViews(View v) {
        tabLayout = (TabLayout) v.findViewById(R.id.notif_tab_layout);
        notificationsViewPager = (ViewPager) v.findViewById(R.id.notif_view_pager);
        loginView = (RelativeLayout) v.findViewById(R.id.login_view_root);
        progressView = (ProgressView) v.findViewById(R.id.progressBarFull);
        notificationsViewPager.addOnPageChangeListener(this);
        customLoginView = (LoginView) v.findViewById(R.id.login_view);
    }

    private void setUpTabLayout() {
        tabLayout.getTabAt(1).setText("Activity");
        tabLayout.getTabAt(0).setText("You");
    }

    private void setupViewPager() {
        NotificationsPagerAdapter notificationsPagerAdapter = new NotificationsPagerAdapter(getFragmentManager());
        notificationsPagerAdapter.addFragment(personalNotificationsFragment, "Notifications");
        notificationsPagerAdapter.addFragment(activityNotificationsFragment, "Activity");
        notificationsViewPager.setAdapter(notificationsPagerAdapter);
        setUi();
    }

    private void setUi() {
        tabLayout.setupWithViewPager(notificationsViewPager);
        notificationsViewPager.setOffscreenPageLimit(2);
        setUpTabLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DDScannerApplication.isActivitiesFragmentVisible) {
            Date date1 = new Date();
            long currentDateInMillis = date1.getTime();
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setLastShowingNotificationTime(currentDateInMillis);
        } else {
//            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
//                progressView.setVisibility(View.VISIBLE);
//                notificationsViewPager.setVisibility(View.GONE);
//               // getUserNotifications();
//            }
        }
        if (!getUserVisibleHint()) {
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
//        notificationsResultListener.setCancelled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
//        notificationsResultListener.setCancelled(true);
    }

    @Override
    public void setUserVisibleHint(final boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible) {
            if (getView() != null) {
                isViewNull = false;
                if (DDScannerApplication.isActivitiesFragmentVisible) {
                    Date date1 = new Date();
                    long currentDateInMillis = date1.getTime();
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setLastShowingNotificationTime(currentDateInMillis);
                } else {
                    if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
//                        progressView.setVisibility(View.VISIBLE);
//                        notificationsViewPager.setVisibility(View.GONE);
//                   //     getUserNotifications();
                    }
                }
            } else {
                isViewNull = true;
            }
        }
    }

    private void getUserNotifications() {
        if (notificationsViewPager != null) {
//            onPageSelected(notificationsViewPager.getCurrentItem());
            activityNotificationsFragment.loadNotifications();
            personalNotificationsFragment.loadNotifications();
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
//            personalNotificationsFragment.loadNotifications();
        }
        if (position == 1) {
//            activityNotificationsFragment.loadNotifications();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_OPEN_LOGIN_SCREEN:
                if (resultCode == android.app.Activity.RESULT_OK) {
                    tabLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        if (loginView != null && notificationsViewPager != null) {
            loginView.setVisibility(View.GONE);
            notificationsViewPager.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoggedOut() {
        if (loginView != null && notificationsViewPager != null) {
            loginView.setVisibility(View.VISIBLE);
            notificationsViewPager.setVisibility(View.GONE);
        }
    }

    public void setActivityNotificationsFragment(ActivityNotificationsFragment activityNotificationsFragment) {
        this.activityNotificationsFragment = activityNotificationsFragment;
    }

    public void setPersonalNotificationsFragment(PersonalNotificationsFragment personalNotificationsFragment) {
        this.personalNotificationsFragment = personalNotificationsFragment;
    }

    @Subscribe
    public void getNotifications(GetNotificationsEvent event) {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            getUserNotifications();
            return;
        }
        onLoggedOut();
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_NOTIFICATIONS_FRAGMENT_UNEXPECTED_ERROR:
            case DialogsRequestCodes.DRC_NOTIFICATIONS_FRAGMENT_FAILED_TO_CONNECT:
                DDScannerApplication.bus.post(new ChangePageOfMainViewPagerEvent(0));
                break;
        }
    }

    @Subscribe
    public void changeLoginView(ChangeLoginViewEvent event) {
        customLoginView.changeViewToStart();
    }

}

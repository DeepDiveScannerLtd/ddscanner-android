package com.ddscanner.ui.fragments;

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
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Activity;
import com.ddscanner.entities.Notification;
import com.ddscanner.entities.Notifications;
import com.ddscanner.events.ChangePageOfMainViewPagerEvent;
import com.ddscanner.events.GetNotificationsEvent;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.events.ChangeLoginViewEvent;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.adapters.NotificationsPagerAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsFragment extends BaseFragment implements ViewPager.OnPageChangeListener, LoginView.LoginStateChangeListener, InfoDialogFragment.DialogClosedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = NotificationsFragment.class.getName();

    private List<Activity> activities = new ArrayList<>();
    private List<Notification> notificationList = new ArrayList<>();
    private Notifications notifications = new Notifications();
    private TabLayout tabLayout;
    private ViewPager notificationsViewPager;
    private RelativeLayout loginView;
    private ProgressView progressView;
    private LoginView customLoginView;
    private AllNotificationsFragment allNotificationsFragment = new AllNotificationsFragment();
    private ActivityNotificationsFragment activityNotificationsFragment = new ActivityNotificationsFragment();
    private boolean isViewNull = true;
    private SwipeRefreshLayout swipeRefreshLayout;

    private DDScannerRestClient.ResultListener<Notifications> notificationsResultListener = new DDScannerRestClient.ResultListener<Notifications>() {
        @Override
        public void onSuccess(Notifications result) {
            notifications = result;
            progressView.setVisibility(View.GONE);
            notificationsViewPager.setVisibility(View.VISIBLE);
            setData();
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_NOTIFICATIONS_FRAGMENT_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    DDScannerApplication.bus.post(new LoggedOutEvent());
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_NOTIFICATIONS_FRAGMENT_UNEXPECTED_ERROR, false);
                    break;
            }
        }
    };

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
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            progressView.setVisibility(View.VISIBLE);
            notificationsViewPager.setVisibility(View.GONE);
          //  getUserNotifications();
        }
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            onLoggedIn();
        } else {
            onLoggedOut();
        }
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

        notificationsResultListener.setCancelled(true);
    }

    protected void onAttachToContext(Context context) {
        try {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.setNotificationsFragment(this);
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
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        customLoginView = (LoginView) v.findViewById(R.id.login_view);
    }

    private void setUpTabLayout() {
        tabLayout.getTabAt(1).setText("Activity");
        tabLayout.getTabAt(0).setText("Notifications");
    }

    private void setupViewPager() {
        NotificationsPagerAdapter notificationsPagerAdapter = new NotificationsPagerAdapter(getFragmentManager());
        notificationsPagerAdapter.addFragment(allNotificationsFragment, "Notifications");
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
//            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
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
        notificationsResultListener.setCancelled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
        notificationsResultListener.setCancelled(true);
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
                    if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
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
       DDScannerApplication.getInstance().getDdScannerRestClient().getUserNotifications(notificationsResultListener);
    }

    private void setData() {
        Log.i(TAG, "asdf setData this = " + this);
        if (notificationsViewPager.getCurrentItem() == 0) {
            if (allNotificationsFragment != null) {
                if (notifications.getNotifications() != null) {
                    allNotificationsFragment.addList((ArrayList<Notification>)
                            notifications.getNotifications());
                } else {
                    allNotificationsFragment.addList(null);
                }
            }
        }
        if (notificationsViewPager.getCurrentItem() == 1) {
            if (activityNotificationsFragment != null) {
                if (notifications.getActivities() != null) {
                    activities = notifications.getActivities();
                    activityNotificationsFragment.addList((ArrayList<Activity>) activities);
                } else {
                    activityNotificationsFragment.addList(null);
                }
            }
        }

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            if (notifications.getNotifications() != null) {
                allNotificationsFragment.addList((ArrayList<Notification>) notifications.getNotifications());
            }
        }
        if (position == 1) {
            if (notifications.getActivities() != null) {
                activities = notifications.getActivities();
                activityNotificationsFragment.addList((ArrayList<Activity>) activities);
            }
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
            swipeRefreshLayout.setEnabled(true);
        }
    }

    @Override
    public void onLoggedOut() {
        if (loginView != null && notificationsViewPager != null) {
            loginView.setVisibility(View.VISIBLE);
            notificationsViewPager.setVisibility(View.GONE);
            swipeRefreshLayout.setEnabled(false);
        }
    }

    public void setActivityNotificationsFragment(ActivityNotificationsFragment activityNotificationsFragment) {
        this.activityNotificationsFragment = activityNotificationsFragment;
    }

    public void setAllNotificationsFragment(AllNotificationsFragment allNotificationsFragment) {
        this.allNotificationsFragment = allNotificationsFragment;
    }

    @Subscribe
    public void getNotifications(GetNotificationsEvent event) {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
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

    @Override
    public void onRefresh() {
        DDScannerApplication.getInstance().getDdScannerRestClient().getUserNotifications(notificationsResultListener);
    }

    @Subscribe
    public void changeLoginView(ChangeLoginViewEvent event) {
        customLoginView.changeViewToStart();
    }
}

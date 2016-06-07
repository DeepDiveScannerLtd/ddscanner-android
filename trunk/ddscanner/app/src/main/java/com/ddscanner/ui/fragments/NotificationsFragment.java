package com.ddscanner.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Activity;
import com.ddscanner.entities.Notification;
import com.ddscanner.entities.Notifications;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.events.PickPhotoFromGallery;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.activities.DiveSpotsListActivity;
import com.ddscanner.ui.activities.SocialNetworks;
import com.ddscanner.ui.activities.UsersDivespotListSwipableActivity;
import com.ddscanner.ui.adapters.NotificationsPagerAdapter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 20.4.16.
 */
public class NotificationsFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private List<Activity> activities = new ArrayList<>();
    private List<Notification> notificationList = new ArrayList<>();
    private Notifications notifications = new Notifications();
    private Helpers helpers = new Helpers();
    private TabLayout tabLayout;
    private ViewPager notificationsViewPager;
    private TextView needToLoginMessage;
    private Button openLoginActivityButton;
    private View needToLoginLayout;
    private AllNotificationsFragment allNotificationsFragment = new AllNotificationsFragment();
    private ActivityNotificationsFragment activityNotificationsFragment = new ActivityNotificationsFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        findViews(view);
        needToLoginMessage.setText(R.string.notifications_need_to_login);
        openLoginActivityButton.setOnClickListener(this);
        setUserLoggedInUI();
        //   getUserNotifications();
        return view;
    }

    private void findViews(View v) {
        tabLayout = (TabLayout) v.findViewById(R.id.notif_tab_layout);
        notificationsViewPager = (ViewPager) v.findViewById(R.id.notif_view_pager);
        needToLoginMessage = (TextView) v.findViewById(R.id.need_to_login_message);
        openLoginActivityButton = (Button) v.findViewById(R.id.btn_open_login_screen);
        needToLoginLayout = v.findViewById(R.id.need_to_login);
        notificationsViewPager.addOnPageChangeListener(this);
        setupViewPager();
    }

    private void setUpTabLayout() {
        tabLayout.getTabAt(1).setText("Activity");
        tabLayout.getTabAt(0).setText("Notifications");
    }

    private void setupViewPager() {
        NotificationsPagerAdapter notificationsPagerAdapter = new NotificationsPagerAdapter(
                getFragmentManager()
        );
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
            SharedPreferenceHelper.setLastShowingNotificationTime(currentDateInMillis);
        }
        setUserLoggedInUI();
        if (!getUserVisibleHint()) {
            return;
        }
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
    public void setUserVisibleHint(final boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible) {
            if (SharedPreferenceHelper.getIsUserLogined()) {
                getUserNotifications();
            }
        }
    }

    private void getUserNotifications() {
        Call<ResponseBody> call = RestClient.getServiceInstance().getNotifications(
                SharedPreferenceHelper.getUserServerId(), helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    if (response.raw().code() == 200) {
                        try {
                            responseString = response.body().string();
                            notifications = new Gson().fromJson(responseString, Notifications.class);
//                            if (notifications.getActivities() != null) {
//                                activities = notifications.getActivities();
//                                activityNotificationsFragment.addList((ArrayList<Activity>) activities);
//                            }
//                            if (notifications.getNotifications() != null) {
//                                allNotificationsFragment.addList((ArrayList<Notification>)
//                                        notifications.getNotifications());
//                            }
                            setData();
                        } catch (IOException e) {

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }

    private void setData() {
        if (notificationsViewPager.getCurrentItem() == 0) {
            if (notifications.getNotifications() != null) {
                allNotificationsFragment.addList((ArrayList<Notification>)
                        notifications.getNotifications());
            } else {
                allNotificationsFragment.addList(null);
            }
        }
        if (notificationsViewPager.getCurrentItem() == 1) {
            if (notifications.getActivities() != null) {
                activities = notifications.getActivities();
                activityNotificationsFragment.addList((ArrayList<Activity>) activities);
            } else {
                activityNotificationsFragment.addList(null);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_login_screen:
                startActivityForResult(new Intent(getActivity(), SocialNetworks.class), Constants.REQUEST_CODE_OPEN_LOGIN_SCREEN);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_OPEN_LOGIN_SCREEN:
                if (resultCode == android.app.Activity.RESULT_OK) {
                    setUserLoggedInUI();
                }
                break;
        }
    }

    @Subscribe
    public void onLoggedOut(LoggedOutEvent event) {
        setUserLoggedInUI();
        // TODO Update all data
    }

    public void setUserLoggedInUI() {
        if (needToLoginLayout == null || tabLayout == null || notificationsViewPager == null) {
            return;
        }
        if (SharedPreferenceHelper.getIsUserLogined()) {
            needToLoginLayout.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            notificationsViewPager.setVisibility(View.VISIBLE);
        } else {
            needToLoginLayout.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);
            notificationsViewPager.setVisibility(View.GONE);
        }
    }
}

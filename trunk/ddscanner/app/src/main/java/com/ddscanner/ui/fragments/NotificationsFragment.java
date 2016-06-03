package com.ddscanner.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Activity;
import com.ddscanner.entities.Notification;
import com.ddscanner.entities.Notifications;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.activities.SocialNetworks;
import com.ddscanner.ui.adapters.NotificationsPagerAdapter;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;

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
public class NotificationsFragment extends Fragment implements ViewPager.OnPageChangeListener{

    private List<Activity> activities = new ArrayList<>();
    private List<Notification> notificationList = new ArrayList<>();
    private Notifications notifications = new Notifications();
    private Helpers helpers = new Helpers();
    private TabLayout tabLayout;
    private ViewPager photosViewPager;
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
     //   getUserNotifications();
        return view;
    }

    private void findViews(View v) {
        tabLayout = (TabLayout) v.findViewById(R.id.notif_tab_layout);
        photosViewPager = (ViewPager) v.findViewById(R.id.notif_view_pager);
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
        photosViewPager.setAdapter(notificationsPagerAdapter);
        setUi();
    }

    private void setUi() {
        tabLayout.setupWithViewPager(photosViewPager);
        photosViewPager.setOffscreenPageLimit(2);
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
        if (SharedPreferenceHelper.getIsUserLogined()) {
           // getUserNotifications();
        }
        DDScannerApplication.bus.register(this);
        if (!getUserVisibleHint())
        {
            return;
        }
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
                            if (notifications.getActivities() != null) {
                                activities = notifications.getActivities();
                                activityNotificationsFragment.addList((ArrayList<Activity>) activities);
                            }
                            if (notifications.getNotifications() != null) {
                                allNotificationsFragment.addList((ArrayList<Notification>)
                                        notifications.getNotifications());
                            }
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

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}

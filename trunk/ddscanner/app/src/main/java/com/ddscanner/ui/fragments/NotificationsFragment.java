package com.ddscanner.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Activity;
import com.ddscanner.entities.Notification;
import com.ddscanner.entities.Notifications;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.events.LoggedOutEvent;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.NotificationsPagerAdapter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
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
public class NotificationsFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private List<Activity> activities = new ArrayList<>();
    private List<Notification> notificationList = new ArrayList<>();
    private Notifications notifications = new Notifications();
    private Helpers helpers = new Helpers();
    private TabLayout tabLayout;
    private ViewPager notificationsViewPager;
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
        notificationsViewPager = (ViewPager) v.findViewById(R.id.notif_view_pager);
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
            if (SharedPreferenceHelper.isUserLoggedIn()) {
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
                String responseString = "";
                if (response.isSuccessful()) {
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
                if (!response.isSuccessful()) {
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
                        helpers.showToast(getContext(), R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(getContext(), R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(getContext(), R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(getContext(), R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(getContext(), R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        DDScannerApplication.bus.post(new LoggedOutEvent());
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(getContext(), R.string.toast_server_error);
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_OPEN_LOGIN_SCREEN:
                if (resultCode == android.app.Activity.RESULT_OK) {

                }
                break;
        }
    }

}

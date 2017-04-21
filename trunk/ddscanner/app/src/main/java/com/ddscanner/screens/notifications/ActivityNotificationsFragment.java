package com.ddscanner.screens.notifications;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.FragmnetActivityNotificationsBinding;
import com.ddscanner.entities.NotificationEntity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.MainActivity;

import java.util.ArrayList;

/**
 * Created by lashket on 25.5.16.
 */
public class ActivityNotificationsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ActivityNotificationsFragment.class.getName();
    private static final int PAGE_SIZE = 20;

    private ArrayList<NotificationEntity> activities;
    private FragmnetActivityNotificationsBinding binding;
    private NotificationsListAdapter notificationsListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private NotificationResultListener paginationResultListener = new NotificationResultListener(true);
    private NotificationResultListener simpleResultListener = new NotificationResultListener(false);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmnet_activity_notifications, container, false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        binding.activityRc.setHasFixedSize(true);
        binding.activityRc.setLayoutManager(linearLayoutManager);
        binding.activityRc.setItemAnimator(new DefaultItemAnimator());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initializeListenerForHighVersions();
        } else {
            initilizeListenerForLowVersions();
        }
        notificationsListAdapter = new NotificationsListAdapter(getActivity(), false, DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType());
        binding.activityRc.setAdapter(notificationsListAdapter);
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(context);
        }
    }

    @Override
    public void onRefresh() {
        loadNotifications();
    }

    protected void onAttachToContext(Context context) {
        try {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.setActivityNotificationsFragment(this);
        } catch (ClassCastException e) {
            // waaat?
            e.printStackTrace();
        }
    }

    public void loadNotifications() {
        DDScannerApplication.getInstance().getDdScannerRestClient().getActivityNotifications(simpleResultListener, null);
    }

    @TargetApi(23)
    private void initializeListenerForHighVersions() {
        RecyclerView.OnScrollChangeListener listener = new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                tryingToReloadData();
            }
        };
        binding.activityRc.setOnScrollChangeListener(listener);
    }

    @SuppressWarnings("deprecation")
    private void initilizeListenerForLowVersions() {
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                tryingToReloadData();
            }
        };
        binding.activityRc.setOnScrollListener(scrollListener);
    }

    private void tryingToReloadData() {
        int visibleItemsCount = linearLayoutManager.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        if (!isLoading) {
            if ((visibleItemsCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                DDScannerApplication.getInstance().getDdScannerRestClient().getActivityNotifications(paginationResultListener, notificationsListAdapter.getLastNotificationDate());
                binding.progressBarPagination.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        }
    }

    class NotificationResultListener extends DDScannerRestClient.ResultListener<ArrayList<NotificationEntity>> {

        private boolean isFromPagination;

        public NotificationResultListener(boolean isFromPagination) {
            this.isFromPagination = isFromPagination;
        }

        @Override
        public void onSuccess(ArrayList<NotificationEntity> result) {
            isLoading = false;
            activities = result;
            binding.swipeRefreshLayout.setRefreshing(false);
            if (binding != null) {
                binding.progressBarPagination.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.activityRc.setVisibility(View.VISIBLE);
                binding.noNotifsView.setVisibility(View.GONE);
                if ((activities == null || activities.size() == 0) && !isFromPagination){
                    binding.noNotifsView.setVisibility(View.VISIBLE);
                    binding.activityRc.setVisibility(View.GONE);
                    return;
                }
                if (isFromPagination) {
                    notificationsListAdapter.add(result);
                } else {
                    if (result.size() > 0 && !result.get(0).getId().equals(notificationsListAdapter.getFirstNotificationId())) {
                        notificationsListAdapter.setNotifications(result);
                    }
                }
            }
        }

        @Override
        public void onConnectionFailure() {
            binding.swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            binding.swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onInternetConnectionClosed() {
            binding.swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        simpleResultListener.setCancelled(false);
        paginationResultListener.setCancelled(false);
        EventsTracker.trackActivityView();
    }

    @Override
    public void onStop() {
        super.onStop();
        paginationResultListener.setCancelled(true);
        simpleResultListener.setCancelled(true);
    }

}

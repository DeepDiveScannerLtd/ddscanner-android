package com.ddscanner.screens.notifications;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.FragmentPersonalNotificationsBinding;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.NotificationEntity;
import com.ddscanner.events.LogoutEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespots.list.DiveSpotsListActivity;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.SharedPreferenceHelper;

import java.util.ArrayList;

public class PersonalNotificationsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, DialogClosedListener {

    private static final String TAG = PersonalNotificationsFragment.class.getName();

    private LinearLayout noNotificationsLayout;
    private boolean isHasSections = false;
    private static final int PAGE_SIZE = 20;
    ArrayList<NotificationEntity> activities;
    private NotificationsListAdapter notificationsListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private boolean isApprovedLoaded = false;
    private boolean isNotificationsLoaded = false;

    private NotificationResultListener paginationResultListener = new NotificationResultListener(true);
    private NotificationResultListener simpleResultListener = new  NotificationResultListener(false);

    private DDScannerRestClient.ResultListener<Integer> resultListener = new DDScannerRestClient.ResultListener<Integer>() {
        @Override
        public void onSuccess(Integer result) {
            if (result > 0) {
                isApprovedLoaded = true;
                binding.approveCount.setText(DDScannerApplication.getInstance().getString(R.string.approve_count_pattern, result.toString()));
                if (isNotificationsLoaded) {
                    binding.approveLayout.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForFragmentResult(getChildFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_PROFILE_FRAGMENT_UNEXPECTED_ERROR, false);
        }

        @Override
        public void onInternetConnectionClosed() {

        }
    };

    private FragmentPersonalNotificationsBinding binding;

    public boolean isNotificationsLoaded() {
        return notificationsListAdapter != null && binding.progressBar.getVisibility() == View.GONE && binding.activityRc.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_notifications, container, false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        binding.activityRc.setHasFixedSize(true);
        binding.activityRc.setLayoutManager(linearLayoutManager);
        binding.activityRc.setItemAnimator(new DefaultItemAnimator());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initializeListenerForHighVersions();
        } else {
            initilizeListenerForLowVersions();
        }
        notificationsListAdapter = new NotificationsListAdapter(getActivity(), true, SharedPreferenceHelper.getActiveUserType() );
        binding.activityRc.setAdapter(notificationsListAdapter);
        binding.approveLayout.setOnClickListener(this);
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.approve_layout:
                DiveSpotsListActivity.show(getContext(), DiveSpotListSource.APPROVE, "");
                break;
        }
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onAttachToContext(context);
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
                DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getPersonalNotifications(paginationResultListener, notificationsListAdapter.getLastNotificationDate());
                notificationsListAdapter.startLoading();
//                binding.progressBarPagination.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(android.app.Activity context) {
        super.onAttach(context);

        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(context);
        }
    }

    protected void onAttachToContext(Context context) {
        try {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.setAllNotificationsFragment(this);
        } catch (ClassCastException e) {
            // waaat?
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resultListener.setCancelled(false);
        simpleResultListener.setCancelled(false);
        paginationResultListener.setCancelled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        simpleResultListener.setCancelled(true);
        paginationResultListener.setCancelled(true);
        resultListener.setCancelled(true);
    }

    public void loadNotifications() {
        if (SharedPreferenceHelper.getActiveUserType().equals(SharedPreferenceHelper.UserType.DIVECENTER)) {
            DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getApproveCount(resultListener);
        }
        DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getPersonalNotifications(simpleResultListener, null);
    }

    @Override
    public void onRefresh() {
        loadNotifications();
    }

    private class NotificationResultListener extends DDScannerRestClient.ResultListener<ArrayList<NotificationEntity>> {

        private boolean isFromPagination;

        public NotificationResultListener(boolean isFromPagination) {
            this.isFromPagination = isFromPagination;
        }

        @Override
        public void onSuccess(ArrayList<NotificationEntity> result) {
            isLoading = false;
            activities = result;
            isNotificationsLoaded = true;
            binding.swipeRefreshLayout.setRefreshing(false);
            if (binding != null) {
//                binding.progressBarPagination.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.activityRc.setVisibility(View.VISIBLE);
                binding.noNotifsView.setVisibility(View.GONE);
                if (isApprovedLoaded) {
                    binding.approveLayout.setVisibility(View.VISIBLE);
                }
                if ((activities == null || activities.size() == 0) && !isFromPagination) {
                    binding.activityRc.setVisibility(View.GONE);
                    binding.noNotifsView.setVisibility(View.VISIBLE);
                    return;
                }
                if (isFromPagination) {
                    notificationsListAdapter.dataLoaded();
                    notificationsListAdapter.add(result);
                } else {
                    if (result.size() > 0) {
                        notificationsListAdapter.setNotifications(result);
                        binding.activityRc.scrollToPosition(0);
                    }
                }
            }
        }

        @Override
        public void onConnectionFailure() {
            if (isFromPagination) {
                notificationsListAdapter.dataLoaded();
            }
            binding.swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            if (isFromPagination) {
                notificationsListAdapter.dataLoaded();
            }
            binding.swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onInternetConnectionClosed() {
            if (isFromPagination) {
                notificationsListAdapter.dataLoaded();
            }
            binding.swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onDialogClosed(int requestCode) {

    }
}

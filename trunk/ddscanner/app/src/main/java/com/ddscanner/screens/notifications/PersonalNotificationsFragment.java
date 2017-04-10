package com.ddscanner.screens.notifications;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.ddscanner.entities.NotificationOld;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.DiveSpotsListActivity;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.adapters.SectionedRecyclerViewAdapter;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lashket on 25.5.16.
 */
public class PersonalNotificationsFragment extends Fragment implements View.OnClickListener {

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

        }

        @Override
        public void onInternetConnectionClosed() {

        }
    };

    private FragmentPersonalNotificationsBinding binding;

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
            binding.activityRc.setOnScrollChangeListener(listener);
        } else {
            binding.activityRc.setOnScrollListener(scrollListener);
        }
        notificationsListAdapter = new NotificationsListAdapter(getActivity(), true, DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() );
        binding.activityRc.setAdapter(notificationsListAdapter);
        binding.approveLayout.setOnClickListener(this);
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

    private RecyclerView.OnScrollChangeListener listener = new View.OnScrollChangeListener() {
        @Override
        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
            tryingToReloadData();
        }
    };

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
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

    private void tryingToReloadData() {
        int visibleItemsCount = linearLayoutManager.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        if (!isLoading) {
            if ((visibleItemsCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                DDScannerApplication.getInstance().getDdScannerRestClient().getPersonalNotifications(paginationResultListener, notificationsListAdapter.getLastNotificationDate());
                binding.progressBarPagination.setVisibility(View.VISIBLE);
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
        Log.i("RES", "resumed");
    }

    public void loadNotifications() {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType().equals(SharedPreferenceHelper.UserType.DIVECENTER)) {
            DDScannerApplication.getInstance().getDdScannerRestClient().getApproveCount(resultListener);
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().getPersonalNotifications(simpleResultListener, null);
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
            if (binding != null) {
                binding.progressBarPagination.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.activityRc.setVisibility(View.VISIBLE);
                if (isApprovedLoaded) {
                    binding.approveLayout.setVisibility(View.VISIBLE);
                }
                if (activities == null || activities.size() == 0) {
                    binding.activityRc.setVisibility(View.GONE);
                    binding.noNotifsView.setVisibility(View.VISIBLE);
                    return;
                }
                if (isFromPagination) {
                    notificationsListAdapter.add(result);
                } else {
                    if (result.size() > 0) {
                        notificationsListAdapter.setNotifications(result);
                        binding.scrollView.scrollTo(0,0);
                    }
                }
            }
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {

        }

    }

}

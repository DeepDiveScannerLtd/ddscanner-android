package com.ddscanner.screens.notifications;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.FragmentDiveCenterProfileNotificationsBinding;
import com.ddscanner.databinding.FragmnetActivityNotificationsBinding;
import com.ddscanner.entities.Activity;
import com.ddscanner.entities.NotificationEntity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lashket on 25.5.16.
 */
public class ActivityNotificationsFragment extends Fragment {

    private static final String TAG = ActivityNotificationsFragment.class.getName();
    private static final int PAGE_SIZE = 15;

    private ArrayList<NotificationEntity> activities;
    private FragmnetActivityNotificationsBinding binding;
    private NotificationsListAdapter notificationsListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;


    private DDScannerRestClient.ResultListener<ArrayList<NotificationEntity>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<NotificationEntity>>() {
        @Override
        public void onSuccess(ArrayList<NotificationEntity> result) {
            activities = result;
            isLoading = false;
            binding.progressBarPagination.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            binding.activityRc.setVisibility(View.VISIBLE);
            if (activities == null || activities.size() == 0) {
                binding.noNotifsView.setVisibility(View.VISIBLE);
                return;
            }
            notificationsListAdapter.add(result);

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
            binding.activityRc.setOnScrollChangeListener(listener);
        } else {
            binding.activityRc.setOnScrollListener(scrollListener);
        }
        notificationsListAdapter = new NotificationsListAdapter(getActivity());
        binding.activityRc.setAdapter(notificationsListAdapter);
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
        DDScannerApplication.getInstance().getDdScannerRestClient().getActivityNotifications(resultListener);
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
                DDScannerApplication.getInstance().getDdScannerRestClient().getActivityNotifications(resultListener);
                binding.progressBarPagination.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        }
    }

}

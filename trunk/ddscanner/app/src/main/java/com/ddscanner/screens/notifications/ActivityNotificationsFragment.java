package com.ddscanner.screens.notifications;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private RecyclerView recyclerView;
    private ArrayList<NotificationEntity> activities;
    private boolean isHasSections = false;
    private LinearLayout noNotificationsLayout;
    private boolean isViewCreated;
    private FragmnetActivityNotificationsBinding binding;


    private DDScannerRestClient.ResultListener<ArrayList<NotificationEntity>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<NotificationEntity>>() {
        @Override
        public void onSuccess(ArrayList<NotificationEntity> result) {
            activities = result;
            binding.progressBar.setVisibility(View.GONE);
            binding.activityRc.setVisibility(View.VISIBLE);
                if (activities == null || activities.size() == 0) {
                    binding.noNotifsView.setVisibility(View.VISIBLE);
                    return;
                }
                binding.activityRc.setAdapter(new NotificationsListAdapter(getActivity(), activities));

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmnet_activity_notifications, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.activityRc.setHasFixedSize(true);
        binding.activityRc.setLayoutManager(linearLayoutManager);
        if (activities != null) {
            binding.activityRc.setAdapter(new NotificationsListAdapter(getActivity(), activities));
        }
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

}

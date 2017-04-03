package com.ddscanner.screens.notifications;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.ddscanner.entities.NotificationEntity;
import com.ddscanner.entities.NotificationOld;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.adapters.NotificationsListAdapterOld;
import com.ddscanner.ui.adapters.SectionedRecyclerViewAdapter;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lashket on 25.5.16.
 */
public class PersonalNotificationsFragment extends Fragment {

    private static final String TAG = PersonalNotificationsFragment.class.getName();

    private LinearLayout noNotificationsLayout;
    private boolean isHasSections = false;
    ArrayList<NotificationEntity> activities;
    private NotificationsListAdapter notificationsListAdapter;

    private DDScannerRestClient.ResultListener<ArrayList<NotificationEntity>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<NotificationEntity>>() {
        @Override
        public void onSuccess(ArrayList<NotificationEntity> result) {
            activities = result;
            binding.progressBarFull.setVisibility(View.GONE);
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

    private FragmentPersonalNotificationsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_notifications, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.activityRc.setHasFixedSize(true);
        binding.activityRc.setLayoutManager(linearLayoutManager);
        notificationsListAdapter = new NotificationsListAdapter(getActivity(), true);
        binding.activityRc.setAdapter(notificationsListAdapter);
        return binding.getRoot();
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onAttachToContext(context);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

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
        DDScannerApplication.getInstance().getDdScannerRestClient().getPersonalNotifications(resultListener);
    }

}

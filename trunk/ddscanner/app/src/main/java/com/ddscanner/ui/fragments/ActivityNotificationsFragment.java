package com.ddscanner.ui.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Activity;
import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.ui.adapters.ActivitiesListAdapter;
import com.ddscanner.ui.adapters.SectionedRecyclerViewAdapter;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lashket on 25.5.16.
 */
public class ActivityNotificationsFragment extends Fragment {

    private static final String TAG = ActivityNotificationsFragment.class.getName();

    private RecyclerView recyclerView;
    private ArrayList<Activity> activities;
    private boolean isHasSections = false;
    private LinearLayout noNotificationsLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        List<Activity> activities = new ArrayList<>();
//        activities = (ArrayList<Activity>)getArguments().getSerializable("NOTIF");

        View view = inflater.inflate(R.layout.fragmnet_activity_notifications, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.activity_rc);
        noNotificationsLayout = (LinearLayout) view.findViewById(R.id.noNotificationsView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (activities != null) {
            addList(activities);
        }
      //  recyclerView.setAdapter(new ActivitiesListAdapter(getContext()));
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

    public void addList(ArrayList<Activity> activities) {
        if (recyclerView == null) {
            this.activities = activities;
            return;
        }
        if (getContext() == null) {
            this.activities = activities;
            return;
        }
        EventsTracker.trackActivityView();
        if (activities == null || activities.size() == 0) {
            noNotificationsLayout.setVisibility(View.VISIBLE);
        } else {
            noNotificationsLayout.setVisibility(View.GONE);
        }
        if (activities == null) {
            recyclerView.setAdapter(new ActivitiesListAdapter(
                    getContext(), activities));
            return;
        }
//        if (!isHasSections) {
//            if (activities != null && this.activities != null) {
//                if (checkIsListDifferent(activities, this.activities)) {
//                    isHasSections = false;
//                    return;
//                }
//            }
//        }
        this.activities = activities;
        if (Helpers.comparingTimes(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLastShowingActivityTime(), activities.get(activities.size() - 1).getDate())
                || !Helpers.comparingTimes(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLastShowingActivityTime(), activities.get(0).getDate())) {
            recyclerView.setAdapter(new ActivitiesListAdapter(
                    getContext(), activities));
            Date date = new Date();
            long currentDateInMillis = date.getTime();
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setLastShowingActivityTime(currentDateInMillis);
            return;
        }
        int i = 0;
        while (Helpers.comparingTimes(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLastShowingActivityTime(),
                activities.get(i).getDate()) && i < activities.size() - 1) {
            i++;
        }
        ActivitiesListAdapter notificationsListAdapter = new ActivitiesListAdapter(
                getContext(), activities);
        List<SectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SectionedRecyclerViewAdapter.Section>();
        sections.add(new SectionedRecyclerViewAdapter.Section(0, "Newest"));
        sections.add(new SectionedRecyclerViewAdapter.Section(i, "Older"));
//        isHasSections = true;
        SectionedRecyclerViewAdapter.Section[] dummy =
                new SectionedRecyclerViewAdapter.Section[sections.size()];
        SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter =
                new SectionedRecyclerViewAdapter(getContext(), R.layout.section_layout,
                        R.id.section_title, notificationsListAdapter);
        sectionedRecyclerViewAdapter.setSections(sections.toArray(dummy));
        notificationsListAdapter.setSectionAdapter(sectionedRecyclerViewAdapter);
        recyclerView.setAdapter(sectionedRecyclerViewAdapter);
        Date date = new Date();
        long currentDateInMillis = date.getTime();
        DDScannerApplication.getInstance().getSharedPreferenceHelper().setLastShowingActivityTime(currentDateInMillis);
    }

    @Override
    public void setUserVisibleHint(final boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible) {
            if (recyclerView != null) {
                Date date1 = new Date();
                long currentDateInMillis = date1.getTime();
            }
        }
    }

    private boolean checkIsListDifferent(ArrayList<Activity> newNotifications, ArrayList<Activity> oldNotifications) {
        for (Activity notification : oldNotifications) {
            for (Activity notification1 : newNotifications) {
                if (!notification.getType().equals(notification1.getType()) || !notification.getUserOld().getId().equals(notification1.getUserOld().getId()) || notification.getDiveSpot().getId() != notification1.getDiveSpot().getId()) {
                    return true;
                }
            }
        }
        return false;
    }
}

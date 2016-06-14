package com.ddscanner.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.entities.Activity;
import com.ddscanner.entities.Notification;
import com.ddscanner.ui.adapters.ActivitiesListAdapter;
import com.ddscanner.ui.adapters.NotificationsListAdapter;
import com.ddscanner.ui.adapters.SectionedRecyclerViewAdapter;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lashket on 25.5.16.
 */
public class ActivityNotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Helpers helpers = new Helpers();
    private ArrayList<Activity> activities;
    private boolean isHasSections = false;

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (activities != null) {
            addList(activities);
        }
      //  recyclerView.setAdapter(new ActivitiesListAdapter(getContext()));
        return view;
    }

    public void addList(ArrayList<Activity> activities) {
        if (recyclerView == null) {
            this.activities = activities;
            return;
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
        if (helpers.comparingTimes(SharedPreferenceHelper.getLastShowingActivityTime(), activities.get(activities.size() - 1).getDate())
                || !helpers.comparingTimes(SharedPreferenceHelper.getLastShowingActivityTime(), activities.get(0).getDate())) {
            recyclerView.setAdapter(new ActivitiesListAdapter(
                    getContext(), activities));
            Date date = new Date();
            long currentDateInMillis = date.getTime();
            SharedPreferenceHelper.setLastShowingActivityTime(currentDateInMillis);
            return;
        }
        int i = 0;
        while (helpers.comparingTimes(SharedPreferenceHelper.getLastShowingActivityTime(),
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
        SharedPreferenceHelper.setLastShowingActivityTime(currentDateInMillis);
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
                if (!notification.getType().equals(notification1.getType()) || !notification.getUser().getId().equals(notification1.getUser().getId()) || notification.getDiveSpot().getId() != notification1.getDiveSpot().getId()) {
                    return true;
                }
            }
        }
        return false;
    }
}

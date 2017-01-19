package com.ddscanner.ui.fragments;

import android.annotation.TargetApi;
import android.content.Context;
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
import com.ddscanner.entities.NotificationOld;
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
public class AllNotificationsFragment extends Fragment {

    private static final String TAG = AllNotificationsFragment.class.getName();

    private RecyclerView recyclerView;
    private FragmentActivity myContext;
    private ArrayList<NotificationOld> notificationOlds = new ArrayList<>();
    private LinearLayout noNotificationsLayout;
    private boolean isHasSections = false;
    ArrayList<NotificationOld> activities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_notifications, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.activity_rc);
        noNotificationsLayout = (LinearLayout) view.findViewById(R.id.noNotificationsView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (activities != null) {
            addList(activities);
        }
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
            mainActivity.setAllNotificationsFragment(this);
        } catch (ClassCastException e) {
            // waaat?
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(final boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible) {
            if (recyclerView != null) {
                Date date1 = new Date();
                long currentDateInMillis = date1.getTime();
                DDScannerApplication.getInstance().getSharedPreferenceHelper().setLastShowingNotificationTime(currentDateInMillis);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("RES", "resumed");
    }

    public void addList(ArrayList<NotificationOld> activities) {
        if (recyclerView == null) {
            // this means fragment have not yet been inited.
            this.activities = activities;
            return;
        }
        EventsTracker.trackNotificationsView();
        if (activities == null || activities.size() == 0) {
            noNotificationsLayout.setVisibility(View.VISIBLE);
        } else {
            noNotificationsLayout.setVisibility(View.GONE);
        }
        if (activities == null) {
            recyclerView.setAdapter(new NotificationsListAdapterOld(activities, getContext(), getFragmentManager()));
            return;
        }
        this.activities = activities;
        if (Helpers.comparingTimes(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLastShowingNotificationTime(),
                activities.get(activities.size() -1).getDate()) || !Helpers.comparingTimes(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLastShowingNotificationTime(), activities.get(0).getDate())) {
            recyclerView.setAdapter(new NotificationsListAdapterOld(
                    activities, getContext(), getFragmentManager()));
            Date date = new Date();
            long currentDateInMillis = date.getTime();
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setLastShowingNotificationTime(currentDateInMillis);
            return;
        }
        int i = 0;
        while (Helpers.comparingTimes(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLastShowingNotificationTime(),
                activities.get(i).getDate()) && i < activities.size() - 1) {
            i++;
        }
        NotificationsListAdapterOld notificationsListAdapterOld = new NotificationsListAdapterOld(activities, getContext(), getFragmentManager());
        List<SectionedRecyclerViewAdapter.Section> sections = new ArrayList<SectionedRecyclerViewAdapter.Section>();
        sections.add(new SectionedRecyclerViewAdapter.Section(0, "Newest"));
        sections.add(new SectionedRecyclerViewAdapter.Section(i, "Older"));
       // isHasSections = true;
        SectionedRecyclerViewAdapter.Section[] dummy = new SectionedRecyclerViewAdapter.Section[sections.size()];
        SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter(getContext(), R.layout.section_layout, R.id.section_title, notificationsListAdapterOld);
        sectionedRecyclerViewAdapter.setSections(sections.toArray(dummy));
        notificationsListAdapterOld.setSectionAdapter(sectionedRecyclerViewAdapter);
        recyclerView.setAdapter(sectionedRecyclerViewAdapter);
        Date date = new Date();
        long currentDateInMillis = date.getTime();
        DDScannerApplication.getInstance().getSharedPreferenceHelper().setLastShowingNotificationTime(currentDateInMillis);
    }

    private boolean checkIsListDifferent(ArrayList<NotificationOld> newNotificationOlds, ArrayList<NotificationOld> oldNotificationOlds) {
        for (NotificationOld notificationOld : oldNotificationOlds) {
            for (NotificationOld notificationOld1 : newNotificationOlds) {
                if (!notificationOld.getType().equals(notificationOld1.getType()) || !notificationOld.getUserOld().getId().equals(notificationOld1.getUserOld().getId()) || notificationOld.getDiveSpotShort().getId() != notificationOld1.getDiveSpotShort().getId()) {
                    return true;
                }
            }
        }
        return false;
    }

}

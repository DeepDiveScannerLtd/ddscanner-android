package com.ddscanner.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
public class AllNotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FragmentActivity myContext;
    private ArrayList<Notification> notifications = new ArrayList<>();
    private Helpers helpers = new Helpers();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_notifications, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.activity_rc);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void setUserVisibleHint(final boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible) {
            if (recyclerView != null) {
                Date date1 = new Date();
                long currentDateInMillis = date1.getTime();
                SharedPreferenceHelper.setLastShowingNotificationTime(currentDateInMillis);
            }
        }
    }

    public void addList(ArrayList<Notification> activities) {
        NotificationsListAdapter notificationsListAdapter = new NotificationsListAdapter(
                activities, getContext(), getFragmentManager());
        List<SectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SectionedRecyclerViewAdapter.Section>();
        sections.add(new SectionedRecyclerViewAdapter.Section(0, "Newest"));
        sections.add(new SectionedRecyclerViewAdapter.Section(1, "Older"));
        SectionedRecyclerViewAdapter.Section[] dummy =
                new SectionedRecyclerViewAdapter.Section[sections.size()];
        SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter =
                new SectionedRecyclerViewAdapter(getContext(), R.layout.section_layout,
                        R.id.section_title, notificationsListAdapter);
        sectionedRecyclerViewAdapter.setSections(sections.toArray(dummy));
        recyclerView.setAdapter(sectionedRecyclerViewAdapter);
    }

}

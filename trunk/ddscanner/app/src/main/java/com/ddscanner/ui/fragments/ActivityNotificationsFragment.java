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
import com.ddscanner.ui.adapters.ActivitiesListAdapter;
import com.ddscanner.ui.adapters.NotificationsListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 25.5.16.
 */
public class ActivityNotificationsFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        List<Activity> activities = new ArrayList<>();
         // activities = (ArrayList<Activity>)getArguments().getSerializable("NOTIF");

        View view = inflater.inflate(R.layout.fragmnet_activity_notifications, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.activity_rc);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new ActivitiesListAdapter(getContext()));
        return view;
    }
}

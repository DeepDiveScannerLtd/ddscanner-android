package com.ddscanner.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.ui.adapters.DiveCentersListAdapter;
import com.ddscanner.utils.EventTrackerHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lashket on 4.2.16.
 */
public class DiveCenterListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppsFlyerLib.getInstance().trackEvent(getActivity().getApplicationContext(),
                EventTrackerHelper.EVENT_DIVE_CENTERS_LIST_OPENED, new HashMap<String, Object>());
        Bundle args = this.getArguments();
        ArrayList<DiveCenter> divecenters = args.getParcelableArrayList("DIVESPOTS");
        String logoPath = args.getString("LOGOPATH");
        View view = inflater.inflate(R.layout.dive_center_list_fragment, container, false);
        RecyclerView rc = (RecyclerView) view.findViewById(R.id.dc_rc);
        rc.setHasFixedSize(true);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));
        rc.setAdapter(new DiveCentersListAdapter(divecenters, logoPath, getActivity()));
        return view;
    }
}
package com.ddscanner.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.ui.adapters.SearchDiveSpotListAdapter;

import java.util.ArrayList;

/**
 * Created by lashket on 15.6.16.
 */
public class SearchDiveSpotsFragment extends Fragment{

    private RecyclerView diveSpotsListRc;
    private ScrollView noResultsView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_serach_dive_spot, container, false);
        diveSpotsListRc = (RecyclerView) view.findViewById(R.id.spots_list_rc);
        noResultsView = (ScrollView) view.findViewById(R.id.no_results);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        diveSpotsListRc.setLayoutManager(linearLayoutManager);
        return view;
    }

    public void setDiveSpots(ArrayList<DiveSpot> diveSpots) {
        if (diveSpots == null || diveSpots.size() == 0) {
            noResultsView.setVisibility(View.VISIBLE);
            diveSpotsListRc.setVisibility(View.GONE);
        } else {
            noResultsView.setVisibility(View.GONE);
            diveSpotsListRc.setVisibility(View.VISIBLE);
            diveSpotsListRc.setAdapter(new SearchDiveSpotListAdapter(diveSpots, getContext()));
        }
    }

}

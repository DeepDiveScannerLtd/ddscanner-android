package com.ddscanner.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.ui.activities.DiveCentersActivity;
import com.ddscanner.ui.adapters.DiveCentersListAdapter;

import java.util.ArrayList;

/**
 * Created by lashket on 4.2.16.
 */
public class DiveCenterListFragment extends Fragment {

    private DiveCentersListAdapter diveCentersListAdapter;
    private RecyclerView rc;
    private RelativeLayout please;
    private Button btnGoToMap;
    private DiveCentersActivity diveCentersActivity;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dive_center_list_fragment, container, false);
        rc = (RecyclerView) view.findViewById(R.id.dc_rc);
        please = (RelativeLayout) view.findViewById(R.id.please);
        btnGoToMap = (Button) view.findViewById(R.id.btn_back_to_map);
        btnGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        rc.setHasFixedSize(true);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    public void fillDiveCenters(ArrayList<DiveCenter> diveCenters, String logoPath, ViewPager viewPager, TabLayout tabLayout) {
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
        if (diveCentersListAdapter == null) {
            diveCentersListAdapter = new DiveCentersListAdapter(diveCenters, logoPath, getActivity());
            rc.setAdapter(diveCentersListAdapter);
        } else {
            diveCentersListAdapter.setDiveCenters(diveCenters, logoPath);
        }

        if (!diveCenters.isEmpty()) {
            rc.setVisibility(View.VISIBLE);
            please.setVisibility(View.GONE);
        } else {
            rc.setVisibility(View.GONE);
            please.setVisibility(View.VISIBLE);
        }

    }
}

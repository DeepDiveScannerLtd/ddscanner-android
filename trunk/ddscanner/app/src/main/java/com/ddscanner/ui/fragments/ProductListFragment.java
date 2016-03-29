package com.ddscanner.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.ui.adapters.ProductListAdapter;
import com.ddscanner.utils.EventTrackerHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lashket on 23.12.15.
 */
public class ProductListFragment extends Fragment {

    private RecyclerView rc;
    private ProductListAdapter productListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AppsFlyerLib.getInstance().trackEvent(getActivity().getApplicationContext(),
                EventTrackerHelper.EVENT_DIVE_SITES_LIST_OPENED, new HashMap<String, Object>());

        View view = inflater.inflate(R.layout.product_list_fragment, container, false);
        rc = (RecyclerView) view.findViewById(R.id.cv);
        
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
     //   linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rc.setLayoutManager(linearLayoutManager);
        rc.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    public void fillDiveSpots(ArrayList<DiveSpot> diveSpots) {
        if (productListAdapter == null) {
            productListAdapter = new ProductListAdapter(diveSpots, getActivity());
            rc.setAdapter(productListAdapter);
        } else {
            productListAdapter.setDiveSpots(diveSpots);
        }
    }

}

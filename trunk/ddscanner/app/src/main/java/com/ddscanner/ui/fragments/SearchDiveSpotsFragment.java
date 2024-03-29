package com.ddscanner.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.screens.divespot.add.AddDiveSpotActivity;
import com.ddscanner.ui.adapters.SearchDiveSpotListAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.SharedPreferenceHelper;

import java.util.ArrayList;

public class SearchDiveSpotsFragment extends Fragment implements View.OnClickListener{

    private RecyclerView diveSpotsListRc;
    private RelativeLayout noResultsView;
    private TextView addManually;
    private ArrayList<DiveSpotShort> diveSpotShorts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_serach_dive_spot, container, false);
        diveSpotsListRc = view.findViewById(R.id.spots_list_rc);
        noResultsView = view.findViewById(R.id.no_results);
        addManually = view.findViewById(R.id.add_spot);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        diveSpotsListRc.setLayoutManager(linearLayoutManager);
        addManually.setOnClickListener(this);
        if (diveSpotShorts != null) {
            setDiveSpotShorts(diveSpotShorts);
        }
        return view;
    }

    public void setDiveSpotShorts(ArrayList<DiveSpotShort> diveSpotShorts) {
        if (diveSpotsListRc == null) {
            this.diveSpotShorts = diveSpotShorts;
            return;
        }
        if (diveSpotShorts == null || diveSpotShorts.size() == 0) {
            noResultsView.setVisibility(View.VISIBLE);
            diveSpotsListRc.setVisibility(View.GONE);
        } else {
            noResultsView.setVisibility(View.GONE);
            diveSpotsListRc.setVisibility(View.VISIBLE);
            diveSpotsListRc.setAdapter(new SearchDiveSpotListAdapter(diveSpotShorts));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_spot:
                if (SharedPreferenceHelper.getIsUserSignedIn()) {
                    AddDiveSpotActivity.showForResult(getActivity(), ActivitiesRequestCodes.REQUEST_CODE_SEARCH_DIVE_SPOT_ADD_SPOT, true);
                } else {
                    DDScannerApplication.bus.post(new OpenAddDsActivityAfterLogin());
                }
                break;
        }
    }

}

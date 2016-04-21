package com.ddscanner.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.adapters.MapListPagerAdapter;
import com.ddscanner.ui.adapters.PlacesPagerAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by lashket on 20.4.16.
 */
public class MapListFragment extends Fragment {

    private ViewPager mapListViewPager;
    private View view;
    private RelativeLayout toast;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maplist, container, false);
        findViews();
        setUi();
        return view;
    }

    private void findViews() {
        mapListViewPager = (ViewPager) view.findViewById(R.id.maplist_view_pager);
        toast = (RelativeLayout) view.findViewById(R.id.toast);
        progressBar = (ProgressBar) view.findViewById(R.id.request_progress);
    }

    private void setUi() {
        mapListViewPager.setAdapter(new MapListPagerAdapter(getContext(),getActivity().getFragmentManager(), new LatLng(1,1),
                new LatLngBounds(new LatLng(1,1), new LatLng(2,2)), toast, progressBar, mapListViewPager));
    }

}

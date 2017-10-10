package com.ddscanner.ui.fragments;


import android.support.v4.app.Fragment;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.interfaces.LocationReadyListener;
import com.ddscanner.interfaces.PermissionsGrantedListener;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

public class BaseFragment extends Fragment {

    public void getLocation(PermissionsGrantedListener listener) {
        BaseAppCompatActivity baseAppCompatActivity = (BaseAppCompatActivity) getActivity();
        baseAppCompatActivity.grandLocationPermission(listener);
    }

    @Override
    public void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

}

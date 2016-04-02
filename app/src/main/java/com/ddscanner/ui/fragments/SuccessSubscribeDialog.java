package com.ddscanner.ui.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;

/**
 * Created by lashket on 26.1.16.
 */
public class SuccessSubscribeDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.succes_subscribe_dialog, container, false);
        return v;
    }

}

package com.ddscanner.ui.fragments;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment{

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

}

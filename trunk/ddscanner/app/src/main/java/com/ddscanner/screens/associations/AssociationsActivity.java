package com.ddscanner.screens.associations;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.ddscanner.R;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.rey.material.widget.ProgressView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssociationsActivity extends BaseAppCompatActivity {

    @BindView(R.id.search_list)
    RecyclerView searchList;
    @BindView(R.id.progress_view)
    ProgressView progressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_search);
        ButterKnife.bind(this);
    }
}

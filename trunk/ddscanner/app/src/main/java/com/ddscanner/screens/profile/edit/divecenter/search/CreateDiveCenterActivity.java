package com.ddscanner.screens.profile.edit.divecenter.search;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.databinding.ActivityNewDiveCenterBinding;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

public class CreateDiveCenterActivity extends BaseAppCompatActivity {

    private ActivityNewDiveCenterBinding binding;
    private static final String ARG_DIVE_CENTER = "dive_center";

    public static void showForCreateDiveCenter(Activity context, int requestCode) {
        Intent intent = new Intent(context, CreateDiveCenterActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void showForEditCurrentDiveCenter(Activity context, int requestCode, String currentData) {
        Intent intent = new Intent(context, CreateDiveCenterActivity.class);
        intent.putExtra(ARG_DIVE_CENTER, currentData);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_dive_center);
        setupToolbar(R.string.new_dive_center, R.id.toolbar);
    }

    public void saveData(View view) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }
}

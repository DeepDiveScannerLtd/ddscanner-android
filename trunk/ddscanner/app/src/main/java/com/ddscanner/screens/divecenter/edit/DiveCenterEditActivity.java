package com.ddscanner.screens.divecenter.edit;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.ddscanner.R;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

//todo create BaseActivity and import to this class
public class DiveCenterEditActivity extends BaseAppCompatActivity {

    DiveCenterEditContract.Presenter mPresenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dive_center_edit_layout);

        DiveCenterEditFragment DiveCenterEditFragment = (DiveCenterEditFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frame_layout_content);
        if (DiveCenterEditFragment == null) {
            DiveCenterEditFragment = DiveCenterEditFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frame_layout_content, DiveCenterEditFragment);
            transaction.commit();
        }
        mPresenter = new DiveCenterEditPresenter(this, DiveCenterEditFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.stop();
    }

}

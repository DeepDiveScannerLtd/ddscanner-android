package com.ddscanner.screens.divecenter.edit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.ddscanner.ui.base.BasePresenter;
import com.ddscanner.ui.views.EmailInputView;
import com.ddscanner.ui.views.PhoneInputView;

public class DiveCenterEditPresenter extends BasePresenter implements DiveCenterEditContract.Presenter {

    private DiveCenterEditContract.View mView;

    private Context mContext;

    public DiveCenterEditPresenter(@NonNull Context context, @NonNull DiveCenterEditContract.View view) {
        this.mView = view;
        this.mContext = context;
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void addDiveSpot() {

    }

    @Override
    public void addPhone() {
        PhoneInputView phoneInputView = new PhoneInputView(mContext);
        mView.addPhone(phoneInputView);
    }

    @Override
    public void addEmail() {
        EmailInputView emailInputView = new EmailInputView(mContext);
        mView.addEmail(emailInputView);
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

        }
    }

}

package com.ddscanner.screens.divecenter.edit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Address;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.Language;
import com.ddscanner.ui.base.BaseFragment;
import com.ddscanner.ui.views.EmailInputView;
import com.ddscanner.ui.views.PhoneInputView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class DiveCenterEditFragment extends BaseFragment implements DiveCenterEditContract.View {

    @BindView(R.id.add_phone_btn)
    TextView addPhoneButton;
    @BindView(R.id.phones)
    LinearLayout phonesLayout;
    @BindView(R.id.add_email_btn)
    TextView addEmailButton;
    @BindView(R.id.emails)
    LinearLayout emails;

    private DiveCenterEditContract.Presenter mPresenter;

    public DiveCenterEditFragment() {

    }

    public static DiveCenterEditFragment newInstance() {
        return new DiveCenterEditFragment();
    }

    @Override
    public void setPresenter(DiveCenterEditContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dive_center_edit_layout, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setBio(String bio) {

    }

    @Override
    public void setAddress(Address address) {

    }

    @Override
    public void setDiveSpots(ArrayList<DiveSpotShort> diveSpots) {

    }

    @Override
    public void setEmails(ArrayList<String> emails) {

    }

    @Override
    public void setLanguages(ArrayList<Language> languages) {

    }

    @Override
    public void setPhones(ArrayList<String> phones) {

    }

    @Override
    public void setPhoto(String photoUrl) {

    }

    @Override
    public void addEmail(EmailInputView emailInputView) {
        emails.addView(emailInputView);
    }

    @Override
    public void addPhone(PhoneInputView phoneInputView) {
        phonesLayout.addView(phoneInputView);
    }

    @Override
    public void addDiveSpot(DiveSpotShort diveSpotShort) {

    }

    @OnClick(R.id.add_email_btn)
    public void addEmailClick() {
        mPresenter.addEmail();
    }

    @OnClick(R.id.add_phone_btn)
    public void addPhoneClick() {
        mPresenter.addPhone();
    }

}

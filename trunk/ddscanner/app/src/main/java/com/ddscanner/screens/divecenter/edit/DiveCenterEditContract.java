package com.ddscanner.screens.divecenter.edit;

import android.content.Intent;

import com.ddscanner.entities.Address;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.Language;
import com.ddscanner.ui.base.BaseContract;
import com.ddscanner.ui.views.EmailInputView;
import com.ddscanner.ui.views.PhoneInputView;

import java.util.ArrayList;

//todo create BaseContract and import to this class
public interface DiveCenterEditContract {

    interface View extends BaseContract.View<Presenter> {

        void setName(String name);

        void setBio(String bio);

        void setPhones(ArrayList<String> phones);

        void setEmails(ArrayList<String> emails);

        void setAddress(Address address);

        void setDiveSpots(ArrayList<DiveSpotShort> diveSpots);

        void setLanguages(ArrayList<Language> languages);

        void setPhoto(String photoUrl);

        void addPhone(PhoneInputView phoneInputView);

        void addEmail(EmailInputView emailInputView);

        void addDiveSpot(DiveSpotShort diveSpotShort);
    }

    interface Presenter extends BaseContract.Presenter {

        void result(int requestCode, int resultCode, Intent data);

        void addPhone();

        void addEmail();

        void addDiveSpot();

    }
}

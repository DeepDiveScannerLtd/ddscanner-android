package com.ddscanner.ui.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.ddscanner.R;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

public class ExampleActivityCountry extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText editText;

    public static void show(Context context) {
        Intent intent = new Intent(context, ExampleActivityCountry.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        countryCodePicker = findViewById(R.id.ccp);
        editText = findViewById(R.id.edit_text);
        countryCodePicker.registerCarrierNumberEditText(editText);
        int countryCode;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            // phone must begin with '+'
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse("+3752922896", "");
            countryCode = numberProto.getCountryCode();
            System.err.println("NumberParseException was thrown: ");
            countryCodePicker.setFullNumber("+3752922896");
//            countryCodePicker.setCountryForPhoneCode(countryCode);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

    }
}

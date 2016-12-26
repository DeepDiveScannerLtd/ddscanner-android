package com.ddscanner.screens.profile.edit;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.databinding.EditDcProfileViewBinding;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.rey.material.widget.EditText;

import java.util.ArrayList;


public class EditDiveCenterProfileActivity extends BaseAppCompatActivity implements BaseAppCompatActivity.PictureTakenListener {

    private ArrayList<EditText> phonesEditTexts = new ArrayList<>();
    private ArrayList<EditText> emailsEditTexts = new ArrayList<>();

    private EditDcProfileViewBinding binding;

    public static void showForResult(Activity context, String diveCenterString, int requestCode) {
        Intent intent = new Intent(context, EditDiveCenterProfileActivity.class);
        intent.putExtra("divecenter", diveCenterString);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.edit_dc_profile_view);
        binding.setHandlers(this);
        setupToolbar(R.string.edit_profile_activity, R.id.toolbar, R.menu.edit_profile_menu);
        addPhoneClicked(null);
        addEmailClicked(null);
        setuoUi();
    }

    private void setuoUi() {
        binding.country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("213", "3123");
            }
        });
    }

    public void addEmailClicked(View view) {
        EditText editText = (EditText) getLayoutInflater().inflate(R.layout.edit_dive_center_email_edit_text, null);
        editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        emailsEditTexts.add(editText);
        binding.emails.addView(editText);
    }

    public void addPhoneClicked(View view) {
        EditText editText = (EditText) getLayoutInflater().inflate(R.layout.edit_dive_center_edit_text, null);
        editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phonesEditTexts.add(editText);
        binding.phones.addView(editText);
    }

    public void pickCountryClicked(View view) {
        Log.i("213", "3123");
    }

    public void changePhotoButtonCLicked(View view) {

    }

    @Override
    public void onPicturesTaken(ArrayList<String> pictures) {

    }

    @Override
    public void onPictureFromCameraTaken(String picture) {

    }

}

package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Translation;

public class AddTranslationDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextView errorName;
    private TextView errorDescription;
    private TextView languageName;
    private AppCompatEditText name;
    private AppCompatEditText description;
    private Button buttonSave;
    private String languageCode;
    private String languageNameText;
    private ImageView close;

    private static final String ARG_LANGUAGE = "language";
    private static final String ARG_LANGUAGE_CODE = "code";
    private static final String ARG_NAME = "name";
    private static final String ARG_DESCRIPTION = "description";

    public AddTranslationDialogFragment() {

    }

    public static void show(FragmentManager fragmentManager, String code, String languageName, String name, String description) {
        AddTranslationDialogFragment addTranslationDialogFragment = new AddTranslationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_LANGUAGE, languageName);
        bundle.putString(ARG_NAME, name);
        bundle.putString(ARG_DESCRIPTION, description);
        bundle.putString(ARG_LANGUAGE_CODE, code);
        addTranslationDialogFragment.setArguments(bundle);
        addTranslationDialogFragment.setCancelable(false);
        addTranslationDialogFragment.show(fragmentManager, "");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_translation, null);
        builder.setView(view);
        builder.setTitle(null);
        languageCode = getArguments().getString(ARG_LANGUAGE_CODE);
        languageNameText = getArguments().getString(ARG_LANGUAGE);
        errorName = (TextView) view.findViewById(R.id.error_name);
        errorDescription = (TextView) view.findViewById(R.id.error_description);
        languageName = (TextView) view.findViewById(R.id.language_name);
        name = (AppCompatEditText) view.findViewById(R.id.name);
        close = (ImageView) view.findViewById(R.id.image_close);
        description = (AppCompatEditText) view.findViewById(R.id.description);
        buttonSave = (Button) view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(this);
        close.setOnClickListener(this);
        languageName.setText(getArguments().getString(ARG_LANGUAGE));
        name.setText(getArguments().getString(ARG_NAME));
        description.setText(getArguments().getString(ARG_DESCRIPTION));

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_save:
                errorDescription.setVisibility(View.GONE);
                errorName.setVisibility(View.GONE);
                if (!validateData()) {
                    return;
                }
                TranslationChangedListener translationChangedListener;
                translationChangedListener = (TranslationChangedListener) getActivity();
                Translation translation = new Translation();
                translation.setCode(languageCode);
                translation.setLanguage(languageNameText);
                if (name.getText().toString().length() > 1 && description.getText().toString().length() > 31) {
                    translation.setName(name.getText().toString());
                    translation.setDescription(description.getText().toString());
                } else {
                    return;
                }
                translationChangedListener.onTranslationChanged(translation);
                this.dismiss();
                break;
            case R.id.image_close:
                this.dismiss();
                break;
        }
    }

    public interface TranslationChangedListener {
        void onTranslationChanged(Translation translation);
    }


    private boolean validateData() {
        boolean isNameBad = false;
        boolean isDescriptionBad = false;
        if (name.getText().toString().length() < 2) {
            errorName.setText(R.string.name_error_text_small);
            errorName.setVisibility(View.VISIBLE);
            isNameBad = true;
        }
        if (name.getText().toString().length() > 128) {
            errorName.setText(R.string.name_error_text_long);
            errorName.setVisibility(View.VISIBLE);
            isNameBad = true;
        }
        if (description.getText().length() < 32) {
            errorDescription.setText(R.string.description_error_text_short);
            errorDescription.setVisibility(View.VISIBLE);
            isDescriptionBad = true;
        }
        if (description.getText().length() > 2048) {
            errorDescription.setText(R.string.description_error_text_long);
            errorDescription.setVisibility(View.VISIBLE);
            isDescriptionBad = true;
        }

        if (isNameBad || isDescriptionBad) {
            return false;
        }
        return true;
    }

}
package com.ddscanner.ui.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.ddscanner.R;

public class BaseTextInput extends RelativeLayout {

    TextInputLayout textInputLayout;
    EditText editText;

    public BaseTextInput(Context context) {
        super(context);
        init(context, null);
    }

    public BaseTextInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseTextInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        inflate(context, R.layout.view_base_text_input, this);
        textInputLayout = findViewById(R.id.text_input_layout);
        editText = findViewById(R.id.edit_text);
        if (attributeSet != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attributeSet, R.styleable.BaseTextInput);
            CharSequence hint = arr.getString(R.styleable.BaseTextInput_edit_text_hint);
            CharSequence text = arr.getString(R.styleable.BaseTextInput_edit_text_text);
            int length = arr.getInteger(R.styleable.BaseTextInput_edit_text_length, 255);
            editText.setText(text);
            textInputLayout.setHint(hint);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
            arr.recycle();
        }
    }

    public String getText() {
        return editText.getText().toString().trim();
    }

    public void setText(String text) {
        editText.setText(text);
    }

    public void setError(String text) {
        textInputLayout.setError(text);
    }

    public void hideError() {
        textInputLayout.setErrorEnabled(false);
    }

    public void disable() {
        editText.setEnabled(false);
        editText.setClickable(false);
    }

}

package com.ddscanner.screens.divecenter.request;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;

public class DiveCenterRequestInputView extends LinearLayout {

    View divider;
    EditText input;
    TextView title;
    TextView error;

    public DiveCenterRequestInputView(Context context) {
        super(context);
        init(context, null);
    }

    public DiveCenterRequestInputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DiveCenterRequestInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attributeSet) {
        inflate(context, R.layout.view_div_center_request_input, this);
        divider = findViewById(R.id.divider);
        error = findViewById(R.id.error_view);
        title = findViewById(R.id.input_title);
        input = findViewById(R.id.input_text);
        if (attributeSet != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attributeSet, R.styleable.DiveCenterRequestInputView);
            CharSequence title_text = arr.getString(R.styleable.DiveCenterRequestInputView_title_name);
            CharSequence inputHint = arr.getString(R.styleable.DiveCenterRequestInputView_input_hint);
            CharSequence error_text = arr.getString(R.styleable.DiveCenterRequestInputView_error_text);
            int length = arr.getInteger(R.styleable.DiveCenterRequestInputView_length, 255);
            int lines = arr.getInteger(R.styleable.DiveCenterRequestInputView_lines, 255);
            error.setText(error_text);
            title.setText(title_text);
            input.setHint(inputHint);
            input.setMaxLines(lines);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
            arr.recycle();
        }
    }

    public void showError(int errorResourceId) {
        divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.divider_error_color));
        error.setVisibility(VISIBLE);
        error.setText(errorResourceId);
    }

    public void hideError() {
        divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.divider_color));
        error.setVisibility(GONE);
    }

    public String getInputText() {
        return input.getText().toString();
    }

    public void setInputType(int inputType) {
        input.setInputType(inputType);
    }

}

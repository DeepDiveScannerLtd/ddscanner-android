package com.ddscanner.ui.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.ddscanner.R;

public class EditTextWithErrorView extends ConstraintLayout {

    private EditText inputText;
    private TextInputLayout textInputLayout;

    public EditTextWithErrorView(Context context) {
        super(context);
        init(null);
    }

    public EditTextWithErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EditTextWithErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_edit_text_with_error, this);
        inputText = findViewById(R.id.data_input);
        textInputLayout = findViewById(R.id.input_layout);
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EditTextWithErrorView, 0, 0);
            try {
                inputText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(typedArray.getInt(R.styleable.EditTextWithErrorView_maxLength, 32))});
            } finally {
                typedArray.recycle();
            }
        }
    }

    public void setError(String error) {
        textInputLayout.setError(error);
    }

    public String getText() {
        return inputText.getText().toString();
    }

}

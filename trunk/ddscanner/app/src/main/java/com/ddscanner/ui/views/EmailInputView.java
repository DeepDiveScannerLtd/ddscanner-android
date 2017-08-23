package com.ddscanner.ui.views;


import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.interfaces.RemoveLayoutClickListener;

public class EmailInputView extends RelativeLayout {

    TextView errorView;
    EditText editText;
    RemoveLayoutClickListener removeLayoutClickListener;
    TextInputLayout textInputLayout;

    public EmailInputView(Context context) {
        super(context);
        init();
    }

    public EmailInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmailInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.edit_dive_center_email_edit_text, this);
        errorView = findViewById(R.id.email_error);
        editText = findViewById(R.id.email);
        textInputLayout = findViewById(R.id.textInputLayout);
    }

    public void setRemoveLayoutClickListener(RemoveLayoutClickListener removeLayoutClickListener) {
        this.removeLayoutClickListener = removeLayoutClickListener;
        editText.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                    this.removeLayoutClickListener.onRemoveClicked(this);
                    return true;
                }
            }
            return false;
        });
//        removeView.setOnClickListener(view -> this.removeLayoutClickListener.onRemoveClicked(this));
    }

    public void setText(String text) {
        editText.setText(text);
    }

    public void showError() {
        errorView.setVisibility(VISIBLE);
    }

    public void hideError() {
        errorView.setVisibility(GONE);
    }

    public String getText() {
        return editText.getText().toString();
    }

}

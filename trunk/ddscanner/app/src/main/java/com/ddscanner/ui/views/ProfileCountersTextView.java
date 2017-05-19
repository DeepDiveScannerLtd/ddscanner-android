package com.ddscanner.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ddscanner.R;

public class ProfileCountersTextView extends AppCompatTextView {

    public ProfileCountersTextView(Context context) {
        super(context);
    }

    public ProfileCountersTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileCountersTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setValue(int value) {
        if (value > 0) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.black_text));
            setText(String.valueOf(value));
            return;
        }
        setTextColor(ContextCompat.getColor(getContext(), R.color.profile_counter_gray_color));
        setText(String.valueOf(value));
    }

}

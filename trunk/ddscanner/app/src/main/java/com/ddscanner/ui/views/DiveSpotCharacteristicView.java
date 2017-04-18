package com.ddscanner.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.databinding.ViewDiveSpotCharachteristicBinding;

public class DiveSpotCharacteristicView extends RelativeLayout {

    private ViewDiveSpotCharachteristicBinding binding;

    public DiveSpotCharacteristicView(Context context) {
        super(context);

        init(null);
    }

    public DiveSpotCharacteristicView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public DiveSpotCharacteristicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.view_dive_spot_charachteristic, this, true);
    }

    public void setViewData(int iconResource, int titleResId, String subtitle) {
        binding.title.setText(titleResId);
        binding.subtitle.setText(subtitle);
        binding.icon.setImageDrawable(ContextCompat.getDrawable(getContext(), iconResource));
    }

}

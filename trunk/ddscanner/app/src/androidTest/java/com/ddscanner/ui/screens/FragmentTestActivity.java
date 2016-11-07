package com.ddscanner.ui.screens;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.ddscanner.R;

public class FragmentTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FrameLayout view = new FrameLayout(this);
        view.setId(android.R.id.content);

        setContentView(view);
    }
}

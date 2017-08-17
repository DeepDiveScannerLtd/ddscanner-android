package com.ddscanner.screens.booking.tickets;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ddscanner.R;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

public class TicketsActivity extends BaseAppCompatActivity {

    public static void show(Context context) {
        Intent intent = new Intent(context, TicketsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_tickets);
    }
}

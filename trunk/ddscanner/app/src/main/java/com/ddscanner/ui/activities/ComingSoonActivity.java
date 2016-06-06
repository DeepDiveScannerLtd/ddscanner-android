package com.ddscanner.ui.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.utils.EventTrackerHelper;

import java.util.HashMap;

public class ComingSoonActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendButton;
    private EditText emailField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coming_soon);

        AppsFlyerLib.getInstance().startTracking(this, "5A7vyAMVwKT4RBiTaxrpSU");

        emailField = (EditText) findViewById(R.id.email_field);
        TextView tv = (TextView) findViewById(R.id.title);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        tv.setTypeface(face);
        tv = (TextView) findViewById(R.id.subtitle);
        face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        tv.setTypeface(face);
        face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        sendButton = (Button) findViewById(R.id.btn_send);
        sendButton.setTypeface(face);

        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final String email = emailField.getText().toString();
        if (!TextUtils.isEmpty(email)) {
            switch (view.getId()) {
                case R.id.btn_send:
                    AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                            EventTrackerHelper.EVENT_SUBSCRIBED_FOR_UPDATE, new HashMap<String, Object>() {{
                                put(EventTrackerHelper.PARAM_SUBSCRIPTION_EMAIL, email);
                            }});
                    Toast.makeText(this, "Thanks! You’ll get the link soon.", Toast.LENGTH_LONG).show();
                    break;
            }
        } else {
            Toast.makeText(this, "Email field must not be empty", Toast.LENGTH_SHORT).show();
        }
    }
}

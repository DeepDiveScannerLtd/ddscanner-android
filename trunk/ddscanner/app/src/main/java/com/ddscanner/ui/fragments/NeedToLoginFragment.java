package com.ddscanner.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.LoginViaFacebookClickEvent;
import com.ddscanner.events.LoginViaGoogleClickEvent;
import com.ddscanner.ui.activities.PrivacyPolicyActivity;

/**
 * Created by lashket on 20.4.16.
 */
public class NeedToLoginFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = NeedToLoginFragment.class.getName();
    private static final String ARGS_KEY_TITLE_RES_ID = "ARGS_KEY_TITLE_RES_ID";

    private TextView titleTextView;
    private TextView privacyPolicy;
    private Button fbCustomLogin;
    private Button googleCustomSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_need_to_login, container, false);
        findViews(v);

        if (getArguments() != null) {
            int titleResId = getArguments().getInt(ARGS_KEY_TITLE_RES_ID);
            titleTextView.setText(titleResId);
        }

        fbCustomLogin.setOnClickListener(this);
        googleCustomSignIn.setOnClickListener(this);
        int color = ContextCompat.getColor(getContext(), R.color.primary);
        SpannableString spannableString = new SpannableString(privacyPolicy.getText());
        spannableString.setSpan(new ForegroundColorSpan(color), 32, 48, 0);
        spannableString.setSpan(new ForegroundColorSpan(color), 53, spannableString.length(), 0);
        privacyPolicy.setText(spannableString);
        privacyPolicy.setOnClickListener(this);

        return v;
    }

    private void findViews(View v) {
        titleTextView = (TextView) v.findViewById(R.id.need_to_login_message);
        privacyPolicy = (TextView) v.findViewById(R.id.privacy_policy);
//        fbCustomLogin = (Button) v.findViewById(R.id.fb_custom);
//        googleCustomSignIn = (Button) v.findViewById(R.id.custom_google);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fb_custom:
                DDScannerApplication.bus.post(new LoginViaFacebookClickEvent());
                break;
            case R.id.custom_google:
                DDScannerApplication.bus.post(new LoginViaGoogleClickEvent());
                break;
            case R.id.privacy_policy:
                PrivacyPolicyActivity.show(getContext());
                break;
        }
    }

    public static NeedToLoginFragment getInstance(int titleResId) {
        NeedToLoginFragment needToLoginFragment = new NeedToLoginFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_KEY_TITLE_RES_ID, titleResId);
        needToLoginFragment.setArguments(args);
        return needToLoginFragment;
    }

}

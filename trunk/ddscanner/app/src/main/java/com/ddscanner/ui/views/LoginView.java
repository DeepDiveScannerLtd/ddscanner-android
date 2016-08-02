package com.ddscanner.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.LoginViaFacebookClickEvent;
import com.ddscanner.events.LoginViaGoogleClickEvent;
import com.ddscanner.ui.activities.PrivacyPolicyActivity;
import com.ddscanner.ui.activities.TermsOfServiceActivity;

public class LoginView extends RelativeLayout implements View.OnClickListener {

    private TextView titleTextView;
    private TextView privacyPolicy;
    private Button fbCustomLogin;
    private Button googleCustomSignIn;
    
    public LoginView(Context context) {
        super(context);

        init(null);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_login, this);

        titleTextView = (TextView) findViewById(R.id.need_to_login_message);
        privacyPolicy = (TextView) findViewById(R.id.privacy_policy);
        fbCustomLogin = (Button) findViewById(R.id.fb_custom);
        googleCustomSignIn = (Button) findViewById(R.id.custom_google);

        fbCustomLogin.setOnClickListener(this);
        googleCustomSignIn.setOnClickListener(this);
        final SpannableString spannableString = new SpannableString(privacyPolicy.getText());
        privacyPolicy.setHighlightColor(Color.TRANSPARENT);
        spannableString.setSpan(new MyClickableSpan(privacyPolicy.getText().toString()) {
            @Override
            public void onClick(View tv) {
                TermsOfServiceActivity.show(getContext());
            }
        }, 32,48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new MyClickableSpan(privacyPolicy.getText().toString()) {
            @Override
            public void onClick(View tv) {
                PrivacyPolicyActivity.show(getContext());
                tv.invalidate();
            }
        }, 53, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        privacyPolicy.setText(spannableString);

        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.LoginView);
            CharSequence title = arr.getString(R.styleable.LoginView_title);
            if (title != null) {
                titleTextView.setText(title);
            }
            arr.recycle();  // Do this when done.
        }
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

    public interface LoginStateChangeListener {
        void onLoggedIn();
        void onLoggedOut();
    }

    class MyClickableSpan extends ClickableSpan {

        String clicked;
        public MyClickableSpan(String string) {
            super();
            clicked = string;
        }

        public void onClick(View tv) {

        }

        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.primary));
            ds.setUnderlineText(false);
        }
    }
}
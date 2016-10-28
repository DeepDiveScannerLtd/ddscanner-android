package com.ddscanner.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.activities.PrivacyPolicyActivity;
import com.ddscanner.ui.activities.TermsOfServiceActivity;

public class LoginView extends RelativeLayout implements View.OnClickListener {

    private TextView titleTextView;
    private TextView privacyPolicy;
    private Button signUpButton;
    private Button loginButton;
    private TabLayout tabLayout;
    private RelativeLayout signView;
    private RelativeLayout loginView;
    private Button buttonSubmitData;
    private LinearLayout loginWithFacebook;
    private LinearLayout loginWithGoogle;

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
        signUpButton = (Button) findViewById(R.id.sign_up);
        loginButton = (Button) findViewById(R.id.login);
        signView = (RelativeLayout) findViewById(R.id.sign_up_view);
        loginView = (RelativeLayout) findViewById(R.id.login_view_first);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        buttonSubmitData = (Button) findViewById(R.id.btn_sign_up);
        loginWithFacebook = (LinearLayout) findViewById(R.id.fb_custom);
        loginWithGoogle = (LinearLayout) findViewById(R.id.custom_google);
        signUpButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        setupTabLayout();
        setPrivacyPolicyText();

        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.LoginView);
            CharSequence title = arr.getString(R.styleable.LoginView_title);
            if (title != null) {
                titleTextView.setText(title);
            }
            arr.recycle();  // Do this when done.
        }
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.diver));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.dive_cente));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        changeSocialButtonsState(View.VISIBLE);
                        break;
                    case 1:
                        changeSocialButtonsState(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setPrivacyPolicyText() {
        final SpannableString spannableString = new SpannableString(privacyPolicy.getText());
        privacyPolicy.setHighlightColor(Color.TRANSPARENT);
        spannableString.setSpan(new MyClickableSpan(privacyPolicy.getText().toString()) {
            @Override
            public void onClick(View tv) {
                TermsOfServiceActivity.show(getContext());
            }
        }, 32, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new MyClickableSpan(privacyPolicy.getText().toString()) {
            @Override
            public void onClick(View tv) {
                PrivacyPolicyActivity.show(getContext());
                tv.invalidate();
            }
        }, 53, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        privacyPolicy.setText(spannableString);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up:
                signView.setVisibility(VISIBLE);
                loginView.setVisibility(GONE);
                buttonSubmitData.setText(R.string.sign_up);
                //DDScannerApplication.bus.post(new LoginViaFacebookClickEvent());
                break;
            case R.id.login:
                loginView.setVisibility(GONE);
                signView.setVisibility(VISIBLE);
                buttonSubmitData.setText(R.string.login);
                //DDScannerApplication.bus.post(new LoginViaGoogleClickEvent());
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

    private void changeSocialButtonsState(int visibility) {
        loginWithFacebook.setVisibility(visibility);
        loginWithGoogle.setVisibility(visibility);
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
            ds.setColor(Color.parseColor("#a3a3a3"));
            ds.setUnderlineText(false);
        }
    }
}

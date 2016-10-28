package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.transition.Visibility;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.views.LoginView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private Toolbar toolbar;
    private LinearLayout fbLogin;
    private LinearLayout googleLogin;
    private Button buttonSignUp;
    private MenuItem signMenuItem;
    private boolean isRegister;
    private TextView privacyPolicy;
    private TextView forgotPasswordView;

    private boolean isSignUpScreen = true;

    public static void show(Context context, boolean isRegister) {
        Intent intent = new Intent(context, SignUpActivity.class);
        intent.putExtra("isregister", isRegister);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        isRegister = getIntent().getBooleanExtra("isregister", false);
        findViews();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        fbLogin = (LinearLayout) findViewById(R.id.fb_custom);
        googleLogin = (LinearLayout) findViewById(R.id.custom_google);
        buttonSignUp = (Button) findViewById(R.id.btn_sign_up);
        privacyPolicy = (TextView) findViewById(R.id.privacy_policy);
        forgotPasswordView = (TextView) findViewById(R.id.forgot_password);
        setUi();
    }

    private void setUi() {
        forgotPasswordView.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        changeUiAccordingRegister();
        setupTabLayout();
        setPrivacyPolicyText();
    }

    private void setPrivacyPolicyText() {
        final SpannableString spannableString = new SpannableString(privacyPolicy.getText());
        privacyPolicy.setHighlightColor(Color.TRANSPARENT);
        spannableString.setSpan(new MyClickableSpan(privacyPolicy.getText().toString()) {
            @Override
            public void onClick(View tv) {
                TermsOfServiceActivity.show(SignUpActivity.this);
            }
        }, 32, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new MyClickableSpan(privacyPolicy.getText().toString()) {
            @Override
            public void onClick(View tv) {
                PrivacyPolicyActivity.show(SignUpActivity.this);
                tv.invalidate();
            }
        }, 53, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        privacyPolicy.setText(spannableString);
    }

    private void changeUiAccordingRegister() {
        if (isRegister) {
            getSupportActionBar().setTitle(R.string.sign_up_toolbar);
            buttonSignUp.setText(getString(R.string.sign_up));
            return;
        }
        getSupportActionBar().setTitle(R.string.login_toolbar);
        buttonSignUp.setText(getString(R.string.login));
        return;
    }

    private void changeSocialButtonsState(int visibility) {
        fbLogin.setVisibility(visibility);
        googleLogin.setVisibility(visibility);
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.diver)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.dive_cente)));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forgot_password:
                ForgotPasswordActivity.show(this);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

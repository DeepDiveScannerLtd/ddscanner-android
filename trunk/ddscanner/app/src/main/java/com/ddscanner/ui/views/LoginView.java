package com.ddscanner.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.LoginSignUpViaEmailEvent;
import com.ddscanner.events.LoginViaFacebookClickEvent;
import com.ddscanner.events.LoginViaGoogleClickEvent;
import com.ddscanner.events.SignupLoginButtonClicked;
import com.ddscanner.ui.activities.ForgotPasswordActivity;
import com.ddscanner.ui.activities.PrivacyPolicyActivity;
import com.ddscanner.ui.activities.TermsOfServiceActivity;
import com.ddscanner.utils.Constants;

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
    private TextView forgotPasswordView;
    private EditText email;
    private EditText name;
    private EditText password;
    private boolean isSignUp;
    private String userType;
    private boolean isNameEmpty = true;
    private boolean isPasswordEmpty = true;
    private boolean isEmailEmpty = true;
    private TextWatcher nameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            isNameEmpty = name.getText().length() <= 0;
            changeButtonState();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            isPasswordEmpty = password.getText().length() <= 3;
            changeButtonState();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            isEmailEmpty = !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
            changeButtonState();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

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

        userType = Constants.USER_TYPE_DIVER;

        titleTextView = findViewById(R.id.need_to_login_message);
        privacyPolicy = findViewById(R.id.privacy_policy);
        signUpButton = findViewById(R.id.sign_up);
        loginButton = findViewById(R.id.login);
        signView = findViewById(R.id.sign_up_view);
        loginView = findViewById(R.id.login_view_first);
        tabLayout = findViewById(R.id.tab_layout);
        buttonSubmitData = findViewById(R.id.btn_login_or_sign_up_via_email);
        loginWithFacebook = findViewById(R.id.fb_custom);
        loginWithGoogle = findViewById(R.id.custom_google);
        forgotPasswordView = findViewById(R.id.forgot_password);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        name.addTextChangedListener(nameTextWatcher);
        email.addTextChangedListener(emailTextWatcher);
        password.addTextChangedListener(passwordTextWatcher);
        forgotPasswordView.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        loginWithFacebook.setOnClickListener(this);
        loginWithGoogle.setOnClickListener(this);
        changeButtonState();
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
                        userType = Constants.USER_TYPE_DIVER;
                        break;
                    case 1:
                        changeSocialButtonsState(View.GONE);
                        userType = Constants.USER_TYPE_DIVE_CENTER;
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
                tabLayout.setVisibility(VISIBLE);
                signView.setVisibility(VISIBLE);
                loginView.setVisibility(GONE);
                name.setVisibility(VISIBLE);
                buttonSubmitData.setText(R.string.sign_up);
                DDScannerApplication.bus.post(new SignupLoginButtonClicked(true));
                isSignUp = true;
                isNameEmpty = true;
                break;
            case R.id.login:
                tabLayout.setVisibility(GONE);
                loginView.setVisibility(GONE);
                signView.setVisibility(VISIBLE);
                name.setVisibility(GONE);
                buttonSubmitData.setText(R.string.login);
                DDScannerApplication.bus.post(new SignupLoginButtonClicked(true));
                isSignUp = false;
                isNameEmpty = false;
                break;
            case R.id.privacy_policy:
                PrivacyPolicyActivity.show(getContext());
                break;
            case R.id.forgot_password:
                ForgotPasswordActivity.show(getContext());
                break;
            case R.id.fb_custom:
                DDScannerApplication.bus.post(new LoginViaFacebookClickEvent());
                break;
            case R.id.custom_google:
                DDScannerApplication.bus.post(new LoginViaGoogleClickEvent());
                break;
            case R.id.btn_login_or_sign_up_via_email:
                DDScannerApplication.bus.post(new LoginSignUpViaEmailEvent(email.getText().toString(), password.getText().toString(), userType, isSignUp, name.getText().toString()));
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

    public void changeViewToStart() {
        loginView.setVisibility(VISIBLE);
        signView.setVisibility(GONE);
        userType = Constants.USER_TYPE_DIVER;
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

    private void changeButtonState() {
        if (!isEmailEmpty && !isNameEmpty && !isPasswordEmpty) {
            buttonSubmitData.setTextColor(ContextCompat.getColor(getContext(), R.color.black_text));
            buttonSubmitData.setOnClickListener(this);
        } else {
            buttonSubmitData.setTextColor(ContextCompat.getColor(getContext(), R.color.empty_login_button_text));
            buttonSubmitData.setOnClickListener(null);
        }

    }
    
}

package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Visibility;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ddscanner.R;

public class SignUpActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private Toolbar toolbar;
    private LinearLayout fbLogin;
    private LinearLayout googleLogin;
    private Button buttonSignUp;
    private MenuItem signMenuItem;

    private boolean isSignUpScreen = true;

    public static void show(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        fbLogin = (LinearLayout) findViewById(R.id.fb_custom);
        googleLogin = (LinearLayout) findViewById(R.id.custom_google);
        buttonSignUp = (Button) findViewById(R.id.btn_sign_up);
        setUi();
    }

    private void setUi() {
        tabLayout.addTab(tabLayout.newTab().setText("Diver"));
        tabLayout.addTab(tabLayout.newTab().setText("Dive Center"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
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

    private void changeSocialButtonsState(int visibility) {
        fbLogin.setVisibility(visibility);
        googleLogin.setVisibility(visibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        signMenuItem =  menu.findItem(R.id.login);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                if (isSignUpScreen) {
                    buttonSignUp.setText("Sign in");
                    getSupportActionBar().setTitle("Sign In");
                    signMenuItem.setTitle("Sign up");
                    isSignUpScreen = !isSignUpScreen;
                    return true;
                }
                getSupportActionBar().setTitle("Sign Up");
                buttonSignUp.setText("Sign up");
                signMenuItem.setTitle("Login");
                isSignUpScreen = !isSignUpScreen;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

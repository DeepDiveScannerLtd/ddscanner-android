package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.User;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.squareup.picasso.Picasso;


/**
 * Created by lashket on 11.3.16.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private Toolbar toolbar;
    private ImageView avatar;
    private TextView userName;
    private TextView userLink;
    private Button logout;
    private String social;
    private String id;
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();
        toolbarSettings();
        user = (User) getIntent().getSerializableExtra("USER");
        setUi();
    }

    private void setUi() {
        switch (user.getType()) {
            case "fb":
                userLink.setText("Open on facebook");
                break;
            case "tw":
                userLink.setText("Open on twitter");
                break;
            case "go":
                userLink.setText("Open on Google+");
                break;
        }
        userName.setText(user.getName());
        Picasso.with(this).load(user.getPicture()).resize(100,100).transform(new TransformationRoundImage(2,1)).into(avatar);
        if (user.getName().equals(SharedPreferenceHelper.getUsername()) && user.getType().equals(SharedPreferenceHelper.getSn())) {
            logout.setVisibility(View.VISIBLE);
            logout.setOnClickListener(this);
            userName.setPadding(0,0,0,40);
            userLink.setVisibility(View.GONE);
        }
        userLink.setOnClickListener(this);
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        avatar = (ImageView) findViewById(R.id.avatar);
        userName = (TextView) findViewById(R.id.user_name);
        userLink = (TextView) findViewById(R.id.link);
        logout = (Button) findViewById(R.id.button_logout);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openLink(String userName, String socialNetwork) {
        switch (socialNetwork) {
            case "tw":
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("twitter://user?screen_name=" + userName));
                    startActivity(intent);

                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/#!/" + userName)));
                }
                break;
            case "go":
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://plus.google.com/" + userName));
                    intent.setPackage("com.google.android.apps.plus"); // don't open the browser, make sure it opens in Google+ app
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + userName)));
                }
                break;
            case "fb":
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + userName));
                    startActivity(intent1);

                }catch(Exception e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + userName)));
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link:
                if (!user.getType().equals("fb")) {
                    openLink(user.getId(), user.getType());
                } else {
                    openLink(user.getLink(), user.getType());
                    Log.i(TAG, user.getLink());
                }
                break;
            case R.id.button_logout:
                SharedPreferenceHelper.logout();
                finish();
                break;
        }
    }

    public static void show(Context context, User user) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("USER", user);
        context.startActivity(intent);
    }

}

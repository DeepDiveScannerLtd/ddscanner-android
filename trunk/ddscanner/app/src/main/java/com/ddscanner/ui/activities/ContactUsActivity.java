package com.ddscanner.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.ContactUsEntity;
import com.ddscanner.events.SocialLinkOpenEvent;
import com.ddscanner.ui.adapters.SocialListAdapter;
import com.ddscanner.utils.Constants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 6.8.16.
 */
public class ContactUsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<ContactUsEntity> contactUsEntities = new ArrayList<>();
    private String FACEBOOK_URL;
    private String FACEBOOK_PAGE_ID;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.contact_us);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        setList();
    }

    public String getFacebookPageURL(Context context) {
        FACEBOOK_PAGE_ID = "DDScanner";
        FACEBOOK_URL = Constants.PROFILE_DIALOG_FACEBOOK_URL + "DDScanner";
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                return Constants.PROFILE_DIALOG_FACEBOOK_OLD_URI + FACEBOOK_URL;
            } else {
                return Constants.PROFILE_DIALOG_FACEBOOK_NEW_URI + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL;
        }
    }

    private void openFacebookApp() {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = getFacebookPageURL(this);
        facebookIntent.setData(Uri.parse(facebookUrl));
        startActivity(facebookIntent);
    }

    private void openTwitterApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Constants.PROFILE_DIALOG_TWITTER_URI + "DDScanner"));
            startActivity(intent);

        }catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Constants.PROFILE_DIALOG_TWITTER_URL + "DDScanner")));
        }
    }

    private void openEmailIntent() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@ddscanner.com"});
        startActivity(intent);
    }

    private void openInstagramApp() {
        Uri uri = Uri.parse("http://instagram.com/_u/ddscanner");
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/ddscanner")));
        }
    }

    private void openYoutubeApp() {
        Intent intent=null;
        try {
            intent =new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/channel/UCvQ5C8nj4Nntx1-jk3FHU-w"));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.youtube.com/channel/UCvQ5C8nj4Nntx1-jk3FHU-w"));
            startActivity(intent);
        }
    }

    private void openPinterestApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("pinterest://www.pinterest.com/ddscanner")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pinterest.com/ddscanner")));
        }
    }

    private void openTumblrApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tumblr://www.ddscanner.tumblr.com")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ddscanner.tumblr.com")));
        }
    }

    @Subscribe
    public void openSocialApp(SocialLinkOpenEvent event) {
        switch (event.getType()) {
            case "email":
                openEmailIntent();
                break;
            case "fb":
                openFacebookApp();
                break;
            case "inst":
                openInstagramApp();
                break;
            case "tw":
                openTwitterApp();
                break;
            case "yt":
                openYoutubeApp();
                break;
            case "tmblr":
                openTumblrApp();
                break;
            case "pint":
                openPinterestApp();
                break;
        }
    }

    @Override
    protected void onStart() {
        DDScannerApplication.bus.register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        DDScannerApplication.bus.unregister(this);
        super.onStop();
    }

    private void setList() {
        contactUsEntities.add(new ContactUsEntity("Email", "info@ddscanner.com", R.drawable.ic_social_email, "email"));
        contactUsEntities.add(new ContactUsEntity("Facebook", null, R.drawable.ic_social_fb, "fb"));
        contactUsEntities.add(new ContactUsEntity("Instagram", null, R.drawable.ic_social_insta, "inst"));
        contactUsEntities.add(new ContactUsEntity("Twitter", null, R.drawable.ic_social_tw, "tw"));
        contactUsEntities.add(new ContactUsEntity("Youtube", null, R.drawable.ic_social_yt, "yt"));
        contactUsEntities.add(new ContactUsEntity("Tumblr", null, R.drawable.ic_social_tumblr, "tmblr"));
        contactUsEntities.add(new ContactUsEntity("Pinterest", null, R.drawable.ic_social_pint, "pint"));

        recyclerView.setAdapter(new SocialListAdapter((ArrayList<ContactUsEntity>) contactUsEntities, this));
    }
}

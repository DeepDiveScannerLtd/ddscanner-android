package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.AboutScreenItem;
import com.ddscanner.ui.adapters.AboutListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends BaseAppCompatActivity {

    private RecyclerView recyclerView;
    private List<AboutScreenItem> aboutScreenItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        findViews();
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recyclerView);
        setupToolbar(R.string.about_dds, R.id.toolbar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        createListItems();
    }

    private void createListItems() {
        aboutScreenItems.add(new AboutScreenItem(R.drawable.ic_list_guide, getString(R.string.guide_to_dds), new Intent(this, GuideActivity.class)));
        aboutScreenItems.add(new AboutScreenItem(R.drawable.ic_list_terms, getString(R.string.terms_of_use), new Intent(this, TermsOfServiceActivity.class)));
        aboutScreenItems.add(new AboutScreenItem(R.drawable.ic_list_policy, getString(R.string.privacy_policy), new Intent(this, PrivacyPolicyActivity.class)));
        aboutScreenItems.add(new AboutScreenItem(R.drawable.ic_list_about, getString(R.string.about_dds), new Intent(this, AboutDDSActivity.class)));
        aboutScreenItems.add(new AboutScreenItem(R.drawable.ic_list_contact, getString(R.string.contact_us), new Intent(this, ContactUsActivity.class)));
        aboutScreenItems.add(new AboutScreenItem(R.drawable.ic_list_license, getString(R.string.lienes), new Intent(this, LicensesActivity.class)));

        recyclerView.setAdapter(new AboutListAdapter((ArrayList<AboutScreenItem>) aboutScreenItems, this));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void show(Context context) {
        EventsTracker.trackAboutDDsMenuView();
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

}

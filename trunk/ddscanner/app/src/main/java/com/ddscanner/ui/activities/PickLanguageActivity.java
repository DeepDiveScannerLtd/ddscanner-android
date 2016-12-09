package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.entities.Language;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.ui.adapters.LanguageSearchAdapter;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class PickLanguageActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Language> languages;
    private Menu menu;
    private LanguageSearchAdapter languageSearchAdapter = new LanguageSearchAdapter(this, languages);

    public static void showForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PickLanguageActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_language);
        findViews();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.new_language);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sealife, menu);
        this.menu = menu;
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private List<SealifeShort> filter(List<SealifeShort> models, String query) {
        query = query.toLowerCase();

        final List<SealifeShort> filteredModelList = new ArrayList<>();
        for (SealifeShort model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
    
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}

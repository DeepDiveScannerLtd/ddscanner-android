package com.ddscanner.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ddscanner.R;
import com.ddscanner.entities.GuideItem;
import com.ddscanner.ui.adapters.GuideListAdapter;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseAppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<GuideItem> guideItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        findViews();
    }

    private void findViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.guide_to_dds);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        createList();
    }

    private void createList() {
        guideItems.add(new GuideItem(getString(R.string.guide_question_1), getString(R.string.guide_description_1)));
        guideItems.add(new GuideItem(getString(R.string.guide_question_2), getString(R.string.guide_description_2)));
        guideItems.add(new GuideItem(getString(R.string.guide_question_3), getString(R.string.guide_description_3)));
        guideItems.add(new GuideItem(getString(R.string.guide_question_4), getString(R.string.guide_description_4)));
        guideItems.add(new GuideItem(getString(R.string.guide_question_5), getString(R.string.guide_description_5)));
        guideItems.add(new GuideItem(getString(R.string.guide_question_6), getString(R.string.guide_description_6)));

        recyclerView.setAdapter(new GuideListAdapter((ArrayList<GuideItem>) guideItems, this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

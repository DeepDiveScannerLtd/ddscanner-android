package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.GuideItem;

/**
 * Created by lashket on 5.8.16.
 */
public class GuideDescriptionActivity extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private Toolbar toolbar;
    private GuideItem item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_description);

        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        item = (GuideItem) getIntent().getSerializableExtra("ITEM");

        title.setText(item.getTitle());
        description.setText(item.getDescription());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.guide_to_dds);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void show(Context context, GuideItem guideItem) {
        Intent intent = new Intent(context, GuideDescriptionActivity.class);
        intent.putExtra("ITEM", guideItem);
        context.startActivity(intent);
    }
}

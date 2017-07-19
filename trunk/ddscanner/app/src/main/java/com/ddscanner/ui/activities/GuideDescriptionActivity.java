package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.GuideItem;
import com.ddscanner.utils.Constants;

public class GuideDescriptionActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private TextView title;
    private TextView description;
    private TextView thnaks;
    private Toolbar toolbar;
    private GuideItem item;
    private ImageView buttonYes;
    private ImageView buttonNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_description);
        EventsTracker.trackGuideToDDSItemView();
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        buttonNo = (ImageView) findViewById(R.id.no);
        buttonYes = (ImageView) findViewById(R.id.yes);
        thnaks = (TextView) findViewById(R.id.titleUseful);

        item = (GuideItem) getIntent().getSerializableExtra(Constants.GUIDE_DESCRIPTION_ACTIVITY_INTENT_ITEM);

        title.setText(item.getTitle());
        description.setText(item.getDescription());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.guide_to_dds);
        buttonNo.setOnClickListener(this);
        buttonYes.setOnClickListener(this);
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
        intent.putExtra(Constants.GUIDE_DESCRIPTION_ACTIVITY_INTENT_ITEM, guideItem);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes:
                buttonYes.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_smile_active));
                thnaks.setText(R.string.thank_you_title);
                EventsTracker.trackGuideUseful(item.getTitle());
                buttonYes.setOnClickListener(null);
                buttonNo.setOnClickListener(null);
                break;
            case R.id.no:
                buttonNo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_sad_active));
                thnaks.setText(R.string.thank_you_title);
                EventsTracker.trackGuideNotUseful(item.getTitle());
                buttonYes.setOnClickListener(null);
                buttonNo.setOnClickListener(null);
                break;
        }
    }
}

package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import com.google.android.gms.maps.model.LatLng;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.ui.adapters.DiveCentersAdapter;
import travel.ilave.deepdivescanner.ui.adapters.DiveCentersPagerAdapter;

/**
 * Created by lashket on 29.1.16.
 */
public class DiveCentersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DiveCentersPagerAdapter diveCentersPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_centers);
        tabLayout = (TabLayout) findViewById(R.id.place_sliding_tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.place_view_pager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dive centers");
    }

    private void populateDiveCentesPager() {
        diveCentersPagerAdapter = new DiveCentersPagerAdapter(this, getFragmentManager());
        viewPager.setAdapter(diveCentersPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, DiveCentersActivity.class);
        context.startActivity(intent);
    }


}

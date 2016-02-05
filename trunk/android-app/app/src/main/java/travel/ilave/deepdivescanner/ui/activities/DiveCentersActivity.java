package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Divecenters;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.adapters.DiveCentersPagerAdapter;

/**
 * Created by lashket on 29.1.16.
 */
public class DiveCentersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Divecenters divecenters = new Divecenters();
    private DiveCentersPagerAdapter diveCentersPagerAdapter;
    private LatLng latLng;
    private Map<String, String> map = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_centers);
        findViews();
        latLng = getIntent().getParcelableExtra("LATLNG");
        requestDiveCenters(latLng);
    }

    private void findViews() {
        tabLayout = (TabLayout) findViewById(R.id.place_sliding_tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.place_view_pager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dive centers");
    }

    private void populateDiveCentesPager(Divecenters divecenters) {
        diveCentersPagerAdapter = new DiveCentersPagerAdapter(this, getFragmentManager(), divecenters, latLng);
        viewPager.setAdapter(diveCentersPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static void show(Context context, LatLng latLng) {
        Intent intent = new Intent(context, DiveCentersActivity.class);
        intent.putExtra("LATLNG", latLng);
        context.startActivity(intent);
    }

    private void requestDiveCenters(LatLng latLng) {
        map.put("lat", String.valueOf(latLng.latitude));
        map.put("lng", String.valueOf(latLng.longitude));
        RestClient.getServiceInstance().getDiveCenters(map, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                divecenters = new Gson().fromJson(responseString, Divecenters.class);
                populateDiveCentesPager(divecenters);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


}

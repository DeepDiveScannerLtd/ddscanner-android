package travel.ilave.deepdivescanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


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
import travel.ilave.deepdivescanner.ui.adapters.PlacesPagerAdapter;

/**
 * Created by lashket on 29.1.16.
 */
public class DiveCentersActivity extends AppCompatActivity {
    private static final String TAG = "DiveCentersActivity";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Divecenters divecenters = new Divecenters();
    private DiveCentersPagerAdapter diveCentersPagerAdapter;
    private LatLng latLng;
    private Map<String, String> map = new HashMap<String, String>();
    private ProgressDialog progressDialog;
    private String dsName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_centers);
        findViews();
        latLng = getIntent().getParcelableExtra("LATLNG");
        dsName = getIntent().getStringExtra("NAME");
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
        diveCentersPagerAdapter = new DiveCentersPagerAdapter(this, getFragmentManager(), divecenters, latLng, dsName);
        viewPager.setAdapter(diveCentersPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static void show(Context context, LatLng latLng, String name) {
        Intent intent = new Intent(context, DiveCentersActivity.class);
        intent.putExtra("LATLNG", latLng);
        intent.putExtra("NAME", name);
        context.startActivity(intent);
    }

    private void requestDiveCenters(LatLng latLng) {
        progressDialog = new ProgressDialog(DiveCentersActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.pleaseWait));
        progressDialog.show();
        map.put("latLeft", String.valueOf(latLng.latitude - 2.0));
        map.put("lngLeft", String.valueOf(latLng.longitude - 2.0));
        map.put("lngRight", String.valueOf(latLng.longitude + 2.0));
        map.put("latRight", String.valueOf(latLng.latitude + 2.0));
        RestClient.getServiceInstance().getDiveCenters(map, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                System.out.println(responseString);
                divecenters = new Gson().fromJson(responseString, Divecenters.class);
                populateDiveCentesPager(divecenters);
                progressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                    Toast.makeText(DiveCentersActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                    Toast.makeText(DiveCentersActivity.this, "Server is not responsible, please try later", Toast.LENGTH_LONG).show();
                }
                String json =  new String(((TypedByteArray)error.getResponse().getBody()).getBytes());
                Log.i(TAG, json);
            }
        });
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

}

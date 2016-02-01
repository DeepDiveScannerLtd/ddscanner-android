package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Filters;

/**
 * Created by lashket on 22.1.16.
 */
public class FilterActivity extends AppCompatActivity {

    private RadioGroup rgLevel;
    private RadioGroup rgCurrents;
    private LatLng latLng;
    private RadioGroup rgVisibility;
    private Toolbar toolbar;
    private Filters filters = new Filters();
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_filter);
        latLng = getIntent().getParcelableExtra("LATLNG");
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(getCurrents(rgCurrents.getCheckedRadioButtonId()));
                filters.setCurrents(String.valueOf(getCurrents(rgCurrents.getCheckedRadioButtonId())));
                filters.setVisibility(getVisibility(rgVisibility.getCheckedRadioButtonId()));
                filters.setLevel("");
                Intent intent = new Intent(FilterActivity.this, CityActivity.class);
                intent.putExtra("LATLNG", latLng);
                intent.putExtra("FILTERS", filters);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rgCurrents = (RadioGroup) findViewById(R.id.rg_currents);
        rgLevel = (RadioGroup) findViewById(R.id.rg_level);
        rgVisibility = (RadioGroup) findViewById(R.id.rg_visibility);
        button = (Button) findViewById(R.id.apply_filter);
    }

    public static void show(Context context, LatLng latLng) {
        Intent intent = new Intent(context, FilterActivity.class);
        intent.putExtra("LATLNG", latLng);
        context.startActivity(intent);
    }

    private String getVisibility(int checkedId) {
        String visibility = "";
        switch (checkedId) {
            case -1:
                break;
            case R.id.vis_excelent:
                visibility = "excellent";
                break;
            case R.id.vis_good:
                visibility = "good";
                break;
            case R.id.vis_bad:
                visibility = "bad";
                break;
            default:
                break;
        }
        return visibility;
    }

    private String getCurrents(int checkedId) {
        String currents = "";
        switch (checkedId) {
            case -1:
                currents = "";
                break;
            case R.id.cur_moderate:
                currents = "moderate";
                break;
            case R.id.cur_strong:
                currents = "strong";
                break;
            case R.id.cur_low:
                currents = "low";
                break;
            default:
                break;
        }
        return currents;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_filters:
                resetChecking();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void resetChecking() {
        RadioButton radioButton = (RadioButton) findViewById(rgCurrents.getCheckedRadioButtonId());
        if (radioButton != null) {
            radioButton.setChecked(false);
        }
        radioButton = (RadioButton) findViewById(rgLevel.getCheckedRadioButtonId());
        if (radioButton != null) {
            radioButton.setChecked(false);
        }
        radioButton = (RadioButton) findViewById(rgVisibility.getCheckedRadioButtonId());
        if (radioButton != null) {
            radioButton.setChecked(false);
        }
    }

}

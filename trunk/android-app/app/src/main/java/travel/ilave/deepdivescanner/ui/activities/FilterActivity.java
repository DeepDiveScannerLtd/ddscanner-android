package travel.ilave.deepdivescanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Filters;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.adapters.PlacesPagerAdapter;

/**
 * Created by lashket on 22.1.16.
 */
public class FilterActivity extends AppCompatActivity implements View.OnClickListener{

    private RadioGroup rgLevel;
    private RadioGroup rgCurrents;
    private RadioButton radioButton;
    private LatLng latLng;
    private RadioGroup rgVisibility;
    private Toolbar toolbar;
    private Filters filters = new Filters();
    private Button button;
    private List<String> currents;
    private List<String> level;
    private List<String> visibility;
    private ProgressDialog progressDialog;
    private List<String> values;
    private HashMap<String, String> filtersSend = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_filter);
        latLng = getIntent().getParcelableExtra("LATLNG");
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.pleaseWait));
        progressDialog.show();
        request();
        progressDialog.dismiss();
        button.setOnClickListener(this);


    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rgCurrents = (RadioGroup) findViewById(R.id.rg_currents);
        rgLevel = (RadioGroup) findViewById(R.id.rg_level);
        rgVisibility = (RadioGroup) findViewById(R.id.rg_visibility);
        button = (Button) findViewById(R.id.apply_filter);
    }

    private void request() {

        RestClient.getServiceInstance().getFilters(new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                System.out.println(responseString);
                filters = new Gson().fromJson(responseString, Filters.class);
        //        setCurrents(filters.getCurrents());
             //   setVisibility(filters.getVisibility());
               // setLevel(filters.getLevel());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void setCurrents(List<String> currents) {
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,RadioGroup.LayoutParams.WRAP_CONTENT);
        for (int i=0; i<currents.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i);
            radioButton.setText(firstSymbolUp(currents.get(i)));
            rgCurrents.addView(radioButton, 0 , layoutParams);

        }
    }

    private void setVisibility(List<String> visibility) {
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,RadioGroup.LayoutParams.WRAP_CONTENT);
        for (int i=0; i < visibility.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i + 10);
            radioButton.setText(firstSymbolUp(visibility.get(i)));
            rgVisibility.addView(radioButton, 0 , layoutParams);
        }
    }

    private void setLevel(List <String> level) {
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,RadioGroup.LayoutParams.WRAP_CONTENT);
        for (int i=0; i<level.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i + 20);
            radioButton.setText(firstSymbolUp(level.get(i)));
            rgLevel.addView(radioButton, 0 , layoutParams);
        }
    }

    private String firstSymbolUp(String string) {
        if (string == null || string.isEmpty()) return "";
        return string.substring(0,1).toUpperCase() + string.substring(1);
    }

    @Override
    public void onClick(View v) {
  /*      if (rgCurrents.getCheckedRadioButtonId() != -1) {
            filtersSend.put("currents",((RadioButton) findViewById(rgCurrents.getCheckedRadioButtonId())).getText().toString());
        } else {
            filtersSend.put("currents", "");
        }
        if (rgLevel.getCheckedRadioButtonId() != -1) {
            filtersSend.put("level",((RadioButton) findViewById(rgLevel.getCheckedRadioButtonId())).getText().toString());
        } else {
            filtersSend.put("level", "");
        }
        if (rgVisibility.getCheckedRadioButtonId() != -1) {
            filtersSend.put("visibility",((RadioButton) findViewById(rgVisibility.getCheckedRadioButtonId())).getText().toString());
        } else {
            filtersSend.put("visibility", "");
        }

        CityActivity.showWIthFIlters(this, filtersSend, PlacesPagerAdapter.getLastLatlng());*/
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, FilterActivity.class);
        context.startActivity(intent);
    }


}

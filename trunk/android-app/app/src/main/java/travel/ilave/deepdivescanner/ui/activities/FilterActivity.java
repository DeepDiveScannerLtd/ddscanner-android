package travel.ilave.deepdivescanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.FiltersResponseEntity;
import travel.ilave.deepdivescanner.entities.request.DiveSpotsRequestMap;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.utils.SharedPreferenceHelper;

/**
 * Created by lashket on 22.1.16.
 */
public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FilterActivity.class.getSimpleName();

    private RadioGroup rgLevel;
    private RadioGroup rgCurrents;
    private RadioGroup rgVisibility;
    private RadioGroup rgObject;
    private Toolbar toolbar;
    private FiltersResponseEntity filters = new FiltersResponseEntity();
    private Button button;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_filter);
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
        rgObject = (RadioGroup) findViewById(R.id.rg_object);
        button = (Button) findViewById(R.id.apply_filter);
    }


    private void setFilerGroup(RadioGroup radioGroup, Map<String, String> currents) {
        ImageView divider = new ImageView(this);
        divider.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.divider));
        divider.setPadding(0,16,0,0);
        int i = 0;
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        for (Map.Entry<String, String> entry : currents.entrySet()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setButtonDrawable(R.drawable.bg_radio_button);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            layoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            layoutParams.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            radioButton.setTag(entry.getKey());
            radioButton.setText(entry.getValue());
            radioButton.setTypeface(Typeface.SANS_SERIF);
            radioGroup.addView(radioButton, 0, layoutParams);
           /* if (i < currents.size() - 1) {
                radioGroup.addView(divider, 0, layoutParams);
            }*/
        }
    }


    @Override
    public void onClick(View v) {
        Intent data = new Intent();
        int selectedRadioButtonId = rgCurrents.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            data.putExtra(DiveSpotsRequestMap.KEY_CURRENTS, findViewById(selectedRadioButtonId).getTag().toString());
        }
        selectedRadioButtonId = rgLevel.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            data.putExtra(DiveSpotsRequestMap.KEY_LEVEL, findViewById(selectedRadioButtonId).getTag().toString());
        }
        selectedRadioButtonId = rgVisibility.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            data.putExtra(DiveSpotsRequestMap.KEY_VISIBILITY, findViewById(selectedRadioButtonId).getTag().toString());
        }
        selectedRadioButtonId = rgObject.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            data.putExtra(DiveSpotsRequestMap.KEY_OBJECT, findViewById(selectedRadioButtonId).getTag().toString());
        }
//        selectedRadioButtonId = rgCurrents.getCheckedRadioButtonId();
//        if (selectedRadioButtonId != -1) {
//            data.putExtra(DiveSpotsRequestMap.KEY_CURRENTS, findViewById(selectedRadioButtonId).getTag().toString());
//        }
//        selectedRadioButtonId = rgCurrents.getCheckedRadioButtonId();
//        if (selectedRadioButtonId != -1) {
//            data.putExtra(DiveSpotsRequestMap.KEY_CURRENTS, findViewById(selectedRadioButtonId).getTag().toString());
//        }

        setResult(RESULT_OK, data);
        finish();
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, FilterActivity.class);
        context.startActivity(intent);
    }

    private void request() {

        RestClient.getServiceInstance().getFilters(new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());

                filters = new FiltersResponseEntity();

                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(responseString).getAsJsonObject();
                JsonObject currentsJsonObject = jsonObject.getAsJsonObject("currents");
                for (Map.Entry<String, JsonElement> elementEntry : currentsJsonObject.entrySet()) {
                    filters.getCurrents().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                }
                JsonObject levelJsonObject = jsonObject.getAsJsonObject("level");
                for (Map.Entry<String, JsonElement> elementEntry : levelJsonObject.entrySet()) {
                    filters.getLevel().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                }
                JsonObject objectJsonObject = jsonObject.getAsJsonObject("object");
                for (Map.Entry<String, JsonElement> elementEntry : objectJsonObject.entrySet()) {
                    filters.getObject().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                }
                JsonObject visibilityJsonObject = jsonObject.getAsJsonObject("visibility");
                for (Map.Entry<String, JsonElement> elementEntry : visibilityJsonObject.entrySet()) {
                    filters.getVisibility().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                }
                Gson gson = new Gson();
                filters.setRating(gson.fromJson(jsonObject.get("rating").getAsJsonArray(), int[].class));

                Log.i(TAG, responseString);

                setFilerGroup(rgCurrents, filters.getCurrents());
                setFilerGroup(rgVisibility, filters.getVisibility());
                setFilerGroup(rgLevel, filters.getLevel());
                setFilerGroup(rgObject, filters.getObject());
            }

            @Override
            public void failure(RetrofitError error) {

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

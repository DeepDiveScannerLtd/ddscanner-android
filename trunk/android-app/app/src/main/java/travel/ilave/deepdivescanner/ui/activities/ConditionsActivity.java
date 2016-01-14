package travel.ilave.deepdivescanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.opengl.ETC1;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.SocketTimeoutException;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.ConditionLanguage;
import travel.ilave.deepdivescanner.entities.Offer;
import travel.ilave.deepdivescanner.entities.OfferCondition;
import travel.ilave.deepdivescanner.entities.PickUp;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.adapters.PlaceImagesPagerAdapter;
import travel.ilave.deepdivescanner.utils.LogUtils;

public class ConditionsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String OFFER = "OFFER";
    public static final String DATE = "DATE";

    private Toolbar toolbar;
    private TextView productName;
    private TextView duration;
    private TextView depth_value;
    private TextView type;
    private TextView type_desc;
    private TextView plus;
    private TextView adultCountValue;
    private TextView minus;
    private TextView total_value;
    private EditText note;
    private Button continue_to_billing;
    private Spinner langSpinner;
    private Spinner placesSpinner;
    private ProgressDialog progressDialog;

    private Offer offer;
    private OfferCondition offerCondition;
    private PlaceImagesPagerAdapter placeImagesPagerAdapter;

    private int adultsCount = 1;
    private String date;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.conditionsActivityTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productName = (TextView) findViewById(R.id.name);
        duration = (TextView) findViewById(R.id.duration);
       // depth_value = (TextView) findViewById(R.id.depth_value);
        type = (TextView) findViewById(R.id.type);
        type_desc = (TextView) findViewById(R.id.type_desc);
        plus = (TextView) findViewById(R.id.plus);
        plus.setOnClickListener(this);
        adultCountValue = (TextView) findViewById(R.id.value);
        minus = (TextView) findViewById(R.id.minus);
        minus.setOnClickListener(this);
        total_value = (TextView) findViewById(R.id.total_value);
        continue_to_billing = (Button) findViewById(R.id.continue_to_billing);
        continue_to_billing.setOnClickListener(this);
        note = (EditText) findViewById(R.id.editText);
        langSpinner = (Spinner) findViewById(R.id.language_spinner);
        placesSpinner = (Spinner) findViewById(R.id.address_for_pickup);

        offer = (Offer) getIntent().getSerializableExtra(OFFER);
        date = getIntent().getStringExtra(DATE);
        name = getIntent().getStringExtra(VoucherActivity.PLACE);
        requestOfferConditionss(offer.getId());
    }

    private void requestOfferConditionss(String productId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.pleaseWait));
        progressDialog.show();
        RestClient.getServiceInstance().getOfferConditions(productId, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                // TODO Handle result handling when activity stopped
                responseString = responseString.replaceAll("\\n/", "/");
                offerCondition = new Gson().fromJson(responseString, OfferCondition.class);
                populateOfferConditions();
                progressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                LogUtils.i("failure Message is " + error.getMessage());
                LogUtils.i("failure body is " + error.getBody());
                if (error.getCause() instanceof SocketTimeoutException) {
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        Toast.makeText(ConditionsActivity.this, R.string.errorConnection, Toast.LENGTH_LONG);
                    } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                        Toast.makeText(ConditionsActivity.this, R.string.serverNotResp, Toast.LENGTH_LONG);
                    }
                }
                // TODO Handle result handling when activity stopped
                // TODO Handle errors
            }
        });
    }

    private void populateOfferConditions() {
        productName.setText(offerCondition.getName());
        duration.setText(offerCondition.getDuration());
        type.setText(offerCondition.getPrices().get(0).getType());
        type_desc.setText(offerCondition.getPrices().get(0).getDescription());
        populateLangsSpinner(offerCondition.getLanguages());
        populatePickupsSpinner(offerCondition.getPickups());
        total_value.setText(String.valueOf(adultsCount * Float.valueOf(offerCondition.getPrices().get(0).getPrice())));
        updateTotalPrice();
    }

    private void populateLangsSpinner(List<ConditionLanguage> langs) {
        ArrayAdapter<ConditionLanguage> adapter = new ArrayAdapter<ConditionLanguage>(this, R.layout.item_spinner, android.R.id.text1, langs);
        langSpinner.setAdapter(adapter);
    }

    private void populatePickupsSpinner(List<PickUp> pickups) {
        ArrayAdapter<PickUp> adapter = new ArrayAdapter<PickUp>(this, R.layout.item_spinner, android.R.id.text1, pickups);
        placesSpinner.setAdapter(adapter);
    }

    private void updateTotalPrice() {
        total_value.setText(String.valueOf(adultsCount * Float.valueOf(offerCondition.getPrices().get(0).getPrice())) + "$");
    }

    public static void show(Context context, Offer offer, String date, String name) {
        Intent intent = new Intent(context, ConditionsActivity.class);
        intent.putExtra(OFFER, offer);
        intent.putExtra(DATE, date);
        intent.putExtra(VoucherActivity.PLACE, name);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.plus:
                adultsCount++;
                adultCountValue.setText(String.valueOf(adultsCount));
                updateTotalPrice();
                break;
            case R.id.minus:
                if (adultsCount == 1) {
                    return;
                }
                adultsCount--;
                adultCountValue.setText(String.valueOf(adultsCount));
                updateTotalPrice();
                break;
            case R.id.continue_to_billing:
                BillingActivity.show(this, offerCondition.getId(), date, note.getText().toString(), offerCondition.getLanguages().get(0).getId(), offerCondition.getPickups().get(0).getId(), offerCondition.getPrices().get(0).getId(), String.valueOf(adultsCount), name);
                break;
        }
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

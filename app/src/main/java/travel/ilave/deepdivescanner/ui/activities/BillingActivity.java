package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.google.gson.Gson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedString;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Booking;
import travel.ilave.deepdivescanner.entities.request.BookingRequest;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.utils.LogUtils;

/**
 * Created by Admin on 28.11.2015.
 */
public class BillingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String KEY_OPTION_ID = "option_id_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_NOTE = "note_key";
    private static final String KEY_LANGUAGE_ID = "language_id_key";
    private static final String KEY_PICKUP_ID = "pickup_id_key";
    private static final String KEY_PRICE_ID = "price_id_key";
    private static final String KEY_COUNT = "count_key";
    private String optionId;
    private String date;
    private String note;
    private String languageId;
    private String pickupId;
    private String priceId;
    private String count;
    String place;

    private Toolbar toolbar;
    private EditText nameOnCard;
    private EditText cardNumber;
    private EditText cardMonth;
    private EditText cardYear;
    private EditText cvv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.billingActivityTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViews();

        Bundle bundle = getIntent().getBundleExtra("extra");
        optionId = bundle.getString(KEY_OPTION_ID);
        date = bundle.getString(KEY_DATE);
        note = bundle.getString(KEY_NOTE);
        languageId = bundle.getString(KEY_LANGUAGE_ID);
        pickupId = bundle.getString(KEY_PICKUP_ID);
        priceId = bundle.getString(KEY_PRICE_ID);
        count = bundle.getString(KEY_COUNT);
        place = bundle.getString(VoucherActivity.PLACE);
    }

    private void findViews() {
        nameOnCard = (EditText) findViewById(R.id.edit_name);
        cardNumber = (EditText) findViewById(R.id.edit_card_number);
        cardMonth = (EditText) findViewById(R.id.month);
        cardYear = (EditText) findViewById(R.id.year);
        cvv = (EditText) findViewById(R.id.edit_ccv);

        findViewById(R.id.button_pay).setOnClickListener(this);
    }

    private void makeRequest() {
        BookingRequest br = new BookingRequest();
        br.setCount(new String[] { count});
        br.setDate(date);
        br.setLanguageId(languageId);
        br.setNote(note);
        br.setOptionId(optionId);
        br.setPickupId(pickupId);
        br.setPriceId(new String[] {priceId});
        RestClient.getServiceInstance().booking(br,
                new Callback<Response>() {
                    @Override
                    public void success(Response s, Response response) {
                        String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                        LogUtils.i("response code is " + s.getStatus());
                        LogUtils.i("response body is " + responseString);
                        Booking bookingEntity = new Gson().fromJson(responseString, Booking.class);
                        VoucherActivity.show(BillingActivity.this, bookingEntity.getCountries().get(0).id, bookingEntity.getBookingId(), place);
                        // TODO Handle result handling when activity stopped
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        LogUtils.i("failure Message is " + error.getMessage());
                        LogUtils.i("failure body is " + error.getBody());
                        // TODO Handle result handling when activity stopped
                        // TODO Handle errors
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_pay:
                makeRequest();
                break;
        }
    }

    public static void show(Context context,
                            String optionId,
                            String date,
                            String note,
                            String languageId,
                            String pickupId,
                            String priceId,
                            String count,
                            String place) {
        Intent intent = new Intent(context, BillingActivity.class);
        Bundle bundle = new Bundle(7);
        bundle.putString(KEY_OPTION_ID, optionId);
        bundle.putString(KEY_DATE, date);
        bundle.putString(KEY_NOTE, note);
        bundle.putString(KEY_LANGUAGE_ID, languageId);
        bundle.putString(KEY_PICKUP_ID, pickupId);
        bundle.putString(KEY_PRICE_ID, priceId);
        bundle.putString(KEY_COUNT, count);
        bundle.putString(VoucherActivity.PLACE, place);
        intent.putExtra("extra", bundle);
        context.startActivity(intent);
    }
}

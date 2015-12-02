package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.ProductsWrapper;
import travel.ilave.deepdivescanner.entities.Traveller;
import travel.ilave.deepdivescanner.entities.request.TravelerRequest;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.utils.LogUtils;
import travel.ilave.deepdivescanner.utils.SharedPreferenceHelper;

/**
 * Created by Admin on 28.11.2015.
 */
public class VoucherActivity extends AppCompatActivity {

    public static final String PLACE = "PLACE";
    public static final String COUNTRYID = "COUNTRYID";
    public static final String BOOKINGID = "BOOKINGID";

    TextView mOptionNameText;
    TextView mDescriptionText;
    TextView mNameText;
    TextView mDateText;
    TextView mDurationText;
    TextView mNoteText;
    private Toolbar toolbar;
    LinearLayout note_wrapper;

    Traveller traveller;
    String countryId;
    String bookingId;
    String place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voucher);

        findViews();
        countryId = getIntent().getStringExtra(COUNTRYID);
        bookingId = getIntent().getStringExtra(BOOKINGID);
        place = getIntent().getStringExtra(PLACE);

        requestTraveler();
    }

    private void findViews() {
        mDescriptionText = (TextView) findViewById(R.id.text_description);
        mNameText = (TextView) findViewById(R.id.text_name);
        mDateText = (TextView) findViewById(R.id.text_date);
        mDurationText = (TextView) findViewById(R.id.text_duration);
        mNoteText = (TextView) findViewById(R.id.note);
        note_wrapper = (LinearLayout) findViewById(R.id.note_wrapper);
        mOptionNameText = (TextView) findViewById(R.id.text_option_name);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("VOUCHER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_home_white);

    }

    private void requestTraveler() {
        TravelerRequest tr = new TravelerRequest();
        tr.setBookingId(bookingId);
        tr.setCountryId(countryId);
//        tr.setGoogleId(SharedPreferenceHelper.getGcmId());
        tr.setGoogleId("sadf");
        RestClient.getServiceInstance().traveler(tr, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                // TODO Handle result handling when activity stopped
                traveller = new Gson().fromJson(responseString, Traveller.class);
                populateVoucher();
            }

            @Override
            public void failure(RetrofitError error) {
                LogUtils.i("failure Message is " + error.getMessage());
                LogUtils.i("failure body is " + error.getBody());
                if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                    Toast.makeText(VoucherActivity.this, R.string.errorConnection, Toast.LENGTH_LONG);
                } else if(error.getKind().equals(RetrofitError.Kind.HTTP)) {
                    Toast.makeText(VoucherActivity.this, R.string.serverNotResp, Toast.LENGTH_LONG);
                }
                // TODO Handle result handling when activity stopped
                // TODO Handle errors
            }
        });
    }

    private void populateVoucher() {
        mOptionNameText.setText(traveller.getOptionName());
        mDescriptionText.setText(traveller.getOptionName());
        mNameText.setText(traveller.getFirstName() + " " + traveller.getLastName());
        mDateText.setText(traveller.getDate());
        mDurationText.setText(traveller.getDuration());
        if (!TextUtils.isEmpty(traveller.getNote())) {
            note_wrapper.setVisibility(View.VISIBLE);
            mNoteText.setText(traveller.getNote());
        }
    }

    public static void show(Context context, String countryId, String bookingId, String place) {
        Intent intent = new Intent(context, VoucherActivity.class);
        intent.putExtra(COUNTRYID, countryId);
        intent.putExtra(BOOKINGID, bookingId);
        intent.putExtra(PLACE, place);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

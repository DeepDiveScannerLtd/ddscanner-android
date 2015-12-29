package travel.ilave.deepdivescanner.ui.activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.net.SocketTimeoutException;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.entities.ProductDetails;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.adapters.IconsAdapter;
import travel.ilave.deepdivescanner.ui.adapters.PlaceImagesPagerAdapter;
import travel.ilave.deepdivescanner.ui.fragments.DatePickerFragment;
import travel.ilave.deepdivescanner.utils.LogUtils;

public class DivePlaceActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    public static final String PRODUCT = "PRODUCT";

    private Toolbar toolbar;
    private ViewPager productImagesViewPager;
    private LinearLayout starsLayout;
    private TextView reviewsCount;
    private TextView price;
    private TextView depth_value;
    private TextView visibility_value;
    private TextView access_value;
    private TextView description;
    private Button book_now;
    private ProgressDialog progressDialog;

    private HashMap<String, String> characteristiscs;
    private Product product;
    private ProductDetails productDetails;
    private PlaceImagesPagerAdapter placeImagesPagerAdapter;

    private String[] iconsUrls = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_place);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImagesViewPager = (ViewPager) findViewById(R.id.product_images);
        starsLayout = (LinearLayout) findViewById(R.id.stars);
        price = (TextView) findViewById(R.id.price);
        depth_value = (TextView) findViewById(R.id.depth_value);
        visibility_value = (TextView) findViewById(R.id.visibility_value);
        access_value = (TextView) findViewById(R.id.access_value);
        description = (TextView) findViewById(R.id.description);
        book_now = (Button) findViewById(R.id.book_now);
        book_now.setOnClickListener(this);

        product = (Product) getIntent().getParcelableExtra(PRODUCT);
        requestProductDetails(product.getId());
    }

    private void requestProductDetails(String productId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.pleaseWait));
        progressDialog.show();
        RestClient.getServiceInstance().getProductById(productId, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                // TODO Handle result handling when activity stopped
                responseString = responseString.replaceAll("\\\\", "");
                System.out.println(responseString);
                productDetails = new Gson().fromJson(responseString, ProductDetails.class);
                populateProductDetails();
            }

            @Override
            public void failure(RetrofitError error) {
                LogUtils.i("failure Message is " + error.getMessage());
                LogUtils.i("failure body is " + error.getBody());
                if (error.getCause() instanceof SocketTimeoutException) {
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        Toast.makeText(DivePlaceActivity.this, R.string.errorConnection, Toast.LENGTH_LONG);
                    } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                        Toast.makeText(DivePlaceActivity.this, R.string.serverNotResp, Toast.LENGTH_LONG);
                    }
                }
                // TODO Handle result handling when activity stopped
                // TODO Handle errors
            }
        });
    }

    private void populateProductDetails() {
        getSupportActionBar().setTitle(productDetails.getName());

        placeImagesPagerAdapter = new PlaceImagesPagerAdapter(getFragmentManager(), productDetails.getImages());
        productImagesViewPager.setAdapter(placeImagesPagerAdapter);
        for (int i = 0; i < productDetails.getRating(); i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_star_white_24dp);
            starsLayout.addView(iv);
        }
        for (int i = 0; i < 5 - productDetails.getRating(); i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_star_border_white_24dp);
            iv.setAlpha(0.6f);
            starsLayout.addView(iv);
        }

        depth_value.setText("" + productDetails.getDept() + "m");
        visibility_value.setText(productDetails.getVisiblity());
        access_value.setText(productDetails.getAccess());
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int i = 0;
        for (String sealifeIcon : productDetails.getSealife()) {
            iconsUrls[i] = sealifeIcon.toString();
            i++;
        }
        GridView sealife = (GridView) findViewById(R.id.usage_example_gridview);
        sealife.setAdapter(new IconsAdapter(DivePlaceActivity.this, iconsUrls));
        description.setText(product.getDescription());
        progressDialog.dismiss();
    }

    public static void show(Context context, Product product) {
        Intent intent = new Intent(context, DivePlaceActivity.class);
        intent.putExtra(PRODUCT, product);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        showDatePickerDialog();
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        OffersActivity.show(this, product.getId(), "" + i + "-" + (i1 + 1) + "-" + i2, product.getName());
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

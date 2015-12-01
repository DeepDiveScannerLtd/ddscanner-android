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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.entities.ProductDetails;
import travel.ilave.deepdivescanner.rest.RestClient;
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
    private LinearLayout sealifeLayout;
    private TextView description;
    private Button book_now;
    private ProgressDialog progressDialog;

    private Product product;
    private ProductDetails productDetails;
    private PlaceImagesPagerAdapter placeImagesPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_place);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImagesViewPager = (ViewPager) findViewById(R.id.product_images);
        starsLayout = (LinearLayout) findViewById(R.id.stars);
        reviewsCount = (TextView) findViewById(R.id.reviews_count);
        price = (TextView) findViewById(R.id.price);
        depth_value = (TextView) findViewById(R.id.depth_value);
        visibility_value = (TextView) findViewById(R.id.visibility_value);
        access_value = (TextView) findViewById(R.id.access_value);
        sealifeLayout = (LinearLayout) findViewById(R.id.sealife_icons);
        description = (TextView) findViewById(R.id.description);
        book_now = (Button) findViewById(R.id.book_now);
        book_now.setOnClickListener(this);

        product = (Product) getIntent().getParcelableExtra(PRODUCT);
        requestProductDetails(product.getId());
    }

    private void requestProductDetails(String productId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        RestClient.getServiceInstance().getProductById(productId, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                // TODO Handle result handling when activity stopped
                responseString = responseString.replaceAll("\\n/", "/");
                productDetails = new Gson().fromJson(responseString, ProductDetails.class);
                populateProductDetails();
                progressDialog.dismiss();
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
        reviewsCount.setText("150 Reviews");
        price.setText("from " + product.getPrice() + "$");
        depth_value.setText("" + productDetails.getDept() + "m");
        visibility_value.setText(productDetails.getVisiblity());
        access_value.setText(productDetails.getAccess());
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (String sealifeIcon : productDetails.getSealife()) {
            FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.view_sealife_icon, sealifeLayout, false);
            SimpleDraweeView iv = (SimpleDraweeView) fl.findViewById(R.id.icon);

            Uri uri = Uri.parse("http://" + sealifeIcon.replaceAll("\\n/", "/"));
            iv.setImageURI(uri);

            sealifeLayout.addView(fl);
        }
        description.setText(product.getDescription());
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

package com.ddscanner.ui.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.DiveSpotFull;
import com.ddscanner.entities.DivespotDetails;
import com.ddscanner.entities.Product;
import com.ddscanner.entities.Sealife;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.PlaceImagesPagerAdapter;
import com.ddscanner.ui.adapters.SealifeListAdapter;
import com.ddscanner.ui.dialogs.SubscribeDialog;
import com.ddscanner.ui.managers.SealifeLinearLayoutManager;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.LogUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DivePlaceActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    public static final String PRODUCT = "PRODUCT";
    public static final String TAG = DivePlaceActivity.class.getSimpleName();


    private Toolbar toolbar;
    private ViewPager productImagesViewPager;
    private LinearLayout starsLayout;
    private TextView reviewsCount;
    private TextView price;
    private TextView depth_value;
    private TextView visibility_value;
    private TextView description;
    private TextView currents_value;
    private TextView object_value;
    private TextView level_value;
    private ImageView[] dots;
    private int dotsCount = 0;
    private LinearLayout pager_indicator;
    private String productId;

    private Button book_now;
    private ImageView none_photo;
    private PlaceImagesPagerAdapter placeImagesPagerAdapter;
    private ProgressDialog progressDialog;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private SubscribeDialog subscribeDialog = new SubscribeDialog();
    private RecyclerView sealifeRecyclerview;
    private ArrayList<Sealife> sealifes;
    private static ArrayList<String> images;
    private RelativeLayout sealifeLayout;
    private RelativeLayout reviews_rating;
    private static String PATH;
    private DiveSpotFull diveSpot;

    private HashMap<String, String> values= new HashMap<String, String>();

    private DivespotDetails divespotDetails;

    private String[] iconsUrls = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        findViews();
        book_now.setOnClickListener(this);
        reviews_rating.setOnClickListener(this);
        productImagesViewPager.setOnPageChangeListener(this);
        productId = getIntent().getStringExtra(PRODUCT);
        requestProductDetails(getIntent().getStringExtra(PRODUCT));
    }

    private void requestProductDetails(String productId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.pleaseWait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        RestClient.getServiceInstance().getDiveSpotById(productId, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                divespotDetails = new Gson().fromJson(responseString, DivespotDetails.class);
                toolbarSetting(divespotDetails.getDivespot().getName());
                populateProductDetails();
            }

            @Override
            public void failure(RetrofitError error) {
                LogUtils.i("failure Message is " + error.getMessage());
                LogUtils.i("failure body is " + error.getBody());
                if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                    Toast.makeText(DivePlaceActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                    Toast.makeText(DivePlaceActivity.this, "Server is not responsible, please try later", Toast.LENGTH_LONG).show();
                }
                if (error != null) {
                    Log.i(TAG, error.getMessage());
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    Log.i(TAG, json.toString());
                }
                // TODO Handle result handling when activity stopped
                // TODO Handle errors
            }
        });
    }

    private void findViews() {
        productImagesViewPager = (ViewPager) findViewById(R.id.product_images);
        starsLayout = (LinearLayout) findViewById(R.id.stars);
        price = (TextView) findViewById(R.id.price);
        depth_value = (TextView) findViewById(R.id.characteristic_value_depth);
        visibility_value = (TextView) findViewById(R.id.characteristic_value_visibility);
        description = (TextView) findViewById(R.id.dive_place_description);
        book_now = (Button) findViewById(R.id.book_now);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_collapse);
        sealifeRecyclerview = (RecyclerView) findViewById(R.id.sealife_rc);
        currents_value = (TextView) findViewById(R.id.characteristic_value_currents);
        level_value = (TextView) findViewById(R.id.characteristic_value_level);
        none_photo = (ImageView) findViewById(R.id.nonne_photos);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        reviews_rating = (RelativeLayout) findViewById(R.id.reviews_rating_layout);
        reviewsCount = (TextView) findViewById(R.id.reviews_number);
        object_value = (TextView) findViewById(R.id.characteristic_value_object);

    }

    private void toolbarSetting(String name) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
    }

    private void populateProductDetails() {
        sealifes = (ArrayList<Sealife>)divespotDetails.getSealifes();
//        images = diveSpot.getImages();
        diveSpot = divespotDetails.getDivespot();
        PATH = diveSpot.getDiveSpotPathMedium();
        visibility_value.setText(diveSpot.getVisibility());
        currents_value.setText(diveSpot.getCurrents());
        description.setText(diveSpot.getDescription());
        level_value.setText(diveSpot.getLevel());
        depth_value.setText(diveSpot.getDepth() + " metres");
        if (diveSpot.getObject() != null && !diveSpot.getObject().equals("Other")) {
            RelativeLayout relativeLayout;
            ImageView lastDivider;
            relativeLayout = (RelativeLayout) findViewById(R.id.layout_object);
            lastDivider = (ImageView) findViewById(R.id.last_divider);
            lastDivider.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.VISIBLE);
            object_value.setText(diveSpot.getObject());

        }
        if (divespotDetails.getComments() != null) {
            if (divespotDetails.getComments().size() == 1) {
                reviewsCount.setText(divespotDetails.getComments().size() + " review");
            } else  {
                reviewsCount.setText(divespotDetails.getComments().size() + " reviews");
            }
        } else {
            reviewsCount.setText("No reviews");
        }
        LinearLayoutManager linearLayoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        if (divespotDetails.getSealifes() != null) {
            findViewById(R.id.sealife).setVisibility(View.VISIBLE);
            findViewById(R.id.list_sl).setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new SealifeLinearLayoutManager(DivePlaceActivity.this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            sealifeRecyclerview.setNestedScrollingEnabled(false);
            sealifeRecyclerview.setHasFixedSize(false);
            sealifeRecyclerview.setLayoutManager(layoutManager);
         //   sealifeRecyclerview.setLayoutManager(linearLayoutManager);
            sealifeRecyclerview.setAdapter(new SealifeListAdapter(sealifes, this, diveSpot.getSealifePathSmall(), diveSpot.getSealifePathMedium()));
        }
        if (diveSpot.getImages() != null) {
            images = (ArrayList<String>)diveSpot.getImages();
            none_photo.setVisibility(View.GONE);
            productImagesViewPager.setVisibility(View.VISIBLE);
            placeImagesPagerAdapter = new PlaceImagesPagerAdapter(getFragmentManager(), diveSpot.getImages(), diveSpot.getDiveSpotPathMedium(), DivePlaceActivity.this);
            productImagesViewPager.setAdapter(placeImagesPagerAdapter);
            setUi();
        }
        for (int i = 0; i < diveSpot.getRating(); i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(5,0,5,0);
            starsLayout.addView(iv);
        }
        for (int i = 0; i < 5 - diveSpot.getRating(); i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_flag_empty_small);
            iv.setPadding(5, 0, 5, 0);
            starsLayout.addView(iv);
        }
        progressDialog.dismiss();
    }

    public static void show(Context context, Product product) {
        Intent intent = new Intent(context, DivePlaceActivity.class);
        intent.putExtra(PRODUCT, product);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.book_now:
                LatLng latLng = new LatLng(Double.valueOf(diveSpot.getLat()), Double.valueOf(diveSpot.getLng()));
                String name = diveSpot.getName();
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                        EventTrackerHelper.EVENT_BOOK_CLICK, new HashMap<String, Object>());
                DiveCentersActivity.show(DivePlaceActivity.this, latLng, name);
                break;
            case R.id.reviews_rating_layout:
                if (diveSpot.getImages() != null) {
                    values.put("image", diveSpot.getDiveSpotPathSmall() + diveSpot.getImages().get(0));
                } else {
                    values.put("image","");
                }
                values.put("name", diveSpot.getName());
                values.put("id", String.valueOf(diveSpot.getId()));
                Intent intent = new Intent(DivePlaceActivity.this, ReviewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("COMMENTS", (ArrayList<Comment>) divespotDetails.getComments());
                intent.putExtras(bundle);
                intent.putExtra("VALUES", values);
                intent.putExtra("RATING", diveSpot.getRating());
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                        EventTrackerHelper.EVENT_REVIEWS_CLICK, new HashMap<String, Object>() {{
                            put(EventTrackerHelper.PARAM_REVIEWS_CLICK, diveSpot.getId());
                        }});
                startActivityForResult(intent, 9001);
               // ReviewsActivity.show(DivePlaceActivity.this, (ArrayList<Comment>)divespotDetails.getComments(), values, diveSpot.getRating());
                break;
        }
    }

    private void setUi() {
        dotsCount = placeImagesPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i=0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4,0,4,0);

            pager_indicator.addView(dots[i],  params);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
          /*  case R.id.favorite:
                if (SharedPreferenceHelper.getIsUserLogined()) {
                    Toast.makeText(DivePlaceActivity.this, "You are already login", Toast.LENGTH_LONG);
                } else {
                    SocialNetworks.show(DivePlaceActivity.this);
                }*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static ArrayList<String> getImages() {
        return images;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }
        System.out.println(position);
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 9001) {
            if(resultCode == Activity.RESULT_OK){
                Intent intent = new Intent(DivePlaceActivity.this, DivePlaceActivity.class);
                intent.putExtra("PRODUCT", productId);
                finish();
                startActivity(intent);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

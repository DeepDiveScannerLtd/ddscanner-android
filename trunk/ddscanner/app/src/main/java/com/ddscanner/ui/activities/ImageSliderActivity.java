package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.Image;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.SliderImagesAdapter;
import com.ddscanner.ui.views.SimpleGestureFilter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 4.3.16.
 */
public class ImageSliderActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, SimpleGestureFilter.SimpleGestureListener{

    private LinearLayout pager_indicator;
    private int dotsCount = 0;
    private SliderImagesAdapter sliderImagesAdapter;
    private ImageView[] dots;
    private FrameLayout baseLayout;
    private ViewPager viewPager;
    private ImageView close;
    private ArrayList<Image> images;
    private Drawable drawable;
    private int position;
    private ImageView avatar;
    private TextView date;
    private TextView userName;
    private ImageView options;
    private Helpers helpers = new Helpers();
    private boolean isChanged = false;
    private FiltersResponseEntity filters = new FiltersResponseEntity();
    private String imageNameForDeletion;
    private String reportName;
    private String path;
    private String reportType;
    private String reportDescription;
    private String deleteImageName;
    private MaterialDialog materialDialog;
    private SimpleGestureFilter detector;
    float x1,x2;
    float y1, y2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        findViews();
        detector = new SimpleGestureFilter(this,this);
        materialDialog = helpers.getMaterialDialog(this);
        getReportsTypes();
        Bundle bundle = getIntent().getExtras();
        images = bundle.getParcelableArrayList("IMAGES");
        position = getIntent().getIntExtra("position", 0);
        path = getIntent().getStringExtra("path");
        viewPager.addOnPageChangeListener(this);
        sliderImagesAdapter = new SliderImagesAdapter(getFragmentManager(), images);
        viewPager.setAdapter(sliderImagesAdapter);
        close.setOnClickListener(this);
        options.setVisibility(View.VISIBLE);
        changeUiAccrodingPosition(position);
        setUi();

    }

    private void changeUiAccrodingPosition(final int position) {
        this.position = position;
        userName.setText(images.get(position).getAuthor().getName());
        date.setText(helpers.convertDateToImageSliderActivity(images.get(position).getAuthor().getDate()));
        Picasso.with(this)
                .load(images.get(position).getAuthor().getPhoto())
                .resize(Math.round(helpers.convertDpToPixel(35, this)), Math.round(helpers.convertDpToPixel(35, this)))
                .centerCrop()
                .placeholder(R.drawable.avatar_profile_default)
                .transform(new CropCircleTransformation())
                .into(avatar);
        if (images.get(position).isReport()) {
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showReportMenu(options, position);
                }
            });
        } else {
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteMenu(options);
                }
            });
        }
    }

    private void findViews() {
        viewPager = (ViewPager) findViewById(R.id.image_slider);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        close = (ImageView) findViewById(R.id.close_btn);
        baseLayout = (FrameLayout) findViewById(R.id.swipe_layout);
        avatar = (ImageView) findViewById(R.id.user_avatar);
        date = (TextView) findViewById(R.id.date);
        userName = (TextView) findViewById(R.id.user_name);
        options = (ImageView) findViewById(R.id.options);
    }

    private void setUi() {
        dotsCount = sliderImagesAdapter.getCount();
        dots = new ImageView[dotsCount];
        pager_indicator.removeAllViews();
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
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_btn:
                onBackPressed();
                break;
        }
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    private void imageDeleted(int position) {
        images.remove(position);
        if (images.size() > 0) {
            sliderImagesAdapter = new SliderImagesAdapter(getFragmentManager(), images);
            viewPager.setAdapter(sliderImagesAdapter);
            if (images.size() == 1) {
                this.position = 0;
            } else if (position + 1 <= images.size()) {
                this.position = position;
            } else if (position == images.size()) {
                this.position = images.size() - 1;
            }
            setUi();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
        changeUiAccrodingPosition(position);
    }

    private void showDeleteMenu(View view) {
        deleteImageName = images.get(position).getName();
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_delete, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemsClickListener(deleteImageName));
        popup.show();
    }

    private void showReportMenu(View view, int position) {
        if (images.size() == 0) {
            position = 0;
            this.position = 0;
        }
        reportName = images.get(position).getName();
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_report, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemsClickListener(reportName));
        popup.show();
    }

    public static void showForResult(Activity context, ArrayList<Image> images, int position, String path, int requestCode) {
        Intent intent = new Intent(context, ImageSliderActivity.class);
        intent.putParcelableArrayListExtra("IMAGES", images);
        intent.putExtra("position", position);
        intent.putExtra("path", path);
        context.startActivityForResult(intent, requestCode);
    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

    private class MenuItemsClickListener implements PopupMenu.OnMenuItemClickListener {

        private String imageName;

        public MenuItemsClickListener(String imageName) {
            this.imageName = imageName;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.photo_report:
                    EventsTracker.trackPhotoReport();
                    reportName = imageName.replace(path, "");
                    showReportDialog();
                    break;
                case R.id.photo_delete:
                    deleteImageName = imageName.replace(path, "");
                    deleteImage(deleteImageName);
                    break;
            }
            return false;
        }
    }

    private void deleteImage(String name) {
        isChanged = true;
        materialDialog.show();
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().deleteImage(name, helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                materialDialog.dismiss();
                if (response.isSuccessful()) {
                    imageDeleted(ImageSliderActivity.this.position);
                }
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(ImageSliderActivity.this, Constants.SLIDER_ACTIVITY_REQUEST_CODE_LOGIN_FOR_DELETE);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(ImageSliderActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isChanged) {
            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else {
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.SLIDER_ACTIVITY_REQUEST_CODE_LOGIN_FOR_REPORT) {
            if (resultCode == RESULT_OK) {
                reportImage(reportName, reportType, reportDescription);
            }
        }
        if (requestCode == Constants.SLIDER_ACTIVITY_REQUEST_CODE_LOGIN_FOR_DELETE) {
            if (resultCode == RESULT_OK) {
                deleteImage(deleteImageName);
            }
            if (resultCode == RESULT_CANCELED) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reportImage(String imageName, String reportType, String reportDescription) {
        isChanged = true;
        materialDialog.show();
        ReportRequest reportRequest = new ReportRequest();
        if (!SharedPreferenceHelper.isUserLoggedIn() || SharedPreferenceHelper.getToken().isEmpty() || SharedPreferenceHelper.getSn().isEmpty()) {
            SocialNetworks.showForResult(this, Constants.SLIDER_ACTIVITY_REQUEST_CODE_LOGIN_FOR_REPORT);
            return;
        }
        reportRequest.setName(imageName);
        reportRequest.setType(reportType);
        if (reportDescription != null) {
            reportRequest.setDescription(reportDescription);
        }
        reportRequest.setSocial(SharedPreferenceHelper.getSn());
        reportRequest.setToken(SharedPreferenceHelper.getToken());
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().reportImage(reportRequest);
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                materialDialog.dismiss();
                if (response.isSuccessful()) {
                    EventsTracker.trackDiveSpotphotoReportSent();
                    Toast.makeText(ImageSliderActivity.this, R.string.report_sent, Toast.LENGTH_SHORT).show();
                    imageDeleted(ImageSliderActivity.this.position);
                }
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(ImageSliderActivity.this, Constants.SLIDER_ACTIVITY_REQUEST_CODE_LOGIN_FOR_REPORT);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(ImageSliderActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(ImageSliderActivity.this);
            }
        });
    }

    private void getReportsTypes() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getFilters();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {

                    }
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(responseString).getAsJsonObject();
                    JsonObject currentsJsonObject = jsonObject.getAsJsonObject(Constants.FILTERS_VALUE_REPORT);
                    for (Map.Entry<String, JsonElement> elementEntry : currentsJsonObject.entrySet()) {
                        filters.getReport().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void showReportDialog() {
        List<String> objects = new ArrayList<String>();
        for (Map.Entry<String, String> entry : filters.getReport().entrySet()) {
            objects.add(entry.getValue());
        }
        new MaterialDialog.Builder(this)
                .title("Report")
                .items(objects)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                        reportType = helpers.getMirrorOfHashMap(filters.getReport()).get(text);
                        if (reportType.equals("other")) {
                            showOtherReportDialog();
                            dialog.dismiss();
                        } else {
                            reportImage(reportName, reportType, null);
                        }
                    }
                })
                .show();
    }

    private void showOtherReportDialog() {
        new MaterialDialog.Builder(this)
                .title("Other")
                .widgetColor(getResources().getColor(R.color.primary))
                .input("Write reason", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().trim().length() > 1) {
                            reportImage(reportName, "other", input.toString());
                            reportDescription = input.toString();
                        } else {
                            Toast.makeText(ImageSliderActivity.this, "Write a reason", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                x2 = touchevent.getX();
                y2 = touchevent.getY();
                break;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {
            case SimpleGestureFilter.SWIPE_DOWN:
               // onBackPressed();
                break;
            case SimpleGestureFilter.SWIPE_UP:
                onBackPressed();
                break;

        }
    }

    @Override
    public void onDoubleTap() {

    }

}

package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.ReviewImageSLiderAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.views.SimpleGestureFilter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewImageSliderActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener , SimpleGestureFilter.SimpleGestureListener, InfoDialogFragment.DialogClosedListener {

    private LinearLayout pager_indicator;
    private int dotsCount = 0;
    private ReviewImageSLiderAdapter sliderImagesAdapter;
    private ImageView[] dots;
    private FrameLayout baseLayout;
    private ViewPager viewPager;
    private ImageView close;
    private ArrayList<String> images;
    private Drawable drawable;
    private int position;
    private SimpleGestureFilter detector;
    float x1,x2;
    float y1, y2;
    private boolean isSelfImages;
    private boolean isFromReviews;
    private String reportName;
    private String deleteImageName;
    private ImageView menu;
    private String path;
    private FiltersResponseEntity filters = new FiltersResponseEntity();
    private String reportType;
    private String reportDescription;
    private boolean isSomethingReported;
    private MaterialDialog materialDialog;
    private ArrayList<String> deletedImages = new ArrayList<>();

    private DDScannerRestClient.ResultListener<Void> reportImageResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            isSomethingReported = true;
            materialDialog.dismiss();
            deletedImages.add(images.get(ReviewImageSliderActivity.this.position));
            imageDeleted(ReviewImageSliderActivity.this.position);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(ReviewImageSliderActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT);
                    break;
                default:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_SLIDER_ACTIVITY_UNEXPECTED_ERROR, false);
                    break;
            }
        }
    };


    private DDScannerRestClient.ResultListener<FiltersResponseEntity> filtersResponseEntityResultListener = new DDScannerRestClient.ResultListener<FiltersResponseEntity>() {
        @Override
        public void onSuccess(FiltersResponseEntity result) {
            filters = result;
            if (isFromReviews && !isSelfImages) {
                menu.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_SLIDER_ACTIVITY_UNEXPECTED_ERROR, false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        findViews();
        materialDialog = Helpers.getMaterialDialog(this);
        images = (ArrayList<String>) getIntent().getSerializableExtra(Constants.REVIEWS_IMAGES_SLIDE_INTENT_IMAGES);
        isSelfImages = getIntent().getBooleanExtra("isSelf", false);
        isFromReviews = getIntent().getBooleanExtra("isFromReviews", false);
        path = getIntent().getStringExtra("path");
        detector = new SimpleGestureFilter(this,this);
        position = getIntent().getIntExtra(Constants.REVIEWS_IMAGES_SLIDE_INTENT_POSITION, 0);
        viewPager.addOnPageChangeListener(this);
        sliderImagesAdapter = new ReviewImageSLiderAdapter(getFragmentManager(), images);
        viewPager.setAdapter(sliderImagesAdapter);
        DDScannerApplication.getInstance().getDdScannerRestClient().getReportTypes(filtersResponseEntityResultListener);
        setUi();

    }

    private void showDeleteMenu(View view) {
        deleteImageName = images.get(position).replace(path, "");
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_delete, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemsClickListener(deleteImageName));
        popup.show();
    }

    private void showReportMenu(View view) {
        if (images.size() == 0) {
            position = 0;
            this.position = 0;
        }
        reportName = images.get(position).replace(path, "");
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_report, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemsClickListener(reportName));
        popup.show();
    }

    private void findViews() {
        viewPager = (ViewPager) findViewById(R.id.image_slider);
        close = (ImageView) findViewById(R.id.close_btn);
        baseLayout = (FrameLayout) findViewById(R.id.swipe_layout);
        menu = (ImageView) findViewById(R.id.options);
        close.setOnClickListener(this);
        menu.setOnClickListener(this);
    }

    private void setUi() {
        viewPager.setCurrentItem(position);
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
                    reportName = imageName.replace(path, "");
                    EventsTracker.trackPhotoReport();
                    showReportDialog();
                    break;
                case R.id.photo_delete:
                    deleteImageName = imageName.replace(path, "");
                    break;
            }
            return false;
        }
    }

    private void imageDeleted(int position) {
        images.remove(position);
        if (images.size() > 0) {
            sliderImagesAdapter = new ReviewImageSLiderAdapter(getFragmentManager(), images);
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
            onBackPressed();
        }
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
                        reportType = Helpers.getMirrorOfHashMap(filters.getReport()).get(text);
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

    private void reportImage(String imageName, String reportType, String reportDescription) {
        materialDialog.show();
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn() || DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken().isEmpty() || DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn().isEmpty()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT);
            return;
        }
//        DDScannerApplication.getInstance().getDdScannerRestClient().postReportImage(imageName, reportType, reportDescription, reportImageResultListener);

    }

    private void showOtherReportDialog() {
        new MaterialDialog.Builder(this)
                .title("Other")
                .positiveColor(ContextCompat.getColor(this, R.color.black_text))
                .widgetColor(ContextCompat.getColor(this, R.color.accent))
                .input("Write reason", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().trim().length() > 1) {
                            reportImage(reportName, "other", input.toString());
                            reportDescription = input.toString();
                        } else {
                            Toast.makeText(ReviewImageSliderActivity.this, "Write a reason", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_btn:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.options:
                if (!isSelfImages) {
                    showReportMenu(menu);
                    break;
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
    }


    public static void show(Context context, ArrayList<String> images, int position, boolean isSelfImages, boolean isFromReviews, String path) {
        Intent intent = new Intent(context, ReviewImageSliderActivity.class);
        intent.putExtra(Constants.REVIEWS_IMAGES_SLIDE_INTENT_IMAGES, images);
        intent.putExtra(Constants.REVIEWS_IMAGES_SLIDE_INTENT_POSITION, position);
        intent.putExtra("isSelf", isSelfImages);
        intent.putExtra("isFromReviews", isFromReviews);
        intent.putExtra("path", path);
        context.startActivity(intent);
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
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
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

    @Override
    public void onBackPressed() {
        if (isSomethingReported) {
            Intent intent = new Intent();
            intent.putExtra("deletedImages", deletedImages);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
        }
    }

    public static void showForResult(Activity context, ArrayList<String> images, int position, boolean isSelfImages, boolean isFromReviews, String path, int requestCode) {
        Intent intent = new Intent(context, ReviewImageSliderActivity.class);
        intent.putExtra(Constants.REVIEWS_IMAGES_SLIDE_INTENT_IMAGES, images);
        intent.putExtra(Constants.REVIEWS_IMAGES_SLIDE_INTENT_POSITION, position);
        intent.putExtra("isSelf", isSelfImages);
        intent.putExtra("isFromReviews", isFromReviews);
        intent.putExtra("path", path);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT:
                if (resultCode == RESULT_OK) {
//                    DDScannerApplication.getInstance().getDdScannerRestClient().postReportImage(reportName, reportType, reportDescription,reportImageResultListener);
                }
                break;
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_REVIEWS_SLIDER_ACTIVITY_UNEXPECTED_ERROR:
            case DialogsRequestCodes.DRC_REVIEWS_SLIDER_ACTIVITY_FAILED_TO_CONNECT:
                onBackPressed();
                break;
        }
    }
}
package com.ddscanner.ui.activities;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotDetails;
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
import com.ddscanner.rest.BaseCallbackOld;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.SliderImagesAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.views.SimpleGestureFilter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.DialogsRequestCodes;
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

public class ImageSliderActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, SimpleGestureFilter.SimpleGestureListener, InfoDialogFragment.DialogClosedListener {

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
    private String diveSpotId;
    float x1, x2;
    float y1, y2;

    private DDScannerRestClient.ResultListener<DiveSpotDetails> imagesResulListener = new DDScannerRestClient.ResultListener<DiveSpotDetails>() {
        @Override
        public void onSuccess(DiveSpotDetails result) {
            images = (ArrayList<Image>) result.getDivespot().getImages();
            changeUiAccrodingPosition(position);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }
    };

    private DDScannerRestClient.ResultListener<Void> reportImageRequestListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            EventsTracker.trackDiveSpotphotoReportSent();
            Toast.makeText(ImageSliderActivity.this, R.string.report_sent, Toast.LENGTH_SHORT).show();
            imageDeleted(ImageSliderActivity.this.position);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case BAD_REQUEST_ERROR_400:
                    DDScannerApplication.getDdScannerRestClient().getDiveSpotPhotos(diveSpotId, imagesResulListener);
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_you_cannot_report_self_photo, false);
                    break;
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    SocialNetworks.showForResult(ImageSliderActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT);
                    break;
                default:
                    break;
            }
        }
    };

    private DDScannerRestClient.ResultListener<Void> deleteImageRequestistener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            imageDeleted(ImageSliderActivity.this.position);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case BAD_REQUEST_ERROR_400:
                    break;
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    SocialNetworks.showForResult(ImageSliderActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_DELETE);
                    break;
                default:
                    break;
            }
        }
    };

    private DDScannerRestClient.ResultListener<FiltersResponseEntity> filtersResponseEntityResultListener = new DDScannerRestClient.ResultListener<FiltersResponseEntity>() {
        @Override
        public void onSuccess(FiltersResponseEntity result) {
            filters = result;
            options.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_CONNECTION_FAILURE_GET_REPORT_TYPES, false);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        findViews();
        detector = new SimpleGestureFilter(this, this);
        materialDialog = Helpers.getMaterialDialog(this);
        DDScannerApplication.getDdScannerRestClient().getReportTypes(filtersResponseEntityResultListener);
        Bundle bundle = getIntent().getExtras();
        images = bundle.getParcelableArrayList("IMAGES");
        position = getIntent().getIntExtra("position", 0);
        path = getIntent().getStringExtra("path");
        diveSpotId = getIntent().getStringExtra("divespotid");
        viewPager.addOnPageChangeListener(this);
        sliderImagesAdapter = new SliderImagesAdapter(getFragmentManager(), images);
        viewPager.setAdapter(sliderImagesAdapter);
        close.setOnClickListener(this);
//        options.setVisibility(View.VISIBLE);
        changeUiAccrodingPosition(position);
        setUi();

    }

    private void changeUiAccrodingPosition(final int position) {
        this.position = position;
        userName.setText(images.get(position).getAuthor().getName());
        date.setText(Helpers.convertDateToImageSliderActivity(images.get(position).getAuthor().getDate()));
        Picasso.with(this)
                .load(images.get(position).getAuthor().getPhoto())
                .resize(Math.round(Helpers.convertDpToPixel(35, this)), Math.round(Helpers.convertDpToPixel(35, this)))
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
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }
        dots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selecteditem_dot));
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
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonselecteditem_dot));
        }
        dots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selecteditem_dot));
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

    public static void showForResult(Activity context, ArrayList<Image> images, int position, String path, int requestCode, String diveSpotId) {
        Intent intent = new Intent(context, ImageSliderActivity.class);
        intent.putParcelableArrayListExtra("IMAGES", images);
        intent.putExtra("position", position);
        intent.putExtra("path", path);
        intent.putExtra("divespotid", diveSpotId);
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
        if (!Helpers.hasConnection(this)) {
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
        DDScannerApplication.getDdScannerRestClient().deleteImage(name, deleteImageRequestistener);
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
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT:
                if (resultCode == RESULT_OK) {
                    reportImage(reportName, reportType, reportDescription);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_DELETE:
                if (resultCode == RESULT_OK) {
                    deleteImage(deleteImageName);
                }
                if (resultCode == RESULT_CANCELED) {

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reportImage(String imageName, String reportType, String reportDescription) {
        isChanged = true;
        materialDialog.show();
        if (!SharedPreferenceHelper.isUserLoggedIn() || SharedPreferenceHelper.getToken().isEmpty() || SharedPreferenceHelper.getSn().isEmpty()) {
            SocialNetworks.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT);
            return;
        }
       DDScannerApplication.getDdScannerRestClient().postReportImage(imageName, reportType, reportDescription, reportImageRequestListener);
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

    private void showOtherReportDialog() {
        new MaterialDialog.Builder(this)
                .title("Other")
                .widgetColor(ContextCompat.getColor(this, R.color.primary))
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
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = touchevent.getX();
                y2 = touchevent.getY();
                break;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
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
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT:
            case DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_CONNECTION_FAILURE_GET_REPORT_TYPES:
                finish();
                break;
        }
    }
}

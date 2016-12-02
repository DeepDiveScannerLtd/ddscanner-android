package com.ddscanner.screens.photo.slider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.DiveSpotPhotosResponseEntity;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.views.SimpleGestureFilter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ImageSliderActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, SimpleGestureFilter.SimpleGestureListener, InfoDialogFragment.DialogClosedListener {

    private SliderImagesAdapter sliderImagesAdapter;
    private FrameLayout baseLayout;
    private ViewPager viewPager;
    private ImageView close;
    private ArrayList<DiveSpotPhoto> images;
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
    private String reportType;
    private String reportDescription;
    private String deleteImageName;
    private MaterialDialog materialDialog;
    private SimpleGestureFilter detector;
    private RelativeLayout likesLayout;
    private ImageView likeIcon;
    private TextView likesCount;
    private LikeDislikeResultListener likeResultListener = new LikeDislikeResultListener(true);
    private LikeDislikeResultListener dislikeResultListener = new LikeDislikeResultListener(false);
    float x1, x2;
    float y1, y2;
    PhotoOpenedSource photoOpenedSource;

    private DDScannerRestClient.ResultListener<DiveSpotPhotosResponseEntity> imagesResulListener = new DDScannerRestClient.ResultListener<DiveSpotPhotosResponseEntity>() {
        @Override
        public void onSuccess(DiveSpotPhotosResponseEntity result) {
            switch (photoOpenedSource) {
                case ALL:
                    images =Helpers.compareObjectsArray(result.getDiveSpotPhotos(), result.getCommentPhotos());
                    break;
                case DIVESPOT:
                    images = result.getDiveSpotPhotos();
                    break;
                case REVIEWS:
                    images = result.getCommentPhotos();
                    break;
            }
            changeUiAccrodingPosition(position);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
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
                     InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_you_cannot_report_self_photo, true);
                    break;
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(ImageSliderActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, true);
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
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(ImageSliderActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_DELETE);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, true);
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
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_text));
        }
        detector = new SimpleGestureFilter(this, this);
        materialDialog = Helpers.getMaterialDialog(this);
//        DDScannerApplication.getInstance().getDdScannerRestClient().getReportTypes(filtersResponseEntityResultListener);
        Bundle bundle = getIntent().getExtras();
        images = bundle.getParcelableArrayList("IMAGES");
        position = getIntent().getIntExtra("position", 0);
        photoOpenedSource = (PhotoOpenedSource) getIntent().getSerializableExtra("source");
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
        likesCount.setText(images.get(position).getLikesCount());
        if (images.get(position).isLiked()) {
            likeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like_photo_full));
        }
        userName.setText(images.get(position).getAuthor().getName());
        date.setText(Helpers.convertDateToImageSliderActivity(images.get(position).getDate()));
        Picasso.with(this)
                .load(getString(R.string.base_photo_url, images.get(position).getAuthor().getPhoto(), "1"))
                .resize(Math.round(Helpers.convertDpToPixel(35, this)), Math.round(Helpers.convertDpToPixel(35, this)))
                .centerCrop()
                .placeholder(R.drawable.avatar_profile_default)
                .error(R.drawable.avatar_profile_default)
                .transform(new CropCircleTransformation())
                .into(avatar);
        if (!images.get(position).getAuthor().getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
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
        likesLayout = (RelativeLayout) findViewById(R.id.likes_layout);
        likeIcon = (ImageView) findViewById(R.id.icon);
        likesCount = (TextView) findViewById(R.id.likes_count);
        viewPager = (ViewPager) findViewById(R.id.image_slider);
        close = (ImageView) findViewById(R.id.close_btn);
        baseLayout = (FrameLayout) findViewById(R.id.swipe_layout);
        avatar = (ImageView) findViewById(R.id.user_avatar);
        date = (TextView) findViewById(R.id.date);
        userName = (TextView) findViewById(R.id.user_name);
        options = (ImageView) findViewById(R.id.options);
    }

    private void setUi() {
        options.setVisibility(View.VISIBLE);
        viewPager.setCurrentItem(position);
        likesLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_btn:
                onBackPressed();
                break;
            case R.id.likes_layout:
                if (!images.get(position).isLiked()) {
                    likeUi();
                    DDScannerApplication.getInstance().getDdScannerRestClient().postLikePhoto(images.get(position).getId(), likeResultListener);
                    break;
                }
                dislikeUi();
                DDScannerApplication.getInstance().getDdScannerRestClient().postDislikePhoto(images.get(position).getId(), dislikeResultListener);
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
        changeUiAccrodingPosition(position);
    }

    private void showDeleteMenu(View view) {
//        deleteImageName = images.get(position).getName();
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
//        reportName = images.get(position).getName();
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_report, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemsClickListener(reportName));
        popup.show();
    }

    public static void showForResult(Activity context, ArrayList<DiveSpotPhoto> images, int position, int requestCode, PhotoOpenedSource photoOpenedSource) {
        Intent intent = new Intent(context, ImageSliderActivity.class);
        intent.putParcelableArrayListExtra("IMAGES", images);
        intent.putExtra("position", position);
        intent.putExtra("source", photoOpenedSource);
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
                    reportName = imageName;
                    showReportDialog();
                    break;
                case R.id.photo_delete:
                    deleteImageName = imageName;
                    deleteImage(deleteImageName);
                    break;
            }
            return false;
        }
    }

    private void deleteImage(String name) {
        isChanged = true;
        materialDialog.show();
        DDScannerApplication.getInstance().getDdScannerRestClient().postDeleteImage(images.get(position).getId(), deleteImageRequestistener);

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
                    setResult(RESULT_OK);
                    reportImage(reportName, reportType, reportDescription);
//                    DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotPhotos(diveSpotId, imagesResulListener);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_DELETE:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    deleteImage(deleteImageName);
//                    DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotPhotos(diveSpotId, imagesResulListener);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reportImage(String imageName, String reportType, String reportDescription) {
        isChanged = true;
        materialDialog.show();
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn() || DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken().isEmpty() || DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn().isEmpty()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT);
            return;
        }
       DDScannerApplication.getInstance().getDdScannerRestClient().postReportImage(imageName, reportType, reportDescription, reportImageRequestListener);

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
                .positiveColor(ContextCompat.getColor(this, R.color.black_text))
                .widgetColor(ContextCompat.getColor(this, R.color.accent))
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

    private void likeUi() {
        likeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like_photo_full));
        images.get(position).setLiked(true);
        images.get(position).setLikesCount(String.valueOf(Integer.parseInt(likesCount.getText().toString()) + 1));
        likesCount.setText(images.get(position).getLikesCount());
    }

    private void dislikeUi() {
        likeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like_photo_empty));
        images.get(position).setLiked(false);
        images.get(position).setLikesCount(String.valueOf(Integer.parseInt(likesCount.getText().toString()) - 1));
        likesCount.setText(images.get(position).getLikesCount());
    }

    private class LikeDislikeResultListener extends DDScannerRestClient.ResultListener<Void> {

        private boolean isLike;

        LikeDislikeResultListener(boolean isLike) {
            this.isLike = isLike;
        }

        @Override
        public void onSuccess(Void result) {

        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
            if (isLike) {
                dislikeUi();
                return;
            }
            likeUi();
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            if (isLike) {
                dislikeUi();
            } else {
                likeUi();
            }
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
        }
    }


}
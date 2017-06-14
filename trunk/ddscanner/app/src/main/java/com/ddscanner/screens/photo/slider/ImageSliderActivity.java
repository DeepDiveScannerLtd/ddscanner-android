package com.ddscanner.screens.photo.slider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.DiveSpotPhotosResponseEntity;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.entities.ReloadSliderImagesEvent;
import com.ddscanner.entities.request.ReportImageRequest;
import com.ddscanner.events.HidePhotoInfoEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.interfaces.ReportReasonIsWritenListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.dialogs.WriteReportReasonDialog;
import com.ddscanner.ui.views.SimpleGestureFilter;
import com.ddscanner.ui.views.SliderViewPager;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ImageSliderActivity extends BaseAppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, DialogClosedListener, ReportReasonIsWritenListener {

    private SliderImagesAdapter sliderImagesAdapter;
    private FrameLayout baseLayout;
    private SliderViewPager viewPager;
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
    private int reportType;
    private String reportDescription;
    private String deleteImageName;
    private MaterialDialog materialDialog;
    private SimpleGestureFilter detector;
    private RelativeLayout likesLayout;
    private TextView title;
    private ImageView likeIcon;
    private TextView likesCount;
    private String sourceId;
    private TextView photosCount;
    private LikeDislikeResultListener likeResultListener = new LikeDislikeResultListener(true);
    private LikeDislikeResultListener dislikeResultListener = new LikeDislikeResultListener(false);
    private ArrayList<DiveSpotPhoto> deletedPhotos = new ArrayList<>();
    private boolean isLikeRequestStarted = false;
    private RelativeLayout topLayout;
    private RelativeLayout bottomLayout;
    private LinearLayout userDataLayout;

    float x1, x2;
    float y1, y2;
    PhotoOpenedSource photoOpenedSource;

    private DDScannerRestClient.ResultListener<DiveSpotPhotosResponseEntity> imagesResulListener = new DDScannerRestClient.ResultListener<DiveSpotPhotosResponseEntity>() {
        @Override
        public void onSuccess(DiveSpotPhotosResponseEntity result) {
            switch (photoOpenedSource) {
                case ALL:
                    images = Helpers.compareObjectsArray(result.getDiveSpotPhotos(), result.getCommentPhotos());
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
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

    };

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>> photosResultListenr = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotPhoto> result) {
            images = result;
            changeUiAccrodingPosition(position);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT, false);
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
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logoutFromAllAccounts();
                    LoginActivity.showForResult(ImageSliderActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, true);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
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
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logoutFromAllAccounts();
                    LoginActivity.showForResult(ImageSliderActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_DELETE);
                    break;
                case DATA_ALREADY_EXIST_409:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.sorry, R.string.cant_delete_all_photos, false);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, true);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
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
        materialDialog = Helpers.getMaterialDialog(this);
        images = DDScannerApplication.getInstance().getDiveSpotPhotosContainer().getPhotos();
        position = getIntent().getIntExtra("position", 0);
        sourceId = getIntent().getStringExtra("sourceId");
        photoOpenedSource = (PhotoOpenedSource) getIntent().getSerializableExtra("source");
        viewPager.addOnPageChangeListener(this);
        sliderImagesAdapter = new SliderImagesAdapter(getFragmentManager(), images);
        viewPager.setAdapter(sliderImagesAdapter);
        close.setOnClickListener(this);
        changeUiAccrodingPosition(position);
        setUi();

    }

    private void changeUiAccrodingPosition(final int position) {
        this.position = position;
        if (images == null) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        likesCount.setText(images.get(position).getLikesCount());
        photosCount.setText(DDScannerApplication.getInstance().getString(R.string.slider_photos_count_pattern, String.valueOf(position + 1), String.valueOf(images.size())));
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId().equals(images.get(position).getAuthor().getId())) {
            likesLayout.setOnClickListener(null);
        } else {
            likesLayout.setOnClickListener(this);
        }
        if (images.get(position).isLiked()) {
            likeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like_photo_full));
        } else {
            likeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like_photo_empty));
        }
        userName.setText(images.get(position).getAuthor().getName());
        date.setText(Helpers.convertDateToImageSliderActivity(images.get(position).getDate()));
        Picasso.with(this).load(getString(R.string.base_photo_url, images.get(position).getAuthor().getPhoto(), "1")).resize(Math.round(Helpers.convertDpToPixel(35, this)), Math.round(Helpers.convertDpToPixel(35, this))).centerCrop().placeholder(R.drawable.gray_circle_placeholder).error(R.drawable.avatar_profile_default).transform(new CropCircleTransformation()).into(avatar);
        if (!images.get(position).getAuthor().getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
            options.setOnClickListener(view -> showReportMenu(options, position));
        } else {
            options.setOnClickListener(view -> showDeleteMenu(options));
        }
    }

    private void findViews() {
        userDataLayout = (LinearLayout) findViewById(R.id.user_data);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        topLayout = (RelativeLayout) findViewById(R.id.top_layout);
        title = (TextView) findViewById(R.id.title);
        photosCount = (TextView) findViewById(R.id.images_count);
        likesLayout = (RelativeLayout) findViewById(R.id.likes_layout);
        likeIcon = (ImageView) findViewById(R.id.icon);
        likesCount = (TextView) findViewById(R.id.likes_count);
        viewPager = (SliderViewPager) findViewById(R.id.image_slider);
        close = (ImageView) findViewById(R.id.close_btn);
        baseLayout = (FrameLayout) findViewById(R.id.swipe_layout);
        avatar = (ImageView) findViewById(R.id.user_avatar);
        date = (TextView) findViewById(R.id.date);
        userName = (TextView) findViewById(R.id.user_name);
        options = (ImageView) findViewById(R.id.options);
    }

    private void setUi() {
        switch (photoOpenedSource) {
            case ALL:
                title.setText(R.string.slider_title_all);
                break;
            case NOTIFICATION:
            case DIVESPOT:
                title.setText(R.string.slider_title_dive_spot);
                break;
            case REVIEW:
                title.setText(R.string.slider_title_reiews);
                break;
            case REVIEWSLIST:
                title.setText(R.string.slider_title_review);
                break;
            case REVIEWS:
                title.setText(R.string.slider_title_review);
                break;
            case PROFILE:
                title.setText(R.string.slider_title_profile);
                break;
            case MAPS:
                title.setText(R.string.slider_title_maps);
                break;
        }
        photosCount.setText(DDScannerApplication.getInstance().getString(R.string.slider_photos_count_pattern, String.valueOf(position + 1), String.valueOf(images.size())));
        options.setVisibility(View.VISIBLE);
        viewPager.setCurrentItem(position);
        if (photoOpenedSource.equals(PhotoOpenedSource.MAPS)) {
            likesLayout.setVisibility(View.GONE);
        }
        likesLayout.setOnClickListener(this);
        userDataLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_btn:
                onBackPressed();
                break;
            case R.id.likes_layout:
                likesLayout.setOnClickListener(null);
                if (!images.get(position).getAuthor().getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
                    if (!images.get(position).isLiked()) {
                        likeUi();
                        DDScannerApplication.getInstance().getDdScannerRestClient(this).postLikePhoto(images.get(position).getId(), likeResultListener);
                        break;
                    }
                    dislikeUi();
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).postDislikePhoto(images.get(position).getId(), dislikeResultListener);
                }
                break;
            case R.id.user_data:
                if (!images.get(position).getAuthor().getId().equals(DDScannerApplication.getInstance().getString(R.string.dds_server_id))) {
                    UserProfileActivity.show(this, images.get(position).getAuthor().getId(), images.get(position).getAuthor().getType());
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    private void imageDeleted(int position) {
        deletedPhotos.add(images.get(position));
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
            Intent intent = new Intent();
            intent.putExtra("deletedImages", deletedPhotos);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onPageSelected(int position) {
        changeUiAccrodingPosition(position);
    }

    private void showDeleteMenu(View view) {
        deleteImageName = images.get(position).getId();
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
        reportName = images.get(position).getId();
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_photo_report, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemsClickListener(reportName));
        popup.show();
    }

    public static void showForResult(Activity context, ArrayList<DiveSpotPhoto> images, int position, int requestCode, PhotoOpenedSource photoOpenedSource, String sourceId) {
        Intent intent = new Intent(context, ImageSliderActivity.class);
        intent.putParcelableArrayListExtra("IMAGES", images);
        intent.putExtra("position", position);
        intent.putExtra("source", photoOpenedSource);
        intent.putExtra("sourceId", sourceId);
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

    @Override
    protected void onStart() {
        super.onStart();
//        DDScannerApplication.bus.register(this);
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
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postDeleteImage(images.get(position).getId(), deleteImageRequestistener);

    }

    @Override
    public void onBackPressed() {
        if (isChanged) {
            Intent intent = new Intent();
            intent.putExtra("deletedImages", deletedPhotos);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        DDScannerApplication.bus.unregister(this);
        DDScannerApplication.getInstance().getDiveSpotPhotosContainer().setPhotos(images);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    reportImage(reportName, reportType, reportDescription);
                    switch (photoOpenedSource) {
                        case DIVESPOT:
                        case REVIEWS:
                        case ALL:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotPhotos(sourceId, imagesResulListener);
                            break;
                        case REVIEW:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getReviewPhotos(photosResultListenr, sourceId);
                            break;
                        case PROFILE:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserAddedPhotos(photosResultListenr, sourceId);
                            break;
                    }

                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_DELETE:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    deleteImage(deleteImageName);
                    switch (photoOpenedSource) {
                        case DIVESPOT:
                        case REVIEWS:
                        case ALL:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotPhotos(sourceId, imagesResulListener);
                            break;
                        case REVIEW:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getReviewPhotos(photosResultListenr, sourceId);
                            break;
                        case PROFILE:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserAddedPhotos(photosResultListenr, sourceId);
                            break;
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_LIKE:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    switch (photoOpenedSource) {
                        case DIVESPOT:
                        case REVIEWS:
                        case ALL:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotPhotos(sourceId, imagesResulListener);
                            break;
                        case REVIEW:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getReviewPhotos(photosResultListenr, sourceId);
                            break;
                        case PROFILE:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserAddedPhotos(photosResultListenr, sourceId);
                            break;
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reportImage(String imageName, int reportType, String reportDescription) {
        isChanged = true;
        materialDialog.show();
        EventsTracker.trackPhotoReport();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postReportImage(reportImageRequestListener, new ReportImageRequest(imageName, reportType, reportDescription));
    }


    public void showReportDialog() {
        ArrayList<String> objects = Helpers.getReportTypes();
        new MaterialDialog.Builder(this)
                .title("Report")
                .items(objects)
                .itemsCallback((dialog, view, which, text) -> {
                    reportType = Helpers.getReportTypes().indexOf(text) + 1;
                    if (text.equals("Other")) {
                        showOtherReportDialog();
                        dialog.dismiss();
                    } else {
                        reportImage(reportName, reportType, null);
                    }
                })
                .show();
    }

    private void showOtherReportDialog() {
        WriteReportReasonDialog writeReportReasonDialog = new WriteReportReasonDialog();
        writeReportReasonDialog.show(getSupportFragmentManager(), "");
//        new MaterialDialog.Builder(this)
//                .title("Other")
//                .inputRange(1, Integer.MAX_VALUE)
//                .positiveColor(ContextCompat.getColor(this, R.color.black_text))
//                .widgetColor(ContextCompat.getColor(this, R.color.accent))
//                .input("Write reason", "", (dialog, input) -> {
////                    if (input.toString().trim().length() > 1) {
//                        reportImage(reportName, 7, input.toString());
//                        reportDescription = input.toString();
////                    } else {
////                        Toast.makeText(ImageSliderActivity.this, "Write a reason", Toast.LENGTH_SHORT).show();
////                    }
//                })
//                .onPositive((materialDialog, dialogAction) -> {
//
//                }).show();
    }

    @Override
    public void onReasonWriten(String reason) {
        reportImage(reportName, 7, reason);
        reportDescription = reason;
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT:
            case DialogsRequestCodes.DRC_IMAGE_SLIDER_ACTIVITY_CONNECTION_FAILURE_GET_REPORT_TYPES:
                setResult(RESULT_OK);
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

    @Subscribe
    public void changeLayoutStates(HidePhotoInfoEvent event) {
        if (bottomLayout.getVisibility() == View.VISIBLE) {
            hideLayouts();
        } else {
            showLayouts();
            topLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideLayouts() {
        bottomLayout.animate()
                .translationY(bottomLayout.getHeight())
                .alpha(0.0f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bottomLayout.setVisibility(View.GONE);
                    }
                });

        topLayout.animate()
                .translationY(-topLayout.getHeight())
                .alpha(0.0f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        topLayout.setVisibility(View.GONE);
                    }
                });
    }

    private void showLayouts() {
        bottomLayout.setVisibility(View.VISIBLE);
        bottomLayout.setAlpha(0.0f);
        topLayout.setVisibility(View.VISIBLE);
        topLayout.setAlpha(0.0f);
        bottomLayout.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bottomLayout.setVisibility(View.VISIBLE);
                    }
                });

        topLayout.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        topLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Subscribe
    public void dismissActivity(ReloadSliderImagesEvent event) {
        finish();
    }

    private class LikeDislikeResultListener extends DDScannerRestClient.ResultListener<Void> {

        private boolean isLike;

        LikeDislikeResultListener(boolean isLike) {
            this.isLike = isLike;
        }

        @Override
        public void onSuccess(Void result) {
            isLikeRequestStarted = false;
            likesLayout.setOnClickListener(ImageSliderActivity.this::onClick);
        }

        @Override
        public void onConnectionFailure() {
            isLikeRequestStarted = false;
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
            if (isLike) {
                dislikeUi();
                return;
            }
            likeUi();
            likesLayout.setOnClickListener(ImageSliderActivity.this::onClick);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            isLikeRequestStarted = false;
            if (isLike) {
                dislikeUi();
            } else {
                likeUi();
            }
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(ImageSliderActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_LIKE);
                    break;
                default:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
                    break;
            }
            likesLayout.setOnClickListener(ImageSliderActivity.this::onClick);
        }

        @Override
        public void onInternetConnectionClosed() {
            likesLayout.setOnClickListener(ImageSliderActivity.this::onClick);
            isLikeRequestStarted = false;
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    }


}

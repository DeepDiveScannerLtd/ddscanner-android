package com.ddscanner.ui.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Checkins;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.DiveSpotDetails;
import com.ddscanner.entities.DiveSpotFull;
import com.ddscanner.entities.Image;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.User;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.entities.request.ValidationReguest;
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.ddscanner.events.UnknownErrorCatchedEvent;
import com.ddscanner.rest.BaseCallbackOld;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.DiveSpotsPhotosAdapter;
import com.ddscanner.ui.adapters.EditorsListAdapter;
import com.ddscanner.ui.adapters.SealifeListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DiveSpotDetailsActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener, InfoDialogFragment.DialogClosedListener {

    private static final String TAG = DiveSpotDetailsActivity.class.getName();

    private static final String EXTRA_ID = "ID";

    private DiveSpotDetails diveSpotDetails;
    private ProgressDialog progressDialog;
    private String diveSpotId;
    private LatLng diveSpotCoordinates;
    private boolean isCheckedIn = false;
    private DiveSpotFull diveSpot;

    /*Ui*/
    private TextView diveSpotName;
    private LinearLayout rating;
    private LinearLayout informationLayout;
    private TextView diveSpotDescription;
    private ImageView diveSpotMainPhoto;
    private ProgressView progressBar;
    private ProgressView progressBarFull;
    private RecyclerView photosRecyclerView;
    private RecyclerView sealifeRecyclerView;
    private MapFragment mapFragment;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private LinearLayout mapLayout;
    private TextView object;
    private TextView level;
    private TextView depth;
    private TextView visibility;
    private TextView currents;
    private TextView access;
    private RelativeLayout checkInPeoples;
    private TextView showMore;
    private RatingBar ratingBar;
    private FloatingActionButton btnCheckIn;
    private Button showAllReviews;
    private LinearLayout isInfoValidLayout;
    private LinearLayout thanksLayout;
    private LinearLayout photos;
    private RelativeLayout accessLayout;
    private Button btnDsDetailsIsValid;
    private Button btnDsDetailsIsInvalid;
    private Button showDiveCenters;
    private RelativeLayout creatorLayout;
    private TextView numberOfCheckinPeoplesHere;
    private TextView creatorName;
    private TextView newDiveSpotView;
    private ImageView creatorAvatar;
    private ImageButton btnAddPhoto;
    private ImageView expandEditorsArrow;
    private Menu menu;
    private List<User> creatorsEditorsList = new ArrayList<>();

    private RelativeLayout editorsWrapperView;
    private RecyclerView editorsRecyclerView;
    private MaterialDialog materialDialog;

    private boolean isCLickedFavorite = false;
    private boolean isClickedCHeckin = false;
    private boolean isClickedCheckOut = false;
    private boolean isClickedYesValidation = false;
    private boolean isClickedNoValidation = false;
    private boolean isFavorite = false;
    private boolean isClickedRemoveFromFavorites = false;
    private boolean isClickedEdit = false;
    private boolean isNewDiveSpot = false;
    private LinearLayout serveConnectionErrorLayout;
    private Button btnRefreshLayout;
    private CoordinatorLayout mainLayout;
    private ImageView checkinsArrow;

    private List<Comment> usersComments;
    private List<User> usersCheckins;

    private boolean editorsListExpanded;

    private DDScannerRestClient.ResultListener<DiveSpotDetails> diveSpotDetailsResultListener = new DDScannerRestClient.ResultListener<DiveSpotDetails>() {
        @Override
        public void onSuccess(DiveSpotDetails result) {
            diveSpotDetails = result;
            diveSpotCoordinates = new LatLng(diveSpotDetails.getDivespot().getLat(),
                    diveSpotDetails.getDivespot().getLng());
            usersComments = diveSpotDetails.getComments();
            setUi();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_dive_spot_not_found, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error_title, DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
            }
        }
    };

    private DDScannerRestClient.ResultListener<Void> checkInResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            getCheckins();
            EventsTracker.trackCheckIn(EventsTracker.CheckInStatus.SUCCESS);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
            checkoutUi();
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            checkoutUi();
            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_message_dive_spot_not_found);
                    break;
                case USER_NOT_FOUND_ERROR_C801:
                    isClickedCHeckin = true;
                    showLoginActivity();
                    break;
                case BAD_REQUEST_ERROR_400:
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_already_checked_in, false);
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
            }
        }
    };

    private DDScannerRestClient.ResultListener<Void> checkOutResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            getCheckins();
            EventsTracker.trackCheckOut();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
            checkInUi();
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            checkInUi();
            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_message_dive_spot_not_found);
                    break;
                case USER_NOT_FOUND_ERROR_C801:
                    isClickedCheckOut = true;
                    showLoginActivity();
                    break;
                case BAD_REQUEST_ERROR_400:
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_already_checked_out, false);
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
            }
        }
    };

    /**
     * Show current activity from another place of app
     *
     * @param context
     * @param id
     * Andrei Lashkevich
     */
    public static void show(Context context, String id, EventsTracker.SpotViewSource spotViewSource) {
        if (spotViewSource != null) {
            EventsTracker.trackDiveSpotView(id, spotViewSource);
        }

        Intent intent = new Intent(context, DiveSpotDetailsActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    public static void showNewDiveSpot(Context context, String id) {
        Intent intent = new Intent(context, DiveSpotDetailsActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(Constants.DIVE_SPOT_DETAILS_ACTIVITY_EXTRA_IS_FROM_AD_DIVE_SPOT, true);
        intent.putExtra(Constants.IS_HAS_INTERNET, true);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_spot_details);
        findViews();
        toolbarSettings();
        isNewDiveSpot = getIntent().getBooleanExtra(Constants.DIVE_SPOT_DETAILS_ACTIVITY_EXTRA_IS_FROM_AD_DIVE_SPOT, false);
        diveSpotId = getIntent().getStringExtra(EXTRA_ID);
        DDScannerApplication.getDdScannerRestClient().getDiveSpotDetails(diveSpotId, diveSpotDetailsResultListener);
    }

    /**
     * Find views in activity
     *
     * Andrei Lashkevich
     */

    private void findViews() {
        checkinsArrow = (ImageView) findViewById(R.id.checkins_arrow);
        serveConnectionErrorLayout = (LinearLayout) findViewById(R.id.server_error);
        btnRefreshLayout =(Button) findViewById(R.id.button_refresh);
        mainLayout = (CoordinatorLayout) findViewById(R.id.main_layout);
        photos = (LinearLayout) findViewById(R.id.photos);
        diveSpotName = (TextView) findViewById(R.id.dive_spot_name);
        rating = (LinearLayout) findViewById(R.id.stars);
        informationLayout = (LinearLayout) findViewById(R.id.informationLayout);
        diveSpotDescription = (TextView) findViewById(R.id.dive_place_description);
        diveSpotMainPhoto = (ImageView) findViewById(R.id.main_photo);
        progressBar = (ProgressView) findViewById(R.id.progressBar);
        progressBarFull = (ProgressView) findViewById(R.id.progressBarFull);
        photosRecyclerView = (RecyclerView) findViewById(R.id.photos_rc);
        sealifeRecyclerView = (RecyclerView) findViewById(R.id.sealife_rc);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_map_fragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mapLayout = (LinearLayout) findViewById(R.id.map_layout);
        object = (TextView) findViewById(R.id.object);
        level = (TextView) findViewById(R.id.level);
        depth = (TextView) findViewById(R.id.depth);
        visibility = (TextView) findViewById(R.id.visibility);
        currents = (TextView) findViewById(R.id.currents);
        checkInPeoples = (RelativeLayout) findViewById(R.id.check_in_peoples);
        showMore = (TextView) findViewById(R.id.showmore);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        btnCheckIn = (FloatingActionButton) findViewById(R.id.fab_checkin);
        btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        showAllReviews = (Button) findViewById(R.id.btn_show_all_reviews);
        isInfoValidLayout = (LinearLayout) findViewById(R.id.is_info_valid_layout);
        thanksLayout = (LinearLayout) findViewById(R.id.thanks_layout);
        btnDsDetailsIsValid = (Button) findViewById(R.id.yes_button);
        btnDsDetailsIsInvalid = (Button) findViewById(R.id.no_button);
        creatorLayout = (RelativeLayout) findViewById(R.id.creator);
        numberOfCheckinPeoplesHere = (TextView) findViewById(R.id.number_of_checking_people);
        creatorName = (TextView) findViewById(R.id.creator_name);
        creatorAvatar = (ImageView) findViewById(R.id.creator_avatar);
        editorsRecyclerView = (RecyclerView) findViewById(R.id.editors);
        editorsWrapperView = (RelativeLayout) findViewById(R.id.editors_wrapper);
        expandEditorsArrow = (ImageView) findViewById(R.id.expand_editors_arrow);
        showDiveCenters = (Button) findViewById(R.id.button_show_divecenters);
        access = (TextView) findViewById(R.id.access);
        accessLayout = (RelativeLayout) findViewById(R.id.acces_layout);
        newDiveSpotView = (TextView) findViewById(R.id.newDiveSpot);
        btnAddPhoto = (ImageButton) findViewById(R.id.btn_add_photo);
    }

    /**
     * Create toolbar ui
     *
     * Andrei Lashkevich
     */

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle("");
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(this, android.R.color.transparent));
        //  collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.transparent));
    }

    /**
     * Set ui data at current activity
     *
     * Andrei Lashkevich
     */

    private void setUi() {
        int avatarImageRadius = (int) getResources().getDimension(R.dimen.editor_avatar_radius);
        rating.removeAllViews();
        int avatarImageSize = 2 * avatarImageRadius;
        btnRefreshLayout.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
        materialDialog = Helpers.getMaterialDialog(this);
        btnDsDetailsIsInvalid.setOnClickListener(this);
        showDiveCenters.setOnClickListener(this);
        btnDsDetailsIsValid.setOnClickListener(this);
        btnCheckIn.setOnClickListener(this);
        photos.setOnClickListener(this);
     //   creatorLayout.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(this);
        //checkInPeoples.setOnClickListener(this);
        showMore.setOnClickListener(this);
        showAllReviews.setOnClickListener(this);
        diveSpot = diveSpotDetails.getDivespot();
        isFavorite = diveSpot.getIsFavorite();
        updateMenuItems(menu, isFavorite);
        if (diveSpotDetails.getComments() != null) {
            showAllReviews.setText(getString(R.string.show_all, String.valueOf(diveSpotDetails.getComments().size())));
        } else {
            showAllReviews.setText(R.string.write_review);
        }
        if (diveSpot.getAccess() != null) {
            access.setText(diveSpot.getAccess());
        } else {
            accessLayout.setVisibility(View.GONE);
        }
        if (diveSpot.getStatus().equals("waiting")) {
            newDiveSpotView.setVisibility(View.VISIBLE);
        }
        object.setText(diveSpot.getObject());
        level.setText(diveSpot.getLevel());
        depth.setText(diveSpot.getDepth());
        visibility.setText(getString(R.string.visibility_pattern, diveSpot.getVisibilityMin(), diveSpot.getVisibilityMax()));
        currents.setText(diveSpot.getCurrents());
        if (diveSpot.getImages() != null && !diveSpot.getImages().isEmpty()) {
            Picasso.with(this).load(diveSpot.getDiveSpotPathMedium() + diveSpot.getImages().get(0).getName()).into(diveSpotMainPhoto, new ImageLoadedCallback(progressBar) {
                @Override
                public void onSuccess() {
                    super.onSuccess();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            diveSpotMainPhoto.setImageResource(R.drawable.ds_head_photo_default);
        }
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(diveSpot.getName());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
        diveSpotName.setText(diveSpot.getName());
        diveSpotDescription.post(new Runnable() {
            @Override
            public void run() {
                diveSpotDescription.setText(diveSpot.getDescription());
                if (diveSpotDescription.getLineCount() > 3) {
                    diveSpotDescription.setMaxLines(3);
                    diveSpotDescription.setEllipsize(TextUtils.TruncateAt.END);
                    showMore.setVisibility(View.VISIBLE);
                }
            }
        });
        for (int k = 0; k < Math.round(diveSpot.getRating()); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_ds_star_full);
            iv.setPadding(0, 0, 5, 0);
            rating.addView(iv);
        }
        for (int k = 0; k < 5 - Math.round(diveSpot.getRating()); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_ds_star_empty);
            iv.setPadding(0, 0, 5, 0);
            rating.addView(iv);
        }

        if (diveSpot.getImages() != null) {
            btnAddPhoto.setVisibility(View.GONE);
            photosRecyclerView.setVisibility(View.VISIBLE);
        }

        photosRecyclerView.setLayoutManager(new GridLayoutManager(DiveSpotDetailsActivity.this, 4));
        //      photosRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4));
        photosRecyclerView.setAdapter(new DiveSpotsPhotosAdapter((ArrayList<Image>) diveSpot.getImages(),
                diveSpot.getDiveSpotPathMedium(), DiveSpotDetailsActivity.this, (ArrayList<Image>) diveSpot.getCommentImages()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(DiveSpotDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        sealifeRecyclerView.setNestedScrollingEnabled(false);
        sealifeRecyclerView.setHasFixedSize(false);
        sealifeRecyclerView.setLayoutManager(layoutManager);
        sealifeRecyclerView.setAdapter(new SealifeListAdapter(
                (ArrayList<Sealife>) diveSpotDetails.getSealifes(),
                this, diveSpot.getSealifePathMedium(),
                diveSpot.getSealifePathMedium()));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                workWithMap(googleMap);
            }
        });
        if (diveSpot.getCreator() != null) {
            creatorsEditorsList.add(diveSpot.getCreator());
            creatorLayout.setVisibility(View.VISIBLE);
            Picasso.with(this).load(diveSpot.getCreator().getPicture())
                    .resize(Math.round(Helpers.convertDpToPixel(avatarImageSize, this)), Math.round(Helpers.convertDpToPixel(avatarImageSize, this)))
                    .centerCrop()
                    .transform(new CropCircleTransformation()).into(creatorAvatar);
            if (diveSpot.getCreator().getAuthor() != null && diveSpot.getCreator().getAuthor().equals("social")) {
                creatorLayout.setOnClickListener(this);
                expandEditorsArrow.setVisibility(View.VISIBLE);
            }
            creatorName.setText(diveSpot.getCreator().getName());
        } else {
            creatorName.setText("DDScanner");
            creatorLayout.setVisibility(View.VISIBLE);
            Picasso.with(this).load(R.drawable.avatar_profile_dds)
                    .resize(Math.round(Helpers.convertDpToPixel(avatarImageSize, this)), Math.round(Helpers.convertDpToPixel(avatarImageSize, this)))
                    .centerCrop()
                    .placeholder(R.drawable.avatar_profile_default)
                    .transform(new CropCircleTransformation()).into(creatorAvatar);
            User user = new User("DDScanner", String.valueOf(R.drawable.avatar_profile_dds));
            creatorsEditorsList.add(user);
        }

        progressBarFull.setVisibility(View.GONE);
        informationLayout.setVisibility(View.VISIBLE);
        if (diveSpot.getCheckin()) {
            checkInUi();
        } else {
            checkoutUi();
        }
        if (diveSpot.getValidation() != null) {
            if (!diveSpot.getValidation()) {
                isInfoValidLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        isInfoValidLayout.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                if (menu != null && menu.findItem(R.id.edit_dive_spot) != null) {
                    menu.findItem(R.id.edit_dive_spot).setVisible(false);
                }
            }
        }

        if (diveSpotDetails.getCheckins() != null) {
            checkinsArrow.setVisibility(View.GONE);
            usersCheckins = diveSpotDetails.getCheckins();
            if (usersCheckins.size() == 1) {
                setCheckinsCountPeople(String.valueOf(usersCheckins.size()) + " " +
                        getString(R.string.one_person_checked_in), false);
            } else {
                setCheckinsCountPeople(String.valueOf(usersCheckins.size()) + " " +
                        getString(R.string.peoples_checked_in_here), false);
            }
        }
        if (diveSpotDetails.getEditors() != null) {
            for (User user : diveSpotDetails.getEditors()) {
                creatorsEditorsList.add(user);
            }
            expandEditorsArrow.setVisibility(View.VISIBLE);
            creatorLayout.setOnClickListener(this);
           // EditorsListActivity.show(DiveSpotDetailsActivity.this, (ArrayList<User>) creatorsEditorsList);

        }
        showDiveCenters.setVisibility(View.VISIBLE);
    }

    private void setCheckinsCountPeople(String count, boolean isNull) {
        if (isNull) {
            checkinsArrow.setVisibility(View.GONE);
            checkInPeoples.setOnClickListener(null);
        } else {
            checkinsArrow.setVisibility(View.VISIBLE);
            checkInPeoples.setOnClickListener(this);
        }
        numberOfCheckinPeoplesHere.setText(count);

    }

    private void setReviewsCount(String count) {
        showAllReviews.setText(count);
    }

    /**
     * Handling map click events
     *
     * @param googleMap
     * Andrei Lashkevich
     */
    private void workWithMap(GoogleMap googleMap) {
        // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds))
//                .position(diveSpotCoordinates));
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_ds)))
                .position(diveSpotCoordinates));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(diveSpotCoordinates, 7.0f));
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
                intent.putExtra("LATLNG", diveSpotCoordinates);
                startActivity(intent);
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
                intent.putExtra("LATLNG", diveSpotCoordinates);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_refresh:
                mainLayout.setVisibility(View.VISIBLE);
                serveConnectionErrorLayout.setVisibility(View.GONE);
                DDScannerApplication.getDdScannerRestClient().getDiveSpotDetails(diveSpotId, diveSpotDetailsResultListener);
                break;
            case R.id.map_layout:
                Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
                intent.putExtra("LATLNG", new LatLng(diveSpotDetails.getDivespot().getLat(), diveSpotDetails.getDivespot().getLng()));
                startActivity(intent);
                break;
            case R.id.check_in_peoples:
                EventsTracker.trackDiveSpotCheckinsView();
                CheckInPeoplesActivity.show(DiveSpotDetailsActivity.this, (ArrayList<User>) usersCheckins);
                break;
            case R.id.showmore:
                showMore.setVisibility(View.GONE);
                diveSpotDescription.setMaxLines(10000);
                diveSpotDescription.setEllipsize(null);
                break;
            case R.id.fab_checkin:
                if (isCheckedIn) {
                    checkOut();
                } else {
                    showCheckInDialog();
                }
                break;
            case R.id.btn_show_all_reviews:
                if (diveSpotDetails.getComments() != null || usersComments != null) {
                    EventsTracker.trackDeviSpotReviewsView();
                    Intent reviewsIntent = new Intent(DiveSpotDetailsActivity.this, ReviewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("COMMENTS", (ArrayList<Comment>) usersComments);
                    bundle.putString(Constants.DIVESPOTID, String.valueOf(diveSpotDetails.getDivespot().getId()));
                    bundle.putString("PATH", diveSpotDetails.getDivespot().getDiveSpotPathMedium());
                    reviewsIntent.putExtras(bundle);
                    startActivityForResult(reviewsIntent, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_REVIEWS);
                } else {
                    LeaveReviewActivity.showForResult(this, String.valueOf(diveSpot.getId()), 0f, EventsTracker.SendReviewSource.FROM_EMPTY_REVIEWS_LIST, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LEAVE_REVIEW);
                }
                break;
            case R.id.yes_button:
                diveSpotValidation(true);
                break;
            case R.id.no_button:
                showEditDiveSpotDialog();
                break;
            case R.id.creator:
                EditorsListActivity.show(DiveSpotDetailsActivity.this, (ArrayList<User>) creatorsEditorsList);
                break;
            case R.id.button_show_divecenters:
                DiveCentersActivity.show(this, new LatLng(diveSpot.getLat(), diveSpot.getLng()), diveSpot.getName());
                break;
            case R.id.photos:
                DDScannerApplication.bus.post(new OpenPhotosActivityEvent());
                break;
            case R.id.btn_add_photo:
                if (checkReadStoragePermission(this)) {
                    addPhotosToDiveSpot();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.DIVE_SPOT_DETAILS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE);
                }
                break;
        }
    }

    public boolean checkReadStoragePermission(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.DIVE_SPOT_DETAILS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPhotosToDiveSpot();
                } else {
                    Toast.makeText(DiveSpotDetailsActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void addPhotosToDiveSpot() {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            SocialNetworks.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_PICK_PHOTOS);
        } else {
            MultiImageSelector.create(this).count(3).start(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PICK_PHOTOS);
        }
    }

    private void tryToCallEditDiveSpotActivity() {
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            Intent editDiveSpotIntent = new Intent(DiveSpotDetailsActivity.this,
                    EditDiveSpotActivity.class);
            editDiveSpotIntent
                    .putExtra(Constants.DIVESPOTID, String.valueOf(diveSpot.getId()));
            startActivityForResult(editDiveSpotIntent, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_EDIT_DIVE_SPOT);
        } else {
            isClickedEdit = true;
            showLoginActivity();
        }
    }

    private void showEditDiveSpotDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(this)
                .title(R.string.edit)
                .content(R.string.question_edit_dive_spot)
                .positiveText(R.string.btn_yes)
                .positiveColor(ContextCompat.getColor(this, R.color.primary))
                .negativeColor(ContextCompat.getColor(this, R.color.primary))
                .negativeText(R.string.no_just_vote_answer)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        tryToCallEditDiveSpotActivity();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        diveSpotValidation(false);
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void showCheckInDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_title_check_in)
                .content(R.string.dialog_check_in_content)
                .positiveText(R.string.ok)
                .positiveColor(ContextCompat.getColor(this, R.color.primary))
                .negativeColor(ContextCompat.getColor(this, R.color.primary))
                .negativeText(R.string.dialog_cancel)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        EventsTracker.trackCheckIn(EventsTracker.CheckInStatus.CANCELLED);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        checkIn();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        dialog.dismiss();
                        EventsTracker.trackCheckIn(EventsTracker.CheckInStatus.CANCELLED);
                    }
                });
        dialog.show();
    }

    private void checkIn() {
        checkInUi();
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            checkoutUi();
            isClickedCHeckin = true;
            showLoginActivity();
            return;
        }
        DDScannerApplication.getDdScannerRestClient().postCheckIn(diveSpotId, checkInResultListener);
    }

    private void checkInUi() {
        btnCheckIn.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                DiveSpotDetailsActivity.this, R.drawable.ic_acb_pin_checked));
        btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        isCheckedIn = true;
    }

    private void checkoutUi() {
        btnCheckIn.setImageDrawable(AppCompatDrawableManager.get().getDrawable(DiveSpotDetailsActivity.this, R.drawable.ic_acb_pin));
        btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange)));
        isCheckedIn = false;
    }

    private void checkOut() {
        checkoutUi();
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            checkInUi();
            isClickedCheckOut = true;
            showLoginActivity();
            return;
        }
        DDScannerApplication.getDdScannerRestClient().postCheckOut(diveSpotId, checkOutResultListener);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        LeaveReviewActivity.showForResult(this, String.valueOf(diveSpot.getId()), rating, EventsTracker.SendReviewSource.FROM_RATING_BAR, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LEAVE_REVIEW);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.favorite:
                if (!isFavorite) {
                    addDiveSpotToFavorites();
                } else {
                    removeFromFavorites(String.valueOf(diveSpot.getId()));
                }
                break;
            case R.id.edit_dive_spot:
                tryToCallEditDiveSpotActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void diveSpotValidation(final boolean isValid) {
        materialDialog.show();
        ValidationReguest validationReguest = new ValidationReguest();
        validationReguest.setAppId(Helpers.getRegisterRequest().getAppId());
        validationReguest.setToken(Helpers.getRegisterRequest().getToken());
        validationReguest.setSocial(Helpers.getRegisterRequest().getSocial());
        if (SharedPreferenceHelper.getSn().equals("tw")) {
            validationReguest.setSecret(Helpers.getRegisterRequest().getSecret());
        }
        validationReguest.setValid(isValid);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance()
                .divespotValidation(String.valueOf(diveSpotDetails.getDivespot().getId()),
                        validationReguest);
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                materialDialog.dismiss();
                if (response.isSuccessful()) {
                    if (menu != null && menu.findItem(R.id.edit_dive_spot) != null) {
                        menu.findItem(R.id.edit_dive_spot).setVisible(false);
                    }
                    isInfoValidLayout.setVisibility(View.GONE);
                    thanksLayout.setVisibility(View.VISIBLE);
                    hideThanksLayout();
                    if (isValid) {
                        EventsTracker.trackDiveSpotValid();
                    } else {
                        EventsTracker.trackDiveSpotInvalid();
                    }
                } else {
                    if (response.raw().code() == 422) {
                        String error = "";
                        try {
                            error = response.errorBody().string();
                            if (Helpers.checkIsErrorByLogin(error)) {
                                if (isValid) {
                                    isClickedYesValidation = true;
                                    isClickedNoValidation = false;
                                } else {
                                    isClickedNoValidation = true;
                                    isClickedYesValidation = false;
                                }
                                showLoginActivity();
                                return;
                            }
                        } catch (IOException e) {
                        }
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(DiveSpotDetailsActivity.this);
            }
        });
    }

    private void hideThanksLayout() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               thanksLayout.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void addDiveSpotToFavorites() {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            isCLickedFavorite = true;
            showLoginActivity();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addDiveSpotToFavourites(
                String.valueOf(diveSpot.getId()),
                Helpers.getRegisterRequest()
        );
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().code() == 200) {
                    isFavorite = true;
                    updateMenuItems(menu, isFavorite);
                    Toast.makeText(DiveSpotDetailsActivity.this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
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
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.also_added_to_favorites);
                    } catch (ValidationErrorException e) {
                        // TODO Handle

                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        isCLickedFavorite = true;
                        showLoginActivity();
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(DiveSpotDetailsActivity.this);
            }
        });
    }

    private void removeFromFavorites(String id) {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            isCLickedFavorite = true;
            showLoginActivity();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance()
                .removeSpotFromFavorites(id, Helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().code() == 200) {
                    isFavorite = false;
                    updateMenuItems(menu, isFavorite);
                    Toast.makeText(DiveSpotDetailsActivity.this, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
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
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle

                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        isClickedRemoveFromFavorites = true;
                        showLoginActivity();
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(DiveSpotDetailsActivity.this);
            }
        });
    }

    private void getCheckins() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getCheckins(String.valueOf(diveSpot.getId()));
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                if (response.isSuccessful()) {
                    Checkins checkins;
                    try {
                        responseString = response.body().string();
                        checkins = new Gson().fromJson(responseString, Checkins.class);
                        if (checkins.getCheckins() != null) {
                            usersCheckins = checkins.getCheckins();
                            if (usersCheckins.size() == 1) {
                                setCheckinsCountPeople(String.valueOf(usersCheckins.size()) + " " +
                                        getString(R.string.one_person_checked_in), false);
                            } else {
                                setCheckinsCountPeople(String.valueOf(usersCheckins.size()) + " " +
                                        getString(R.string.peoples_checked_in_here), false);
                            }
                        } else {
                            setCheckinsCountPeople(getString(R.string.no_one_has_checked_in_here), true);
                        }
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(DiveSpotDetailsActivity.this);
            }
        });
    }

    private void getComments() {

        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance()
                .getComments(String.valueOf(diveSpot.getId()), Helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                if (response.isSuccessful()) {
                    try {
                        usersComments = null;
                        responseString = response.body().string();
                        Comments comments = new Gson().fromJson(responseString, Comments.class);
                        if (comments.getComments() != null) {
                            usersComments = comments.getComments();
                        }
                        if (usersComments != null && usersComments.size() > 0) {
                            setReviewsCount(getString(R.string.show_all,String.valueOf(usersComments.size())));
                        } else {
                            setReviewsCount(getString(R.string.write_review));
                        }
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(DiveSpotDetailsActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LEAVE_REVIEW:
                if (resultCode == RESULT_OK) {
                    if (diveSpot != null) {
                        getComments();
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_REVIEWS:
                if (resultCode == RESULT_OK) {
                    if (diveSpot != null) {
                        getComments();
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PHOTOS:
                if (resultCode == RESULT_OK) {
                    refreshActivity();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_EDIT_DIVE_SPOT:
                if (resultCode == RESULT_OK) {
                    refreshActivity();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (isClickedCHeckin) {
                        checkIn();
                        isClickedCHeckin = false;
                    }
                    if (isClickedCheckOut) {
                        checkOut();
                        isClickedCheckOut = false;
                    }
                    if (isCLickedFavorite) {
                        addDiveSpotToFavorites();
                        isCLickedFavorite = false;
                    }
                    if (isClickedRemoveFromFavorites) {
                        removeFromFavorites(String.valueOf(diveSpot.getId()));
                        isClickedRemoveFromFavorites = false;
                    }
                    if (isClickedNoValidation) {
                        diveSpotValidation(false);
                    }
                    if (isClickedYesValidation) {
                        diveSpotValidation(true);
                    }
                    if (isClickedEdit) {
                        Intent editDiveSpotIntent = new Intent(DiveSpotDetailsActivity.this,
                                EditDiveSpotActivity.class);
                        editDiveSpotIntent
                                .putExtra(Constants.DIVESPOTID, String.valueOf(diveSpot.getId()));
                        startActivityForResult(editDiveSpotIntent, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_EDIT_DIVE_SPOT);
                        isClickedEdit = false;
                    }
                } else {
                    if (isClickedCHeckin) {
                        checkoutUi();
                        isClickedCHeckin = false;
                    }
                    if (isClickedCheckOut) {
                        checkInUi();
                        isClickedCheckOut = false;
                    }
                    if (isCLickedFavorite) {
                        updateMenuItems(menu, false);
                    }
                    if (isClickedRemoveFromFavorites) {
                        updateMenuItems(menu, true);
                    }
                    if (isClickedYesValidation || isClickedNoValidation) {
                        isInfoValidLayout.setVisibility(View.VISIBLE);
                        thanksLayout.setVisibility(View.GONE);
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PICK_PHOTOS:
                if (resultCode == RESULT_OK) {
                    List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity
                            .EXTRA_RESULT);
                    AddPhotosDoDiveSpotActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_ADD_PHOTOS_ACTIVITY, (ArrayList<String>)path, String.valueOf(diveSpot.getId()));
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_ADD_PHOTOS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getDdScannerRestClient().getDiveSpotDetails(String.valueOf(diveSpot.getId()), diveSpotDetailsResultListener);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_PICK_PHOTOS:
                if (resultCode == RESULT_OK) {
                    MultiImageSelector.create(this).count(3).start(this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_ADD_PHOTOS_ACTIVITY);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateMenuItems(Menu menu, boolean isFavorite) {
        if (isFavorite) {
            menu.findItem(R.id.favorite).setTitle("Remove from favorites");
            return;
        }
        if (menu != null) {
            if (menu.findItem(R.id.favorite) != null) {
                menu.findItem(R.id.favorite).setTitle("Add to favorites");
            }
        }
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this, SocialNetworks.class);
        startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN);
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
    public void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Subscribe
    public void openImagesActivity(OpenPhotosActivityEvent event) {
//        DiveSpotPhotosActivity.show(this, (ArrayList<String>) diveSpot.getImages(),
//                diveSpot.getDiveSpotPathMedium(), (ArrayList<String>) diveSpot.getCommentImages(),
//                String.valueOf(diveSpot.getId()));
        Intent intent = new Intent(this, DiveSpotPhotosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("images", (ArrayList<Image>) diveSpot.getImages());
        bundle.putString("path", diveSpot.getDiveSpotPathMedium());
        bundle.putParcelableArrayList("reviewsImages", (ArrayList<Image>) diveSpot.getCommentImages());
        bundle.putString("id", String.valueOf(diveSpot.getId()));
        intent.putExtras(bundle);
        startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PHOTOS);
    }

    private void showEditorsList() {
        editorsWrapperView.setVisibility(View.VISIBLE);
        editorsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        editorsRecyclerView.setAdapter(new EditorsListAdapter(this, diveSpotDetails.getEditors()));
        editorsRecyclerView.setHasFixedSize(true);
        final int viewHeight = (int) (getResources().getDimension(R.dimen.editor_item_height) * diveSpotDetails.getEditors().size());
        ValueAnimator editorsAnimator = ValueAnimator.ofInt(0, viewHeight);
        editorsAnimator.setDuration(300);
        editorsAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ViewGroup.LayoutParams lp = editorsWrapperView.getLayoutParams();
                editorsWrapperView.getLayoutParams().height = viewHeight;
                editorsWrapperView.setLayoutParams(lp);
                editorsRecyclerView.getLayoutParams().height = viewHeight;
                editorsRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        editorsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams lp = editorsWrapperView.getLayoutParams();
                lp.height = (int) valueAnimator.getAnimatedValue();
                editorsWrapperView.setLayoutParams(lp);
            }
        });
        editorsAnimator.setInterpolator(new AccelerateInterpolator());
        editorsAnimator.start();

        ValueAnimator arrowRotationAnimator = ValueAnimator.ofFloat(0f, -90f);
        arrowRotationAnimator.setDuration(300);
        arrowRotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                expandEditorsArrow.setRotation((Float) valueAnimator.getAnimatedValue());
            }
        });
        arrowRotationAnimator.setInterpolator(new AccelerateInterpolator());
        arrowRotationAnimator.start();

        editorsRecyclerView.setNestedScrollingEnabled(false);
        editorsListExpanded = true;
    }

    private void hideEditorsList() {
        // TODO Implement
        final int viewHeight = (int) (getResources().getDimension(R.dimen.editor_item_height) * diveSpotDetails.getEditors().size());
        ValueAnimator editorsAnimator = ValueAnimator.ofInt(viewHeight, 0);
        editorsAnimator.setDuration(300);
        editorsAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ViewGroup.LayoutParams lp = editorsWrapperView.getLayoutParams();
                editorsWrapperView.getLayoutParams().height = 0;
                editorsWrapperView.setLayoutParams(lp);
                editorsRecyclerView.getLayoutParams().height = 0;
                editorsRecyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        editorsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams lp = editorsWrapperView.getLayoutParams();
                lp.height = (int) valueAnimator.getAnimatedValue();
                editorsWrapperView.setLayoutParams(lp);
            }
        });
        editorsAnimator.setInterpolator(new AccelerateInterpolator());
        editorsAnimator.start();

        ValueAnimator arrowRotationAnimator = ValueAnimator.ofFloat(-90f, 0f);
        arrowRotationAnimator.setDuration(300);
        arrowRotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                expandEditorsArrow.setRotation((Float) valueAnimator.getAnimatedValue());
            }
        });
        arrowRotationAnimator.setInterpolator(new AccelerateInterpolator());
        arrowRotationAnimator.start();

        editorsListExpanded = false;
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_FAILED_TO_CONNECT:
            case DialogsRequestCodes.DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND:
                finish();
                break;
        }
    }

    private class ImageLoadedCallback implements Callback {
        ProgressView progressBar;

        public ImageLoadedCallback(ProgressView progBar) {
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;

        public GridSpacingItemDecoration(int spanCount) {
            this.spanCount = spanCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position >= spanCount) {
                outRect.top = Math.round(Helpers.convertDpToPixel(Float.valueOf(10), DiveSpotDetailsActivity.this));
            }
        }
    }

    @Override
    public void onBackPressed() {
//        if (diveSpot != null) {
//            if (isNewDiveSpot) {
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.putExtra(Constants.MAIN_ACTIVITY_ACTVITY_EXTRA_LATLNGBOUNDS, new LatLngBounds(new LatLng(diveSpot.getLat() - 0.2, diveSpot.getLng() - 0.2), new LatLng(diveSpot.getLat() + 0.2, diveSpot.getLng() + 0.2)));
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra(Constants.IS_HAS_INTERNET, true);
//                startActivity(intent);
//                finish();
//            }
//        }
        finish();
    }

    @Subscribe
    public void serverConnectionError(UnknownErrorCatchedEvent event) {
        mainLayout.setVisibility(View.GONE);
        serveConnectionErrorLayout.setVisibility(View.VISIBLE);
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }
}
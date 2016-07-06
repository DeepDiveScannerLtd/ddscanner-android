package com.ddscanner.ui.activities;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Checkins;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.DiveSpotFull;
import com.ddscanner.entities.DivespotDetails;
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
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.DiveSpotsPhotosAdapter;
import com.ddscanner.ui.adapters.EditorsListAdapter;
import com.ddscanner.ui.adapters.SealifeListAdapter;
import com.ddscanner.utils.Constants;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DiveSpotDetailsActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private static final String TAG = DiveSpotDetailsActivity.class.getName();

    private static final String EXTRA_ID = "ID";
    private static final int RC_LEAVE_REVIEW_ACTIVITY = 1001;
    private static final int RC_EDIT_DIVE_SPOT = 2001;
    private static final int RC_LOGIN = 2000;
    private static final int RC_PHOTOS = 3001;

    private DivespotDetails divespotDetails;
    private ProgressDialog progressDialog;
    private String productId;
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
    private RecyclerView sealifeRecyclerview;
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
    private RelativeLayout accessLayout;
    private Button btnDsDetailsIsValid;
    private Button btnDsDetailsIsInvalid;
    private Button showDiveCenters;
    private ImageView thanksClose;
    private RelativeLayout creatorLayout;
    private TextView numberOfCheckinPeoplesHere;
    private TextView creatorName;
    private TextView newDiveSpotView;
    private ImageView creatorAvatar;
    private int avatarImageSize;
    private int avatarImageRadius;
    private ImageView expandEditorsArrow;
    private Menu menu;
    private List<User> creatorsEditorsList = new ArrayList<>();

    private RelativeLayout editorsWrapperView;
    private RecyclerView editorsRecyclerView;
    private MaterialDialog materialDialog;
    private Helpers helpers = new Helpers();

    private boolean isCLickedFavorite = false;
    private boolean isClickedCHeckin = false;
    private boolean isClickedCheckOut = false;
    private boolean isClickedYesValidation = false;
    private boolean isClickedNoValidation = false;
    private boolean isFavorite = false;
    private boolean isClickedRemoveFromFavorites = false;
    private boolean isClickedEdit = false;


    private List<Comment> usersComments;
    private List<User> usersCheckins;

    private boolean editorsListExpanded;

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * Show current activity from another place of app
     *
     * @param context
     * @param id
     * @author Andrei Lashkevich
     */

    public static void show(Context context, String id, EventsTracker.SpotViewSource spotViewSource) {
        if (spotViewSource != null) {
            EventsTracker.trackDiveSpotView(id, spotViewSource);
        }

        Intent intent = new Intent(context, DiveSpotDetailsActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_spot_details);
        findViews();
        toolbarSettings();
        productId = getIntent().getStringExtra(EXTRA_ID);
        requestProductDetails(productId);
    }

    /**
     * Find views in activity
     *
     * @author Andrei Lashkevich
     */

    private void findViews() {
        diveSpotName = (TextView) findViewById(R.id.dive_spot_name);
        rating = (LinearLayout) findViewById(R.id.stars);
        informationLayout = (LinearLayout) findViewById(R.id.informationLayout);
        diveSpotDescription = (TextView) findViewById(R.id.dive_place_description);
        diveSpotMainPhoto = (ImageView) findViewById(R.id.main_photo);
        progressBar = (ProgressView) findViewById(R.id.progressBar);
        progressBarFull = (ProgressView) findViewById(R.id.progressBarFull);
        photosRecyclerView = (RecyclerView) findViewById(R.id.photos_rc);
        sealifeRecyclerview = (RecyclerView) findViewById(R.id.sealife_rc);
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
        btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        showAllReviews = (Button) findViewById(R.id.btn_show_all_reviews);
        isInfoValidLayout = (LinearLayout) findViewById(R.id.is_info_valid_layout);
        thanksLayout = (LinearLayout) findViewById(R.id.thanks_layout);
        btnDsDetailsIsValid = (Button) findViewById(R.id.yes_button);
        btnDsDetailsIsInvalid = (Button) findViewById(R.id.no_button);
        thanksClose = (ImageView) findViewById(R.id.thank_close);
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
    }

    /**
     * Create toolbar ui
     *
     * @author Andrei Lashkevich
     */

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle("");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(android.R.color.transparent));
        //  collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.transparent));
    }

    /**
     * Set ui data at current activity
     *
     * @author Andrei Lashkevich
     */

    private void setUi() {
        avatarImageRadius = (int) getResources().getDimension(R.dimen.editor_avatar_radius);
        avatarImageSize = 2 * avatarImageRadius;
        materialDialog = helpers.getMaterialDialog(this);
        thanksClose.setOnClickListener(this);
        btnDsDetailsIsInvalid.setOnClickListener(this);
        showDiveCenters.setOnClickListener(this);
        btnDsDetailsIsValid.setOnClickListener(this);
        btnCheckIn.setOnClickListener(this);
        creatorLayout.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(this);
        //checkInPeoples.setOnClickListener(this);
        showMore.setOnClickListener(this);
        showAllReviews.setOnClickListener(this);
        diveSpot = divespotDetails.getDivespot();
        isFavorite = diveSpot.getIsFavorite();
        updateMenuItems(menu, isFavorite);
        if (divespotDetails.getComments() != null) {
            showAllReviews.setText(getString(R.string.show_all)
                    + String.valueOf(divespotDetails.getComments().size())
                    + getString(R.string.skobka));
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
        visibility.setText(diveSpot.getVisibility());
        currents.setText(diveSpot.getCurrents());
        if (diveSpot.getImages() != null && !diveSpot.getImages().isEmpty()) {
            Picasso.with(this).load(diveSpot.getDiveSpotPathMedium() + diveSpot.getImages().get(0)).into(diveSpotMainPhoto, new ImageLoadedCallback(progressBar) {
                @Override
                public void onSuccess() {
                    super.onSuccess();
                    progressBar.setVisibility(View.GONE);
                }
            });
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

        photosRecyclerView.setLayoutManager(new GridLayoutManager(DiveSpotDetailsActivity.this, 4));
        //      photosRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4));
        photosRecyclerView.setAdapter(new DiveSpotsPhotosAdapter((ArrayList<String>) diveSpot.getImages(),
                diveSpot.getDiveSpotPathMedium(), DiveSpotDetailsActivity.this, (ArrayList<String>) diveSpot.getCommentImages()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(DiveSpotDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        sealifeRecyclerview.setNestedScrollingEnabled(false);
        sealifeRecyclerview.setHasFixedSize(false);
        sealifeRecyclerview.setLayoutManager(layoutManager);
        sealifeRecyclerview.setAdapter(new SealifeListAdapter(
                (ArrayList<Sealife>) divespotDetails.getSealifes(),
                this, diveSpot.getSealifePathSmall(),
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
                    .resize(Math.round(helpers.convertDpToPixel(avatarImageSize, this)), Math.round(helpers.convertDpToPixel(avatarImageSize, this)))
                    .centerCrop()
                    .transform(new CropCircleTransformation()).into(creatorAvatar);
            creatorLayout.setOnClickListener(this);
            creatorName.setText(diveSpot.getCreator().getName());
        }

        progressBarFull.setVisibility(View.GONE);
        informationLayout.setVisibility(View.VISIBLE);
        if (diveSpot.getCheckin()) {
            checkinUi();
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
            }
        }
        if (divespotDetails.getCheckins() != null) {
            usersCheckins = divespotDetails.getCheckins();
            setCheckinsCountPeople(String.valueOf(usersCheckins.size()) + " " +
                    getString(R.string.peoples_checked_in_here), false);
        }
        if (divespotDetails.getEditors() != null) {
            for (User user : divespotDetails.getEditors()) {
                creatorsEditorsList.add(user);
            }
           // EditorsListActivity.show(DiveSpotDetailsActivity.this, (ArrayList<User>) creatorsEditorsList);

        }
    }

    private void setCheckinsCountPeople(String count, boolean isNull) {
        if (isNull) {
            checkInPeoples.setOnClickListener(null);
        } else {
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
     * @author Andrei Lashkevich
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

    /**
     * Request for getting data about dive spot
     *
     * @param productId
     * @author Andrei Lashkevich
     */
    private void requestProductDetails(String productId) {
        Map<String, String> map = new HashMap<>();
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            map.put("social", SharedPreferenceHelper.getSn());
            map.put("token", SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                map.put("secret", SharedPreferenceHelper.getSecret());
            }
        }
        Call<ResponseBody> call = RestClient.getServiceInstance().getDiveSpotById(productId, map);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    divespotDetails = new Gson().fromJson(responseString, DivespotDetails.class);
                    diveSpotCoordinates = new LatLng(divespotDetails.getDivespot().getLat(),
                            divespotDetails.getDivespot().getLng());
                    usersComments = divespotDetails.getComments();
                    setUi();
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO Handle errors
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_layout:
                Intent intent = new Intent(DiveSpotDetailsActivity.this, ShowDsLocationActivity.class);
                intent.putExtra("LATLNG", new LatLng(divespotDetails.getDivespot().getLat(), divespotDetails.getDivespot().getLng()));
                startActivity(intent);
                break;
            case R.id.check_in_peoples:
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
                if (divespotDetails.getComments() != null || usersComments != null) {
                    Intent reviewsIntent = new Intent(DiveSpotDetailsActivity.this,
                            ReviewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("COMMENTS",
                            (ArrayList<Comment>) usersComments);
                    bundle.putString(Constants.DIVESPOTID,
                            String.valueOf(divespotDetails.getDivespot().getId()));
                    bundle.putString("PATH", divespotDetails.getDivespot().getDiveSpotPathMedium());
                    reviewsIntent.putExtras(bundle);
                    startActivityForResult(reviewsIntent, 9001);
                } else {
                    Intent leaveReviewIntent = new Intent(DiveSpotDetailsActivity.this,
                            LeaveReviewActivity.class);
                    leaveReviewIntent.putExtra(Constants.DIVESPOTID, String.valueOf(
                            diveSpot.getId()));
                    startActivityForResult(leaveReviewIntent, RC_LEAVE_REVIEW_ACTIVITY);
                }
                break;
            case R.id.yes_button:
                diveSpotValidation(true);
                break;
            case R.id.no_button:
                showEditDiveSpotDialog();
                break;
            case R.id.thank_close:
                thanksLayout.setVisibility(View.GONE);
                break;
            case R.id.creator:
//                helpers.showDialog(diveSpot.getCreator(), getFragmentManager());
//                if (divespotDetails.getEditors() != null) {
//                    for (User user : divespotDetails.getEditors()) {
//                        creatorsEditorsList.add(user);
//                    }
//                    EditorsListActivity.show(DiveSpotDetailsActivity.this, (ArrayList<User>) creatorsEditorsList);
//                    break;
//                }
                EditorsListActivity.show(DiveSpotDetailsActivity.this, (ArrayList<User>) creatorsEditorsList);
                break;
            case R.id.button_show_divecenters:
                DiveCentersActivity.show(this, new LatLng(Double.valueOf(diveSpot.getLat()), Double.valueOf(diveSpot.getLng())), diveSpot.getName());
                break;
        }
    }

    private void showEditDiveSpotDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(this)
                .title(R.string.edit)
                .content(R.string.question_edit_dive_spot)
                .positiveText(R.string.btn_yes)
                .positiveColor(getResources().getColor(R.color.primary))
                .negativeColor(getResources().getColor(R.color.primary))
                .negativeText(R.string.no_just_vote_answer)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        if (SharedPreferenceHelper.isUserLoggedIn()) {
                            Intent editDiveSpotIntent = new Intent(DiveSpotDetailsActivity.this,
                                    EditDiveSpotActivity.class);
                            editDiveSpotIntent
                                    .putExtra(Constants.DIVESPOTID, String.valueOf(diveSpot.getId()));
                            startActivityForResult(editDiveSpotIntent, RC_EDIT_DIVE_SPOT);
                        } else {
                            isClickedEdit = true;
                            showLoginActivity();
                        }
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
                .positiveColor(getResources().getColor(R.color.primary))
                .negativeColor(getResources().getColor(R.color.primary))
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

    /**
     * Sending request when try to check in in dive spot and change FAB style
     *
     * @author Andrei Lashkevich
     */

    private void checkIn() {
        checkinUi();
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            checkoutUi();
            isClickedCHeckin = true;
            showLoginActivity();
            return;
        }
        Call<ResponseBody> call = RestClient.getServiceInstance().checkIn(
                String.valueOf(divespotDetails.getDivespot().getId()),
                helpers.getRegisterRequest()
        );
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    getCheckins();
                    EventsTracker.trackCheckIn(EventsTracker.CheckInStatus.SUCCESS);
                } else {
                    checkoutUi();
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
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle

                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        isClickedCHeckin = true;
                        showLoginActivity();
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void checkinUi() {
        btnCheckIn.setImageDrawable(AppCompatDrawableManager.get().getDrawable(
                DiveSpotDetailsActivity.this, R.drawable.ic_acb_pin_checked));
        btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        isCheckedIn = true;
    }

    private void checkoutUi() {
        btnCheckIn.setImageDrawable(AppCompatDrawableManager.get().getDrawable(DiveSpotDetailsActivity.this, R.drawable.ic_acb_pin));
        btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
        isCheckedIn = false;
    }

    /**
     * Sending request when try to check out in dive spot and change FAB style
     *
     * @author Andrei Lashkevich
     */

    private void checkOut() {
        checkoutUi();
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            checkinUi();
            isClickedCheckOut = true;
            showLoginActivity();
            return;
        }
        Call<ResponseBody> call = RestClient.getServiceInstance().checkOutUser(
                String.valueOf(divespotDetails.getDivespot().getId()),
                helpers.getUserQuryMapRequest()
        );
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    checkinUi();
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
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle

                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        isClickedCheckOut = true;
                        showLoginActivity();
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    }
                } else {
                    EventsTracker.trackCheckOut();
                    getCheckins();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        Intent leaveReviewIntent = new Intent(DiveSpotDetailsActivity.this,
                LeaveReviewActivity.class);
        leaveReviewIntent.putExtra(Constants.DIVESPOTID, String.valueOf(diveSpot.getId()));
        leaveReviewIntent.putExtra("RATING", rating);
        startActivityForResult(leaveReviewIntent, RC_LEAVE_REVIEW_ACTIVITY);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void diveSpotValidation(final boolean isValid) {
        materialDialog.show();
        ValidationReguest validationReguest = new ValidationReguest();
        validationReguest.setAppId(helpers.getRegisterRequest().getAppId());
        validationReguest.setToken(helpers.getRegisterRequest().getToken());
        validationReguest.setSocial(helpers.getRegisterRequest().getSocial());
        if (SharedPreferenceHelper.getSn().equals("tw")) {
            validationReguest.setSecret(helpers.getRegisterRequest().getSecret());
        }
        validationReguest.setValid(isValid);
        Call<ResponseBody> call = RestClient.getServiceInstance()
                .divespotValidation(String.valueOf(divespotDetails.getDivespot().getId()),
                        validationReguest);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                materialDialog.dismiss();
                if (response.isSuccessful()) {
                    isInfoValidLayout.setVisibility(View.GONE);
                    thanksLayout.setVisibility(View.VISIBLE);
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
                            if (helpers.checkIsErrorByLogin(error)) {
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void addDiveSpotToFavorites() {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            isCLickedFavorite = true;
            showLoginActivity();
            return;
        }
        final Helpers helpers = new Helpers();
        Call<ResponseBody> call = RestClient.getServiceInstance().addDiveSpotToFavourites(
                String.valueOf(diveSpot.getId()),
                helpers.getRegisterRequest()
        );
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().code() == 200) {
                    isFavorite = true;
                    updateMenuItems(menu, isFavorite);
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
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle

                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        isCLickedFavorite = true;
                        showLoginActivity();
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void removeFromFavorites(String id) {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            isCLickedFavorite = true;
            showLoginActivity();
            return;
        }
        Call<ResponseBody> call = RestClient.getServiceInstance()
                .removeSpotFromFavorites(id, helpers.getUserQuryMapRequest());
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.raw().code() == 200) {
                    isFavorite = false;
                    updateMenuItems(menu, isFavorite);
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
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle

                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        isClickedRemoveFromFavorites = true;
                        showLoginActivity();
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getCheckins() {
        Call<ResponseBody> call = RestClient.getServiceInstance().getCheckins(String.valueOf(diveSpot.getId()));
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                if (response.isSuccessful()) {
                    Checkins checkins = new Checkins();
                    try {
                        responseString = response.body().string();
                        checkins = new Gson().fromJson(responseString, Checkins.class);
                        if (checkins.getCheckins() != null) {
                            usersCheckins = checkins.getCheckins();
                            setCheckinsCountPeople(String.valueOf(usersCheckins.size()) + " " +
                                    getString(R.string.peoples_checked_in_here), false);
                        } else {
                            setCheckinsCountPeople("No " +
                                    getString(R.string.peoples_checked_in_here), true);
                        }
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getComments() {
        Call<ResponseBody> call = RestClient.getServiceInstance()
                .getComments(String.valueOf(diveSpot.getId()), helpers.getUserQuryMapRequest());
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                if (response.isSuccessful()) {
                    try {
                        responseString = response.body().string();
                        Comments comments = new Gson().fromJson(responseString, Comments.class);
                        usersComments = comments.getComments();
                        setReviewsCount(getString(R.string.show_all)
                                + String.valueOf(usersComments.size())
                                + getString(R.string.skobka));
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_LEAVE_REVIEW_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                getComments();
            }
        }
        if (requestCode == 9001) {
            if (resultCode == RESULT_OK) {
                getComments();
            }
        }
        if (requestCode == RC_PHOTOS && resultCode == RESULT_OK) {
            Intent intent = getIntent();
            startActivity(intent);
            finish();
        }
        if (requestCode == RC_EDIT_DIVE_SPOT && resultCode == RESULT_OK) {
            Intent intent = getIntent();
            startActivity(intent);
            finish();
        }
        if (requestCode == RC_LOGIN) {
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
                    startActivityForResult(editDiveSpotIntent, RC_EDIT_DIVE_SPOT);
                    isClickedEdit = false;
                }
            } else {
                if (isClickedCHeckin) {
                    checkoutUi();
                    isClickedCHeckin = false;
                }
                if (isClickedCheckOut) {
                    checkinUi();
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
        if (menu.findItem(R.id.favorite) != null) {
            menu.findItem(R.id.favorite).setTitle("Add to favorites");
        }
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this, SocialNetworks.class);
        startActivityForResult(intent, RC_LOGIN);
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
        bundle.putSerializable("images", (ArrayList<String>) diveSpot.getImages());
        bundle.putString("path", diveSpot.getDiveSpotPathMedium());
        bundle.putSerializable("reviewsImages", (ArrayList<String>) diveSpot.getCommentImages());
        bundle.putString("id", String.valueOf(diveSpot.getId()));
        intent.putExtras(bundle);
        startActivityForResult(intent, RC_PHOTOS);
    }

    private void showEditorsList() {
        editorsWrapperView.setVisibility(View.VISIBLE);
        editorsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        editorsRecyclerView.setAdapter(new EditorsListAdapter(this, divespotDetails.getEditors()));
        editorsRecyclerView.setHasFixedSize(true);
        final int viewHeight = (int) (getResources().getDimension(R.dimen.editor_item_height) * divespotDetails.getEditors().size());
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
        final int viewHeight = (int) (getResources().getDimension(R.dimen.editor_item_height) * divespotDetails.getEditors().size());
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
                outRect.top = Math.round(convertDpToPixel(Float.valueOf(10), DiveSpotDetailsActivity.this));
            }
        }
    }

}

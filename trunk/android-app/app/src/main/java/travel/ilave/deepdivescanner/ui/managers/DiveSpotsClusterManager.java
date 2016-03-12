package travel.ilave.deepdivescanner.ui.managers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.DDScannerApplication;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.entities.DivespotsWrapper;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.activities.DivePlaceActivity;
import travel.ilave.deepdivescanner.ui.adapters.PlacesPagerAdapter;
import travel.ilave.deepdivescanner.utils.LogUtils;

public class DiveSpotsClusterManager extends ClusterManager<DiveSpot> implements ClusterManager.OnClusterClickListener<DiveSpot> {

    private static final String TAG = DiveSpotsClusterManager.class.getName();
    private static final int CAMERA_ANIMATION_DURATION = 300;

    private Context context;
    private GoogleMap googleMap;
    private Marker lastClickedMarker;
    private Map<String, String> diveSpotsRequestMap = new HashMap<>();
    private DivespotsWrapper divespotsWrapper;
    private ArrayList<DiveSpot> diveSpots = new ArrayList<>();
    private HashMap<LatLng, DiveSpot> diveSpotsMap = new HashMap<>();
    private PlacesPagerAdapter placesPagerAdapter;
    private Drawable clusterBackgroundDrawable;
    private final IconGenerator clusterIconGenerator;
    private final float density;

    public DiveSpotsClusterManager(Context context, GoogleMap googleMap, PlacesPagerAdapter placesPagerAdapter) {
        super(context, googleMap);
        this.context = context;
        this.googleMap = googleMap;
        this.placesPagerAdapter = placesPagerAdapter;
        this.density = context.getResources().getDisplayMetrics().density;
        this.clusterBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.ic_number_2);
        this.clusterIconGenerator = new IconGenerator(context);
        this.clusterIconGenerator.setContentView(this.makeSquareTextView(context));
        this.clusterIconGenerator.setTextAppearance(com.google.maps.android.R.style.ClusterIcon_TextAppearance);
        this.clusterIconGenerator.setBackground(clusterBackgroundDrawable);

        setAlgorithm(new GridBasedAlgorithm<DiveSpot>());
        setRenderer(new IconRenderer(context, googleMap, this));
        setOnClusterClickListener(this);
        getMarkerCollection().setOnInfoWindowAdapter(new InfoWindowAdapter(context));

        requestCityProducts(googleMap.getProjection().getVisibleRegion().latLngBounds.southwest, googleMap.getProjection().getVisibleRegion().latLngBounds.northeast);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        super.onCameraChange(cameraPosition);

        requestCityProducts(googleMap.getProjection().getVisibleRegion().latLngBounds.southwest, googleMap.getProjection().getVisibleRegion().latLngBounds.northeast);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (super.onMarkerClick(marker)) {
            return true;
        }
        marker.showInfoWindow();
        lastClickedMarker = marker;
        Projection projection = googleMap.getProjection();
        Point mapCenteringPoint = projection.toScreenLocation(marker.getPosition());
        mapCenteringPoint.y = mapCenteringPoint.y - DDScannerApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.info_window_height) / 2;
        LatLng newMarkerPosition = new LatLng(projection.fromScreenLocation(mapCenteringPoint).latitude, marker.getPosition().longitude);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(newMarkerPosition), CAMERA_ANIMATION_DURATION, null);
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(context, DivePlaceActivity.class);
        i.putExtra(InfoWindowAdapter.PRODUCT, String.valueOf(diveSpotsMap.get(marker.getPosition()).getId()));
        context.startActivity(i);
    }

    @Override
    public boolean onClusterClick(Cluster<DiveSpot> cluster) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (DiveSpot diveSpot : cluster.getItems()) {
            builder.include(diveSpot.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        googleMap.animateCamera(cu, CAMERA_ANIMATION_DURATION, null);
        return true;
    }

    private SquareTextView makeSquareTextView(Context context) {
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -2);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(com.google.maps.android.R.id.text);
        int twelveDpi = (int)(12.0F * this.density);
        squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
        return squareTextView;
    }

    private class IconRenderer extends DefaultClusterRenderer<DiveSpot> {

        public IconRenderer(Context context, GoogleMap map, ClusterManager<DiveSpot> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<DiveSpot> cluster, MarkerOptions markerOptions) {
            int bucket = this.getBucket(cluster);
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(clusterIconGenerator.makeIcon(this.getClusterText(bucket)));

            markerOptions.icon(descriptor);
        }

        @Override
        protected void onClusterItemRendered(DiveSpot diveSpot, final Marker marker) {
            super.onClusterItemRendered(diveSpot, marker);
            try {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
                if (lastClickedMarker != null && lastClickedMarker.getPosition().equals(marker.getPosition())) {
                    marker.showInfoWindow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDiveSpots(ArrayList<DiveSpot> diveSpots) {
        LogUtils.i(TAG, "incoming dive spots size = " + diveSpots.size());
        final ArrayList<DiveSpot> newDiveSpots = new ArrayList<>();
        newDiveSpots.addAll(diveSpots);
        newDiveSpots.removeAll(this.diveSpots);
        final ArrayList<DiveSpot> deletedDiveSpots = new ArrayList<>();
        deletedDiveSpots.addAll(this.diveSpots);
        deletedDiveSpots.removeAll(diveSpots);
        LogUtils.i(TAG, "removing " + deletedDiveSpots.size() + " dive spots");
        for (DiveSpot diveSpot : deletedDiveSpots) {
            removeDiveSpot(diveSpot);
        }
        LogUtils.i(TAG, "adding " + newDiveSpots.size() + " dive spots");
        for (DiveSpot diveSpot : newDiveSpots) {
            addNewDiveSpot(diveSpot);
        }
        cluster();
    }

    private void addNewDiveSpot(DiveSpot diveSpot) {
        diveSpot.initLatLng();
        if (diveSpot.getPosition() == null) {
            LogUtils.i(TAG, "addNewDiveSpot diveSpot.getPosition() == null");
        } else {
            addItem(diveSpot);

            diveSpotsMap.put(diveSpot.getPosition(), diveSpot);
            diveSpots.add(diveSpot);
        }
    }

    private void removeDiveSpot(DiveSpot diveSpot) {
        removeItem(diveSpot);

        diveSpotsMap.remove(new LatLng(Double.valueOf(diveSpot.getLat()), Double.valueOf(diveSpot.getLng())));
        diveSpots.remove(diveSpot);
    }

    public void requestCityProducts(LatLng left, LatLng right) {
        diveSpotsRequestMap.put("latLeft", String.valueOf(left.latitude - 2.0));
        diveSpotsRequestMap.put("lngLeft", String.valueOf(left.longitude - 1.0));
        diveSpotsRequestMap.put("lngRight", String.valueOf(right.longitude + 1.0));
        diveSpotsRequestMap.put("latRight", String.valueOf(right.latitude + 2.0));
        RestClient.getServiceInstance().getDivespots(diveSpotsRequestMap, new retrofit.Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i(TAG, "response code is " + s.getStatus());
                LogUtils.i(TAG, "response body is " + responseString);
                divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                updateDiveSpots((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots());
                placesPagerAdapter.populateDiveSpotsList((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                    Toast.makeText(DDScannerApplication.getInstance(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                    Toast.makeText(DDScannerApplication.getInstance(), "Server is not responsible, please try later", Toast.LENGTH_SHORT).show();
                }
//               String json =  new String(((TypedByteArray)error.getResponse().getBody()).getBytes());
                //      System.out.println("failure" + json.toString());
            }
        });
    }

    private class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public static final String PRODUCT = "PRODUCT";

        private Context mContext;
        private boolean not_first_time_showing_info_window;

        public InfoWindowAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.info_window, null);
            DiveSpot diveSpot = diveSpotsMap.get(marker.getPosition());
            ImageView photo = (ImageView) view.findViewById(R.id.popup_photo);
            if (diveSpot.getImages() != null) {
                if (not_first_time_showing_info_window) {
                    Picasso.with(mContext).load(diveSpot.getImages().get(0)).resize(260, 116).into(photo);
                } else {
                    not_first_time_showing_info_window = true;
                    Picasso.with(mContext).load(diveSpot.getImages().get(0)).resize(260, 116).into(photo, new InfoWindowRefresher(marker));
                }
            }
            TextView description = ((TextView) view.findViewById(R.id.description_popup));
            TextView title = ((TextView) view.findViewById(R.id.popup_product_name));
            title.setText(diveSpot.getName());
            description.setText(diveSpot.getDescription());
            // from = ((TextView)view.findViewById(R.id.price1));
            LinearLayout stars = (LinearLayout) view.findViewById(R.id.stars);
            stars.removeAllViews();
            for (int i = 0; i < diveSpot.getRating(); i++) {
                ImageView iv = new ImageView(mContext);
                iv.setImageResource(R.drawable.ic_flag_full_small);
                iv.setPadding(5, 0, 0, 0);
                stars.addView(iv);
            }
            for (int i = 0; i < 5 - diveSpot.getRating(); i++) {
                ImageView iv = new ImageView(mContext);
                iv.setImageResource(R.drawable.ic_flag_empty_small);
                iv.setPadding(5, 0, 0, 0);
                stars.addView(iv);
            }
            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private class InfoWindowRefresher implements Callback {
            private Marker markerToRefresh;

            private InfoWindowRefresher(Marker markerToRefresh) {
                this.markerToRefresh = markerToRefresh;
            }

            @Override
            public void onSuccess() {
                markerToRefresh.showInfoWindow();
            }

            @Override
            public void onError() {
            }
        }
    }
}

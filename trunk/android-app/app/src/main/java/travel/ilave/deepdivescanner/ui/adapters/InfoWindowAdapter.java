package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
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
import travel.ilave.deepdivescanner.utils.LogUtils;

/**
 * Created by lashket on 10.12.15.
 */
public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter, ClusterManager.OnClusterClickListener<DiveSpot> {

    private static final String TAG = InfoWindowAdapter.class.getName();
    public static final String PRODUCT = "PRODUCT";
    private static final int CAMERA_ANIMATION_DURATION = 300;

    private PlacesPagerAdapter placesPagerAdapter;
    private Context mContext;
    private ArrayList<DiveSpot> diveSpots = new ArrayList<>();
    private GoogleMap googleMap;
    private HashMap<LatLng, DiveSpot> diveSpotsMap = new HashMap<>();
    private boolean not_first_time_showing_info_window;
    private DiveSpotsClusterManager diveSpotsClusterManager;
    private Map<String, String> diveSpotsRequestMap = new HashMap<>();
    private DivespotsWrapper divespotsWrapper;
    private Marker lastClickedMarker;

    public InfoWindowAdapter(Context context, PlacesPagerAdapter placesPagerAdapter, ArrayList<DiveSpot> diveSpots, GoogleMap map) {
        this.mContext = context;
        this.placesPagerAdapter = placesPagerAdapter;
        this.googleMap = map;
        diveSpotsClusterManager = new DiveSpotsClusterManager(context, map);
        diveSpotsClusterManager.setRenderer(new IconRenderer(context, googleMap, diveSpotsClusterManager));
        diveSpotsClusterManager.setOnClusterClickListener(this);
        googleMap.setOnInfoWindowClickListener(diveSpotsClusterManager);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMarkerClickListener(diveSpotsClusterManager);
        googleMap.setOnCameraChangeListener(diveSpotsClusterManager);

        if (diveSpots != null) {
            for (DiveSpot diveSpot : diveSpots) {
                addNewDiveSpot(diveSpot);
            }
        }
        requestCityProducts(googleMap.getProjection().getVisibleRegion().latLngBounds.southwest,googleMap.getProjection().getVisibleRegion().latLngBounds.northeast);

    }

    @Override
    public View getInfoWindow(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.info_window, null);
        DiveSpot diveSpot = new DiveSpot();
        diveSpot = diveSpotsMap.get(marker.getPosition());
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

    public void updateDiveSpots(ArrayList<DiveSpot> diveSpots) {
        LogUtils.i(TAG, "incoming dive spots size = " + diveSpots.size());
        ArrayList<DiveSpot> newDiveSpots = new ArrayList<>();
        newDiveSpots.addAll(diveSpots);
        newDiveSpots.removeAll(this.diveSpots);
        ArrayList<DiveSpot> deletedDiveSpots = new ArrayList<>();
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
        if (lastClickedMarker != null && !lastClickedMarker.isInfoWindowShown()) {
            lastClickedMarker = null;
        }
        diveSpotsClusterManager.cluster();
    }

    private void addNewDiveSpot(DiveSpot diveSpot) {
        diveSpot.initLatLng();
        if (diveSpot.getPosition() == null) {
            LogUtils.i(TAG, "addNewDiveSpot diveSpot.getPosition() == null");
        } else {
            diveSpotsClusterManager.addItem(diveSpot);

            diveSpotsMap.put(diveSpot.getPosition(), diveSpot);
            diveSpots.add(diveSpot);
        }
    }

    private void removeDiveSpot(DiveSpot diveSpot) {
        diveSpotsClusterManager.removeItem(diveSpot);

        diveSpotsMap.remove(new LatLng(Double.valueOf(diveSpot.getLat()), Double.valueOf(diveSpot.getLng())));
        diveSpots.remove(diveSpot);
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

    private class DiveSpotsClusterManager extends ClusterManager<DiveSpot> {

        public DiveSpotsClusterManager(Context context, GoogleMap map) {
            super(context, map);

            setAlgorithm(new GridBasedAlgorithm<DiveSpot>());
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
            Intent i = new Intent(mContext, DivePlaceActivity.class);
            i.putExtra(PRODUCT, String.valueOf(diveSpotsMap.get(marker.getPosition()).getId()));
            mContext.startActivity(i);
        }
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

    private class IconRenderer extends DefaultClusterRenderer<DiveSpot> {

        public IconRenderer(Context context, GoogleMap map, ClusterManager<DiveSpot> clusterManager) {
            super(context, map, clusterManager);
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

}

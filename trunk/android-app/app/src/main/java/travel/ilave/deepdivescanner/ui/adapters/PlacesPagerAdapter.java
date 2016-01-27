package travel.ilave.deepdivescanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.entities.DivespotsWrapper;
import travel.ilave.deepdivescanner.entities.Filters;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.fragments.ProductListFragment;
import travel.ilave.deepdivescanner.utils.LogUtils;


public class PlacesPagerAdapter extends FragmentStatePagerAdapter implements GoogleMap.OnCameraChangeListener {

    private Context context;
    private LatLng latLng;
    private FragmentManager fm;
    private PlacesPagerAdapter placesPagerAdapter;
    private DivespotsWrapper divespotsWrapper;
    private  static ArrayList<DiveSpot> divespots;
    private ProductListAdapter productListAdapter;
    private static GoogleMap gMap;
    private Filters filters;
    private ProgressDialog progressDialog;

    public PlacesPagerAdapter(Context context, FragmentManager fm, ArrayList<DiveSpot> divespots, LatLng latLng, Filters filters) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.divespots = divespots;
        this.latLng = latLng;
        this.filters = filters;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new MapFragment();
                ((MapFragment) fragment).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8.0f));
                        gMap = googleMap;
                        final double radiusMax = radius(googleMap.getCameraPosition().target, googleMap.getProjection().getVisibleRegion().latLngBounds.northeast);
                        requestCityProducts(googleMap.getCameraPosition().target, radiusMax);
                        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                            @Override
                            public void onCameraChange(CameraPosition cameraPosition) {
                                googleMap.clear();
                                googleMap.setInfoWindowAdapter(new InfoWindowAdapter(context, divespots, googleMap));
                                final double radiusMax = radius(googleMap.getCameraPosition().target, googleMap.getProjection().getVisibleRegion().latLngBounds.northeast);
                                requestCityProducts(googleMap.getCameraPosition().target, radiusMax);
                            }
                        });
                    }
                });
                break;
            case 1:
                fragment = new ProductListFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "MAP";
            case 1:
                return "LIST";
            default:
                return "";
        }
    }


    public double radius(LatLng latCenter, LatLng topRightCorner) {
        double maxRadius = 0;

        maxRadius =  topRightCorner.latitude - latCenter.latitude;

        if ((topRightCorner.longitude - latCenter.longitude) > maxRadius) {
            maxRadius = topRightCorner.longitude - latCenter.longitude;
        }
        return maxRadius;
    }

   @Override
    public  void onCameraChange (CameraPosition cameraPosition) {
    }

    public void requestCityProducts(final LatLng center, Double radius) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("lat", String.valueOf(center.latitude));
        map.put("lng", String.valueOf(center.longitude));
        map.put("radius", String.valueOf(radius));
        RestClient.getServiceInstance().getDivespots(map, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                divespots = (ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots();
                placesPagerAdapter = new PlacesPagerAdapter(context, fm, (ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots(), center, filters);
                gMap.setInfoWindowAdapter(new InfoWindowAdapter(context, (ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots(), gMap));
                ProductListFragment.setadapter((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots());
            }

            @Override
            public void failure(RetrofitError error) {
                LogUtils.i("failure Message is " + error.getMessage());
                LogUtils.i("failure body is " + error.getBody());
                if (error.getCause() instanceof SocketTimeoutException) {
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {

                    } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {

                    }
                }
                // TODO Handle result handling when activity stopped
                // TODO Handle errors
            }
        });
    }

    public static LatLng getLastLatlng() { return gMap.getCameraPosition().target; }
}

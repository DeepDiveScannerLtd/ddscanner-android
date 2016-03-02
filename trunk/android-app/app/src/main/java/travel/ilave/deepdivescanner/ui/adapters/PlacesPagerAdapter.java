package travel.ilave.deepdivescanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.entities.DivespotsWrapper;
import travel.ilave.deepdivescanner.entities.Filters;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.fragments.ProductListFragment;
import travel.ilave.deepdivescanner.utils.LogUtils;


public class PlacesPagerAdapter extends FragmentStatePagerAdapter implements GoogleMap.OnCameraChangeListener {

    private static final String TAG = PlacesPagerAdapter.class.getName();

    private Context context;
    private LatLng latLng;
    private FragmentManager fm;
    private PlacesPagerAdapter placesPagerAdapter;
    private DivespotsWrapper divespotsWrapper;
    private static ArrayList<DiveSpot> divespots = new ArrayList<>();
    private static Map<String, String> map = new HashMap<>();
    private static GoogleMap gMap;
    private HashMap<String, String> filters = new HashMap<String, String>();
    private String path;
    private InfoWindowAdapter infoWindowAdapter;
    private MapFragment mapFragment;
    private ProductListFragment productListFragment;

    public PlacesPagerAdapter(Context context, FragmentManager fm, ArrayList<DiveSpot> divespots, LatLng latLng, HashMap<String, String> filters) {
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
                mapFragment = new MapFragment();
                fragment = mapFragment;
                ((MapFragment) fragment).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        if (latLng.longitude == 0 && latLng.latitude == 0) {
                            latLng = new LatLng(53.902378, 27.557184);
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8.0f));
                        gMap = googleMap;
                        requestCityProducts(googleMap.getProjection().getVisibleRegion().latLngBounds.southwest,googleMap.getProjection().getVisibleRegion().latLngBounds.northeast, googleMap.getCameraPosition().target);
                        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                            @Override
                            public void onCameraChange(CameraPosition cameraPosition) {
                                requestCityProducts(googleMap.getProjection().getVisibleRegion().latLngBounds.southwest,googleMap.getProjection().getVisibleRegion().latLngBounds.northeast, googleMap.getCameraPosition().target);
                            }
                        });
                    }
                });
                break;
            case 1:
                productListFragment = new ProductListFragment();
                fragment = productListFragment;
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


//    public double radius(LatLng latCenter, LatLng topRightCorner) {
//        double maxRadius = 0;
//
//        maxRadius = topRightCorner.latitude - latCenter.latitude;
//
//        if ((topRightCorner.longitude - latCenter.longitude) > maxRadius) {
//            maxRadius = topRightCorner.longitude - latCenter.longitude;
//        }
//        return maxRadius;
//    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    public void requestCityProducts(LatLng left, LatLng right, final LatLng center) {
        map.put("latLeft", String.valueOf(left.latitude - 2.0));
        map.put("lngLeft", String.valueOf(left.longitude - 1.0));
        map.put("lngRight", String.valueOf(right.longitude + 1.0));
        map.put("latRight", String.valueOf(right.latitude + 2.0));
        if (filters != null) {
            if (!filters.get("visibility").equals("")) {
                map.put("visibility", filters.get("visibility").toLowerCase());
                System.out.println(filters.get("visibility"));
            }
            if (!filters.get("level").equals("")) {
                map.put("level", filters.get("level").toLowerCase());
                System.out.println(filters.get("level"));
            }
            if (!filters.get("currents").equals("")) {
                map.put("currents", filters.get("currents").toLowerCase());
                System.out.println(filters.get("currents"));
            }
        }
        RestClient.getServiceInstance().getDivespots(map, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i(TAG, "response code is " + s.getStatus());
                LogUtils.i(TAG, "response body is " + responseString);
                divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                divespots = (ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots();
                        if (placesPagerAdapter == null) {
                            placesPagerAdapter = new PlacesPagerAdapter(context, fm, (ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots(), center, filters);
                        }
                        if (infoWindowAdapter == null) {
                            infoWindowAdapter = new InfoWindowAdapter(context, (ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots(), gMap);
                            gMap.setInfoWindowAdapter(infoWindowAdapter);
                        } else {
                            infoWindowAdapter.updateDiveSpots(divespots);
                        }
                        productListFragment.fillDiveSpots((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                    Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                    Toast.makeText(context, "Server is not responsible, please try later", Toast.LENGTH_SHORT).show();
                }
               String json =  new String(((TypedByteArray)error.getResponse().getBody()).getBytes());
                System.out.println("failure" + json.toString());
            }
        });
    }

    public static LatLng getLastLatlng() {
        if (gMap == null) {
            return null;
        } else {
            return gMap.getCameraPosition().target;
        }
    }

//    public static Map<String, String> getLastRequest() { return map; }
}

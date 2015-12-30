package travel.ilave.deepdivescanner.ui.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.ui.activities.DivePlaceActivity;
import travel.ilave.deepdivescanner.ui.dialogs.ProductInfoDialog;

/**
 * Created by lashket on 10.12.15.
 */
public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    public static final String PRODUCT = "PRODUCT";

    private View view;
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<Product> products;
    private TextView title;
    private TextView from;
    private Product product;
    private GoogleMap googleMap;
    private HashMap<Marker, Product> markersMap = new HashMap<>();
    private ProductInfoDialog.OnExploreClickListener listener;
    private TextView description;

    /**
     *
     * @param context
     * @param prdct
     * @param map
     */
    public InfoWindowAdapter(Context context, ArrayList<Product> prdct, GoogleMap map) {
        products = prdct;
        mContext = context;
        googleMap = map;
        googleMap.setOnInfoWindowClickListener(this);
        for (Product product : products) {
            LatLng place = new LatLng(Double.valueOf(product.getLat()), Double.valueOf(product.getLng()));
            Marker marker = googleMap.addMarker(new MarkerOptions().position(place).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pin)));
            markersMap.put(marker, product);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        product = markersMap.get(marker);
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.info_window, null);
        description = ((TextView) view.findViewById(R.id.description_popup));
        title = ((TextView)view.findViewById(R.id.popup_product_name));
        from = ((TextView)view.findViewById(R.id.price1));
        LinearLayout stars = (LinearLayout) view.findViewById(R.id.stars);
        title.setText(product.getName());
        description.setText(product.getDescription());
        for (int i = 0; i < product.getRating(); i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.mipmap.ic_redflag);
            stars.addView(iv);
        }
        for (int i = 0; i < 5 - product.getRating(); i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.mipmap.ic_grayflag);
            iv.setAlpha(0.6f);
            stars.addView(iv);
        }
        String price = String.valueOf(product.getPrice());
        from.setText(price);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        // onProductSelectedListener.onProductSelected(markersMap.get(marker));
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        System.out.println("---------");
        Intent i = new Intent(mContext, DivePlaceActivity.class);
        i.putExtra(PRODUCT, markersMap.get(marker));
        mContext.startActivity(i);
    }

}

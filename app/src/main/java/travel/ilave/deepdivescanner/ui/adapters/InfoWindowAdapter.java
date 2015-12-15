package travel.ilave.deepdivescanner.ui.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.ui.dialogs.ProductInfoDialog;

/**
 * Created by lashket on 10.12.15.
 */
public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private View view;
    private LayoutInflater inflater;
    private Context mContext;
    private Product product;
    private TextView title;
    private TextView from;

    public InfoWindowAdapter(Context context, Product prdct) {
        product = prdct;
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.info_window, null);
        title = ((TextView)view.findViewById(R.id.markerTitle));
        from = ((TextView)view.findViewById(R.id.price1));
        LinearLayout stars = (LinearLayout) view.findViewById(R.id.stars);
        title.setText(product.getName());
        for (int i = 0; i < product.getRating(); i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.drawable.ic_star_white_24dp);
            stars.addView(iv);
        }
        for (int i = 0; i < 5 - product.getRating(); i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.drawable.ic_star_border_white_24dp);
            iv.setAlpha(0.6f);
            stars.addView(iv);
        }
        String price = String.valueOf(product.getPrice());
        from.setText("From " + price+ "$");
        System.out.println(product.getPrice());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }



}

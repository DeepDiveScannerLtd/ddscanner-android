package travel.ilave.deepdivescanner.ui.adapters;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URI;
import java.util.ArrayList;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.entities.ProductDetails;
import travel.ilave.deepdivescanner.ui.activities.CityActivity;
import travel.ilave.deepdivescanner.ui.activities.DivePlaceActivity;

/**
 * Created by lashket on 23.12.15.
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>{

    public static ArrayList<DiveSpot> divespots;
    private Context conText;

    public ProductListAdapter(ArrayList<DiveSpot> divespots, Context conText) {
        this.divespots = divespots;
        this.conText = conText;
    }

    @Override
    public ProductListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.product_item, viewGroup, false);
        return new ProductListViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ProductListViewHolder productListViewHolder, int i) {
        DiveSpot divespot = divespots.get(i);
//        productListViewHolder.productPrice.setText(String.valueOf("15"));
        productListViewHolder.description.setText(divespot.getDescription());
        if (divespot.getImages() != null) {
            Picasso.with(conText).load(divespot.getImages().get(0)).resize(140, 150).into(productListViewHolder.imageView);
        }
        if(divespot.getName() != null) {
             productListViewHolder.title.setText(divespot.getName());
        }
        productListViewHolder.stars.removeAllViews();
        for (int k = 0; k < divespot.getRating(); k++) {
            ImageView iv = new ImageView(conText);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(5,0,0,0);
            productListViewHolder.stars.addView(iv);
        }
        for (int k = 0; k < 5 - divespot.getRating(); k++) {
            ImageView iv = new ImageView(conText);
            iv.setImageResource(R.drawable.ic_flag_empty_small);
            iv.setPadding(5,0,0,0);
            productListViewHolder.stars.addView(iv);
        }
    }

    @Override
    public int getItemCount() {
        if (divespots == null) { return 0; }
        else { return divespots.size(); }
    }

    public static class ProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView title;
        protected TextView  description;
        protected TextView productPrice;
        protected TextView  price;
        protected LinearLayout stars;
        private int position;
        private static Context context;
        private final String PRODUCT = "PRODUCT";

        public ProductListViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            v.setOnClickListener(this);
           // productPrice = (TextView) v.findViewById(R.id.product_price);
            imageView = (ImageView) v.findViewById(R.id.product_logo);
            title = (TextView) v.findViewById(R.id.product_title);
            description = (TextView) v.findViewById(R.id.product_description);
            stars = (LinearLayout) v.findViewById(R.id.stars);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, DivePlaceActivity.class);
            i.putExtra(PRODUCT, divespots.get(getPosition()).getId());
            context.startActivity(i);
        }
    }



}

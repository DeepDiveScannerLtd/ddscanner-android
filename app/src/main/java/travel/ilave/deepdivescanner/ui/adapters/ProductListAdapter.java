package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.URI;
import java.util.ArrayList;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.entities.ProductDetails;
import travel.ilave.deepdivescanner.ui.activities.DivePlaceActivity;

/**
 * Created by lashket on 23.12.15.
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>{

    public static ArrayList<Product> products;

    public ProductListAdapter(ArrayList<Product> products) {
        this.products = products;
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
        Product product = products.get(i);
        productListViewHolder.productPrice.setText(String.valueOf(product.getPrice()));
       // productListViewHolder.description.setText(product.getDescription());
        productListViewHolder.title.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView title;
        protected TextView  description;
        protected TextView productPrice;
        protected TextView  price;
        private int position;
        private static Context context;
        private final String PRODUCT = "PRODUCT";

        public ProductListViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            v.setOnClickListener(this);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            imageView = (ImageView) v.findViewById(R.id.product_logo);
            title = (TextView) v.findViewById(R.id.product_title);
            description = (TextView) v.findViewById(R.id.product_description);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, DivePlaceActivity.class);
            i.putExtra(PRODUCT, products.get(getPosition()));
            context.startActivity(i);
            System.out.println(products.get(getPosition()).getName());
        }
    }


}

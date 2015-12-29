package travel.ilave.deepdivescanner.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.ui.adapters.ProductListAdapter;

/**
 * Created by lashket on 23.12.15.
 */
public class ProductListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        ArrayList<Product> products = args.getParcelableArrayList("PRODUCTS");
        View view = inflater.inflate(R.layout.product_list_fragment, container, false);
        RecyclerView rc = (RecyclerView) view.findViewById(R.id.cv);
        
        rc.setHasFixedSize(true);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));
        rc.setAdapter(new ProductListAdapter(products));

        return view;
    }
}

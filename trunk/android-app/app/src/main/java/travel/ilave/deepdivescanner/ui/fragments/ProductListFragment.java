package travel.ilave.deepdivescanner.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.ui.adapters.PlacesPagerAdapter;
import travel.ilave.deepdivescanner.ui.adapters.ProductListAdapter;

/**
 * Created by lashket on 23.12.15.
 */
public class ProductListFragment extends Fragment {

    private static RecyclerView rc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.product_list_fragment, container, false);
        rc = (RecyclerView) view.findViewById(R.id.cv);
        
        rc.setHasFixedSize(true);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public static void setadapter(ArrayList<DiveSpot> diveSpots) {
        rc.setAdapter(new ProductListAdapter(diveSpots));
    }

}

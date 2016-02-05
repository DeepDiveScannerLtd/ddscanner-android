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
import travel.ilave.deepdivescanner.entities.DiveCenter;
import travel.ilave.deepdivescanner.ui.adapters.DiveCentersListAdapter;

/**
 * Created by lashket on 4.2.16.
 */
public class DiveCenterListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        ArrayList<DiveCenter> divecenters = args.getParcelableArrayList("DIVESPOTS");
        View view = inflater.inflate(R.layout.dive_center_list_fragment, container, false);
        RecyclerView rc = (RecyclerView) view.findViewById(R.id.dc_rc);
        rc.setHasFixedSize(true);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));
        rc.setAdapter(new DiveCentersListAdapter(divecenters));
        return view;
    }
}

package travel.ilave.deepdivescanner.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.ui.adapters.DiveCentersAdapter;

/**
 * Created by lashket on 4.2.16.
 */
public class DiveCenterListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = this.getArguments();

        View view = inflater.inflate(R.layout.dive_center_list_fragment, container, false);
        RecyclerView rc = (RecyclerView) view.findViewById(R.id.dc_rc);
        rc.setHasFixedSize(true);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));
        rc.setAdapter(new DiveCentersAdapter());
        return view;
    }
}

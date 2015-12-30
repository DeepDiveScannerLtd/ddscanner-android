package travel.ilave.deepdivescanner.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import travel.ilave.deepdivescanner.R;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class PlaceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

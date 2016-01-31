package travel.ilave.deepdivescanner.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;

import travel.ilave.deepdivescanner.ui.views.OnMapTouchedListener;
import travel.ilave.deepdivescanner.ui.views.TouchableWrapper;

public class TouchableMapFragment extends MapFragment implements OnMapTouchedListener {
    private View originalContentView;
    private TouchableWrapper touchView;
    private OnMapTouchedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        originalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        touchView = new TouchableWrapper(getActivity());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        touchView.setLayoutParams(lp);
        touchView.addView(originalContentView);
        touchView.setOnMapTouchedListener(this);
        return touchView;
    }

    @Override
    public View getView() {
        return touchView;
    }

    public void setOnMapTouchedListener(OnMapTouchedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMapTouchedDown() {
        if (listener != null) {
            listener.onMapTouchedDown();
        }
    }

    @Override
    public void onMapTouchedUp() {

    }
}

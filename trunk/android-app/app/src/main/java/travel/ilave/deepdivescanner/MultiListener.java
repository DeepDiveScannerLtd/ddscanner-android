package travel.ilave.deepdivescanner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 18.2.16.
 */
public class MultiListener implements GoogleMap.OnCameraChangeListener {
    private List<GoogleMap.OnCameraChangeListener> mListeners = new ArrayList<GoogleMap.OnCameraChangeListener>();

    public void registerListener (GoogleMap.OnCameraChangeListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
        for (GoogleMap.OnCameraChangeListener ccl: mListeners)
        {
            ccl.onCameraChange(cameraPosition);
        }
    }

}
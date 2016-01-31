package travel.ilave.deepdivescanner.ui.views;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {

    private OnMapTouchedListener listener;

    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (listener != null) {
                    listener.onMapTouchedDown();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    listener.onMapTouchedUp();
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public void setOnMapTouchedListener(OnMapTouchedListener listener) {
        this.listener = listener;
    }
}

package travel.ilave.deepdivescanner;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class DDScannerApplication extends Application {

    private static DDScannerApplication instance;

    public static DDScannerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        Fresco.initialize(this);
    }
}

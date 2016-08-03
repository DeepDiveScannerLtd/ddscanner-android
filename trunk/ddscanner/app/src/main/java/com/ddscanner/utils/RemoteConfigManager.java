package com.ddscanner.utils;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ddscanner.BuildConfig;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.RemoteConfigFetchedEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class RemoteConfigManager {

    private static final String TAG = RemoteConfigManager.class.getName();

    public static final String KEY_REQUIRE_APP_UPDATE = "android_debug_require_app_update";

    private static FirebaseRemoteConfig firebaseRemoteConfig;

    private RemoteConfigManager() {

    }

    public static void initRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        firebaseRemoteConfig.fetch(1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "firebaseRemoteConfig fetched successfully");
                            firebaseRemoteConfig.activateFetched();
                            DDScannerApplication.bus.post(new RemoteConfigFetchedEvent());
                        } else {
                            Log.i(TAG, "firebaseRemoteConfig fetch fail");
                        }
                    }
                });

    }

    public static FirebaseRemoteConfig getFirebaseRemoteConfig() {
        return firebaseRemoteConfig;
    }
}

package com.ddscanner.utils;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.R;
import com.ddscanner.entities.User;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.events.ShowUserDialogEvent;
import com.ddscanner.ui.activities.SocialNetworks;
import com.ddscanner.ui.dialogs.ProfileDialog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lashket on 9.4.16.
 */
public class Helpers {

    /**
     * Method to get real path of file by URI
     * @param context
     * @param contentUri
     * @return Path to image
     * @author Andrei Lashkevich
     */

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * COnverting dp to pixels size
     * @param dp
     * @param context
     * @return dp value in pixels size
     * @author Andrei Lashkevich
     */

    public float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * Show dialog with user information
     * @param user
     * @param fragmentManager
     * @author Andrei Lashkevich
     */

    public void showDialog(User user, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("profile");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment dialogFragment = ProfileDialog.newInstance(user);
        dialogFragment.show(fragmentTransaction, "profile");
    }

    /**
     * Add path to name of image
     * @param images
     * @param path
     * @return full URL's array
     * @author Andrei Lashkevich
     */

    public ArrayList<String> compareImageWithPath(ArrayList<String> images, String path) {
        for (int i = 0; i <images.size(); i++) {
            images.set(i, path + images.get(i));
        }
        return images;
    }

    public RegisterRequest getRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        if (!SharedPreferenceHelper.getIsUserLogined()) {
            registerRequest.setAppId(SharedPreferenceHelper.getUserAppId());
            registerRequest.setpush(SharedPreferenceHelper.getGcmId());
            return registerRequest;
        }

        registerRequest.setSocial(SharedPreferenceHelper.getSn());
        registerRequest.setToken(SharedPreferenceHelper.getToken());
        if (SharedPreferenceHelper.getSn().equals("tw")) {
            registerRequest.setSecret(SharedPreferenceHelper.getSecret());
        }
        registerRequest.setAppId(SharedPreferenceHelper.getUserAppId());
        registerRequest.setpush(SharedPreferenceHelper.getGcmId());
        return registerRequest;
    }

    /**
     * Comparing two arrays to third
     * @param first
     * @param second
     * @return compared array
     * @author Andrei Lashkevich
     */

    public ArrayList<String> compareArrays(ArrayList<String> first, ArrayList<String> second) {
        ArrayList<String> allPhotos = new ArrayList<>();
        if (first != null) {
            allPhotos = (ArrayList<String>) first.clone();
            for (int i = 0; i < second.size(); i++) {
                allPhotos.add(second.get(i));
            }
            return allPhotos;
        }
        allPhotos =(ArrayList<String>) second.clone();
        return allPhotos;
    }

    /**
     * Change key-value params to value-keys to using this in spinners
     * @param map
     * @return mirror map
     * @author Andrei Lashkevich
     */

    public Map<String, String> getMirrorOfHashMap(Map<String, String> map) {
        Map<String, String> returnMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            returnMap.put(entry.getValue(), entry.getKey());
        }
        return returnMap;
    }

    /**
     * Handling errors and showing this in textviews
     * @param context
     * @param errorsMap
     * @param errors
     */

    public void errorHandling( Context context,
                             Map<String,TextView> errorsMap, String errors) {
        JsonObject jsonObject = new JsonParser().parse(errors).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if(!entry.getKey().equals("")) {
                if (entry.getKey().equals("token")) {
                    return;
                }
                if (errorsMap.get(entry.getKey()) != null) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    value = value.replace("[\"", "");
                    value = value.replace("\"]", "");
                    errorsMap.get(key).setVisibility(View.VISIBLE);
                    errorsMap.get(key).setText(value);
                }
            }
        }
    }

    public boolean checkIsErrorByLogin(String errors) {
        if (errors.contains("token") || errors.contains("social") || errors.contains("secret")) {
            return true;
        }
        return false;
    }

    public boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public Map<String, String> getUserQuryMapRequest() {
        Map<String, String> map = new HashMap<>();
        if (SharedPreferenceHelper.getIsUserLogined()) {
            map.put("social", SharedPreferenceHelper.getSn());
            map.put("token", SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                map.put("secret",SharedPreferenceHelper.getSecret());
            }
        } else {
            return null;
        }
        return map;
    }

    public MaterialDialog getMaterialDialog(Context context) {
        MaterialDialog materialDialog;
        materialDialog = new MaterialDialog.Builder(context)
                .cancelable(false)
                .content("Please wait...").progress(true, 0)
                .contentColor(context.getResources().getColor(R.color.black_text))
                .widgetColor(context.getResources().getColor(R.color.primary)).build();
        return materialDialog;
    }

}

package com.ddscanner.utils;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import com.ddscanner.entities.User;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.events.ShowUserDialogEvent;
import com.ddscanner.ui.dialogs.ProfileDialog;

import java.util.ArrayList;

/**
 * Created by lashket on 9.4.16.
 */
public class Helpers {

    /**
     * Method to get real path of file by URI
     * @param context
     * @param contentUri
     * @return Path to image
     */

    public static String getRealPathFromURI(Context context, Uri contentUri) {
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
     */

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * Show dialog with user information
     * @param user
     * @param fragmentManager
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
            return null;
        }

        registerRequest.setSocial(SharedPreferenceHelper.getSn());
        registerRequest.setToken(SharedPreferenceHelper.getToken());
        if (SharedPreferenceHelper.getSn().equals("tw")) {
            registerRequest.setSecret(SharedPreferenceHelper.getSecret());
        }
        return registerRequest;
    }

    /**
     * Comparing two arrays to third
     * @param first
     * @param second
     * @return compared array
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

}

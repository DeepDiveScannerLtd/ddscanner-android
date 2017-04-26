package com.ddscanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Countries;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.Image;
import com.ddscanner.entities.SealifeShort;
import com.google.gson.Gson;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by lashket on 9.4.16.
 */
public class Helpers {

    private static final String TAG = Helpers.class.getName();

    private Helpers() {

    }

    /**
     * Method to get real path of file by URI
     *
     * @param context
     * @param contentUri
     * @return Path to image
     * @author Andrei Lashkevich
     */

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
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
     *
     * @param dp
     * @param context
     * @return dp value in pixels size
     * @author Andrei Lashkevich
     */

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * Add path to name of image
     *
     * @param images
     * @param path
     * @return full URL's array
     * @author Andrei Lashkevich
     */

    public static ArrayList<String> appendImagesWithPath(ArrayList<String> images, String path) {
        if (images == null) {
            return images;
        }
        for (int i = 0; i < images.size(); i++) {
            images.set(i, path + images.get(i));
        }
        return images;
    }

    public static ArrayList<Image> appendFullImagesWithPath(ArrayList<Image> images, String path) {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setName(path + images.get(i).getName());
        }
        return images;
    }

    /**
     * Comparing two arrays to third
     *
     * @param first
     * @param second
     * @return compared array
     * @author Andrei Lashkevich
     */

    public static ArrayList<String> compareArrays(ArrayList<String> first, ArrayList<String> second) {
        ArrayList<String> allPhotos = new ArrayList<>();
        if (first != null) {
            allPhotos = (ArrayList<String>) first.clone();
            for (int i = 0; i < second.size(); i++) {
                allPhotos.add(second.get(i));
            }
            return allPhotos;
        }
        allPhotos = (ArrayList<String>) second.clone();
        return allPhotos;
    }

    public static ArrayList<DiveSpotPhoto> compareObjectsArray(ArrayList<DiveSpotPhoto> first, ArrayList<DiveSpotPhoto> second) {
        ArrayList<DiveSpotPhoto> allPhotos = new ArrayList<>();
        if (first == null && second == null) {
            return allPhotos;
        }
        if (first != null) {
            allPhotos = (ArrayList<DiveSpotPhoto>) first.clone();
            if (second != null) {
                for (int i = 0; i < second.size(); i++) {
                    allPhotos.add(second.get(i));
                }
            }
            return allPhotos;
        }
        if (second != null) {
            allPhotos = (ArrayList<DiveSpotPhoto>) second.clone();
        }
        return allPhotos;
    }


    /**
     * Change key-value params to value-keys to using this in spinners
     *
     * @param map
     * @return mirror map
     * @author Andrei Lashkevich
     */

    public static Map<String, String> getMirrorOfHashMap(Map<String, String> map) {
        Map<String, String> returnMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            returnMap.put(entry.getValue(), entry.getKey());
        }
        return returnMap;
    }

    /**
     * Handling errors and showing this in textviews
     *
     * @param errorsMap
     * @param validationError
     */
    public static void errorHandling(Map<String, TextView> errorsMap, String validationError) {
        Map<String, ArrayList<String>> fields = new HashMap<>();
        fields = new Gson().fromJson(validationError, fields.getClass());
        for (Map.Entry<String, ArrayList<String>> entry : fields.entrySet()) {
            if (errorsMap.get(entry.getKey()) != null) {
                errorsMap.get(entry.getKey()).setText(entry.getValue().get(0));
                errorsMap.get(entry.getKey()).setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Check if has internet connection
     *
     * @param context
     * @return
     * @author Andrei Lashkevich
     */

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //get all networks information
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            int i;

            //checking internet connectivity
            for (i = 0; i < networks.length; ++i) {
                if (cm.getNetworkInfo(networks[i]).getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
            return false;
        } else {
            NetworkInfo wifiInfo = cm.getActiveNetworkInfo();
            if (wifiInfo != null) {
                return true;
            }
            return false;
        }
    }

    public static MaterialDialog getMaterialDialog(Context context) {
        MaterialDialog materialDialog;
        materialDialog = new MaterialDialog.Builder(context)
                .cancelable(false)
                .content("Please wait...").progress(true, 0)
                .contentColor(ContextCompat.getColor(context, R.color.black_text))
                .widgetColor(ContextCompat.getColor(context, R.color.primary)).build();
        return materialDialog;
    }

    public static String getDate(String date) {
        Date date1 = new Date();
        long currentDateInMillis = date1.getTime();
        long differenceOfTime = 0;
        long incomingDateInMillis = 0;
        int yearsSeconds = 3600 * 24 * 365;
        int monthSeconds = 3600 * 24 * 30;
        int weeksSeconds = 3600 * 24 * 7;
        int daysSeconds = 3600 * 24;
        int hourSeconds = 3600;
        int minuteSeconds = 60;
        String returnString = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            Date incomingDate = format.parse(date);
            incomingDateInMillis = incomingDate.getTime();
            differenceOfTime = currentDateInMillis - incomingDateInMillis;
            differenceOfTime = differenceOfTime / 1000;
            if ((differenceOfTime / yearsSeconds) > 0) {
                return String.valueOf(differenceOfTime / yearsSeconds) + "y";
            }
            if ((differenceOfTime / monthSeconds) > 0) {
                return String.valueOf(differenceOfTime / monthSeconds) + "m";
            }
            if ((differenceOfTime / weeksSeconds) > 0) {
                return String.valueOf(differenceOfTime / weeksSeconds) + "w";
            }
            if ((differenceOfTime / daysSeconds) > 0) {
                return String.valueOf(differenceOfTime / daysSeconds) + "d";
            }
            if ((differenceOfTime / hourSeconds) > 0) {
                return String.valueOf(differenceOfTime / hourSeconds) + "h";
            }
            if ((differenceOfTime / minuteSeconds) > 0) {
                return String.valueOf(differenceOfTime / minuteSeconds) + "m";
            }
            if (differenceOfTime > 0 && differenceOfTime < 60) {
                return String.valueOf(differenceOfTime) + "s";
            }
        } catch (ParseException e) {
            return "";
        }
        return returnString;
    }

    public static String getCommentDate(String date) {
        Date date1 = new Date();
        long currentDateInMillis = date1.getTime();
        long differenceOfTime = 0;
        long incomingDateInMillis = 0;
        int yearsSeconds = 3600 * 24 * 365;
        int monthSeconds = 3600 * 24 * 30;
        int weeksSeconds = 3600 * 24 * 7;
        int daysSeconds = 3600 * 24;
        int hourSeconds = 3600;
        int minuteSeconds = 60;
        String returnString = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            Date incomingDate = format.parse(date);
            incomingDateInMillis = incomingDate.getTime();
            differenceOfTime = currentDateInMillis - incomingDateInMillis;
            differenceOfTime = differenceOfTime / 1000;
            if ((differenceOfTime / yearsSeconds) > 0) {
                if ((differenceOfTime / yearsSeconds) == 1) {
                    return String.valueOf(differenceOfTime / yearsSeconds) + " year ago";
                }
                return String.valueOf(differenceOfTime / yearsSeconds) + " years ago";
            }
            if ((differenceOfTime / monthSeconds) > 0) {
                if ((differenceOfTime / monthSeconds) == 1) {
                    return String.valueOf(differenceOfTime / monthSeconds) + " month ago";
                }
                return String.valueOf(differenceOfTime / monthSeconds) + " months ago";
            }
            if ((differenceOfTime / weeksSeconds) > 0) {
                if ((differenceOfTime / weeksSeconds) == 1) {
                    return String.valueOf(differenceOfTime / weeksSeconds) + " week ago";
                }
                return String.valueOf(differenceOfTime / weeksSeconds) + " weeks ago";
            }
            if ((differenceOfTime / daysSeconds) > 0) {
                if ((differenceOfTime) == 1) {
                    return String.valueOf(differenceOfTime / daysSeconds) + " day ago";
                }
                return String.valueOf(differenceOfTime / daysSeconds) + " days ago";

            }
            if ((differenceOfTime / hourSeconds) > 0) {
                if ((differenceOfTime) == 1) {
                    return String.valueOf(differenceOfTime / hourSeconds) + " hour ago";
                }
                return String.valueOf(differenceOfTime / hourSeconds) + " hours ago";
            }
            if ((differenceOfTime / minuteSeconds) > 0) {
                if ((differenceOfTime) == 1) {
                    return String.valueOf(differenceOfTime / minuteSeconds) + " minute ago";
                }
                return String.valueOf(differenceOfTime / minuteSeconds) + " minutes ago";
            }
            if (differenceOfTime > 0 && differenceOfTime < 60) {
                if ((differenceOfTime) == 1) {
                    return String.valueOf(differenceOfTime) + " second ago";
                }
                return String.valueOf(differenceOfTime) + " seconds ago";

            }
        } catch (ParseException e) {
            return "";
        }
        return returnString;
    }

    public static boolean comparingTimes(long lastShowingTime, String notificationTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        try {
            Date notificationDate = format.parse(notificationTime);
            if (lastShowingTime < notificationDate.getTime()) {
                return true;
            }
            return false;
        } catch (ParseException e) {
            return false;
        }

    }

    public static String convertDate(String incomingDate) {
        String returningString = "";
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        serverFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        SimpleDateFormat returnedFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        try {
            Date date = serverFormat.parse(incomingDate);
            returningString = returnedFormat.format(date);
        } catch (ParseException e) {

        }
        return returningString;
    }

    public static String convertDateToImageSliderActivity(String incomingDate) {
        String returningString = "";
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        serverFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        SimpleDateFormat returnedFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH);
        try {
            Date date = serverFormat.parse(incomingDate);
            returningString = returnedFormat.format(date);
        } catch (ParseException e) {

        }
        return returningString;
    }

    public static boolean checkIsSealifeAlsoInList(ArrayList<SealifeShort> sealifes, String id) {
        for (SealifeShort sealife : sealifes) {
            if (sealife.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static void showToast(Context context, int message) {
        Toast toast = Toast.makeText(context, context.getString(message), Toast.LENGTH_LONG);
        toast.show();
    }

    public static String formatLikesCommentsCountNumber(String count) {
        int itemsCount = Integer.parseInt(count);
        if (itemsCount > 999) {
            return String.valueOf(itemsCount / 1000) + "K";
        } else {
            return count;
        }
    }

    public static void hideKeyboard(Activity context) {
        if (context == null || context.getCurrentFocus() == null) {
            return;
        }
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void handleUnexpectedServerError(FragmentManager fragmentManager, String requestUrl, String errorMessage) {
        handleUnexpectedServerError(fragmentManager, requestUrl, errorMessage, R.string.error_server_error_title, R.string.error_unexpected_error);
    }

    public static void handleUnexpectedServerError(FragmentManager fragmentManager, String requestUrl, String errorMessage, int titleResId, int messageResId) {
        // TODO May be should use another tracking mechanism
        EventsTracker.trackUnknownServerError(requestUrl, errorMessage);
//        InfoDialogFragment.show(fragmentManager, titleResId, messageResId, false);
    }

    public static Countries getCountries() {
        Countries countries = null;
        Serializer serializer = new Persister();
        String fileName;
        switch (Locale.getDefault().getLanguage()) {
            case "ru":
//                fileName = "countries/countries_ru.xml";
//                break;
            case "en":
            default:
                fileName = "countries/countries_en.xml";
                break;
        }
        try {
            countries = serializer.read(Countries.class, DDScannerApplication.getInstance().getAssets().open(fileName));
        } catch (Exception e) {
            // Should not happen
            throw new RuntimeException(e);
        }
        return countries;
    }

    /**
     * It would be used like this:
     * getResId("icon", R.drawable.class);
     * @param resName
     * @param c
     * @return
     */
    public static int getResId(String resName, Class<?> c) {
        try {
            java.lang.reflect.Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isFileImage(String file) {
        final String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};
        for (String extension : okFileExtensions) {
            if (file.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static void copyFileStream(File dest, Uri uri, Context context) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }

    public static int getUserType(String userType) {
        List<String> list = new ArrayList<>();
        list.add(Constants.USER_TYPE_DIVE_CENTER);
        list.add(Constants.USER_TYPE_DIVER);
        list.add(Constants.USER_TYPE_INSTRUCTOR);
        return list.indexOf(userType);
    }

    public static List<String> getDiveSpotTypes() {
        List<String> types = new ArrayList<>();
        types.add("Cave");
        types.add("Reef");
        types.add("Wreck");
        types.add("Other");
        return types;
    }

    public static List<String> getDiveLevelTypes() {
        List<String> levels = new ArrayList<>();
        levels.add("Beginner");
        levels.add("Advanced");
        levels.add("Expert");
        return levels;
    }

    public static List<String> getListOfCurrentsTypes() {
        List<String> currents = new ArrayList<>();
        currents.add("None");
        currents.add("Variable");
        currents.add("Low");
        currents.add("Low - Moderate");
        currents.add("Mild");
        currents.add("Mild - Moderate");
        currents.add("Moderate");
        currents.add("Moderate - Strong");
        currents.add("Strong");
        return currents;
    }

    public static String getDiveSpotType(int position) {
        List<String> types = getDiveSpotTypes();
        if (types.get(position - 1) == null) {
            return "";
        }
        return types.get(position - 1);
    }

    public static String getDiverLevel(Integer position) {
        if (position != null && position > 0) {
            List<String> levels = getDiveLevelTypes();
            if (levels.get(position - 1) == null) {
                return "";
            }
            return levels.get(position - 1);
        }
        return "";
    }

    public static String getCurrentsValue(int position) {
        List<String> currents = getListOfCurrentsTypes();
        if (currents.get(position - 1) == null) {
            return "";
        }
        return currents.get(position - 1);
    }

    public static String getUserType(int position) {
        List<String> userType = new ArrayList<>();
        userType.add("Dive center");
        userType.add("Diver");
        userType.add("Instructor");
        if (userType.get(position) == null) {
            return "";
        }
        return userType.get(position);
    }

    public static ArrayList<String> getPhotosFromIntent(Intent data, Activity activity) {
        if (data.getClipData() != null) {
            return getPhotosList(data, activity);
        }
        return getOnePhoto(data, activity);
    }

    private static ArrayList<String> getPhotosList(Intent data, Activity activity) {
        Uri uri = Uri.parse("");
        ArrayList<String> urisList = new ArrayList<>();
        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
            String filename = "DDScanner" + String.valueOf(System.currentTimeMillis());
            try {
                uri = data.getClipData().getItemAt(i).getUri();
                String mimeType = activity.getContentResolver().getType(uri);
                String sourcePath = activity.getExternalFilesDir(null).toString();
                File file = new File(sourcePath + "/" + filename);
                if (Helpers.isFileImage(uri.getPath()) || mimeType.contains("image")) {
                    try {
                        Helpers.copyFileStream(file, uri, activity);
                        Log.i(TAG, file.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    urisList.add(file.getPath());
                } else {
                    Toast.makeText(activity, "You can choose only images", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return urisList;
            }
        }
        return urisList;
    }

    private static ArrayList<String> getOnePhoto(Intent data, Activity activity) {
        String filename = "DDScanner" + String.valueOf(System.currentTimeMillis());
        Uri uri = Uri.parse("");
        ArrayList<String> urisList = new ArrayList<>();
        try {
            uri = data.getData();
            String mimeType = activity.getContentResolver().getType(uri);
            String sourcePath = activity.getExternalFilesDir(null).toString();
            File file = new File(sourcePath + "/" + filename);
            if (Helpers.isFileImage(uri.getPath()) || mimeType.contains("image")) {
                try {
                    Helpers.copyFileStream(file, uri, activity);
                    Log.i(TAG, file.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                urisList.add(file.getPath());
            } else {
                Toast.makeText(activity, "You can choose only images", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return urisList;
        }
        return urisList;
    }

    public static RequestBody createRequestBodyForString(String string) {
        return RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), string);
    }

    public static ArrayList<String> getReportTypes() {
        ArrayList<String> reportTypes = new ArrayList<>();
        reportTypes.add("Adult content");
        reportTypes.add("Child pornography");
        reportTypes.add("Violence");
        reportTypes.add("Advocacy");
        reportTypes.add("Insult");
        reportTypes.add("Spam");
        reportTypes.add("Other");
        return reportTypes;
    }

    public static File compressFile(File inputFile, Context context) {
        Compressor compressor = new Compressor.Builder(context).setMaxHeight(1080).setMaxHeight(1080).build();
        File outputFile = compressor.compressToFile(inputFile);
        return outputFile;
    }

}

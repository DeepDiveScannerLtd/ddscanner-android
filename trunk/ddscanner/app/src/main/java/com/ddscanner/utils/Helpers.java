package com.ddscanner.utils;

import android.annotation.SuppressLint;
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
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Association;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.entities.Countries;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.Image;
import com.ddscanner.entities.SealifeShort;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

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

public class Helpers {

    private static final String TAG = Helpers.class.getName();

    private Helpers() {

    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static int convertDpToIntPixels(float dp, Context context) {
        return Math.round(convertDpToPixel(dp, context));
    }

    public static ArrayList<DiveSpotPhoto> compareObjectsArray(ArrayList<DiveSpotPhoto> first, ArrayList<DiveSpotPhoto> second) {
        ArrayList<DiveSpotPhoto> allPhotos = new ArrayList<>();
        if (first == null && second == null) {
            return allPhotos;
        }
        if (first != null) {
            allPhotos = (ArrayList<DiveSpotPhoto>) first.clone();
            if (second != null) {
                allPhotos.addAll(second);
            }
            return allPhotos;
        }
        if (second != null) {
            allPhotos = (ArrayList<DiveSpotPhoto>) second.clone();
        }
        return allPhotos;
    }

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
            return wifiInfo != null;
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

    @SuppressLint("SimpleDateFormat")
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
                return String.valueOf(differenceOfTime / minuteSeconds) + "min";
            }
            if (differenceOfTime > 0 && differenceOfTime < 60) {
                return String.valueOf(differenceOfTime) + "s";
            }
        } catch (ParseException e) {
            return "";
        }
        return returnString;
    }

    @SuppressLint("SimpleDateFormat")
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

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToImageSliderActivity(String incomingDate) {
        String returningString = "";
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        serverFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        SimpleDateFormat returnedFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH);
        try {
            Date date = serverFormat.parse(incomingDate);
            returningString = returnedFormat.format(date);
        } catch (ParseException ignored) {

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
//        EventsTracker.trackUnknownServerError(requestUrl, errorMessage);
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

    public static int getResId(String resName, Class<?> c) {
        try {
            java.lang.reflect.Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
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
        levels.add("Non-Diver");
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

    public static ArrayList<BaseIdNamePhotoEntity> getAssociationsList() {
        ArrayList<BaseIdNamePhotoEntity> associations = new ArrayList<>();
        associations.add(new BaseIdNamePhotoEntity("1", "PADI"));
        associations.add(new BaseIdNamePhotoEntity("2", "CMAS"));
        associations.add(new BaseIdNamePhotoEntity("3", "SSI"));
        associations.add(new BaseIdNamePhotoEntity("4", "IANTD"));
        associations.add(new BaseIdNamePhotoEntity("5", "TDI"));
        associations.add(new BaseIdNamePhotoEntity("6", "SDI"));
        associations.add(new BaseIdNamePhotoEntity("7", "NDL"));
        associations.add(new BaseIdNamePhotoEntity("8", "IDD"));
        associations.add(new BaseIdNamePhotoEntity("9", "IDEA"));
        associations.add(new BaseIdNamePhotoEntity("10", "NAUI"));
        associations.add(new BaseIdNamePhotoEntity("11", "PSA"));
        associations.add(new BaseIdNamePhotoEntity("12", "BSAC"));
        associations.add(new BaseIdNamePhotoEntity("13", "DAN"));
        return associations;
    }

    public static BaseIdNamePhotoEntity getAssociationByCode(int code) {
        if (getAssociationsList().get(code - 1) == null) {
            return null;
        }
        return getAssociationsList().get(code - 1);
    }

    public static String getDiveSpotType(int position) {
        List<String> types = getDiveSpotTypes();
        if (types.get(position - 1) == null) {
            return "";
        }
        return types.get(position - 1);
    }

    public static String getDiverLevel(Integer position) {
        if (position != null && position > -1) {
            List<String> levels = getDiveLevelTypes();
            if (levels.get(position) == null) {
                return "";
            }
            return levels.get(position);
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

    public static <T> T checkNotNull(T object) {
        if (object != null) {
            return object;
        }
        throw new IllegalArgumentException("Argument must not be null");
    }

    public static boolean checkEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validCellPhone(String number, String coutryCode) {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        try {
            return util.isValidNumber(util.parse(number, coutryCode));
        } catch (NumberParseException exception) {
            return false;
        }
//        if (number.length() > 7 && number.length() < 17) {
//            return true;
//        }
//        return false;
    }


}

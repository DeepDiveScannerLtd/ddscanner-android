package com.ddscanner.utils;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.tutorial.MyLocationHintClosedEvent;
import com.ddscanner.events.tutorial.ShowForDiveSpotTabsClosedEvent;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

public class TutorialHelper {

    private static final int DELAY_MY_LOCATION = 1000;
    private static final int DURATION_MY_LOCATION = 4000;
    private static final int DURATION_DIVE_SPOTS_TAB = 4000;
    private static final int DURATION_DIVE_SPOTS_LIST = 2000;
    private static final int DURATION_DIVE_SPOTS_SEARCH = 4000;
    private static final int DURATION_DIVE_SPOTS_FILTER = 3000;
    private static final int DURATION_DIVE_SPOTS_ADD_SPOT = 4000;
    private static final int DURATION_PROFILE_TAB = 4000;
    private static final int DURATION_NOTIFICATION_TAB = 3000;
    private static final int DURATION_CHECK_IN = 3000;
    private static final int DURATION_VALIDATION = 4000;
    private static final int DURATION_WRITE_REVIEW = 4000;

    public static void showForMyLocation(final Activity activity, final Handler handler, final View view) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final TapTargetView tapTargetView = TapTargetView.showFor(activity,                 // `this` is an Activity
                        TapTarget.forView(view, "This is a target", "We have the best targets, believe me")
                                // All options below are optional
                                .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                .textColor(android.R.color.white)            // Specify a color for text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true),
                        new TapTargetView.Listener() {
                            @Override
                            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                                DDScannerApplication.bus.post(new MyLocationHintClosedEvent());
                            }
                        });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tapTargetView.dismiss(false);
                    }
                }, DURATION_MY_LOCATION);
            }
        }, DELAY_MY_LOCATION);
    }

    public static void showForDiveSpotsTab(final Activity activity, final Handler handler, final View view) {
        final TapTargetView tapTargetView = TapTargetView.showFor(activity,                 // `this` is an Activity
                TapTarget.forView(view, "This is a target", "We have the best targets, believe me")
                        // All options below are optional
                        .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textColor(android.R.color.white)            // Specify a color for text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                        DDScannerApplication.bus.post(new ShowForDiveSpotTabsClosedEvent());
                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tapTargetView.dismiss(false);
            }
        }, DURATION_DIVE_SPOTS_TAB);
    }

    public static void showForDiveSpotList(final Activity activity, final Handler handler, final View view) {
        final TapTargetView tapTargetView = TapTargetView.showFor(activity,
                TapTarget.forView(view, "or listed")
                        .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textColor(android.R.color.white)            // Specify a color for text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {

                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tapTargetView.dismiss(false);
            }
        }, DURATION_DIVE_SPOTS_LIST);
    }

    public static void showForDiveSpotSearch(final Activity activity, final Handler handler, final View view) {
        final TapTargetView tapTargetView = TapTargetView.showFor(activity,
                TapTarget.forView(view, "Search by location or name of the dive spot ")
                        .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textColor(android.R.color.white)            // Specify a color for text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {

                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tapTargetView.dismiss(false);
            }
        }, DURATION_DIVE_SPOTS_SEARCH);
    }

    public static void showForDiveSpotFilter(final Activity activity, final Handler handler, final View view) {
        final TapTargetView tapTargetView = TapTargetView.showFor(activity,
                TapTarget.forView(view, "Filter dive spot by type and level")
                        .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textColor(android.R.color.white)            // Specify a color for text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {

                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tapTargetView.dismiss(false);
            }
        }, DURATION_DIVE_SPOTS_FILTER);
    }

    public static void showForAddSpot(final Activity activity, final Handler handler, final View view) {
        final TapTargetView tapTargetView = TapTargetView.showFor(activity,
                TapTarget.forView(view, "Cannot find the dive spot? Add and become a creator")
                        .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textColor(android.R.color.white)            // Specify a color for text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {

                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tapTargetView.dismiss(false);
            }
        }, DURATION_DIVE_SPOTS_ADD_SPOT);
    }

    public static void showForProfileTab(final Activity activity, final Handler handler, final View view) {
        final TapTargetView tapTargetView = TapTargetView.showFor(activity,
                TapTarget.forView(view, "Create a unique profile and see your experience")
                        .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textColor(android.R.color.white)            // Specify a color for text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {

                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tapTargetView.dismiss(false);
            }
        }, DURATION_PROFILE_TAB);
    }

    public static void showForNotificationsTab(final Activity activity, final Handler handler, final View view) {
        final TapTargetView tapTargetView = TapTargetView.showFor(activity,
                TapTarget.forView(view, "Track activity nearby")
                        .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textColor(android.R.color.white)            // Specify a color for text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {

                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tapTargetView.dismiss(false);
            }
        }, DURATION_NOTIFICATION_TAB);
    }

    public static void showForCheckin(final Activity activity, final Handler handler, final View view) {
        final TapTargetView tapTargetView = TapTargetView.showFor(activity,
                TapTarget.forView(view, "Check in")
                        .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textColor(android.R.color.white)            // Specify a color for text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {

                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tapTargetView.dismiss(false);
            }
        }, DURATION_CHECK_IN);
    }

    public static void showForValidation(final Activity activity, final Handler handler, final View view) {
        final TapTargetView tapTargetView = TapTargetView.showFor(activity,
                TapTarget.forView(view, "Edit the dive spot and add unique photos")
                        .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textColor(android.R.color.white)            // Specify a color for text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {

                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tapTargetView.dismiss(false);
            }
        }, DURATION_VALIDATION);
    }

    public static void showForWriteReview(final Activity activity, final Handler handler, final View view) {
        final TapTargetView tapTargetView = TapTargetView.showFor(activity,
                TapTarget.forView(view, "Write a review to share the experience you got")
                        .outerCircleColor(android.R.color.holo_red_light)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textColor(android.R.color.white)            // Specify a color for text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {

                    }
                });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tapTargetView.dismiss(false);
            }
        }, DURATION_WRITE_REVIEW);
    }

}

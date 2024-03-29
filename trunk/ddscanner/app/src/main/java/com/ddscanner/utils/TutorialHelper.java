package com.ddscanner.utils;


import android.app.Activity;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.View;

import com.ddscanner.R;

import me.toptas.fancyshowcase.DismissListener;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class TutorialHelper {

    public void showBookingTutorial(Activity context, View view) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .fitSystemWindows(true)
                .disableFocusAnimation()
                .title(Html.fromHtml(context.getString(R.string.onboarding_book)))
                .build()
                .show();
    }

    public void showSelectPinTutorial(Activity context, DismissListener dismissListener) {
        new FancyShowCaseView.Builder(context)
                .dismissListener(dismissListener)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(Html.fromHtml(context.getString(R.string.onboarding_select_pin)))
                .build()
                .show();
    }

    public void showTapOnInfoWindowTutorial(Activity context, View view, DismissListener dismissListener) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .dismissListener(dismissListener)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(Html.fromHtml(context.getString(R.string.onboarding_tap_on_infowindow)))
                .build()
                .show();
    }

    public void showGoToPhuketTitle(Activity context, DismissListener dismissListener) {
        new FancyShowCaseView.Builder(context)
                .dismissListener(dismissListener)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(Html.fromHtml(context.getString(R.string.onboarding_go_to_phuket)))
                .build()
                .show();
    }

    public void showMapListTutorial(Activity context, View view, DismissListener dismissListener) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .dismissListener(dismissListener)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(Html.fromHtml(context.getString(R.string.onboarding_map_list)))
                .build()
                .show();
    }

    public void showFilterTutorial(Activity context, View view, DismissListener dismissListener) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .dismissListener(dismissListener)
                .focusCircleRadiusFactor(2)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(Html.fromHtml(context.getString(R.string.onboarding_filter)))
                .build()
                .show();
    }

    public void showSearchTutorial(Activity context, View view, DismissListener dismissListener) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .dismissListener(dismissListener)
                .focusCircleRadiusFactor(2)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(context.getString(R.string.onboarding_search))
                .build()
                .show();
    }

    public void showNotificationTutorial(Activity context, View view, DismissListener dismissListener) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .dismissListener(dismissListener)
                .focusCircleRadiusFactor(2)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(Html.fromHtml(context.getString(R.string.tutorial_notifications)))
                .build()
                .show();
    }


    public void showProfileTutorial(Activity context, View view, DismissListener dismissListener) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .focusCircleRadiusFactor(2)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .dismissListener(dismissListener)
                .title(Html.fromHtml(context.getString(R.string.tutorial_profile)))
                .build()
                .show();
    }

    public void showAddDiveSpotTutorial(Activity context, View view) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .title(Html.fromHtml(context.getString(R.string.tutorial_add_spot)))
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .build()
                .show();
    }

    public void showCheckinTutorial(Activity context, View view, DismissListener dismissListener) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .dismissListener(dismissListener)
                .fitSystemWindows(true)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(Html.fromHtml(context.getString(R.string.tutorial_checkin)))
                .build()
                .show();
    }

    public void showAddPhotoTutorial(Activity context, View view, DismissListener dismissListener) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .dismissListener(dismissListener)
                .fitSystemWindows(true)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(Html.fromHtml(context.getString(R.string.tutorial_add_photo)))
                .build()
                .show();
    }

    public void showWriteReviewTutorial(Activity context, View view) {
        new Handler().postDelayed(() -> new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .fitSystemWindows(true)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(Html.fromHtml(context.getString(R.string.write_review_tutorial)))
                .build()
                .show(), 500);

    }

    public void showListButtonTutorial(Activity context, View view) {
        new FancyShowCaseView.Builder(context)
                .focusOn(view)
                .focusShape(FocusShape.CIRCLE)
                .titleStyle(R.style.TutorialTextStyle, Gravity.CENTER)
                .title(Html.fromHtml(context.getString(R.string.see_list_tutorial)))
                .build()
                .show();
    }

}

package com.ddscanner.ui.screens.sealife.details;


import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.ddscanner.R;
import com.ddscanner.entities.Sealife;
import com.ddscanner.screens.sealife.details.SealifeDetailsActivity;
import com.google.gson.Gson;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SeaLifeDetailsTest {

    private Gson gson = new Gson();

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(
            SealifeDetailsActivity.class,
            true,    // initialTouchMode
            false);  // launchActivity. False to set intent per method

    @Test
    public void seaLifeFullDetails_Displayed() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_FULL, -1);
    }

    @Test
    public void seaLifeDisplayedWithout_Length() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_LENGTH, R.id.length);
    }

    @Test
    public void seaLifeDisplayedWithout_Weight() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_WEIGHT, R.id.weight);
    }

    @Test
    public void seaLifeDisplayedWithout_Depth() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_DEPTH, R.id.depth);
    }

    @Test
    public void seaLifeDisplayedWithout_Scname() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_SCNAME, R.id.scname);
    }

    @Test
    public void seaLifeDisplayedWithout_Order() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_ORDER, R.id.order);
    }

    @Test
    public void seaLifeDisplayedWithout_Distribution() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_DISTRIBUTION, R.id.distribution);
    }

    @Test
    public void seaLifeDisplayedWithout_Scclass() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_SCCLASS, R.id.scclass);
    }

    @Test
    public void seaLifeDisplayedWithout_Habitat() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_HABITAT, R.id.habitat);
    }

    @Test
    public void seaLifeDisplayedWithout_SeveralFields_1() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_FIELDS_1, R.id.habitat);
    }

    @Test
    public void seaLifeDisplayedWithout_SeveralFields_2() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_FIELDS_2, R.id.habitat);
    }

    @Test
    public void seaLifeDisplayedWithout_SeveralFields_3() {
        seaLife_DisplayedWithoutView(TestData.SEA_LIFE_DETAILS_NO_FIELDS_3, R.id.habitat);
    }

    private Intent generateSeaLifeDetailsActivityIntent(Sealife sealife) {
        Intent intent = new Intent();
        intent.putExtra(SealifeDetailsActivity.EXTRA_SEALIFE, sealife);
        intent.putExtra(SealifeDetailsActivity.EXTRA_PATH, "https://ddsapi.ilave.pro/images/divespots/medium/");
        return intent;
    }

    private void seaLife_DisplayedWithoutView(String seaLifeJson, int viewId) {
        Sealife sealife = gson.fromJson(seaLifeJson, Sealife.class);
        activityRule.launchActivity(generateSeaLifeDetailsActivityIntent(sealife));

        onView(withId(R.id.name)).check(matches(withText(sealife.getName())));

        if (!TextUtils.isEmpty(sealife.getLength())) {
            onView(withId(R.id.length)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.length)).check(matches(withText(sealife.getLength())));
        } else {
            onView(withId(R.id.length)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }

        if (!TextUtils.isEmpty(sealife.getWeight())) {
            onView(withId(R.id.weight)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.weight)).check(matches(withText(sealife.getWeight())));
        } else {
            onView(withId(R.id.weight)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }

        if (!TextUtils.isEmpty(sealife.getDepth())) {
            onView(withId(R.id.depth)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.depth)).check(matches(withText(sealife.getDepth())));
        } else {
            onView(withId(R.id.depth)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }

        if (!TextUtils.isEmpty(sealife.getScName())) {
            onView(withId(R.id.scname)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.scname)).check(matches(withText(sealife.getScName())));
        } else {
            onView(withId(R.id.scname)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }

        if (!TextUtils.isEmpty(sealife.getOrder())) {
            onView(withId(R.id.order)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.order)).check(matches(withText(sealife.getOrder())));
        } else {
            onView(withId(R.id.order)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }

        if (!TextUtils.isEmpty(sealife.getDistribution())) {
            onView(withId(R.id.distribution)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.distribution)).check(matches(withText(sealife.getDistribution())));
        } else {
            onView(withId(R.id.distribution)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }

        if (!TextUtils.isEmpty(sealife.getScClass())) {
            onView(withId(R.id.scclass)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.scclass)).check(matches(withText(sealife.getScClass())));
        } else {
            onView(withId(R.id.scclass)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }

        if (!TextUtils.isEmpty(sealife.getHabitat())) {
            onView(withId(R.id.habitat)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.habitat)).check(matches(withText(sealife.getHabitat())));
        } else {
            onView(withId(R.id.habitat)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }
    }
}

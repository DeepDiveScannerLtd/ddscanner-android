package com.ddscanner.ui.screens.profile;


import android.content.Context;
import android.content.Intent;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.ddscanner.DDScannerActivityTestRule;
import com.ddscanner.DDScannerTestApplication;
import com.ddscanner.R;
import com.ddscanner.TestSharedPreferenceHelper;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.profile.user.ProfileFragment;
import com.ddscanner.ui.activities.FragmentTestActivity;
import com.ddscanner.utils.SharedPreferenceHelper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ProfileFragmentTest {

    @Rule
    public ProfileTestRule activityRule = new ProfileTestRule(FragmentTestActivity.class, true, false, new ProfileTestRestClient(TestData.USER_DATA_JSON_FULL), new TestSharedPreferenceHelper());

    @Rule
    public ProfileTestRule activityRule2 = new ProfileTestRule(FragmentTestActivity.class, true, false, new ProfileTestRestClient(), new TestSharedPreferenceHelper());

    @Test
    public void userProfileFull_Displayed() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.user_name)).check(matches(withText(((ProfileTestRestClient) activityRule.getProfileTestRestClient()).getUserResponseEntity().getName())));
    }

    @Test
    public void connectionErrorDialog_Displayed() {
        activityRule2.launchActivity(new Intent());

        onView(withText(R.string.error_connection_error_title)).check(matches(isDisplayed()));
    }

    private class ProfileTestRule extends DDScannerActivityTestRule<FragmentTestActivity> {

        private DDScannerRestClient profileTestRestClient;
        private SharedPreferenceHelper sharedPreferenceHelper;

        public ProfileTestRule(Class<FragmentTestActivity> activityClass, boolean initialTouchMode, boolean launchActivity, DDScannerRestClient profileTestRestClient, SharedPreferenceHelper sharedPreferenceHelper) {
            super(activityClass, initialTouchMode, launchActivity);

            this.profileTestRestClient = profileTestRestClient;
            this.sharedPreferenceHelper = sharedPreferenceHelper;
        }

        @Override
        protected void beforeActivityLaunched(Context applicationContext) {
            ((DDScannerTestApplication) applicationContext).setDdScannerTestRestClient(profileTestRestClient);
            ((DDScannerTestApplication) applicationContext).setSharedPreferenceHelper(sharedPreferenceHelper);
        }

        @Override
        protected void afterActivityLaunched() {
            Fragment fragment = new ProfileFragment();
            getActivity().getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment, null).commit();
        }

        public DDScannerRestClient getProfileTestRestClient() {
            return profileTestRestClient;
        }
    }
}

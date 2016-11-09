package com.ddscanner.ui.screens.profile;


import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.ddscanner.ui.fragments.ProfileFragment;
import com.ddscanner.ui.screens.FragmentTestActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ProfileFragmentTest {

    @Rule
    public ActivityTestRule<FragmentTestActivity> activityRule = new ActivityTestRule<>(
            FragmentTestActivity.class);

    @Before
    public void setup() {
        Fragment fragment = new ProfileFragment();
        activityRule.getActivity().getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment, null).commit();
    }
}

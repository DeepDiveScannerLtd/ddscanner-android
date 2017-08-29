package com.ddscanner.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.ddscanner.screens.notifications.ActivityNotificationsFragment;
import com.ddscanner.screens.notifications.DiveCenterNotificationsFragment;
import com.ddscanner.screens.notifications.DiverNotificationsFragment;
import com.ddscanner.screens.notifications.PersonalNotificationsFragment;
import com.ddscanner.screens.profile.divecenter.DiveCenterProfileFragment;
import com.ddscanner.screens.profile.user.ProfileFragment;
import com.ddscanner.screens.map.MapListFragment;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.SharedPreferenceHelper;

public class MainActivityPagerAdapter extends FragmentStatePagerAdapter implements LoginView.LoginStateChangeListener {

    private static final String TAG = MainActivityPagerAdapter.class.getName();

    private MapListFragment mapListFragment = new MapListFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private DiveCenterProfileFragment diveCenterProfileFragment = new DiveCenterProfileFragment();
    private DiverNotificationsFragment diverNotificationsFragment = new DiverNotificationsFragment();

    public MainActivityPagerAdapter(FragmentManager manager) {
        super(manager);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mapListFragment;
            case 1:
                return diverNotificationsFragment;
            case 2:
                switch (SharedPreferenceHelper.getActiveUserType()) {
                    case DIVECENTER:
                        return diveCenterProfileFragment;
                    case DIVER:
                    case INSTRUCTOR:
                        return profileFragment;
                    case NONE:
                        return profileFragment;
                }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getItemPosition(Object object) {
        Log.i(TAG, "getItemPosition");
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public void onLoggedIn() {
        profileFragment.onLoggedIn();
        diverNotificationsFragment.onLoggedIn();
    }

    @Override
    public void onLoggedOut() {
        profileFragment.onLoggedOut();
        diverNotificationsFragment.onLoggedOut();
    }

    public void setProfileFragment(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
    }

    public void setDiverNotificationsFragment(DiverNotificationsFragment diverNotificationsFragment) {
        this.diverNotificationsFragment = diverNotificationsFragment;
    }

    public void setActivityNotificationsFragment(ActivityNotificationsFragment activityNotificationsFragment) {
        this.diverNotificationsFragment.setActivityNotificationsFragment(activityNotificationsFragment);
    }

    public void setAllNotificationsFragment(PersonalNotificationsFragment personalNotificationsFragment) {
        this.diverNotificationsFragment.setPersonalNotificationsFragment(personalNotificationsFragment);
    }

    public void setDiveCenterProfileFragment(DiveCenterProfileFragment diveCenterProfileFragment) {
        this.diveCenterProfileFragment = diveCenterProfileFragment;
    }

    public void setDiveCenterNotificationsFragment(DiveCenterNotificationsFragment diveCenterNotificationsFragment) {
//        this.diveCenterNotificationsFragment = diveCenterNotificationsFragment;
    }

    public ProfileFragment getProfileFragment() {
        return profileFragment;
    }

    public DiverNotificationsFragment getDiverNotificationsFragment() {
        return diverNotificationsFragment;
    }

    public DiveCenterProfileFragment getDiveCenterProfileFragment() {
        return diveCenterProfileFragment;
    }

    public MapListFragment getMapListFragment() {
        return mapListFragment;
    }

}

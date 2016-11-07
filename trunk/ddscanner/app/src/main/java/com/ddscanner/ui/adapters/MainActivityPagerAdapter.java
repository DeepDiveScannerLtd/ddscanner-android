package com.ddscanner.ui.adapters;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ddscanner.ui.fragments.ActivityNotificationsFragment;
import com.ddscanner.ui.fragments.AllNotificationsFragment;
import com.ddscanner.ui.fragments.MapListFragment;
import com.ddscanner.ui.fragments.NotificationsFragment;
import com.ddscanner.screens.profile.ProfileFragment;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;

/**
 * Created by lashket on 20.4.16.
 */
public class MainActivityPagerAdapter extends FragmentStatePagerAdapter implements LoginView.LoginStateChangeListener {

    private static final String TAG = MainActivityPagerAdapter.class.getName();

    private MapListFragment mapListFragment = new MapListFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private NotificationsFragment notificationsFragment = new NotificationsFragment();

    public MainActivityPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        LogUtils.i(TAG, "getItem position = " + position + " SharedPreferenceHelper.isUserLoggedIn() = " + SharedPreferenceHelper.isUserLoggedIn());
        switch (position) {
            case 0:
                return mapListFragment;
            case 1:
                return notificationsFragment;
            case 2:
                return profileFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    public void setProfileImage(String uri) {
        profileFragment.setImage(uri);
    }

    public void setProfileImageFromCamera(Uri uri) {
        profileFragment.setImage(uri);
    }

    @Override
    public void onLoggedIn() {
        profileFragment.onLoggedIn();
        notificationsFragment.onLoggedIn();
    }

    @Override
    public void onLoggedOut() {
        profileFragment.onLoggedOut();
        notificationsFragment.onLoggedOut();
    }

    public void setProfileFragment(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
    }

    public void setNotificationsFragment(NotificationsFragment notificationsFragment) {
        this.notificationsFragment = notificationsFragment;
    }

    public void setActivityNotificationsFragment(ActivityNotificationsFragment activityNotificationsFragment) {
        this.notificationsFragment.setActivityNotificationsFragment(activityNotificationsFragment);
    }

    public void setAllNotificationsFragment(AllNotificationsFragment allNotificationsFragment) {
        this.notificationsFragment.setAllNotificationsFragment(allNotificationsFragment);
    }
}

package com.ddscanner.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.screens.notifications.DiveCenterNotificationsFragment;
import com.ddscanner.screens.profile.divecenter.DiveCenterProfileFragment;
import com.ddscanner.screens.profile.user.ProfileFragment;
import com.ddscanner.ui.fragments.ActivityNotificationsFragment;
import com.ddscanner.ui.fragments.AllNotificationsFragment;
import com.ddscanner.screens.notifications.DiverNotificationsFragment;
import com.ddscanner.ui.fragments.MapListFragment;
import com.ddscanner.ui.views.LoginView;

import java.util.ArrayList;

/**
 * Created by lashket on 20.4.16.
 */
public class MainActivityPagerAdapter extends FragmentStatePagerAdapter implements LoginView.LoginStateChangeListener {

    private static final String TAG = MainActivityPagerAdapter.class.getName();

    private MapListFragment mapListFragment = new MapListFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private DiveCenterProfileFragment diveCenterProfileFragment = new DiveCenterProfileFragment();
    private DiverNotificationsFragment diverNotificationsFragment = new DiverNotificationsFragment();
    private DiveCenterNotificationsFragment diveCenterNotificationsFragment = new DiveCenterNotificationsFragment();

    private ArrayList<String> titles = new ArrayList<>();

    public MainActivityPagerAdapter(FragmentManager manager) {
        super(manager);
        titles.add("1");
        titles.add("2");
        titles.add("3");
        titles.add("4");
//        profileFragment.setTitle("3");
//        diveCenterProfileFragment.setTitle("4");
//        mapListFragment.setTitle("1");
//        diverNotificationsFragment.setTitle("2");

    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "getItem position = " + position + " SharedPreferenceHelper.getIsUserSignedIn() = " + DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn());
        switch (position) {
            case 0:
                return mapListFragment;
            case 1:
                switch (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType()) {
                    case 0:
                        return diveCenterNotificationsFragment;
                    case 1:
                    case 2:
                        return diverNotificationsFragment;
                    case -1:
                        return diverNotificationsFragment;
                }
            case 2:
                switch (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType()) {
                    case 0:
                        return diveCenterProfileFragment;
                    case 1:
                    case 2:
                        return profileFragment;
                    case -1:
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

    public void setAllNotificationsFragment(AllNotificationsFragment allNotificationsFragment) {
        this.diverNotificationsFragment.setAllNotificationsFragment(allNotificationsFragment);
    }

    public void setDiveCenterProfileFragment(DiveCenterProfileFragment diveCenterProfileFragment) {
        this.diveCenterProfileFragment = diveCenterProfileFragment;
    }

    public void setDiveCenterNotificationsFragment(DiveCenterNotificationsFragment diveCenterNotificationsFragment) {
        this.diveCenterNotificationsFragment = diveCenterNotificationsFragment;
    }

    public ProfileFragment getProfileFragment() {
        return profileFragment;
    }

    public DiveCenterProfileFragment getDiveCenterProfileFragment() {
        return diveCenterProfileFragment;
    }
}

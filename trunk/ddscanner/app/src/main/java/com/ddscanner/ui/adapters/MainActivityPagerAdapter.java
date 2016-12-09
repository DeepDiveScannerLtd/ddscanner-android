package com.ddscanner.ui.adapters;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.screens.profile.DiveCenterProfileFragment;
import com.ddscanner.screens.profile.ProfileFragment;
import com.ddscanner.ui.fragments.ActivityNotificationsFragment;
import com.ddscanner.ui.fragments.AllNotificationsFragment;
import com.ddscanner.ui.fragments.BaseFragment;
import com.ddscanner.ui.fragments.MapListFragment;
import com.ddscanner.ui.fragments.NotificationsFragment;
import com.ddscanner.ui.views.LoginView;
import com.ddscanner.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by lashket on 20.4.16.
 */
public class MainActivityPagerAdapter extends FragmentStatePagerAdapter implements LoginView.LoginStateChangeListener {

    private static final String TAG = MainActivityPagerAdapter.class.getName();

    private MapListFragment mapListFragment = new MapListFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private DiveCenterProfileFragment diveCenterProfileFragment = new DiveCenterProfileFragment();
    private NotificationsFragment notificationsFragment = new NotificationsFragment();

    private ArrayList<String> titles = new ArrayList<>();

    public MainActivityPagerAdapter(FragmentManager manager) {
        super(manager);
        titles.add("1");
        titles.add("2");
        titles.add("3");
        titles.add("4");
        profileFragment.setTitle("3");
        diveCenterProfileFragment.setTitle("4");
        mapListFragment.setTitle("1");
        notificationsFragment.setTitle("2");

    }

    @Override
    public Fragment getItem(int position) {
        LogUtils.i(TAG, "getItem position = " + position + " SharedPreferenceHelper.isUserLoggedIn() = " + DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn());
        switch (position) {
            case 0:
                return mapListFragment;
            case 1:
                return notificationsFragment;
            case 2:
                switch (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType()) {
                    case 0:
                        return diveCenterProfileFragment;
                    case 1:
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

    public void setDiveCenterProfileFragment(DiveCenterProfileFragment diveCenterProfileFragment) {
        this.diveCenterProfileFragment = diveCenterProfileFragment;
    }

    @Override
    public int getItemPosition(Object object) {
        BaseFragment fragment = (BaseFragment) object;
        String title = fragment.getTitle();
        int position = titles.indexOf(title);
        if (position == 3) {
            return 2;
        }
        if (position >= 0) {
            return position;
        }
        return POSITION_NONE;
    }
}

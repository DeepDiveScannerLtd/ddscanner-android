package com.ddscanner.ui.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.ddscanner.R;
import com.ddscanner.ui.fragments.MapListFragment;
import com.ddscanner.ui.fragments.NeedToLoginFragment;
import com.ddscanner.ui.fragments.NotificationsFragment;
import com.ddscanner.ui.fragments.ProfileFragment;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;

/**
 * Created by lashket on 20.4.16.
 */
public class MainActivityPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = MainActivityPagerAdapter.class.getName();

    private FragmentManager fragmentManager;
    private MapListFragment mapListFragment = new MapListFragment();
    private ProfileFragment profileFragment;
    private NeedToLoginFragment needToLoginToViewProfileFragment;
    private NeedToLoginFragment needToLoginToViewNotificationsFragment;
    private NotificationsFragment notificationsFragment;

    public MainActivityPagerAdapter(FragmentManager manager) {
        super(manager);

        fragmentManager = manager;
    }

    @Override
    public Fragment getItem(int position) {
        LogUtils.i(TAG, "getItem position = " + position + " SharedPreferenceHelper.isUserLoggedIn() = " + SharedPreferenceHelper.isUserLoggedIn());
        switch (position) {
            case 0:
                return mapListFragment;
            case 1:
                if (SharedPreferenceHelper.isUserLoggedIn()) {
                    notificationsFragment = new NotificationsFragment();
                    return notificationsFragment;
                } else {
                    needToLoginToViewNotificationsFragment = NeedToLoginFragment.getInstance(R.string.notifications_need_to_login);
                    return needToLoginToViewNotificationsFragment;
                }
            case 2:
                if (SharedPreferenceHelper.isUserLoggedIn()) {
                    profileFragment = new ProfileFragment();
                    return profileFragment;
                } else {
                    needToLoginToViewProfileFragment = NeedToLoginFragment.getInstance(R.string.profile_need_to_login);
                    return needToLoginToViewProfileFragment;
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

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        FragmentTransaction transition = fragmentManager.beginTransaction();
        if (needToLoginToViewNotificationsFragment != null) {
            transition.remove(needToLoginToViewNotificationsFragment);
            needToLoginToViewNotificationsFragment = null;
        }
        if (needToLoginToViewProfileFragment != null) {
            transition.remove(needToLoginToViewProfileFragment);
            needToLoginToViewProfileFragment = null;
        }
        if (profileFragment != null) {
            transition.remove(profileFragment);
            profileFragment = null;
        }
        if (notificationsFragment != null) {
            transition.remove(notificationsFragment);
            notificationsFragment = null;
        }
        transition.commit();
        super.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        profileFragment.onActivityResult(requestCode, resultCode, data);
        notificationsFragment.onActivityResult(requestCode, resultCode, data);
    }

    public void setProfileImage(Uri uri) {
        profileFragment.setImage(uri);
    }
}

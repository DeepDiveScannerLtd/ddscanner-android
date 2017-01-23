package com.ddscanner;

import com.ddscanner.utils.SharedPreferenceHelper;


public class TestSharedPreferenceHelper extends SharedPreferenceHelper {
    @Override
    public boolean getIsUserSignedIn() {
        return true;
    }
}

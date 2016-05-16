package com.ddscanner.events;

/**
 * Created by lashket on 16.5.16.
 */
public class ChooseProfileFragmentViewEvent {

    private boolean isShowEdit = false;

    public boolean getIsShowEdit() {
        return isShowEdit;
    }

    public ChooseProfileFragmentViewEvent(boolean isShowEdit) {
        this.isShowEdit = isShowEdit;

    }

}

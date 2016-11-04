package com.ddscanner.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lashket on 12.5.16.
 */
public class CheckIns implements Serializable {

    private List<UserOld> checkins;

    public List<UserOld> getCheckins() {
        return checkins;
    }

    public void setCheckins(List<UserOld> checkins) {
        this.checkins = checkins;
    }
}

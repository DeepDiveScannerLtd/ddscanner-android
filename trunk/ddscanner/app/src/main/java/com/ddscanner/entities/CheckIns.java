package com.ddscanner.entities;

import java.io.Serializable;
import java.util.List;

public class CheckIns implements Serializable {

    private List<UserOld> checkins;

    public List<UserOld> getCheckins() {
        return checkins;
    }

    public void setCheckins(List<UserOld> checkins) {
        this.checkins = checkins;
    }
}

package com.ddscanner.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lashket on 12.5.16.
 */
public class CheckIns implements Serializable {

    private List<User> checkins;

    public List<User> getCheckins() {
        return checkins;
    }

    public void setCheckins(List<User> checkins) {
        this.checkins = checkins;
    }
}

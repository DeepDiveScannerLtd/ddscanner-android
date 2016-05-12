package com.ddscanner.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lashket on 12.5.16.
 */
public class Editors implements Serializable {

    private List<String> editors;

    public List<String> getEditors() {
        return editors;
    }

    public void setEditors(List<String> editors) {
        this.editors = editors;
    }
}

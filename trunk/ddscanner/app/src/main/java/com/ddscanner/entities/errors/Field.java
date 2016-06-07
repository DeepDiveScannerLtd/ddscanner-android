package com.ddscanner.entities.errors;

import java.util.ArrayList;

public class Field {
    private String name;
    private ArrayList<String> errors = new ArrayList<>();

    public ArrayList<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

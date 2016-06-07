package com.ddscanner.entities.errors;

import java.util.ArrayList;

public class ValidationError {
    private ArrayList<Field> fields = new ArrayList<>();

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void addField(Field field) {
        fields.add(field);
    }
}

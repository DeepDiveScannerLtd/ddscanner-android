package com.ddscanner.entities.request;

import java.util.ArrayList;

public class InstructorsSeeRequests {

    private ArrayList<String> instructors;

    public InstructorsSeeRequests(ArrayList<String> instructors) {
        this.instructors = instructors;
    }

    public ArrayList<String> getInstructors() {
        return instructors;
    }
}

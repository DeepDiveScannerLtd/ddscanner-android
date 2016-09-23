package com.ddscanner.entities.request;

public class ValidationRequest extends RegisterRequest {

    private boolean isValid;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

}

package com.ddscanner.entities.errors;

public class NotFoundException extends Exception {
    private GeneralError generalError;

    public GeneralError getGeneralError() {
        return generalError;
    }

    public NotFoundException setGeneralError(GeneralError generalError) {
        this.generalError = generalError;
        return this;
    }
}

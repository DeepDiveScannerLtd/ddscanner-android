package com.ddscanner.entities.errors;

public class GeneralErrorException extends Exception {
    private GeneralError generalError;

    public GeneralError getGeneralError() {
        return generalError;
    }

    public GeneralErrorException setGeneralError(GeneralError generalError) {
        this.generalError = generalError;
        return this;
    }
}

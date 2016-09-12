package com.ddscanner.entities.errors;

public class ServerInternalErrorException extends Exception {
    private GeneralError generalError;

    public GeneralError getGeneralError() {
        return generalError;
    }

    public ServerInternalErrorException setGeneralError(GeneralError generalError) {
        this.generalError = generalError;
        return this;
    }
}

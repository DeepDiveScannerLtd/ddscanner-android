package com.ddscanner.entities.errors;

public class ValidationErrorException extends Exception {
    private ValidationError validationError;

    public ValidationError getValidationError() {
        return validationError;
    }

    public ValidationErrorException setValidationError(ValidationError validationError) {
        this.validationError = validationError;
        return this;
    }
}

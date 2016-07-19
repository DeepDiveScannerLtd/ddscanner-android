package com.ddscanner.entities.errors;

/**
 * Created by lashket on 19.7.16.
 */
public class CommentLikeException extends Exception {

    private GeneralError generalError;

    public GeneralError getGeneralError() {
        return generalError;
    }

    public CommentLikeException setGeneralError(GeneralError generalError) {
        this.generalError = generalError;
        return this;
    }

}

package com.ddscanner.entities.errors;

public class GeneralError {
//    {
//        "message": "UserOld not found",
//            "status_code": 404
//    }
    private String message;
    private int status_code;

    public int getStatusCode() {
        return status_code;
    }

    public void setStatusCode(int statusCode) {
        this.status_code = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

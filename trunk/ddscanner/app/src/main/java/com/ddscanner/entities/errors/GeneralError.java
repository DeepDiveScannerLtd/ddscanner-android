package com.ddscanner.entities.errors;

public class GeneralError {
//    {
//        "message": "User not found",
//            "status_code": 404
//    }
    private String message;
    private String status_code;

    public String getStatusCode() {
        return status_code;
    }

    public void setStatusCode(String statusCode) {
        this.status_code = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

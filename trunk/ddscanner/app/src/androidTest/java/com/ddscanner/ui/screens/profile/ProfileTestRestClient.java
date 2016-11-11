package com.ddscanner.ui.screens.profile;

import com.ddscanner.entities.User;
import com.ddscanner.rest.DDScannerRestClient;


public class ProfileTestRestClient extends DDScannerRestClient {

    private User userEntity;

    private boolean isError;
    private ErrorType errorType;
    private Object errorData;
    private String url;
    private String errorMessage;

    private boolean isConnectionError;

    public ProfileTestRestClient(String userJson) {
        userEntity = gson.fromJson(userJson, User.class);
    }

    public ProfileTestRestClient(ErrorType errorType, Object errorData, String url, String errorMessage) {
        isError = true;
        this.errorType = errorType;
        this.errorData = errorData;
        this.url = url;
        this.errorMessage = errorMessage;
    }

    public ProfileTestRestClient() {
        isConnectionError = true;
    }

    @Override
    public void getUserSelfInformation(final ResultListener<User> resultListener) {
        if (isConnectionError) {
            resultListener.onConnectionFailure();
        } else if (isError) {
            resultListener.onError(errorType, errorData, url, errorMessage);
        } else {
            resultListener.onSuccess(userEntity);
        }
    }

    public User getUserResponseEntity() {
        return userEntity;
    }
}

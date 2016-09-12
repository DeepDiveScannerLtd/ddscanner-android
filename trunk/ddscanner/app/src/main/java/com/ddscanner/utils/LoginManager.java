package com.ddscanner.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

public class LoginManager implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginManager.class.getName();

    private FragmentActivity context;
    private GoogleApiClient mGoogleApiClient;
    private boolean needToClearDefaultAccount;

    public LoginManager(FragmentActivity context) {
        this.context = context;
    }

    private void initGoogleLoginManager() {
        needToClearDefaultAccount = true;
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("195706914618-ist9f8ins485k2gglbomgdp4l2pn57iq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        refreshIdTokenSilently();
                        if (needToClearDefaultAccount) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            mGoogleApiClient.clearDefaultAccountAndReconnect();
                            needToClearDefaultAccount = false;
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
    }

    private void refreshIdTokenSilently() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("195706914618-ist9f8ins485k2gglbomgdp4l2pn57iq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                        if (pendingResult.isDone()) {
                            // There's immediate result available.
                            GoogleSignInAccount acct = pendingResult.get().getSignInAccount();
                            String idToken = acct.getIdToken();
                            Log.i(TAG, "refreshIdTokenSilently pendingResult.isDone idToken = " + idToken);
                        } else {
                            // There's no immediate result ready, displays some progress indicator and waits for the
                            // async callback.
                            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                                @Override
                                public void onResult(@NonNull GoogleSignInResult result) {
                                    Log.i(TAG, "refreshIdTokenSilently onResult result.isSuccess = " + result.isSuccess());
                                    if (result.isSuccess()) {
                                        GoogleSignInAccount acct = result.getSignInAccount();
                                        String idToken = acct.getIdToken();
                                        Log.i(TAG, "refreshIdTokenSilently onResult idToken = " + idToken);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        // TODO Implement
                    }
                })
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO Implement
    }
}

package com.cezarmoisa.bmicalculator;

import android.app.Service;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

public class MyFirebaseInstanceIDService {
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("fbs", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
}

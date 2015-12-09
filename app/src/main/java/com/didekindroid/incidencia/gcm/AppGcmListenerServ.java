package com.didekindroid.incidencia.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class AppGcmListenerServ extends GcmListenerService {

    private static final String TAG = AppGcmListenerServ.class.getCanonicalName();

    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        Log.d(TAG,"onMessageReceived(), from: " + from);
        Log.d(TAG,"onMessageReceived(), message: " + data.getString("message"));

    }
}

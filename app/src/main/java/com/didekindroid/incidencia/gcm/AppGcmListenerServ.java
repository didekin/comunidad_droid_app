package com.didekindroid.incidencia.gcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AppGcmListenerServ extends Service {
    public AppGcmListenerServ()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

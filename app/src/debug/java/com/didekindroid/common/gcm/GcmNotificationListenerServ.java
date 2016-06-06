package com.didekindroid.common.gcm;

import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GcmNotificationListenerServ extends NotificationListenerService {

    public static final String GCM_NOTIFICATION_action = "com.didekindroid.common.gcm.NOTIFICATION_LISTENER";
    private static final String TAG = GcmNotificationListenerServ.class.getCanonicalName();
    public static final String notification_id_extra = "com.didenkindroid.incidencia.gcm.NOTIFICATION_ID";
    public static final String notification_extra = "com.didenkindroid.incidencia.gcm.NOTIFICATION";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        Log.d(TAG, "onNotificationPosted()");

        int notificationId = sbn.getId();
        Notification notification = sbn.getNotification();

        Intent intent = new Intent(GCM_NOTIFICATION_action);
        intent.putExtra(notification_id_extra,notificationId);
        intent.putExtra(notification_extra,notification);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        Log.d(TAG,"onNotificationPosted(),sendBroadcastMsg with notificationID = " + notificationId);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn)
    {
        Log.d(TAG,"onNotificationRemoved()");
    }

    @Override
    public void onCreate()
    {
        Log.d(TAG,"onCreate()");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG,"onBind(): intent = " + intent.toString());
        return super.onBind(intent);
    }

    @Override
    public void onListenerConnected()
    {
        Log.d(TAG,"onListenerConnected(): I'm connected");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }
}

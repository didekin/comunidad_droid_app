package com.didekindroid.gcm;

import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;

import timber.log.Timber;

public class GcmNotificationListenerServ extends NotificationListenerService {

    public static final String GCM_NOTIFICATION_action = "com.didekindroid.gcm.NOTIFICATION_LISTENER";
    public static final String notification_id_extra = "com.didenkindroid.incidencia.gcm.NOTIFICATION_ID";
    public static final String notification_extra = "com.didenkindroid.incidencia.gcm.NOTIFICATION";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        Timber.d("onNotificationPosted()");

        int notificationId = sbn.getId();
        Notification notification = sbn.getNotification();

        Intent intent = new Intent(GCM_NOTIFICATION_action);
        intent.putExtra(notification_id_extra,notificationId);
        intent.putExtra(notification_extra,notification);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        Timber.d("onNotificationPosted(),sendBroadcastMsg with notificationID = " + notificationId);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn)
    {
        Timber.d("onNotificationRemoved()");
    }

    @Override
    public void onCreate()
    {
        Timber.d("onCreate()");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Timber.d("onBind(): intent = " + intent.toString());
        return super.onBind(intent);
    }

    @Override
    public void onListenerConnected()
    {
        Timber.d("onListenerConnected(): I'm connected");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Timber.d("onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }
}

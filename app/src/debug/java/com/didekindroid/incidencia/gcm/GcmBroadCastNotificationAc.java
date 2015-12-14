package com.didekindroid.incidencia.gcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static com.didekindroid.incidencia.gcm.GcmNotificationListenerServ.GCM_NOTIFICATION_action;
import static com.didekindroid.incidencia.gcm.GcmNotificationListenerServ.notification_extra;
import static com.didekindroid.incidencia.gcm.GcmNotificationListenerServ.notification_id_extra;

@SuppressWarnings("unused")
public class GcmBroadCastNotificationAc extends AppCompatActivity {

    private static final String TAG = GcmBroadCastNotificationAc.class.getCanonicalName();
    private BroadcastReceiver mReceiver;
    Notification notification;
    int notificationId;
    String title;
    String text;
    String subText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        mReceiver = new BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.d(TAG, "GcmBroadCastNotificationAc.BroadcastReceiver.onReceive()");

                notificationId = intent.getIntExtra(notification_id_extra, 0);
                notification = intent.getParcelableExtra(notification_extra);
                title =  notification.extras.getString(Notification.EXTRA_TITLE);
                text = notification.extras.getString(Notification.EXTRA_TEXT);
                subText = notification.extras.getString(Notification.EXTRA_SUB_TEXT);
            }
        };
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume()");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, new IntentFilter(GCM_NOTIFICATION_action));
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause()");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
    }
}

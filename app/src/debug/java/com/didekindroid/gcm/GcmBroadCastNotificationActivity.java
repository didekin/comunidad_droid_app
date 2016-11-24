package com.didekindroid.gcm;

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

import timber.log.Timber;

import static com.didekindroid.gcm.GcmNotificationListenerServ.GCM_NOTIFICATION_action;
import static com.didekindroid.gcm.GcmNotificationListenerServ.notification_extra;
import static com.didekindroid.gcm.GcmNotificationListenerServ.notification_id_extra;

@SuppressWarnings("unused")
public class GcmBroadCastNotificationActivity extends AppCompatActivity {

    private BroadcastReceiver mReceiver;
    Notification notification;
    int notificationId;
    String title;
    String text;
    String subText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        mReceiver = new BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Timber.d("GcmBroadCastNotificationActivity.BroadcastReceiver.onReceive()");

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
        Timber.d("onResume()");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, new IntentFilter(GCM_NOTIFICATION_action));
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Timber.d("onPause()");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        Timber.d("onDestroy()");
        super.onDestroy();
    }
}

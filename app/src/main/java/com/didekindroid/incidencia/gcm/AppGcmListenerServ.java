package com.didekindroid.incidencia.gcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.didekin.incidservice.gcm.GcmMsgData;
import com.didekindroid.R;
import com.didekindroid.incidencia.activity.IncidSeeByComuAc;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.HashMap;
import java.util.Map;

import static android.app.Notification.CATEGORY_SOCIAL;
import static com.didekin.incidservice.gcm.GcmMsgData.incidencia_type;

public class AppGcmListenerServ extends GcmListenerService {

    private static final String TAG = AppGcmListenerServ.class.getCanonicalName();

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs. For Set of keys use data.keySet().
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        Log.d(TAG, "onMessageReceived(), from: " + from);
        String typeMessage = data.getString(GcmMsgData.type_message_extra);
        Log.d(TAG, "onMessageReceived(), typeMessage: " + typeMessage);

        TypeMsgHandler handler = TypeMsgHandler.getHandlerFromType(typeMessage);

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_info_outline_white_36dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setCategory(CATEGORY_SOCIAL)
                .setContentTitle(getResources().getString(handler.getTitleRsc()))
                .setContentText(getResources().getString(handler.getContentTextRsc()))
                .setSubText(getResources().getString(handler.getSubTextRsc()))
                .setAutoCancel(true);

        // This intent is used to make the PendingIntent associated to the taskStack.
        // TODO: hay que meter los extras necesarios para la activity en el back.
        // Back button:  inserting into the task's back stack the complete upward navigation. Hay que tratar también el retorno a parent activity with UP.
        Intent resultIntent = new Intent(getApplicationContext(), handler.getActivityClass());
        // We create a back stack based on the Intent that starts the Activity.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(handler.getActivityClass());
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // To start an activity when the user clicks the notification text in the notification drawer.
        mBuilder.setContentIntent(resultPendingIntent);
        Notification notification = mBuilder.build();

        NotificationManager mNotifyMgr =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // We use as identifier the int assigned in R class to the string resource for the title.
        mNotifyMgr.notify(handler.getTitleRsc(), notification);
        Log.d(TAG, "onMessageReceived(), notification sent with ID: " + handler.getTitleRsc());
    }

    //    ======================== HELPER CLASSES AND METHODS ==========================

    public enum TypeMsgHandler {

        INCIDENCIA {
            @Override
            String getType()
            {
                return this.toString();
            }

            @Override
            public int getTitleRsc()
            {
                return R.string.incid_gcm_nueva_incidencia_title;
            }

            @Override
            int getContentTextRsc()
            {
                return R.string.incid_gcm_nueva_incidencia_body;
            }

            @Override
            int getSubTextRsc()
            {
                return R.string.incid_gcm_nueva_incidencia_subtext;
            }

            @Override
            Class<?> getActivityClass()
            {
                return  IncidSeeByComuAc.class;
            }

            @Override
            public String toString()
            {
                return incidencia_type;
            }
        },;

        abstract String getType();

        public abstract int getTitleRsc();

        abstract int getContentTextRsc();

        abstract int getSubTextRsc();

        abstract Class<?> getActivityClass();

        private static final Map<String, TypeMsgHandler> typeToHandler = new HashMap<>();

        static {
            for (TypeMsgHandler handler : values()) {
                typeToHandler.put(handler.getType(), handler);
            }
        }

        static TypeMsgHandler getHandlerFromType(String handlerType)
        {
            return typeToHandler.get(handlerType);
        }
    }
}
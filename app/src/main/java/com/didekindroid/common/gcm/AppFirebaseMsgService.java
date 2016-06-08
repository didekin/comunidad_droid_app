package com.didekindroid.common.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.didekin.incidservice.gcm.GcmKeyValueIncidData;
import com.didekindroid.R;
import com.didekindroid.incidencia.activity.IncidSeeOpenByComuAc;
import com.didekindroid.usuario.activity.ComuSearchAc;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekin.common.gcm.GcmKeyValueData.type_message_key;
import static com.didekin.incidservice.gcm.GcmKeyValueIncidData.comunidadId_key;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;

public class AppFirebaseMsgService extends FirebaseMessagingService {

    private static final String TAG = AppFirebaseMsgService.class.getCanonicalName();
    Map<String,String> data;

    /**
     * Called when message is received.
     */
    @Override
    public void onMessageReceived(RemoteMessage message)
    {
        Log.d(TAG, "onMessageReceived()");

        // Sender ID.
        String from = message.getFrom();
        data = message.getData();
        String typeMessage = data.get(type_message_key);
        Log.d(TAG, "onMessageReceived(), from: " + from + "  typeMessage: " + typeMessage);

        TypeMsgHandler handler = TypeMsgHandler.getHandlerFromType(typeMessage);
        PendingIntent resultPendingIntent = handler.getPendingIntent(this);
        NotificationManager mManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(handler.getTitleRsc(), getNotification(handler, resultPendingIntent));

        Log.d(TAG, "onMessageReceived(), notification sent with ID: " + handler.getTitleRsc());
    }

//    ================================== HELPER METHODS ====================================

    private Notification getNotification(TypeMsgHandler handler, PendingIntent resultPendingIntent)
    {
        return new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_info_outline_white_36dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(getResources().getString(handler.getTitleRsc()))
                .setContentText(getResources().getString(handler.getContentTextRsc()))
                .setSubText(getResources().getString(handler.getSubTextRsc()))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();
    }

//    ================================== HELPER CLASSES =====================================

    public enum TypeMsgHandler {

        INCIDENCIA {
            @Override
            public String getType()
            {
                return this.toString();
            }

            /**
             *  This string is used as the unique identifier for the notification, in the application.
             */
            @Override
            public int getTitleRsc()
            {
                return R.string.incid_gcm_nueva_incidencia_title;
            }

            @Override
            public int getContentTextRsc()
            {
                return R.string.incid_gcm_nueva_incidencia_body;
            }

            @Override
            public int getSubTextRsc()
            {
                return R.string.incid_gcm_nueva_incidencia_subtext;
            }

            @Override
            PendingIntent getPendingIntent(Context context)
            {

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                Intent comuSearch = new Intent(new Intent(context, ComuSearchAc.class));
                stackBuilder.addNextIntentWithParentStack(comuSearch);
                Intent incidSeeComu = new Intent(context, IncidSeeOpenByComuAc.class);
                incidSeeComu.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                incidSeeComu.putExtra(COMUNIDAD_ID.key, Long.parseLong(((AppFirebaseMsgService)context).data.get(comunidadId_key)));
                stackBuilder.addNextIntent(incidSeeComu);
                return stackBuilder.getPendingIntent(0, FLAG_UPDATE_CURRENT);
            }

            @Override
            public String toString()
            {
                return GcmKeyValueIncidData.incidencia_type;
            }
        },;

        public abstract String getType();

        public abstract int getTitleRsc();

        public abstract int getContentTextRsc();

        public abstract int getSubTextRsc();

        abstract PendingIntent getPendingIntent(Context context);

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
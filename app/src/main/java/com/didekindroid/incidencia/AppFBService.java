package com.didekindroid.incidencia;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.didekindroid.R;
import com.didekindroid.incidencia.activity.IncidSeeClosedByComuAc;
import com.didekindroid.incidencia.activity.IncidSeeOpenByComuAc;
import com.didekindroid.comunidad.ComuSearchAc;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekin.common.gcm.GcmKeyValueData.type_message_key;
import static com.didekin.incidencia.gcm.GcmKeyValueIncidData.comunidadId_key;
import static com.didekin.incidencia.gcm.GcmKeyValueIncidData.incidencia_closed_type;
import static com.didekin.incidencia.gcm.GcmKeyValueIncidData.incidencia_open_type;
import static com.didekin.incidencia.gcm.GcmKeyValueIncidData.resolucion_open_type;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;

public class AppFBService extends FirebaseMessagingService {

    Map<String, String> data;

    /**
     * Called when message is received.
     */
    @Override
    public void onMessageReceived(RemoteMessage message)
    {
        Timber.d("onMessageReceived()");

        // Sender ID.
        String from = message.getFrom();
        data = message.getData();
        String typeMessage = data.get(type_message_key);
        Timber.d("onMessageReceived(), from: %s typeMessage: %s%n", from, typeMessage);

        IncidTypeMsgHandler handler = IncidTypeMsgHandler.getHandlerFromType(typeMessage);
        PendingIntent resultPendingIntent = handler.getPendingIntent(this);
        NotificationManager mManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(handler.getContentTextRsc(), getNotification(handler, resultPendingIntent));

        Timber.d("onMessageReceived(), notification sent with ID: %d%n", handler.getTitleRsc());
    }

//    ================================== HELPER METHODS ====================================

    private Notification getNotification(IncidTypeMsgHandler handler, PendingIntent resultPendingIntent)
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

    public enum IncidTypeMsgHandler {

        INCIDENCIA_OPEN {
            @Override
            public int getContentTextRsc()
            {
                return R.string.incid_gcm_nueva_incidencia_body;
            }

            @Override
            PendingIntent getPendingIntent(Context context)
            {
                Intent comuSearch = new Intent(new Intent(context, ComuSearchAc.class));
                Intent incidSeeComu = new Intent(context, IncidSeeOpenByComuAc.class);
                incidSeeComu.putExtra(COMUNIDAD_ID.key, Long.parseLong(((AppFBService) context).data.get(comunidadId_key)));
                return doBasicPendingIntent(context, incidSeeComu, comuSearch);
            }

            @Override
            public String getType()
            {
                return incidencia_open_type;
            }
        },

        INCIDENCIA_CLOSE {
            @Override
            public int getContentTextRsc()
            {
                return R.string.incid_gcm_incidencia_closed_body;
            }

            @Override
            PendingIntent getPendingIntent(Context context)
            {
                Intent comuSearch = new Intent(new Intent(context, ComuSearchAc.class));
                Intent incidSeeClosedComu = new Intent(context, IncidSeeClosedByComuAc.class);
                incidSeeClosedComu.putExtra(COMUNIDAD_ID.key, Long.parseLong(((AppFBService) context).data.get(comunidadId_key)));
                return doBasicPendingIntent(context, incidSeeClosedComu, comuSearch);
            }

            @Override
            public String getType()
            {
                return incidencia_closed_type;
            }
        },

        RESOLUCION_OPEN {
            @Override
            public int getContentTextRsc()
            {
                return R.string.incid_gcm_resolucion_open_body;
            }

            @Override
            PendingIntent getPendingIntent(Context context)
            {
                return INCIDENCIA_OPEN.getPendingIntent(context);
            }

            @Override
            public String getType()
            {
                return resolucion_open_type;
            }
        },
        ;

        public int getTitleRsc()
        {
            return R.string.gcm_message_title;
        }

        public abstract String getType();

        /**
         * This string is used as the unique identifier for the notification, in the application.
         */
        public abstract int getContentTextRsc();

        public int getSubTextRsc(){
            return R.string.gcm_message_generic_subtext;
        }

        abstract PendingIntent getPendingIntent(Context context);

        private static final Map<String, IncidTypeMsgHandler> typeToHandler = new HashMap<>();

        static {
            for (IncidTypeMsgHandler handler : values()) {
                typeToHandler.put(handler.getType(), handler);
            }
        }

        static IncidTypeMsgHandler getHandlerFromType(String handlerType)
        {
            return typeToHandler.get(handlerType);
        }

        //    ================ HELPER METHODS =============

        PendingIntent doBasicPendingIntent(Context context, Intent firsOutIntent, Intent... lastOutIntents)
        {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            for (Intent intent : lastOutIntents) {
                stackBuilder.addNextIntentWithParentStack(intent);
            }
            firsOutIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
            stackBuilder.addNextIntent(firsOutIntent);
            return stackBuilder.getPendingIntent(0, FLAG_UPDATE_CURRENT);
        }
    }
}
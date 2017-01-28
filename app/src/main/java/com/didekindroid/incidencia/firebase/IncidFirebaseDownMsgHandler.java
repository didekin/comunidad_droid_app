package com.didekindroid.incidencia.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.firebase.FirebaseDownstreamMsgHandler;
import com.didekindroid.incidencia.activity.IncidSeeClosedByComuAc;
import com.didekindroid.incidencia.activity.IncidSeeOpenByComuAc;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.graphics.BitmapFactory.decodeResource;
import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;
import static android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekinlib.model.common.gcm.GcmKeyValueData.type_message_key;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.comunidadId_key;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.incidencia_closed_type;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.incidencia_open_type;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.resolucion_open_type;
import static java.lang.Long.parseLong;

/**
 * User: pedro@didekin
 * Date: 14/01/17
 * Time: 10:31
 */
public enum IncidFirebaseDownMsgHandler implements FirebaseDownstreamMsgHandler {

    INCIDENCIA_OPEN {
        @Override
        int getContentTextRsc()
        {
            return R.string.incid_gcm_nueva_incidencia_body;
        }

        @Override
        TaskStackBuilder doStackBuilder(Context context, Map<String, String> data)
        {
            Intent comuSearch = new Intent(new Intent(context, ComuSearchAc.class));
            Intent incidSeeComu = new Intent(context, IncidSeeOpenByComuAc.class);
            incidSeeComu.putExtra(COMUNIDAD_ID.key, parseLong(data.get(comunidadId_key)));
            return doCommonStackBuilder(context, incidSeeComu, comuSearch);
        }

        @Override
        String getType()
        {
            return incidencia_open_type;
        }
    },

    INCIDENCIA_CLOSE {
        @Override
        int getContentTextRsc()
        {
            return R.string.incid_gcm_incidencia_closed_body;
        }

        @Override
        TaskStackBuilder doStackBuilder(Context context, Map<String, String> data)
        {
            Intent comuSearch = new Intent(context, ComuSearchAc.class);
            Intent incidSeeClosedComu = new Intent(context, IncidSeeClosedByComuAc.class);
            incidSeeClosedComu.putExtra(COMUNIDAD_ID.key, parseLong(data.get(comunidadId_key)));
            return doCommonStackBuilder(context, incidSeeClosedComu, comuSearch);
        }

        @Override
        String getType()
        {
            return incidencia_closed_type;
        }
    },

    RESOLUCION_OPEN {
        @Override
        int getContentTextRsc()
        {
            return R.string.incid_gcm_resolucion_open_body;
        }

        @Override
        TaskStackBuilder doStackBuilder(Context context, Map<String, String> data)
        {
            return INCIDENCIA_OPEN.doStackBuilder(context, data);
        }

        @Override
        String getType()
        {
            return resolucion_open_type;
        }
    },;

    // ======================= STATIC METHODS =========================

    private static final Map<String, IncidFirebaseDownMsgHandler> typeToHandler = new HashMap<>();

    static {
        for (IncidFirebaseDownMsgHandler handler : values()) {
            typeToHandler.put(handler.getType(), handler);
        }
    }

    private static FirebaseDownstreamMsgHandler getHandlerFromType(String handlerType)
    {
        return typeToHandler.get(handlerType);
    }

    public static void processMsgWithHandler(RemoteMessage message, Context context)
    {
        getHandlerFromType(message.getData().get(type_message_key)).processMessage(message, context);
    }

    /* ===================== PUBLIC INSTANCE METHODS ==================*/

    static TaskStackBuilder doCommonStackBuilder(Context context, Intent firsOutIntent, Intent... lastOutIntents)
    {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        for (Intent intent : lastOutIntents) {
            // Remark: add intent without parentStack.
            stackBuilder.addNextIntent(intent);
        }
        firsOutIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
        stackBuilder.addNextIntent(firsOutIntent);
        return stackBuilder;
    }

    @Override
    public void processMessage(RemoteMessage message, Context context)
    {
        Timber.d("processMessage()");
        Map<String, String> data = message.getData();
        PendingIntent resultPendingIntent = doStackBuilder(context, data).getPendingIntent(0, FLAG_UPDATE_CURRENT);
        NotificationManager mManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mManager.notify(getContentTextRsc(), doNotification(context, resultPendingIntent));

        Timber.d("onMessageReceived(), notification sent with ID: %d%n", getBarNotificationId());
    }

    /* ===================== PACKAGE PRIVATE INSTANCE METHODS ==================*/

    @Override
    public int getBarNotificationId()
    {
        return getContentTextRsc();
    }

    abstract TaskStackBuilder doStackBuilder(Context context, Map<String, String> data);

    abstract String getType();

    abstract int getContentTextRsc();

    int getSubTextRsc()
    {
        return R.string.gcm_message_generic_subtext;
    }

    /* ===================== STATIC HELPER METHODS ==================*/

    Notification doNotification(Context context, PendingIntent resultPendingIntent)
    {
        Resources resources = context.getResources();
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_info_outline_white_36dp)
                .setLargeIcon(decodeResource(resources, R.drawable.ic_launcher))
                .setSound(getDefaultUri(TYPE_NOTIFICATION))
                .setPriority(PRIORITY_DEFAULT)
                .setContentTitle(resources.getString(getBarNotificationId()))
                .setContentText(resources.getString(getContentTextRsc()))
                .setSubText(resources.getString(getSubTextRsc()))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();
    }

}

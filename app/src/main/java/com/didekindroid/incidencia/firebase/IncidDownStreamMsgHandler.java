package com.didekindroid.incidencia.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.firebase.FirebaseDownstreamMsgHandler;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.graphics.BitmapFactory.decodeResource;
import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;
import static android.os.Build.VERSION.SDK_INT;
import static android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PRIVATE;
import static android.support.v4.app.TaskStackBuilder.create;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekinlib.model.common.gcm.GcmKeyValueData.type_message_key;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.comunidadId_key;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.incidencia_closed_type;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.incidencia_open_type;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.resolucion_open_type;
import static java.lang.Long.parseLong;
import static java.util.Objects.requireNonNull;

/**
 * User: pedro@didekin
 * Date: 14/01/17
 * Time: 10:31
 */
public enum IncidDownStreamMsgHandler implements FirebaseDownstreamMsgHandler {

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
            Intent incidSeeComu = new Intent(context, IncidSeeByComuAc.class)
                    .putExtra(COMUNIDAD_ID.key, parseLong(data.get(comunidadId_key)))
                    .putExtra(INCID_CLOSED_LIST_FLAG.key, false); // Open incidencias list.
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
            Intent incidSeeClosedComu = new Intent(context, IncidSeeByComuAc.class)
                    .putExtra(COMUNIDAD_ID.key, parseLong(data.get(comunidadId_key)))
                    .putExtra(INCID_CLOSED_LIST_FLAG.key, true);  // Closed incidencias list.
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

    // ======================= STATIC MEMBERS =========================

    private static final String INCID_ID_NOTIF_CHANNEL = "IncidenciaNotificationChannel";
    private static final AtomicBoolean isNotifChannelSet = new AtomicBoolean(false);

    private static final Map<String, IncidDownStreamMsgHandler> typeToHandler = new HashMap<>();

    static {
        for (IncidDownStreamMsgHandler handler : values()) {
            typeToHandler.put(handler.getType(), handler);
        }
    }

    private static FirebaseDownstreamMsgHandler getHandlerFromType(String handlerType)
    {
        Timber.d("getHandlerFromType: %s", handlerType);
        return typeToHandler.get(handlerType);
    }

    static TaskStackBuilder doCommonStackBuilder(Context context, Intent firsOutIntent, Intent... lastOutIntents)
    {
        Timber.d("doCommonStackBuilder()");

        TaskStackBuilder stackBuilder = create(context);
        for (Intent intent : lastOutIntents) {
            // Remark: add intent without parentStack.
            stackBuilder.addNextIntent(intent);
        }
        firsOutIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
        stackBuilder.addNextIntent(firsOutIntent);
        return stackBuilder;
    }

    /**
     * Entry point to process an inbound message.
     */
    public static void processMsgWithHandler(RemoteMessage message, Context context)
    {
        Timber.d("processMsgWithHandler()");
        isNotifChannelSet.compareAndSet(false, doNotificationChannel(context));
        getHandlerFromType(message.getData().get(type_message_key)).processMessage(message, context);
    }

    private static boolean doNotificationChannel(Context context)
    {
        Timber.d("getNotificationChannel()");

        if (SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    INCID_ID_NOTIF_CHANNEL,
                    context.getString(R.string.incid_id_notification_channel),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.incid_desc_notification_channel));
            requireNonNull(context.getSystemService(NotificationManager.class)).createNotificationChannel(channel);
            return NotificationManager.class.cast(context.getSystemService(NOTIFICATION_SERVICE)).getNotificationChannel(INCID_ID_NOTIF_CHANNEL) != null;
        }
        return true;
    }

    /* ===================== PUBLIC INSTANCE METHODS ==================*/

    @Override
    public void processMessage(RemoteMessage message, Context context)
    {
        Timber.d("processMessage()");

        Map<String, String> data = message.getData();
        PendingIntent resultPendingIntent = doStackBuilder(context, data).getPendingIntent(0, FLAG_UPDATE_CURRENT);
        NotificationManager notifManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notifManager != null) {
            notifManager.notify(getBarNotificationId(), doNotification(context, resultPendingIntent));
        }

        Timber.d("processMessage(), notification sent with ID: %d%n", getBarNotificationId());
    }

    /**
     * We use as NotificationId the id for the resource string used as message, in the notification.
     */
    @Override
    public int getBarNotificationId()
    {
        Timber.d("getBarNotificationId()");
        return getContentTextRsc();
    }

    /* ===================== PACKAGE PRIVATE INSTANCE METHODS ==================*/

    abstract TaskStackBuilder doStackBuilder(Context context, Map<String, String> data);

    abstract String getType();

    abstract int getContentTextRsc();

    int getSubTextRsc()
    {
        return R.string.gcm_message_generic_subtext;
    }

    Notification doNotification(Context context, PendingIntent resultPendingIntent)
    {
        Timber.d("doNotification()");

        Resources resources = context.getResources();
        return new NotificationCompat.Builder(context, INCID_ID_NOTIF_CHANNEL)
                .setSmallIcon(R.drawable.ic_info_outline_white_36dp)
                .setLargeIcon(decodeResource(resources, R.drawable.ic_launcher))
                .setSound(getDefaultUri(TYPE_NOTIFICATION))
                .setPriority(PRIORITY_DEFAULT)
                .setContentTitle(resources.getString(getBarNotificationId()))
                .setContentText(resources.getString(getContentTextRsc()))
                .setSubText(resources.getString(getSubTextRsc()))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setVisibility(VISIBILITY_PRIVATE) // lock screen visibility
                .build();
    }
}

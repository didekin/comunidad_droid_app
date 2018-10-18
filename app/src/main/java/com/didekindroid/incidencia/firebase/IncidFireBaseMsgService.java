package com.didekindroid.incidencia.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import timber.log.Timber;

import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.processMsgWithHandler;
import static com.didekinlib.gcm.GcmKeyValueData.type_message_key;

public class IncidFireBaseMsgService extends FirebaseMessagingService {

    /**
     * Called when message is received.
     */
    @Override
    public void onMessageReceived(RemoteMessage message)
    {
        Timber.d("onMessageReceived(), from: %s typeMessage: %s%n",
                message.getFrom(), message.getData().get(type_message_key));
        processMsgWithHandler(message, this);
    }
}
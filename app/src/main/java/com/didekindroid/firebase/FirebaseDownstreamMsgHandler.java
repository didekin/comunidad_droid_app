package com.didekindroid.firebase;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

/**
 * User: pedro@didekin
 * Date: 14/01/17
 * Time: 11:29
 */
public interface FirebaseDownstreamMsgHandler {

    void processMessage(RemoteMessage message, Context context);

    /**
     * It returns the integer field used as the unique identifier for the notification, in the application.
     */
    int getBarNotificationId();
}

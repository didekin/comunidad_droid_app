package com.didekindroid.incidencia.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.usuario.webservices.UsuarioService;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;

/**
 *
 */
public class AppGcmRegistrationServ extends IntentService {

    private static final String TAG = AppGcmRegistrationServ.class.getCanonicalName();
    private static final String[] TOPICS = {"global"};

    public AppGcmRegistrationServ()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

        Log.d(TAG, "onHandleIntent()");

        InstanceID instanceID = InstanceID.getInstance(this);
        String token;
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            subscribeTopics(token);
            sendRegistrationToServer(token);
            updateIsGcmTokenSentServer(true, this);
            Log.i(TAG, "onHandleIntent(), GCM token registered: " + token);
        } catch (IOException|InServiceException|UiException e) {
            updateIsGcmTokenSentServer(false, this);
            Log.e(TAG, "onHandleIntent(), exception:", e);
        }
    }

    /**
     * Persist registration to didekinspring db.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) throws UiException, InServiceException
    {
        Log.d(TAG, "sendRegistrationToServer()");
        UsuarioService.ServOne.modifyUserGcmToken(token);
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException
    {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}

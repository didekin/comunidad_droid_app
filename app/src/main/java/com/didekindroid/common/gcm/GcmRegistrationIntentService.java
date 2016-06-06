package com.didekindroid.common.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.didekindroid.common.activity.UiException;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * User: pedro@didekin
 * Date: 27/05/16
 * Time: 10:44
 */
public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = GcmRegistrationIntentService.class.getCanonicalName();

    public GcmRegistrationIntentService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(TAG, "onHandleIntent()");
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            ServOne.modifyUserGcmToken(token);
            updateIsGcmTokenSentServer(true, this);
            Log.i(TAG, "onHandleIntent(), GCM token registered: " + token);
        } catch (UiException e) {
            updateIsGcmTokenSentServer(false, this);
            Log.e(TAG, "onHandleIntent(), exception:", e);
        }
    }
}
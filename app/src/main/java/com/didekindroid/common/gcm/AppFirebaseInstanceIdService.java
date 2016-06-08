package com.didekindroid.common.gcm;

import android.util.Log;

import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.webservices.UsuarioService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;

/**
 * On initial startup of your app, the FCM SDK generates a registration token for the client app instance.
 * This service accesses that token.
 */
public class AppFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = AppFirebaseInstanceIdService.class.getCanonicalName();

    /**
     * Called if InstanceID token is created or updated. This may occur if the security of
     * the previous token had been compromised.
     */
    @Override
    public void onTokenRefresh()
    {
        Log.d(TAG, "onTokenRefresh()");

        if (!isRegisteredUser(this)) {
            return;
        }

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        try {
            UsuarioService.ServOne.modifyUserGcmToken(refreshedToken);
            updateIsGcmTokenSentServer(true, this);
            Log.i(TAG, "onTokenRefresh(), GCM token registered: " + refreshedToken);
        } catch (UiException e) {
            updateIsGcmTokenSentServer(false, this);
            Log.e(TAG, "onTokenRefresh(), exception:", e);
        }
    }
}

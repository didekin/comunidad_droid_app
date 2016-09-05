package com.didekindroid.common.gcm;

import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.webservices.UsuarioService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import timber.log.Timber;

import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;

/**
 * On initial startup of your app, the FCM SDK generates a registration token for the client app instance.
 * This service accesses that token.
 */
public class AppFirebaseInstanceIdService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is created or updated. This may occur if the security of
     * the previous token had been compromised.
     */
    @Override
    public void onTokenRefresh()
    {
        Timber.d("onTokenRefresh()");

        if (!isRegisteredUser(this)) {
            return;
        }

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        try {
            UsuarioService.ServOne.modifyUserGcmToken(refreshedToken);
            updateIsGcmTokenSentServer(true, this);
            Timber.i("onTokenRefresh(), GCM token registered: %s%n", refreshedToken);
        } catch (UiException e) {
            updateIsGcmTokenSentServer(false, this);
            Timber.e("onTokenRefresh(), exception: %s%n", e.getErrorBean().getMessage());
        }
    }
}

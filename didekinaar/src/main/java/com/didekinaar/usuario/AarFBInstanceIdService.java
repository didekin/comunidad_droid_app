package com.didekinaar.usuario;

import com.didekinaar.exception.UiException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import timber.log.Timber;

import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;

/**
 * On initial startup of your app, the FCM SDK generates a registration token for the client app instance.
 * This service accesses that token.
 */
public class AarFBInstanceIdService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is created or updated. This may occur if the security of
     * the previous token had been compromised.
     */
    @Override
    public void onTokenRefresh()
    {
        Timber.d("onTokenRefresh()");

        if (!TKhandler.isRegisteredUser()) {
            return;
        }

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        try {
            usuarioDaoRemote.modifyUserGcmToken(refreshedToken);
            TKhandler.updateIsGcmTokenSentServer(true);
            Timber.i("onTokenRefresh(), GCM token registered: %s%n", refreshedToken);
        } catch (UiException e) {
            TKhandler.updateIsGcmTokenSentServer(false);
            Timber.e("onTokenRefresh(), exception: %s%n", e.getErrorBean().getMessage());
        }
    }
}

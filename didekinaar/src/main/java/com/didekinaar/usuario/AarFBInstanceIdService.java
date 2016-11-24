package com.didekinaar.usuario;

import com.didekinaar.exception.UiAarException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import timber.log.Timber;

import static com.didekinaar.usuario.AarUsuarioService.AarUserServ;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.updateIsGcmTokenSentServer;

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

        if (!isRegisteredUser(this)) {
            return;
        }

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        try {
            AarUserServ.modifyUserGcmToken(refreshedToken);
            updateIsGcmTokenSentServer(true, this);
            Timber.i("onTokenRefresh(), GCM token registered: %s%n", refreshedToken);
        } catch (UiAarException e) {
            updateIsGcmTokenSentServer(false, this);
            Timber.e("onTokenRefresh(), exception: %s%n", e.getErrorBean().getMessage());
        }
    }
}

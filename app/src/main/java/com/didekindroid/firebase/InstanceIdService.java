package com.didekindroid.firebase;

import com.google.firebase.iid.FirebaseInstanceIdService;

import timber.log.Timber;

import static com.didekindroid.usuario.firebase.FirebaseTokenReactor.tokenReactor;

/**
 * On initial startup of your app, the FCM SDK generates a registration token for the client app instance.
 * This service accesses that token.
 */
public class InstanceIdService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is created or updated. This may occur if the security of
     * the previous token had been compromised.
     */
    @Override
    public void onTokenRefresh()
    {
        Timber.d("onTokenRefresh()");
        tokenReactor.checkGcmTokenSync();
    }
}

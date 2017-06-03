package com.didekindroid.usuario.firebase;

import com.google.firebase.iid.FirebaseInstanceIdService;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

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
        final CtrlerFirebaseTokenIf controller = new CtrlerFirebaseToken();
        controller.checkGcmTokenSync(new ServiceDisposableSingleObserver(controller));
        controller.clearSubscriptions();
    }

    /**
     *  Inner class to make easier to test the service's method in the controller.
     */
    @SuppressWarnings("WeakerAccess")
    public static class ServiceDisposableSingleObserver extends DisposableSingleObserver<Integer> {

        private final CtrlerFirebaseTokenIf controller;

        ServiceDisposableSingleObserver(CtrlerFirebaseTokenIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onSuccess(Integer isUpdated)
        {
            Timber.d("onSuccess(%d)", isUpdated);
            if (isUpdated > 0) {
                controller.updateIsGcmTokenSentServer(true);
            }
        }

        @Override
        public void onError(Throwable error)
        {
            Timber.d("onError()");
        }
    }
}

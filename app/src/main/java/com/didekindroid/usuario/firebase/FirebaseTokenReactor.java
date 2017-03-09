package com.didekindroid.usuario.firebase;

import com.didekindroid.incidencia.core.ControllerFirebaseTokenIf;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

/**
 * User: pedro@didekin
 * Date: 06/03/17
 * Time: 14:29
 */
@SuppressWarnings("AnonymousInnerClassMayBeStatic")
class FirebaseTokenReactor implements ControllerFirebaseTokenIf.FirebaseTokenReactorIf {

    static final ControllerFirebaseTokenIf.FirebaseTokenReactorIf tokenReactor = new FirebaseTokenReactor();

    //    .................................... OBSERVABLES .................................

    /**
     * Preconditions: the user is registered.
     * Postconditions: the user's gcm token in database is updated.
     *
     * @return a Single with an item == 1 if the gcmToken is updated.
     */
    static Single<Integer> updatedGcmTkSingle()
    {
        return Single.fromCallable(new Callable<Integer>() {
                                       @Override
                                       public Integer call() throws Exception
                                       {
                                           String token = FirebaseInstanceId.getInstance().getToken();
                                           return usuarioDao.modifyUserGcmToken(token);
                                       }
                                   }
        );
    }

    // .................................... SUBSCRIPTIONS ..................................

    @Override
    public boolean checkGcmToken(ControllerFirebaseTokenIf controller)
    {
        Timber.d("checkGcmToken()");

        return controller.getSubscriptions().add(
                updatedGcmTkSingle()
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                        .subscribeWith(new RegGcmTokenObserver(controller)));
    }

    /**
     * Synchronous variant for the service InstanceIdService.
     * The method does not check if the gcmToken has been sent previously to database.
     */
    @Override
    public void checkGcmTokenSync(ControllerFirebaseTokenIf controller)
    {
        Timber.d("checkGcmTokenSync()");

        controller.getSubscriptions().add(
                updatedGcmTkSingle()
                        .subscribeWith(
                                new RegGcmTokenObserver(controller) {
                                    @Override
                                    public void onError(Throwable error)
                                    {
                                        Timber.d("onError(): %s", error.getMessage());
                                    }
                                }
                        ));
    }

    // ............................ SUBSCRIBERS ..................................

    static class RegGcmTokenObserver extends DisposableSingleObserver<Integer> {

        private final ControllerFirebaseTokenIf controller;

        RegGcmTokenObserver(ControllerFirebaseTokenIf controller)
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
            Timber.d("onError(): %s", error.getMessage());
            controller.updateIsGcmTokenSentServer(false);
            controller.processReactorError(error);
        }
    }
}

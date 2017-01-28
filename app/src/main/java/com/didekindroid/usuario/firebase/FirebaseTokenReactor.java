package com.didekindroid.usuario.firebase;


import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 17/01/17
 * Time: 09:58
 */

public final class FirebaseTokenReactor implements FirebaseTokenReactorIf {

    public static final FirebaseTokenReactorIf tokenReactor = new FirebaseTokenReactor();

    private FirebaseTokenReactor()
    {
    }

    //  =====================================================================================================
    //    .................................... OBSERVABLES .................................
    /*  =====================================================================================================*/

    /**
     * Preconditions: the user is registered.
     * Postconditions: the user's gcm token in database is updated.
     *
     * @return a Single with an item == 1 if the gcmToken is updated.
     */
    static Single<Integer> updatedGcmTkSingle()
    {
        assertTrue(TKhandler.isRegisteredUser(), user_should_be_registered);
        return Single.fromCallable(new Callable<Integer>() {
                                       @Override
                                       public Integer call() throws Exception
                                       {
                                           String token = FirebaseInstanceId.getInstance().getToken();
                                           int updatedToken = usuarioDao.modifyUserGcmToken(token);
                                           Timber.i("onHandleIntent(), GCM token registered: %s%n", token);
                                           return updatedToken;
                                       }
                                   }
        );
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Override
    public void checkGcmToken(CompositeDisposable subscriptions)
    {
        if (!TKhandler.isRegisteredUser()) {
            return;
        }
        subscriptions.add(
                updatedGcmTkSingle()
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new RegGcmTokenObserver()));
    }

    /**
     * Synchronous variant for the service InstanceIdService.
     */
    @Override
    public void checkGcmTokenSync()
    {
        if (!TKhandler.isRegisteredUser()) {
            return;
        }
        updatedGcmTkSingle().subscribe(new RegGcmTokenObserver());
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    @SuppressWarnings("WeakerAccess")
    static class RegGcmTokenObserver extends DisposableSingleObserver<Integer> {

        @Override
        public void onSuccess(Integer isUpdated)
        {
            Timber.d("onSuccess(%d)", isUpdated);
            if (isUpdated > 0) {
                TKhandler.updateIsGcmTokenSentServer(true);
            }
        }

        @Override
        public void onError(Throwable error)
        {
            TKhandler.updateIsGcmTokenSentServer(false);
            UiExceptionIf uiException = (UiException) error;
            Timber.e("onHandleIntent(), exception: %s%n", uiException.getErrorBean().getMessage());
        }
    }
}

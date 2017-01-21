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
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;

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
     */
    static Single<Integer> regGcmTokenSingle()
    {
        return Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                String token = FirebaseInstanceId.getInstance().getToken();
                int updatedToken = usuarioDao.modifyUserGcmToken(token);
                Timber.i("onHandleIntent(), GCM token registered: %s%n", token);
                return updatedToken;
            }
        });
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Override
    public void checkGcmToken(CompositeDisposable subscriptions)
    {
        // TODO: test.

        if (!TKhandler.isRegisteredUser()) {
            return;
        }
        subscriptions.add(
                regGcmTokenSingle()
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new RegGcmTokenObserver()));
    }

    @Override
    public void checkGcmTokenSync()
    {
        // TODO: test.

        if (!TKhandler.isRegisteredUser()) {
            return;
        }
        regGcmTokenSingle().subscribe(new RegGcmTokenObserver());
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    private static class RegGcmTokenObserver extends DisposableSingleObserver<Integer> {

        RegGcmTokenObserver()
        {
        }

        @Override
        public void onSuccess(Integer isUpdated)
        {
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

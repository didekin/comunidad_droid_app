package com.didekindroid.usuario.password;


import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenAndInitCache;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_password_should_be_updated;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 24/12/16
 * Time: 15:09
 */

@SuppressWarnings("AnonymousInnerClassMayBeStatic")
final class PswdChangeReactor implements PswdChangeReactorIf {

    static final PswdChangeReactorIf pswdChangeReactor = new PswdChangeReactor();

    private PswdChangeReactor()
    {
    }

    // ............................ OBSERVABLES ..................................

    @Override
    public Completable isPasswordChanged(final Usuario usuario)
    {
        Timber.d("isPasswordChanged()");

        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDao.passwordChange(usuario.getPassword());
            }
        }).flatMapCompletable(new Function<Integer, CompletableSource>() {
            @Override
            public CompletableSource apply(Integer passwordUpdated) throws Exception
            {
                assertTrue(passwordUpdated == 1, user_password_should_be_updated);
                return oauthTokenAndInitCache(usuario);
            }
        });
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Override
    public boolean passwordChange(PasswordChangeControllerIf controller, Usuario usuario)
    {
        Timber.d("passwordChange()");
        return controller.getSubscriptions().add(
                isPasswordChanged(usuario)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new PswdChangeSingleObserver(controller))
        );
    }

    // ............................ SUBSCRIBERS ..................................

    private static class PswdChangeSingleObserver extends DisposableCompletableObserver {

        private final PasswordChangeControllerIf controller;

        PswdChangeSingleObserver(PasswordChangeControllerIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onComplete()
        {
            Timber.d("onComplete(), Thread: %s", Thread.currentThread().getName());
            controller.processBackChangedPswdRemote();
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError, Thread: %s", Thread.currentThread().getName());
            controller.processErrorInReactor(e);
        }
    }
}

package com.didekindroid.usuario.password;

import android.os.Bundle;

import com.didekindroid.api.Controller;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenAndInitCache;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_password_should_be_updated;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 22/02/17
 * Time: 20:37
 */
@SuppressWarnings("WeakerAccess")
class CtrlerPasswordChange extends Controller implements CtrlerPasswordChangeIf {

    CtrlerPasswordChange(ViewerPasswordChangeIf viewer)
    {
        this(viewer, TKhandler);
    }

    CtrlerPasswordChange(ViewerPasswordChangeIf viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
    }

    // ............................ OBSERVABLES ..................................

    public static Completable isPasswordChanged(final Usuario usuario)
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

    // ............................ Instance methods ................................

    @Override
    public boolean changePasswordInRemote(Usuario usuario)
    {
        Timber.d("changePasswordInRemote()");
        return subscriptions.add(
                isPasswordChanged(usuario)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new PswdChangeSingleObserver(this))
        );
    }

    @Override
    public void onSuccessChangedPswd()
    {
        Timber.d("onSuccessChangedPswd()");
        ViewerPasswordChange.class.cast(viewer).replaceComponent(new Bundle());
    }

    // ............................ SUBSCRIBERS ..................................

    private static class PswdChangeSingleObserver extends DisposableCompletableObserver {

        private final CtrlerPasswordChangeIf controller;

        PswdChangeSingleObserver(CtrlerPasswordChangeIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onComplete()
        {
            Timber.d("onComplete(), Thread: %s", Thread.currentThread().getName());
            controller.onSuccessChangedPswd();
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorCtrl, Thread: %s", Thread.currentThread().getName());
            controller.onErrorCtrl(e);
        }
    }
}


package com.didekindroid.usuario.password;

import android.view.View;

import com.didekindroid.api.ControllerIdentityAbs;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf;
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
import static com.didekindroid.usuario.password.ControllerPasswordChange.ReactorPswdChange.pswdChangeReactor;
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
class ControllerPasswordChange extends ControllerIdentityAbs implements ControllerPasswordChangeIf {

    private final ViewerPasswordChangeIf<View, Object> viewer;
    private final ReactorPswdChangeIf reactor;

    ControllerPasswordChange(ViewerPasswordChangeIf<View, Object> viewer)
    {
        this(viewer, pswdChangeReactor, TKhandler);
    }

    ControllerPasswordChange(ViewerPasswordChangeIf<View, Object> viewer, ReactorPswdChangeIf reactor, IdentityCacher identityCacher)
    {
        super(identityCacher);
        this.viewer = viewer;
        this.reactor = reactor;
    }

    @Override
    public void changePasswordInRemote(Usuario usuario)
    {
        Timber.d("changePasswordInRemote()");
        reactor.passwordChange(this, usuario);
    }

    @Override
    public void processBackChangedPswdRemote()
    {
        Timber.d("processBackChangedPswdRemote()");
        viewer.getManager().replaceRootView(null);
    }

    @Override
    public ManagerIncidSeeIf.ViewerIf getViewer()
    {
        Timber.d("getViewer()");
        return viewer;
    }

    // ============================================================================================
    // ......................................... REACTOR ..........................................
    // ============================================================================================

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    static final class ReactorPswdChange implements ControllerPasswordChangeIf.ReactorPswdChangeIf {

        static final ReactorPswdChangeIf pswdChangeReactor = new ReactorPswdChange();

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
        public boolean passwordChange(ControllerPasswordChangeIf controller, Usuario usuario)
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

            private final ControllerPasswordChangeIf controller;

            PswdChangeSingleObserver(ControllerPasswordChangeIf controller)
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
                controller.processReactorError(e);
            }
        }
    }
}

package com.didekindroid.usuario.login;

import android.view.View;

import com.didekindroid.ControllerAbs;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenAndInitCache;
import static com.didekindroid.security.TokenIdentityCacher.cleanTkCacheActionBoolean;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.login.ControllerLogin.ReactorLogin.loginReactor;
import static com.didekindroid.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.Single.just;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
class ControllerLogin extends ControllerAbs implements ControllerLoginIf {

    private final ReactorLoginIf reactor;
    private final ViewerLoginIf<View,Object> viewer;

    ControllerLogin(ViewerLoginIf<View,Object> viewer)
    {
        this(viewer, loginReactor);
    }

    @SuppressWarnings("WeakerAccess")
    ControllerLogin(ViewerLoginIf<View, Object> viewer, ReactorLoginIf reactor)
    {
        this.viewer = viewer;
        this.reactor = reactor;
    }

    @Override
    public void validateLoginRemote(Usuario usuario)
    {
        Timber.i("validateLoginRemote()");
        assertTrue(usuario != null, bean_fromView_should_be_initialized);
        reactor.validateLogin(this, usuario);
    }

    @Override
    public void processBackLoginRemote(Boolean isLoginOk)
    {
        Timber.d("processBackLoginRemote()");
        viewer.processLoginBackInView(isLoginOk);
    }

    @Override
    public void doDialogPositiveClick(Usuario usuario)
    {
        Timber.d("doDialogPositiveClick()");
        assertTrue(usuario != null, bean_fromView_should_be_initialized);
        reactor.sendPasswordToUser(this, usuario);
    }

    @Override
    public void processBackDialogPositiveClick(Boolean isSendPassword)
    {
        Timber.d("processBackDialogPositiveClick()");
        viewer.processBackSendPswdInView(isSendPassword);
    }

    @Override
    public ViewerFirebaseTokenIf getViewer()
    {
        Timber.d("getViewer()");
        return viewer;
    }

    //  =====================================================================================================
    //    ............................................. REACTOR .............................................
    //  =====================================================================================================

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    static final class ReactorLogin implements ControllerLoginIf.ReactorLoginIf {

        static final ReactorLoginIf loginReactor = new ReactorLogin();

        private ReactorLogin()
        {
        }

        //    .................................... OBSERVABLES .................................

        static Single<Boolean> loginSingle(final Usuario usuario)
        {
            return fromCallable(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception
                {
                    return usuarioDao.loginInternal(usuario.getUserName(), usuario.getPassword());
                }
            });
        }

        static Single<Boolean> loginUpdateTkCache(final Usuario usuario)
        {
            return loginSingle(usuario).flatMap(new Function<Boolean, Single<Boolean>>() {
                @Override
                public Single<Boolean> apply(Boolean isLoginValid) throws Exception
                {
                    if (isLoginValid) {
                        return oauthTokenAndInitCache(usuario).toSingleDefault(true);
                    }
                    return just(false);
                }
            });
        }

        /**
         * Non static. It has a mock test implementation.
         */
        @Override
        public Single<Boolean> loginPswdSendSingle(final String email)
        {
            return fromCallable(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception
                {
                    return usuarioDao.sendPassword(email);
                }
            }).doOnSuccess(cleanTkCacheActionBoolean);
        }

        // ............................ SUBSCRIPTIONS ..................................

        @Override
        public boolean validateLogin(ControllerLoginIf controller, Usuario usuario)
        {
            return controller.getSubscriptions().add(
                    loginUpdateTkCache(usuario)
                            .subscribeOn(io())
                            .observeOn(mainThread())
                            .subscribeWith(new LoginValidateObserver(controller))
            );
        }

        @Override
        public boolean sendPasswordToUser(ControllerLoginIf controller, Usuario usuario)
        {
            return controller.getSubscriptions().add(
                    loginPswdSendSingle(usuario.getUserName())
                            .subscribeOn(io())
                            .observeOn(mainThread())
                            .subscribeWith(new LoginPswdSendObserver(controller))
            );
        }

        // ............................ SUBSCRIBERS ..................................

        abstract static class LoginObserver extends DisposableSingleObserver<Boolean> {

            final ControllerLoginIf controller;

            LoginObserver(final ControllerLoginIf controller)
            {
                this.controller = controller;
            }

            @Override
            public void onError(Throwable e)
            {
                Timber.d("onError, message: %s", e.getMessage());
                if (e instanceof UiException) {
                    Timber.d("UiException message: %s", ((UiException) e).getErrorBean().getMessage());
                }
                controller.processReactorError(e);
            }
        }

        static class LoginPswdSendObserver extends LoginObserver {

            LoginPswdSendObserver(ControllerLoginIf controller)
            {
                super(controller);
            }

            @Override
            public void onSuccess(Boolean isSendPassword)
            {
                Timber.d("onSuccess");
                controller.processBackDialogPositiveClick(isSendPassword);
            }
        }

        private static class LoginValidateObserver extends LoginObserver {

            LoginValidateObserver(ControllerLoginIf controller)
            {
                super(controller);
            }

            @Override
            public void onSuccess(Boolean isLoginOk)
            {
                Timber.d("onSuccess");
                controller.processBackLoginRemote(isLoginOk);
            }
        }
    }
}

package com.didekindroid.usuario.login;

import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenAndInitCache;
import static com.didekindroid.security.TokenIdentityCacher.cleanTkCacheActionBoolean;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.Single.just;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 07/03/17
 * Time: 17:39
 */
@SuppressWarnings("AnonymousInnerClassMayBeStatic")
class ReactorLogin implements ControllerLoginIf.ReactorLoginIf {

    static final ControllerLoginIf.ReactorLoginIf loginReactor = new ReactorLogin();

    ReactorLogin()
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

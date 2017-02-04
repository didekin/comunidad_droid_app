package com.didekindroid.usuario.login;


import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenAndInitCache;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.Single.just;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 19/12/16
 * Time: 16:32
 */
final class LoginReactor implements LoginReactorIf {

    static final LoginReactorIf loginReactor = new LoginReactor();

    private LoginReactor()
    {
    }

    //  =====================================================================================================
    //    .................................... OBSERVABLES .................................
    //  =====================================================================================================

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
                if (isLoginValid){
                    return oauthTokenAndInitCache(usuario).toSingleDefault(true);
                }
                return just(false);
            }
        });
    }

    static Single<Boolean> loginPswdSendSingle(final String email)
    {
        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDao.sendPassword(email);
            }
        });
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Override
    public boolean validateLogin(LoginControllerIf controller, Usuario usuario)
    {
        return controller.getSubscriptions().add(
                loginUpdateTkCache(usuario)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new LoginValidateObserver(controller))
        );
    }

    @Override
    public boolean sendPasswordToUser(LoginControllerIf controller, Usuario usuario)
    {
        return controller.getSubscriptions().add(
                LoginReactor.loginPswdSendSingle(usuario.getUserName())
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new LoginPswdSendObserver(controller))
        );
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    abstract static class LoginSingleObserver extends DisposableSingleObserver<Boolean> {

        final LoginControllerIf controller;

        LoginSingleObserver(final LoginControllerIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError, message: %s", e.getMessage());
            if (e instanceof UiException){
                Timber.d("UiException message: %s", ((UiException) e).getErrorBean().getMessage());
            }
            controller.processBackErrorInReactor(e);
        }
    }

    private static class LoginPswdSendObserver extends LoginSingleObserver {

        LoginPswdSendObserver(LoginControllerIf controller)
        {
            super(controller);
        }

        @Override
        public void onSuccess(Boolean isSendPassword)
        {
            Timber.d("onSuccess");
            controller.processBackSendPassword(isSendPassword);
        }
    }

    private static class LoginValidateObserver extends LoginSingleObserver {

        LoginValidateObserver(LoginControllerIf controller)
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
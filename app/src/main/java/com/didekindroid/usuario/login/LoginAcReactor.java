package com.didekindroid.usuario.login;

import com.didekin.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenFromUserPswd;
import static com.didekindroid.security.TokenIdentityCacher.initTokenAndRegisterFunc;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 19/12/16
 * Time: 16:32
 */

@SuppressWarnings("AnonymousInnerClassMayBeStatic")
final class LoginAcReactor implements LoginReactorIf {

    static final LoginReactorIf loginReactor = new LoginAcReactor();

    private LoginAcReactor()
    {
    }

    //  =====================================================================================================
    //    .................................... OBSERVABLES .................................
    //  =====================================================================================================

    private static Single<Boolean> getLoginValidateSingle(final Usuario usuario)
    {   // TODO: test.
        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDao.loginInternal(usuario.getUserName(), usuario.getPassword());
            }
        });
    }

    private static Single<Boolean> getZipLoginSingle(Usuario usuario)
    {   // TODO: test.
        return getLoginValidateSingle(usuario)
                .zipWith(
                        oauthTokenFromUserPswd(usuario),
                        initTokenAndRegisterFunc
                );
    }

    private static Single<Boolean> getLoginMailSingle(final String email)
    {   // TODO: test.
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
    {  // TODO: test.
        return controller.getSubscriptions().add(
                getZipLoginSingle(usuario)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new LoginSingleObserver(controller) {
                            @Override
                            public void onSuccess(Boolean isLoginOk)
                            {
                                Timber.d("onSuccess");
                                controller.processBackLoginRemote(isLoginOk);
                            }
                        })
        );
    }

    @Override
    public boolean sendPasswordToUser(LoginControllerIf controller, Usuario usuario)
    {  // TODO: test.
        return controller.getSubscriptions().add(
                LoginAcReactor.getLoginMailSingle(usuario.getUserName())
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new LoginSingleObserver(controller) {
                            @Override
                            public void onSuccess(Boolean isSendPassword)
                            {
                                Timber.d("onSuccess");
                                controller.processBackSendPassword(isSendPassword);
                            }
                        })
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
        {   // TODO: test.
            Timber.d("onError");
            controller.processBackErrorInReactor(e);
        }
    }
}
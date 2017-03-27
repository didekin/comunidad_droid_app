package com.didekindroid.usuario.login;

import android.support.annotation.NonNull;
import android.view.View;

import com.didekindroid.api.CtrlerIdentity;
import com.didekindroid.exception.UiException;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenAndInitCache;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.TokenIdentityCacher.cleanTkCacheActionBoolean;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.Single.just;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
@SuppressWarnings({"AnonymousInnerClassMayBeStatic", "WeakerAccess"})
class CtrlerLogin extends CtrlerIdentity<View> implements CtrlerLoginIf {

    private final ViewerLoginIf viewerLogin;

    CtrlerLogin(ViewerLoginIf viewer)
    {
        this(viewer, TKhandler);
    }

    CtrlerLogin(ViewerLoginIf viewer, IdentityCacher identityCacher)
    {
        super(viewer);
        viewerLogin = viewer;
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
     * It has a mock test implementation.
     */
    static Single<Boolean> loginPswdSendSingle(final Callable<Boolean> sendPswdCall)
    {
        return fromCallable(sendPswdCall).doOnSuccess(cleanTkCacheActionBoolean);
    }

    //    ................................. INSTANCE METHODS .................................

    @Override
    public boolean validateLogin(@NonNull Usuario usuario)
    {
        Timber.i("validateLogin()");
        return subscriptions.add(
                loginUpdateTkCache(usuario)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new LoginObserver(this) {
                            @Override
                            public void onSuccess(Boolean isLoginOk)
                            {
                                Timber.d("onSuccess");
                                controller.onSuccessValidateLogin(isLoginOk);
                            }
                        })
        );
    }

    @Override
    public void onSuccessValidateLogin(Boolean isLoginOk)
    {
        Timber.d("onSuccessValidateLogin()");
        viewerLogin.processLoginBackInView(isLoginOk);
    }

    @Override
    public boolean doDialogPositiveClick(@NonNull final Usuario usuario)
    {
        Timber.d("doDialogPositiveClick()");
        Callable<Boolean> sendPswdCallable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDao.sendPassword(usuario.getUserName());
            }
        };
        return subscriptions.add(
                loginPswdSendSingle(sendPswdCallable)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new LoginObserver(this) {
                            @Override
                            public void onSuccess(Boolean isSendPassword)
                            {
                                Timber.d("onSuccess");
                                controller.onSuccessDialogPositiveClick(isSendPassword);
                            }
                        })
        );
    }

    @Override
    public void onSuccessDialogPositiveClick(Boolean isSendPassword)
    {
        Timber.d("onSuccessDialogPositiveClick()");
        viewerLogin.processBackSendPswdInView(isSendPassword);
    }

    // ............................ SUBSCRIBERS ..................................

    abstract static class LoginObserver extends DisposableSingleObserver<Boolean> {

        final CtrlerLoginIf controller;

        LoginObserver(final CtrlerLoginIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorCtrl, message: %s", e.getMessage());
            if (e instanceof UiException) {
                Timber.d("UiException message: %s", ((UiException) e).getErrorBean().getMessage());
            }
            controller.onErrorCtrl(e);
        }
    }
}

package com.didekindroid.usuario.login;

import android.support.annotation.NonNull;

import com.didekindroid.api.Controller;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenObservable.oauthTokenAndInitCache;
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
class CtrlerLogin extends Controller implements CtrlerLoginIf {

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
    public boolean validateLogin(DisposableSingleObserver<Boolean> observer, @NonNull Usuario usuario)
    {
        Timber.i("validateLogin()");
        return subscriptions.add(
                loginUpdateTkCache(usuario)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean doDialogPositiveClick(DisposableSingleObserver<Boolean> observer, @NonNull final Usuario usuario)
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
                        .subscribeWith(observer)
        );
    }
}

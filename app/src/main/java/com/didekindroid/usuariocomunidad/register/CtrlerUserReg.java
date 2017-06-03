package com.didekindroid.usuariocomunidad.register;

import com.didekindroid.api.Controller;
import com.didekindroid.api.ObserverCacheCleaner;
import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenObservable.oauthTokenInitCacheUpdateRegister;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.util.DaoUtil.getResponseBody;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_DATA_NOT_INSERTED;
import static io.reactivex.Completable.error;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 24/05/17
 * Time: 12:13
 */

public class CtrlerUserReg extends Controller {

    // .................................... OBSERVABLES .................................

    static Completable userAndComuRegistered(final UsuarioComunidad usuarioComunidad)
    {
        Timber.d("userAndComuRegistered()");

        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                Response<Boolean> response = userComuDaoRemote.regComuAndUserAndUserComu(usuarioComunidad).execute();
                return getResponseBody(response);
            }
        }).flatMapCompletable(new Function<Boolean, CompletableSource>() {
            @Override
            public CompletableSource apply(Boolean isUserInDb) throws Exception
            {
                if (isUserInDb) {
                    return oauthTokenInitCacheUpdateRegister(usuarioComunidad.getUsuario());
                } else {
                    return error(new UiException(new ErrorBean(USER_DATA_NOT_INSERTED)));
                }
            }
        });
    }

    static Single<Boolean> userComuAndComuRegistered(final UsuarioComunidad usuarioComunidad)
    {
        Timber.d("userComuAndComuRegistered()");
        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return userComuDaoRemote.regComuAndUserComu(usuarioComunidad);
            }
        });
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    boolean registerUserAndComu(ObserverCacheCleaner observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("registerUserAndComu()");
        return subscriptions.add(
                userAndComuRegistered(usuarioComunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    boolean registerUserComuAndComu(DisposableSingleObserver<Boolean> observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("registerUserComuAndComu()");
        return subscriptions.add(
                userComuAndComuRegistered(usuarioComunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}

package com.didekindroid.usuariocomunidad.register;

import com.didekindroid.api.Controller;
import com.didekindroid.api.ObserverCacheCleaner;
import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.comunidad.Comunidad;
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
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.util.DaoUtil.getResponseBody;
import static com.didekinlib.http.UsuarioServConstant.IS_USER_DELETED;
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

public class CtrlerUsuarioComunidad extends Controller {

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

    static Completable userAndUserComuRegistered(final UsuarioComunidad usuarioComunidad)
    {
        Timber.d("userAndUserComuRegistered");

        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                Response<Boolean> response = userComuDaoRemote.regUserAndUserComu(usuarioComunidad).execute();
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

    static Single<Integer> userComuRegistered(final UsuarioComunidad usuarioComunidad)
    {
        Timber.d("userComuAndComuRegistered()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return userComuDaoRemote.regUserComu(usuarioComunidad);
            }
        });
    }

    static Single<Boolean> isOldestAdmonUser(final Comunidad comunidad)
    {
        Timber.d("isOldestAdmonUser()");
        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                Timber.d("call()");
                return userComuDaoRemote.isOldestOrAdmonUserComu(comunidad.getC_Id());
            }
        });
    }

    static Single<Integer> userComuModified(final UsuarioComunidad usuarioComunidad)
    {
        Timber.d("userComuModified()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return userComuDaoRemote.modifyUserComu(usuarioComunidad);
            }
        });
    }

    Single<Integer> userComuDeleted(final Comunidad comunidad)
    {
        Timber.d("userComuDeleted()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return userComuDaoRemote.deleteUserComu(comunidad.getC_Id());
            }
        }).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer rowsUpdated) throws Exception
            {
                if (rowsUpdated == IS_USER_DELETED) {
                    identityCacher.cleanIdentityCache();
                    identityCacher.updateIsRegistered(false);
                }
                return rowsUpdated;
            }
        });
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    boolean registerUserAndComu(ObserverCacheCleaner observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("registerUserAndComu()");
        return execObserverCacher(observer, userAndComuRegistered(usuarioComunidad));
    }

    boolean registerUserComuAndComu(DisposableSingleObserver<Boolean> observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("registerUserComuAndComu()");
        return execSingleObserver(observer, userComuAndComuRegistered(usuarioComunidad));
    }

    boolean registerUserAndUserComu(ObserverCacheCleaner observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("registerUserAndUserComu()");
        return execObserverCacher(observer, userAndUserComuRegistered(usuarioComunidad));
    }

    boolean registerUserComu(DisposableSingleObserver<Integer> observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("registerUserComu()");
        return execSingleObserver(observer, userComuRegistered(usuarioComunidad));
    }

    public boolean checkIsOldestAdmonUser(DisposableSingleObserver<Boolean> observer, Comunidad comunidad)
    {
        Timber.d("checkIsOldestAdmonUser()");
        return execSingleObserver(observer, isOldestAdmonUser(comunidad));
    }

    public boolean modifyUserComu(DisposableSingleObserver<Integer> observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("modifyUserComu()");
        return execSingleObserver(observer, userComuModified(usuarioComunidad));
    }

    public boolean deleteUserComu(DisposableSingleObserver<Integer> observer, Comunidad comunidad)
    {
        Timber.d("deleteUserComu()");
        return execSingleObserver(observer, userComuDeleted(comunidad));
    }

    // ............................ HELPERS ..................................

    private boolean execObserverCacher(ObserverCacheCleaner observer, Completable completable)
    {
        Timber.d("execObserverCacher()");
        return subscriptions.add(
                completable
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer));
    }

    private <T> boolean execSingleObserver(DisposableSingleObserver<T> observer, Single<T> observable)
    {
        Timber.d("execSingleObserver()");
        return subscriptions.add(
                observable
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}

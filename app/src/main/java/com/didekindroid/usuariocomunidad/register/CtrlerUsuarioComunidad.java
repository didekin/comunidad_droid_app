package com.didekindroid.usuariocomunidad.register;

import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.ObserverCacheCleaner;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_DATA_NOT_INSERTED;
import static com.didekinlib.http.usuario.UsuarioServConstant.IS_USER_DELETED;
import static io.reactivex.Completable.error;
import static io.reactivex.Completable.fromAction;
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

        return fromCallable(() -> httpInitializer.get().getResponseBody(userComuDao.regComuAndUserAndUserComu(usuarioComunidad).execute()))
                .flatMapCompletable(isUserInDb -> {
                    if (isUserInDb) {
                        return fromAction(() -> secInitializer.get().getTkCacher().updateIsRegistered(true));
                    } else {
                        return error(new UiException(new ErrorBean(USER_DATA_NOT_INSERTED)));
                    }
                });
    }

    static Single<Boolean> userComuAndComuRegistered(final UsuarioComunidad usuarioComunidad)
    {
        Timber.d("userComuAndComuRegistered()");
        return fromCallable(() -> userComuDao.regComuAndUserComu(usuarioComunidad));
    }

    static Completable userAndUserComuRegistered(final UsuarioComunidad usuarioComunidad)
    {
        Timber.d("userAndUserComuRegistered");

        return fromCallable(() -> httpInitializer.get().getResponseBody(userComuDao.regUserAndUserComu(usuarioComunidad).execute()))
                .flatMapCompletable(isUserInDb -> {
                    if (isUserInDb) {
                        return fromAction(() -> secInitializer.get().getTkCacher().updateIsRegistered(true));
                    } else {
                        return error(new UiException(new ErrorBean(USER_DATA_NOT_INSERTED)));
                    }
                });
    }

    static Single<Integer> userComuRegistered(final UsuarioComunidad usuarioComunidad)
    {
        Timber.d("userComuAndComuRegistered()");
        return fromCallable(() -> userComuDao.regUserComu(usuarioComunidad));
    }

    static Single<Boolean> isOldestAdmonUser(final Comunidad comunidad)
    {
        Timber.d("isOldestAdmonUser()");
        return fromCallable(() -> userComuDao.isOldestOrAdmonUserComu(comunidad.getC_Id()));
    }

    static Single<Integer> userComuModified(final UsuarioComunidad usuarioComunidad)
    {
        Timber.d("userComuModified()");
        return fromCallable(() -> userComuDao.modifyUserComu(usuarioComunidad));
    }

    Single<Integer> userComuDeleted(final Comunidad comunidad)
    {
        Timber.d("userComuDeleted()");
        return fromCallable(() -> userComuDao.deleteUserComu(comunidad.getC_Id()))
                .map(rowsUpdated -> {
                    if (rowsUpdated == IS_USER_DELETED) {
                        getTkCacher().cleanIdentityCache();
                        getTkCacher().updateIsRegistered(false);
                    }
                    return rowsUpdated;
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
        return getSubscriptions().add(
                completable
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer));
    }

    private <T> boolean execSingleObserver(DisposableSingleObserver<T> observer, Single<T> observable)
    {
        Timber.d("execSingleObserver()");
        return getSubscriptions().add(
                observable
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}

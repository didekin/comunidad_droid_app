package com.didekindroid.usuario.userdata;

import com.didekindroid.api.Controller;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenObservable.oauthTokenAndInitCache;
import static com.didekindroid.security.OauthTokenObservable.oauthTokenFromUserPswd;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;
import static java.lang.Boolean.TRUE;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 10:58
 */
@SuppressWarnings("WeakerAccess")
class CtrlerUserModified extends Controller implements CtrlerUserDataIf {

    // .................................... OBSERVABLES .................................

    static Single<Usuario> userDataLoaded()
    {
        Timber.d("userDataLoaded()");
        return fromCallable(new Callable<Usuario>() {
            @Override
            public Usuario call() throws Exception
            {
                return usuarioDao.getUserData();
            }
        });
    }

    static Completable userModifiedTkUpdated(final SpringOauthToken oldUserToken, final Usuario newUser)
    {
        Timber.d("userModifiedTkUpdated()");
        return Completable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDao.modifyUserWithToken(oldUserToken, newUser);
            }
        }).andThen(oauthTokenAndInitCache(newUser));
    }

    static Single<Boolean> userModifiedWithPswdValidation(Usuario oldUser, final Usuario newUser)
    {
        Timber.d("userModifiedWithPswdValidation()");
        return oauthTokenFromUserPswd(oldUser)
                .flatMap(new Function<SpringOauthToken, Single<Boolean>>() {
                    @Override
                    public Single<Boolean> apply(SpringOauthToken oldUserToken) throws Exception
                    {
                        return userModifiedTkUpdated(oldUserToken, newUser).toSingleDefault(TRUE);
                    }
                });
    }

    // .................................... INSTANCE METHODS .................................

    @Override
    public boolean loadUserData(DisposableSingleObserver<Usuario> observer)
    {
        Timber.d("loadUserData()");
        return subscriptions.add(
                userDataLoaded()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean modifyUser(DisposableSingleObserver<Boolean> observer, Usuario oldUser, Usuario newUser)
    {
        Timber.d("modifyUser()");
        return subscriptions.add(
                userModifiedWithPswdValidation(oldUser, newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer));
    }
}


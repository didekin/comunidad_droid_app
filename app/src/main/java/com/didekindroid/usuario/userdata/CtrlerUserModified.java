package com.didekindroid.usuario.userdata;

import com.didekindroid.api.Controller;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenObservable.oauthTokenFromUserPswd;
import static com.didekindroid.security.TokenIdentityCacher.initTokenAction;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_have_been_modified;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

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
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDao.modifyUserWithToken(oldUserToken, newUser);
            }
        }).flatMap(new Function<Integer, SingleSource<SpringOauthToken>>() {
            @Override
            public SingleSource<SpringOauthToken> apply(Integer modifiedUser) throws Exception
            {
                assertTrue(modifiedUser == 1, user_should_have_been_modified);
                return oauthTokenFromUserPswd(newUser);
            }
        }).doOnSuccess(initTokenAction).toCompletable();
    }

    static Single<Boolean> userModifiedWithPswdValidation(Usuario oldUser, final Usuario newUser)
    {
        return oauthTokenFromUserPswd(oldUser)
                .flatMap(new Function<SpringOauthToken, Single<Boolean>>() {
                    @Override
                    public Single<Boolean> apply(SpringOauthToken oldUserToken) throws Exception
                    {
                        return userModifiedTkUpdated(oldUserToken, newUser).toSingleDefault(Boolean.TRUE);
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


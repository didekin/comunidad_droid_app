package com.didekindroid.usuario.dao;

import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenObservable.oauthTokenAndInitCache;
import static com.didekindroid.security.OauthTokenObservable.oauthTokenFromUserPswd;
import static com.didekindroid.security.TokenIdentityCacher.cleanTkCacheConsumer;
import static com.didekindroid.security.TokenIdentityCacher.cleanTokenAndUnregisterFunc;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDaoRemote;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.Single.just;
import static java.lang.Boolean.TRUE;

/**
 * User: pedro@didekin
 * Date: 31/10/2017
 * Time: 11:19
 */

public class UsuarioDaoObservable {

    @SuppressWarnings("WeakerAccess")
    static final UsuarioDaoIf usuarioDao = usuarioDaoRemote;

    public static Single<Boolean> deleteMeSingle()
    {
        Timber.d("deleteMeSingle()");

        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDaoRemote.deleteUser();
            }
        }).map(cleanTokenAndUnregisterFunc);
    }

    public static Single<Boolean> loginSingle(final Usuario usuario)
    {
        Timber.d("loginSingle()");
        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDao.loginInternal(usuario.getUserName(), usuario.getPassword());
            }
        });
    }

    /**
     * It has a mock test implementation. It clears token in cache.
     */
    public static Single<Boolean> loginPswdSendSingle(final Callable<Boolean> sendPswdCall)
    {
        Timber.d("loginPswdSendSingle()");
        return fromCallable(sendPswdCall).doOnSuccess(cleanTkCacheConsumer);
    }

    public static Single<Boolean> loginUpdateTkCache(final Usuario usuario)
    {
        Timber.d("loginUpdateTkCache()");
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
     * Password change submitting the current password.
     */
    public static Completable passwordChangeWithPswdValidation(final Usuario oldUser, final Usuario newUser)
    {
        Timber.d("passwordChangeWithPswdValidation()");
        return oauthTokenFromUserPswd(oldUser)

                .flatMapCompletable(new Function<SpringOauthToken, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@io.reactivex.annotations.NonNull final SpringOauthToken oldOauthToken) throws Exception
                    {
                        Timber.d("passwordChangeWithPswdValidation()");
                        return fromCallable(new Callable<Integer>() {
                            @Override
                            public Integer call() throws Exception
                            {
                                return usuarioDao.passwordChange(oldOauthToken, newUser.getPassword());
                            }
                        })
                                .flatMapCompletable(new Function<Integer, CompletableSource>() {
                                    @Override
                                    public CompletableSource apply(Integer passwordUpdated) throws Exception
                                    {
                                        Timber.d("apply()");
                                        return oauthTokenAndInitCache(newUser);
                                    }
                                });
                    }
                });
    }

    public static Single<Usuario> userDataLoaded()
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

    public static Completable userModifiedTkUpdated(final SpringOauthToken oldUserToken, final Usuario newUser)
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

    public static Single<Boolean> userModifiedWithPswdValidation(Usuario oldUser, final Usuario newUser)
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
}

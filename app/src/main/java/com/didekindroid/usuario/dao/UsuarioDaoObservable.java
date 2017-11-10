package com.didekindroid.usuario.dao;

import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
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

class UsuarioDaoObservable {

    @SuppressWarnings("WeakerAccess")
    static final UsuarioDaoIf usuarioDao = usuarioDaoRemote;

    static Single<Boolean> deleteMeSingle()
    {
        Timber.d("deleteMeSingle()");
        return fromCallable(usuarioDaoRemote::deleteUser).map(cleanTokenAndUnregisterFunc);
    }

    static Single<Boolean> loginSingle(final Usuario usuario)
    {
        Timber.d("loginSingle()");
        return fromCallable(() -> usuarioDao.loginInternal(usuario.getUserName(), usuario.getPassword()));
    }

    /**
     * It has a mock test implementation. It clears token in cache.
     */
    static Single<Boolean> loginPswdSendSingle(final Callable<Boolean> sendPswdCall)
    {
        Timber.d("loginPswdSendSingle()");
        return fromCallable(sendPswdCall).doOnSuccess(cleanTkCacheConsumer);
    }

    static Single<Boolean> loginUpdateTkCache(final Usuario usuario)
    {
        Timber.d("loginUpdateTkCache()");
        return loginSingle(usuario).flatMap(isLoginValid -> {
            if (isLoginValid) {
                return oauthTokenAndInitCache(usuario).toSingleDefault(true);
            }
            return just(false);
        });
    }

    /**
     * Password change submitting the current password.
     */
    static Completable passwordChangeWithPswdValidation(final Usuario oldUser, final Usuario newUser)
    {
        Timber.d("passwordChangeWithPswdValidation()");
        return oauthTokenFromUserPswd(oldUser)
                .flatMapCompletable(
                        oldOauthToken ->
                                fromCallable(() -> usuarioDao.passwordChange(oldOauthToken, newUser.getPassword()))
                                        .flatMapCompletable(passwordUpdated -> oauthTokenAndInitCache(newUser))
                );
    }

    static Single<Usuario> userData()
    {
        Timber.d("userData()");
        return fromCallable(usuarioDao::getUserData);
    }

    static Completable userModifiedTkUpdated(final SpringOauthToken oldUserToken, final Usuario newUser)
    {
        Timber.d("userModifiedTkUpdated()");
        return Completable
                .fromCallable(() -> usuarioDao.modifyUserWithToken(oldUserToken, newUser))
                .andThen(oauthTokenAndInitCache(newUser));
    }

    static Single<Boolean> userModifiedWithPswdValidation(Usuario oldUser, final Usuario newUser)
    {
        Timber.d("userModifiedWithPswdValidation()");
        return oauthTokenFromUserPswd(oldUser)
                .flatMap(oldUserToken -> userModifiedTkUpdated(oldUserToken, newUser).toSingleDefault(TRUE));
    }
}

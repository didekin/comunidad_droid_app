package com.didekindroid.security;

import android.support.annotation.NonNull;

import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.TokenIdentityCacher.initTokenAction;
import static com.didekindroid.security.TokenIdentityCacher.initTokenUpdateRegisterAction;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 27/11/16
 * Time: 14:35
 */
@SuppressWarnings("AnonymousInnerClassMayBeStatic")
public class OauthTokenObservable {

    /**
     * @return a Single to obtain a new access token with userName and password credentials.
     */
    public static Single<SpringOauthToken> oauthTokenFromUserPswd(final Usuario usuario)
    {
        Timber.d("oauthTokenFromUserPswd(), Thread: %s", Thread.currentThread().getName());

        return fromCallable(() -> Oauth2.getPasswordUserToken(usuario.getUserName(), usuario.getPassword()));
    }

    /**
     * Preconditions:
     * 1. The user should be registered and a refreshToken is passed from the token cache.
     *
     * @return a Completable which obtains a new access token based on the old refresh token credential,
     * and initializes the token cache with the new one.
     */
    static Completable oauthTokenFromRefreshTk(@NonNull final String refreshToken)
    {
        Timber.d("oauthTokenFromRefreshTk()");
        assertTrue(TKhandler.isRegisteredUser(), user_should_be_registered);

        return fromCallable(() -> Oauth2.getRefreshUserToken(refreshToken)).doOnSuccess(initTokenAction)
                .toCompletable();
    }

    /**
     * Preconditions:
     * 1. The user should be registered.
     *
     * @return a Completable which calls oauthTokenFromUserPswd, and initializes the token cache and
     * update registered status (just in case, to keep consistency).
     */
    public static Completable oauthTokenAndInitCache(final Usuario usuario)
    {
        Timber.d("oauthTokenAndInitCache()");
        return oauthTokenFromUserPswd(usuario)
                .doOnSuccess(initTokenUpdateRegisterAction)
                .toCompletable();
    }

    /**
     * Preconditions:
     * 1. The user should not be registered.
     *
     * @return a Completable which calls oauthTokenFromUserPswd, initializes the token cache and update register status to true.
     */
    static Completable oauthTokenInitCacheUpdateRegister(final Usuario usuario)
    {
        Timber.d("oauthTokenAndInitCache()");
        return oauthTokenFromUserPswd(usuario)
                .doOnSuccess(initTokenUpdateRegisterAction)
                .toCompletable();
    }
}

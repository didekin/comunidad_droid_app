package com.didekindroid.lib_one.security;

import android.support.annotation.NonNull;

import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

import static com.didekindroid.lib_one.security.AuthDao.authDao;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_be_registered;
import static io.reactivex.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 27/11/16
 * Time: 14:35
 */
@SuppressWarnings("AnonymousInnerClassMayBeStatic")
public final class OauthTokenObservable {

    /**
     * @return a Single to obtain a new access token with userName and password credentials.
     */
    public static Single<SpringOauthToken> oauthTokenFromUserPswd(final Usuario usuario)
    {
        Timber.d("oauthTokenFromUserPswd(), Thread: %s", Thread.currentThread().getName());

        return fromCallable(() -> authDao.getPasswordUserToken(usuario.getUserName(), usuario.getPassword()));
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
        assertTrue(secInitializer.get().getTkCacher().isRegisteredUser(), user_should_be_registered);

        return fromCallable(() -> authDao.getRefreshUserToken(refreshToken))
                .doOnSuccess(secInitializer.get().getTkCacher()::initIdentityCache)
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
                .doOnSuccess(secInitializer.get().getTkCacher().getInitTokenUpdateRegisterAction())
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
                .doOnSuccess(secInitializer.get().getTkCacher().getInitTokenUpdateRegisterAction())
                .toCompletable();
    }
}

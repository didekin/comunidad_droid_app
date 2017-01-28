package com.didekindroid.security;

import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.TokenIdentityCacher.initTokenAction;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 27/11/16
 * Time: 14:35
 */
public final class OauthTokenReactor implements OauthTokenReactorIf {

    public static final OauthTokenReactorIf tokenReactor = new OauthTokenReactor();

    private OauthTokenReactor()
    {
    }
    //  =====================================================================================================
    //    .................................... OBSERVABLES .................................
    //  =====================================================================================================

    /**
     * Preconditions:
     * 1. The user should be registered.
     *
     * @return a Single to obtain a new access token with userName and password credentials.
     */
    public static Single<SpringOauthToken> oauthTokenFromUserPswd(final Usuario usuario)
    {
        return fromCallable(new Callable<SpringOauthToken>() {
            @Override
            public SpringOauthToken call() throws Exception
            {
                Timber.d("Thread for oauthTokenFromUserPswd: %s", Thread.currentThread().getName());
                return Oauth2.getPasswordUserToken(usuario.getUserName(), usuario.getPassword());
            }
        });
    }

    /**
     * Preconditions:
     * 1. The user should be registered and the token cache previously initialized.
     *
     * @return a Completable which obtains a new access token based on the old refresh token credential,
     * and initializes the token cache with the new one.
     */
    static Completable oauthTokenFromRefreshTk(final String refreshToken)
    {
        return fromCallable(new Callable<SpringOauthToken>() {
            @Override
            public SpringOauthToken call() throws Exception
            {
                Timber.d("Thread for oauthTokenFromRefreshTk: %s", Thread.currentThread().getName());
                return Oauth2.getRefreshUserToken(refreshToken);
            }
        }).doOnSuccess(initTokenAction)
                .toCompletable()
                .subscribeOn(io());
    }

    /**
     * Preconditions:
     * 1. The user should be registered and the token cache previously initialized.
     *
     * @return a Completable which calls oauthTokenFromUserPswd and initializes the token cache.
     */
    public static Completable oauthTokenAndInitCache(final Usuario usuario)
    {
        return oauthTokenFromUserPswd(usuario)
                .doOnSuccess(initTokenAction)
                .toCompletable()
                .subscribeOn(io());
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    /**
     * Convenience disposable which relates a Completable oauthTokenAndInitCache and a
     * oauthUpdateTokenCacheObserver.
     */
    @Override
    public void updateTkAndCacheFromUser(Usuario newUser)
    {
        oauthTokenAndInitCache(newUser).subscribeWith(new OauthUpdateTokenCacheObserver());
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    @SuppressWarnings("WeakerAccess")
    static class OauthUpdateTokenCacheObserver extends DisposableCompletableObserver {
        @Override
        public void onComplete()
        {
            Timber.d("onComplete(), Thread for subscriber: %s", Thread.currentThread().getName());
        }

        /**
         * If there is an error, the cache for oauth tokens is cleared. The user will be forced to
         * login in the next access to a restricted activity.
         */
        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError(), Thread for subscriber: %s", Thread.currentThread().getName());
            TKhandler.cleanIdentityCache();
        }
    }
}

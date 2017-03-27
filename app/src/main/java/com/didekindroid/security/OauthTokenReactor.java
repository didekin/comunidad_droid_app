package com.didekindroid.security;

import android.support.annotation.NonNull;

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
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 27/11/16
 * Time: 14:35
 */
@SuppressWarnings("AnonymousInnerClassMayBeStatic")
public class OauthTokenReactor implements OauthTokenReactorIf {

    public static final OauthTokenReactorIf tokenReactor = new OauthTokenReactor();

    OauthTokenReactor()
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
        Timber.d("oauthTokenFromUserPswd(), Thread: %s", Thread.currentThread().getName());
        assertTrue(TKhandler.isRegisteredUser(), user_should_be_registered);

        return fromCallable(new Callable<SpringOauthToken>() {
            @Override
            public SpringOauthToken call() throws Exception
            {
                return Oauth2.getPasswordUserToken(usuario.getUserName(), usuario.getPassword());
            }
        });
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
     * 1. The user should be registered.
     *
     * @return a Completable which calls oauthTokenFromUserPswd and initializes the token cache.
     */
    public static Completable oauthTokenAndInitCache(final Usuario usuario)
    {
        Timber.d("oauthTokenAndInitCache()");
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
        Timber.d("updateTkAndCacheFromUser()");
        oauthTokenAndInitCache(newUser).observeOn(mainThread()).subscribeWith(new OauthUpdateTokenCacheObserver());
    }

    @Override
    public DisposableCompletableObserver updateTkCacheFromRefreshTk(final String refreshToken){
        Timber.d("updateTkCacheFromRefreshTk()");
        return oauthTokenFromRefreshTk(refreshToken).observeOn(mainThread()).subscribeWith(new OauthUpdateTokenCacheObserver());
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
            dispose();
        }

        /**
         * If there is an error, the cache for oauth tokens is cleared. The user will be forced to
         * login in the next access to a restricted activity.
         */
        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorCtrl(), Thread for subscriber: %s", Thread.currentThread().getName());
            TKhandler.cleanIdentityCache();
            dispose();
        }
    }
}

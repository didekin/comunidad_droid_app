package com.didekindroid.security;

import com.didekindroid.api.Controller;
import com.didekindroid.api.ObserverCacheCleaner;
import com.didekinlib.model.usuario.Usuario;

import timber.log.Timber;

import static com.didekindroid.security.OauthTokenObservable.oauthTokenAndInitCache;
import static com.didekindroid.security.OauthTokenObservable.oauthTokenFromRefreshTk;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 15/05/17
 * Time: 16:25
 */

public class CtrlerAuthToken extends Controller implements CtrlerAuthTokenIf {

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    /**
     * Postconditions:
     * If tokenCache.getRefreshToken() != null, but tokenCache.get().getValue() is null (no access token in cache,
     * but there exists a refresh token), the access token is remotely retrieved and updated in cache.
     */
    @Override
    public void refreshAccessToken()
    {
        Timber.d("refreshAccessToken()");

        if (isRegisteredUser()
                && identityCacher.getTokenCache().get() != null
                && identityCacher.getTokenCache().get().getRefreshToken() != null
                && (identityCacher.getTokenCache().get().getValue() == null || identityCacher.getTokenCache().get().getValue().isEmpty())
                ) {
            updateTkCacheFromRefreshTk(identityCacher.getRefreshTokenValue());
        }
    }

    /**
     * Convenience disposable which relates a Completable oauthTokenAndInitCache and a
     * oauthUpdateTokenCacheObserver.
     */
    @Override
    public void updateTkAndCacheFromUser(Usuario newUser)
    {
        Timber.d("updateTkAndCacheFromUser()");
        oauthTokenAndInitCache(newUser)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(new ObserverCacheCleaner(this));
    }

    @Override
    public boolean updateTkCacheFromRefreshTk(final String refreshToken)
    {
        Timber.d("updateTkCacheFromRefreshTk()");
        return subscriptions.add(
                oauthTokenFromRefreshTk(refreshToken)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ObserverCacheCleaner(this))
        );
    }
}

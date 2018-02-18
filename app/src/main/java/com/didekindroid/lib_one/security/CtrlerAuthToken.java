package com.didekindroid.lib_one.security;

import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.ObserverCacheCleaner;
import com.didekindroid.lib_one.api.Viewer;

import timber.log.Timber;

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
    public void refreshAccessToken(Viewer viewer)
    {
        Timber.d("refreshAccessToken()");

        if (isRegisteredUser()
                && getTkCacher().getTokenCache().get() != null
                && getTkCacher().getTokenCache().get().getRefreshToken() != null
                && (getTkCacher().getTokenCache().get().getValue() == null || getTkCacher().getTokenCache().get().getValue().isEmpty())
                ) {
            updateTkCacheFromRefreshTk(getTkCacher().getRefreshTokenValue(), viewer);
        }
    }

    @Override
    public boolean updateTkCacheFromRefreshTk(final String refreshToken, Viewer viewer)
    {
        Timber.d("updateTkCacheFromRefreshTk()");
        return getSubscriptions().add(
                OauthTokenObservable.oauthTokenFromRefreshTk(refreshToken)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ObserverCacheCleaner(viewer))
        );
    }
}

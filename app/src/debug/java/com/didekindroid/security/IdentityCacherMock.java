package com.didekindroid.security;

import android.content.Context;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.IdentityCacher;
import com.didekinlib.http.auth.SpringOauthToken;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: pedro@didekin
 * Date: 07/03/17
 * Time: 14:53
 */

public class IdentityCacherMock implements IdentityCacher {

    private volatile boolean isRegistered;

    @Override
    public void initIdentityCache(SpringOauthToken springOauthToken)
    {
    }

    @Override
    public String checkBearerTokenInCache() throws UiException
    {
        return null;
    }

    @Override
    public String checkBearerToken(SpringOauthToken oauthToken) throws UiException
    {
        return null;
    }

    @Override
    public void cleanIdentityCache()
    {
    }

    @Override
    public Context getContext()
    {
        return null;
    }

    @Override
    public AtomicReference<SpringOauthToken> getTokenCache()
    {
        return null;
    }

    @Override
    public File getRefreshTokenFile()
    {
        return null;
    }

    @Override
    public String getRefreshTokenValue()
    {
        return null;
    }

    @Override
    public boolean isRegisteredUser()
    {
        return isRegistered;
    }

    @Override
    public void updateIsRegistered(boolean isRegisteredUser)
    {
        isRegistered = isRegisteredUser;
    }
}

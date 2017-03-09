package com.didekindroid.security;

import android.content.Context;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.oauth2.SpringOauthToken;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.testutil.ConstantExecution.IDENTITY_AFTER_IS_REGISTERED;
import static com.didekindroid.testutil.ConstantExecution.IDENTITY_AFTER_UPDATE_REGISTERED;
import static com.didekindroid.testutil.ConstantExecution.IDENTITY_FLAG_INITIAL;
import static com.didekindroid.testutil.ConstantExecution.WRONG_FLAG_VALUE;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 07/03/17
 * Time: 14:53
 */

public class IdentityCacherMock implements IdentityCacher {

    public static final AtomicReference<String> flagIdentityMockMethodExec = new AtomicReference<>(IDENTITY_FLAG_INITIAL);
    private volatile boolean isRegistered;

    @Override
    public void initIdentityCache(SpringOauthToken springOauthToken)
    {
    }

    @Override
    public void cleanIdentityCache()
    {
    }

    @Override
    public String doHttpAuthHeaderFromTkInCache() throws UiException
    {
        return null;
    }

    @Override
    public String doHttpAuthHeader(SpringOauthToken oauthToken) throws UiException
    {
        return null;
    }

    @Override
    public SpringOauthToken getAccessTokenInCache() throws UiException
    {
        return null;
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
        assertTrue(flagIdentityMockMethodExec.getAndSet(IDENTITY_AFTER_IS_REGISTERED).equals(IDENTITY_FLAG_INITIAL), WRONG_FLAG_VALUE);
        return isRegistered;
    }

    @Override
    public void updateIsRegistered(boolean isRegisteredUser)
    {
        assertTrue(flagIdentityMockMethodExec.getAndSet(IDENTITY_AFTER_UPDATE_REGISTERED).equals(IDENTITY_FLAG_INITIAL), WRONG_FLAG_VALUE);
        isRegistered = isRegisteredUser;
    }
}

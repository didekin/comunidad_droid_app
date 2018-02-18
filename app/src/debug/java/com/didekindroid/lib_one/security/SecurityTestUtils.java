package com.didekindroid.lib_one.security;


import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.auth.SpringOauthToken;

import java.sql.Timestamp;
import java.util.Date;

import static com.didekindroid.lib_one.security.AuthDao.authDao;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;


/**
 * User: pedro@didekin
 * Date: 21/12/16
 * Time: 10:40
 */

public final class SecurityTestUtils {

    private SecurityTestUtils()
    {
    }

    //    ============================ SECURITY ============================

    public static void updateSecurityData(String userName, String password) throws UiException
    {
        SpringOauthToken token = authDao.getPasswordUserToken(userName, password);
        secInitializer.get().getTkCacher().initIdentityCache(token);
        secInitializer.get().getTkCacher().updateIsRegistered(true);
    }

    static SpringOauthToken doSpringOauthToken(String accessToken, String refreshToken)
    {
        return new SpringOauthToken(
                accessToken,
                new Timestamp(new Date().getTime() + 7200000),
                "bearer",
                new SpringOauthToken.OauthToken(refreshToken, new Timestamp(new Date().getTime() + 7200000)),
                new String[]{"readwrite"}
        );
    }

    public static SpringOauthToken doSpringOauthToken()
    {
        return doSpringOauthToken("50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef", "50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef");
    }

    static SpringOauthToken doSpringOauthToken(String refreshTokenKey)
    {
        return doSpringOauthToken("50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef", refreshTokenKey);
    }

    public static void checkInitTokenCache(IdentityCacherIf tkCacherIn)
    {
        assertTrue(tkCacherIn.getTokenCache().get() != null, "TokenCache is OK");
        assertTrue(!tkCacherIn.getTokenCache().get().getValue().isEmpty(), "TokenCache is OK");
        assertTrue(!tkCacherIn.getRefreshTokenValue().isEmpty(), "TokenCache is OK");
        assertTrue(tkCacherIn.getRefreshTokenFile().exists(), "TokenCache is OK");
    }

    public static void checkNoInitCache(IdentityCacherIf tkCacherIn)
    {
        assertTrue(tkCacherIn.getTokenCache().get() == null, "TokenCache is OK");
        assertTrue(!tkCacherIn.getRefreshTokenFile().exists(), "TokenCache is OK");
    }

    static void checkUpdateTokenCache(SpringOauthToken oldToken, IdentityCacherIf tkCacherIn)
    {
        checkUpdatedCacheAfterPswd(true, oldToken, tkCacherIn);
    }

    public static void checkUpdatedCacheAfterPswd(boolean isPswdUpdated, SpringOauthToken oldToken, IdentityCacherIf tkCacherIn)
    {
        checkInitTokenCache(tkCacherIn);
        if (isPswdUpdated) {
            assertTrue(!tkCacherIn.getTokenCache().get().equals(oldToken), "TokenCache is OK");
        } else {
            assertTrue(tkCacherIn.getTokenCache().get().equals(oldToken), "TokenCache is OK");
        }
    }
}

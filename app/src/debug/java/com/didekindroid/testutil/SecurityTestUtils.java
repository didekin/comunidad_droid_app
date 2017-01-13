package com.didekindroid.testutil;


import com.didekin.http.oauth2.SpringOauthToken;
import com.didekindroid.exception.UiException;

import java.sql.Timestamp;
import java.util.Date;

import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;

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
        SpringOauthToken token = Oauth2.getPasswordUserToken(userName, password);
        TKhandler.initIdentityCache(token);
        TKhandler.updateIsRegistered(true);
    }

    public static SpringOauthToken doSpringOauthToken()
    {
        return new SpringOauthToken(
                "50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef",
                new Timestamp(new Date().getTime() + 7200000),
                "bearer",
                new SpringOauthToken.OauthToken("50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef", new Timestamp(new Date().getTime() + 7200000)),
                new String[]{"readwrite"}
        );
    }

    public static SpringOauthToken doSpringOauthToken(String refreshTokenKey)
    {
        return new SpringOauthToken(
                "50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef",
                new Timestamp(new Date().getTime() + 7200000),
                "bearer",
                new SpringOauthToken.OauthToken(refreshTokenKey, new Timestamp(new Date().getTime() + 7200000)),
                new String[]{"readwrite"}
        );
    }
}

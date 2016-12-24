package com.didekinaar.testutil;

import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.exception.UiException;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.didekinaar.security.Oauth2DaoRemote.Oauth2;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static java.util.Calendar.DAY_OF_MONTH;

/**
 * User: pedro@didekin
 * Date: 21/12/16
 * Time: 10:40
 */

public final class AarTestUtil {

    private AarTestUtil()
    {
    }

    //    ============================= DATES ===================================

    public static Timestamp doTimeStampFromCalendar(int daysToAdd)
    {
        Calendar fCierre = new GregorianCalendar();
        fCierre.add(DAY_OF_MONTH, daysToAdd);
        return new Timestamp(fCierre.getTimeInMillis());
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

package com.didekindroid.lib_one.security;

import android.util.Base64;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.auth.AuthClient;
import com.didekinlib.http.auth.AuthEndPoints;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.http.exception.ErrorBean;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekinlib.http.auth.AuthClient.BASIC_AND_SPACE;
import static com.didekinlib.http.auth.AuthClient.CL_USER;
import static com.didekinlib.http.auth.AuthConstant.PASSWORD_GRANT;
import static com.didekinlib.http.auth.AuthConstant.REFRESH_TOKEN_GRANT;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 10:52
 */
public final class AuthDao implements AuthEndPoints {

    public static final AuthDao authDao = new AuthDao(httpInitializer.get());
    private final HttpInitializerIf http_initializer;
    private final AuthEndPoints endPoint;

    private AuthDao(HttpInitializerIf httpInitializerIn)
    {
        http_initializer = httpInitializerIn;
        endPoint = http_initializer.getHttpHandler().getService(AuthEndPoints.class);
    }

    //  ================================== AuthEndPoints implementation ============================

    @Override
    public Call<SpringOauthToken> getPasswordUserToken(String authClient, String username, String password, String grantType)
    {
        return endPoint.getPasswordUserToken(authClient, username, password, grantType);
    }

    @Override
    public Call<SpringOauthToken> getRefreshUserToken(String authClient, String refreshToken, String grantType)
    {
        return endPoint.getRefreshUserToken(authClient, refreshToken, grantType);
    }

    @Override
    public Call<ErrorBean> getNotFoundMsg()
    {
        return endPoint.getNotFoundMsg();
    }

//  =============================================================================
//                          CONVENIENCE METHODS
//  =============================================================================

    public SpringOauthToken getPasswordUserToken(String userName, String password) throws UiException
    {
        Timber.d("getPasswordUserToken()");
        try {
            Response<SpringOauthToken> response = getPasswordUserToken(
                    doAuthBasicHeader(CL_USER),
                    userName,
                    password,
                    PASSWORD_GRANT).execute();
            return http_initializer.getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    SpringOauthToken getRefreshUserToken(String refreshTokenKey) throws UiException
    {
        Timber.d("getRefreshUserToken()");
        try {
            Response<SpringOauthToken> response = getRefreshUserToken(
                    doAuthBasicHeader(CL_USER),
                    refreshTokenKey,
                    REFRESH_TOKEN_GRANT
            ).execute();
            return http_initializer.getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

//  =============================================================================
//                          HELPER METHODS
//  =============================================================================

    String doAuthBasicHeader(AuthClient cliente)
    {
        String baseString = cliente.getId() + ":" + cliente.getSecret();
        String base64AuthData = Base64.encodeToString(baseString.getBytes(), Base64.DEFAULT);
        return BASIC_AND_SPACE + base64AuthData.substring(0, base64AuthData.length() - 1);
    }
}

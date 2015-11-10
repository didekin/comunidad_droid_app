package com.didekindroid.usuario.webservices;

import android.util.Base64;
import android.util.Log;

import com.didekin.common.oauth2.Oauth2EndPoints;
import com.didekin.common.oauth2.OauthClient;
import com.didekin.common.oauth2.OauthToken.AccessToken;

import retrofit.client.Response;

import static com.didekin.common.RetrofitRestBuilder.BUILDER;
import static com.didekin.common.oauth2.OauthClient.CL_USER;
import static com.didekin.common.oauth2.OauthConstant.PASSWORD_GRANT;
import static com.didekin.common.oauth2.OauthConstant.REFRESH_TOKEN_GRANT;
import static com.didekin.common.oauth2.OauthTokenHelper.BASIC_AND_SPACE;
import static com.didekindroid.DidekindroidApp.getBaseURL;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 10:52
 */
public enum Oauth2Service implements Oauth2EndPoints {

    Oauth2(BUILDER.getService(Oauth2EndPoints.class, getBaseURL())) {
        @Override
        public AccessToken getPasswordUserToken(String authClient, String username, String password, String
                grantType)
        {
            return Oauth2.endPoint.getPasswordUserToken(authClient, username, password, grantType);
        }

        @Override
        public AccessToken getRefreshUserToken(String authClient, String refreshToken, String grantType)
        {
            return Oauth2.endPoint.getRefreshUserToken(authClient, refreshToken, grantType);
        }

        public Response getNotFoundMsg()
        {
            return Oauth2.endPoint.getNotFoundMsg();
        }
    },;

    private static final String TAG = Oauth2Service.class.getCanonicalName();

    private final Oauth2EndPoints endPoint;

    Oauth2Service(Oauth2EndPoints oauth2EndPoints)
    {
        endPoint = oauth2EndPoints;
    }

//    ::::::::::::::::::::::::::::::::::::::::::
//                 CONVENIENCE METHODS
//    ::::::::::::::::::::::::::::::::::::::::::

    public AccessToken getPasswordUserToken(String userName, String password)
    {
        Log.d(TAG, "getPasswordUserToken()");

        return getPasswordUserToken(
                doAuthBasicHeader(CL_USER),
                userName,
                password,
                PASSWORD_GRANT);
    }

    public AccessToken getRefreshUserToken(String refreshTokenKey)
    {
        Log.d(TAG, "getRefreshUserToken()");

        return getRefreshUserToken(
                doAuthBasicHeader(CL_USER),
                refreshTokenKey,
                REFRESH_TOKEN_GRANT);
    }

    public abstract Response getNotFoundMsg();

//    ::::::::::::::::::::::::::::::::::::::::::
//                  HELPER METHODS
//    ::::::::::::::::::::::::::::::::::::::::::

    String doAuthBasicHeader(OauthClient cliente)
    {
        String baseString = cliente.getId() + ":" + cliente.getSecret();

        String base64AuthData = Base64.encodeToString(baseString.getBytes(), Base64.DEFAULT);

        return BASIC_AND_SPACE + base64AuthData.substring(0, base64AuthData.length() - 1);
    }
}

package com.didekindroid.usuario.webservices;

import android.util.Base64;
import android.util.Log;
import com.didekin.retrofitcl.Oauth2EndPoints;
import com.didekin.security.OauthClient;
import com.didekin.security.OauthToken.AccessToken;
import retrofit.client.Response;

import static com.didekin.retrofitcl.RetrofitRestBuilder.BUILDER;
import static com.didekin.security.OauthClient.CL_USER;
import static com.didekin.security.OauthTokenHelper.BASIC_AND_SPACE;
import static com.didekin.security.SecurityConstant.PASSWORD_GRANT;
import static com.didekin.security.SecurityConstant.REFRESH_TOKEN_GRANT;
import static com.didekindroid.DidekindroidApp.getBaseURL;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 10:52
 */
public enum Oauth2Service implements Oauth2EndPoints, Oauth2ServiceIf {

    Oauth2(BUILDER.getService(Oauth2EndPoints.class, getBaseURL())) {
        @Override
        public AccessToken getPasswordUserToken(String authClient, String username, String password, String grantType)
        {
            return Oauth2.endPoint.getPasswordUserToken(authClient, username, password, grantType);
        }

        @Override
        public AccessToken getRefreshUserToken(String authClient, String refreshToken, String grantType)
        {
            return Oauth2.endPoint.getRefreshUserToken(authClient, refreshToken, grantType);
        }

        @Override
        public BodyText getHello()
        {
            Log.d(TAG, "getHello()");
            return Oauth2.endPoint.getHello();
        }

        @Override
        public BodyText getHelloUserRead(String accessToken)
        {
            Log.d(TAG, "getHelloUserRead()");
            return Oauth2.endPoint.getHelloUserRead(accessToken);
        }

        @Override
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

    @Override
    public AccessToken getPasswordUserToken(String userName, String password)
    {
        Log.d(TAG, "getPasswordUserToken()");

        return getPasswordUserToken(
                doAuthBasicHeader(CL_USER),
                userName,
                password,
                PASSWORD_GRANT);
    }

    @Override
    public AccessToken getRefreshUserToken(String refreshTokenKey)
    {
        Log.d(TAG, "getRefreshUserToken()");

        return getRefreshUserToken(
                doAuthBasicHeader(CL_USER),
                refreshTokenKey,
                REFRESH_TOKEN_GRANT);
    }

//    ::::::::::::::::::::::::::::::::::::::::::
//                  HELPER METHODS
//    ::::::::::::::::::::::::::::::::::::::::::

    String doAuthBasicHeader(OauthClient cliente)
    {
        String baseString = new StringBuilder(new String(cliente.getId()))
                .append(":")
                .append(cliente.getSecret()).toString();

        String base64AuthData = Base64.encodeToString(baseString.getBytes(), Base64.DEFAULT);

        String header = new StringBuilder(BASIC_AND_SPACE)
                .append(base64AuthData.substring(0, base64AuthData.length() - 1))  // To take care of the trailing \n.
                .toString();
        return header;
    }
}

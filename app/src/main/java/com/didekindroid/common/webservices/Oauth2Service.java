package com.didekindroid.common.webservices;

import android.util.Base64;
import android.util.Log;

import com.didekin.common.controller.JksInClient;
import com.didekin.common.controller.RetrofitHandler;
import com.didekin.common.exception.ErrorBean;
import com.didekin.oauth2.Oauth2EndPoints;
import com.didekin.oauth2.OauthClient;
import com.didekin.oauth2.OauthToken.AccessToken;
import com.didekindroid.common.activity.UiException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static com.didekin.common.exception.ErrorBean.GENERIC_ERROR;
import static com.didekin.oauth2.OauthClient.CL_USER;
import static com.didekin.oauth2.OauthConstant.PASSWORD_GRANT;
import static com.didekin.oauth2.OauthConstant.REFRESH_TOKEN_GRANT;
import static com.didekin.oauth2.OauthTokenHelper.BASIC_AND_SPACE;
import static com.didekindroid.DidekindroidApp.getBaseURL;
import static com.didekindroid.DidekindroidApp.getHttpTimeOut;
import static com.didekindroid.DidekindroidApp.getJksPassword;
import static com.didekindroid.DidekindroidApp.getJksResourceId;
import static com.didekindroid.usuario.webservices.UsuarioService.getResponseBody;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 10:52
 */
public class Oauth2Service implements Oauth2EndPoints {

    private static final String TAG = Oauth2Service.class.getCanonicalName();

    public static final Oauth2Service Oauth2 = new Oauth2Service(getBaseURL(), new JksInAndroidApp(getJksPassword(), getJksResourceId()));
    private final Oauth2EndPoints endPoint;
    private final RetrofitHandler retrofitHandler;

    private Oauth2Service(final String hostPort, final JksInClient jksInAppClient)
    {
        retrofitHandler = new RetrofitHandler(hostPort, jksInAppClient, getHttpTimeOut());
        endPoint = retrofitHandler.getService(Oauth2EndPoints.class);
    }

    public RetrofitHandler getRetrofitHandler()
    {
        return retrofitHandler;
    }
    //  ================================== Oauth2EndPoints implementation ============================

    @Override
    public Call<AccessToken> getPasswordUserToken(String authClient, String username, String password, String grantType)
    {
        return endPoint.getPasswordUserToken(authClient, username, password, grantType);
    }

    @Override
    public Call<AccessToken> getRefreshUserToken(String authClient, String refreshToken, String grantType)
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

    public AccessToken getPasswordUserToken(String userName, String password) throws UiException
    {
        Log.d(TAG, "getPasswordUserToken()");
        try {
            Response<AccessToken> response = getPasswordUserToken(
                    doAuthBasicHeader(CL_USER),
                    userName,
                    password,
                    PASSWORD_GRANT).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public AccessToken getRefreshUserToken(String refreshTokenKey) throws UiException
    {
        Log.d(TAG, "getRefreshUserToken()");
        try {
            Response<AccessToken> response = getRefreshUserToken(
                    doAuthBasicHeader(CL_USER),
                    refreshTokenKey,
                    REFRESH_TOKEN_GRANT).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

//  =============================================================================
//                          HELPER METHODS
//  =============================================================================

    String doAuthBasicHeader(OauthClient cliente)
    {
        String baseString = cliente.getId() + ":" + cliente.getSecret();

        String base64AuthData = Base64.encodeToString(baseString.getBytes(), Base64.DEFAULT);

        return BASIC_AND_SPACE + base64AuthData.substring(0, base64AuthData.length() - 1);
    }
}

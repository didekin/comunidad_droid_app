package com.didekindroid.common.webservices;

import android.util.Base64;

import com.didekin.common.exception.ErrorBean;
import com.didekin.oauth2.Oauth2EndPoints;
import com.didekin.oauth2.OauthClient;
import com.didekin.oauth2.SpringOauthToken;
import com.didekindroid.common.activity.UiException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekin.common.exception.ErrorBean.GENERIC_ERROR;
import static com.didekin.oauth2.OauthClient.CL_USER;
import static com.didekin.oauth2.OauthConstant.PASSWORD_GRANT;
import static com.didekin.oauth2.OauthConstant.REFRESH_TOKEN_GRANT;
import static com.didekin.oauth2.OauthTokenHelper.BASIC_AND_SPACE;
import static com.didekindroid.DidekindroidApp.getRetrofitHandler;
import static com.didekindroid.DidekindroidApp.initRetrofitHandler;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 10:52
 */
public final class Oauth2Service implements Oauth2EndPoints {

    public static final Oauth2Service Oauth2 = new Oauth2Service();
    private final Oauth2EndPoints endPoint;

    private Oauth2Service()
    {
        initRetrofitHandler();
        endPoint = getRetrofitHandler().getService(Oauth2EndPoints.class);
    }
    //  ================================== Oauth2EndPoints implementation ============================

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
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public SpringOauthToken getRefreshUserToken(String refreshTokenKey) throws UiException
    {
        Timber.d("getRefreshUserToken()");
        try {
            Response<SpringOauthToken> response = getRefreshUserToken(
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

    private static <T> T getResponseBody(Response<T> response) throws UiException, IOException
    {
        if (response.isSuccessful()) {
            return response.body();
        } else {
            ErrorBean errorBean = getRetrofitHandler().getErrorBean(response);
            Timber.e("getResponseBody() exception: %s%n", errorBean.getMessage());
            throw new UiException(errorBean);
        }
    }
}

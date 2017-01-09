package com.didekinaar.security;

import android.util.Base64;

import com.didekin.common.exception.ErrorBean;
import com.didekin.oauth2.Oauth2EndPoints;
import com.didekin.oauth2.OauthClient;
import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.exception.UiException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekin.common.exception.ErrorBean.GENERIC_ERROR;
import static com.didekin.oauth2.OauthClient.CL_USER;
import static com.didekin.oauth2.OauthConstant.PASSWORD_GRANT;
import static com.didekin.oauth2.OauthConstant.REFRESH_TOKEN_GRANT;
import static com.didekin.oauth2.OauthTokenHelper.BASIC_AND_SPACE;
import static com.didekinaar.AppInitializer.creator;
import static com.didekinaar.utils.AarDaoUtil.getResponseBody;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 10:52
 */
public final class Oauth2DaoRemote implements Oauth2EndPoints, Oauth2Dao {

    public static final Oauth2DaoRemote Oauth2 = new Oauth2DaoRemote();
    private final Oauth2EndPoints endPoint;

    private Oauth2DaoRemote()
    {
        endPoint = creator.get().getRetrofitHandler().getService(Oauth2EndPoints.class);
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

    @Override
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

    @Override
    public SpringOauthToken getRefreshUserToken(String refreshTokenKey) throws UiException
    {
        Timber.d("getRefreshUserToken()");
        try {
            Response<SpringOauthToken> response = getRefreshUserToken(
                    doAuthBasicHeader(CL_USER),
                    refreshTokenKey,
                    REFRESH_TOKEN_GRANT
            ).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

//  =============================================================================
//                          HELPER METHODS
//  =============================================================================

    public String doAuthBasicHeader(OauthClient cliente)
    {
        String baseString = cliente.getId() + ":" + cliente.getSecret();
        String base64AuthData = Base64.encodeToString(baseString.getBytes(), Base64.DEFAULT);
        return BASIC_AND_SPACE + base64AuthData.substring(0, base64AuthData.length() - 1);
    }
}

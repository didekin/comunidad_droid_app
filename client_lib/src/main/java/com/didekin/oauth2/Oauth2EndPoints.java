package com.didekin.oauth2;

import com.didekin.common.exception.ErrorBean;
import com.didekin.oauth2.OauthToken.AccessToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import static com.didekin.oauth2.OauthConstant.GRANT_TYPE_PARAM;
import static com.didekin.oauth2.OauthConstant.REFRESH_TK_PARAM;
import static com.didekin.usuario.controller.UsuarioServiceConstant.PSWD_PARAM;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USER_PARAM;

/**
 * User: pedro@didekin
 * Date: 04/09/15
 * Time: 13:40
 */
@SuppressWarnings("unused")
public interface Oauth2EndPoints {

    @FormUrlEncoded
    @POST(OauthConstant.TOKEN_PATH)
    Call<AccessToken> getPasswordUserToken(@Header("Authorization") String authClient
            , @Field(USER_PARAM) String username
            , @Field(PSWD_PARAM) String password
            , @Field(GRANT_TYPE_PARAM) String grantType);

    @FormUrlEncoded
    @POST(OauthConstant.TOKEN_PATH)
    Call<AccessToken> getRefreshUserToken(@Header("Authorization") String authClient
            , @Field(REFRESH_TK_PARAM) String refreshToken
            , @Field(GRANT_TYPE_PARAM) String grantType);

    @GET("/open/not_found")
    Call<ErrorBean> getNotFoundMsg();
}
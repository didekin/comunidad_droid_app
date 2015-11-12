package com.didekin.common.oauth2;


import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

import static com.didekin.common.oauth2.OauthConstant.GRANT_TYPE_PARAM;
import static com.didekin.common.oauth2.OauthConstant.PSWD_PARAM;
import static com.didekin.common.oauth2.OauthConstant.REFRESH_TK_PARAM;
import static com.didekin.common.oauth2.OauthConstant.USER_PARAM;

/**
 * User: pedro@didekin
 * Date: 04/09/15
 * Time: 13:40
 */
@SuppressWarnings("unused")
public interface Oauth2EndPoints {

    @FormUrlEncoded
    @POST(OauthConstant.TOKEN_PATH)
    OauthToken.AccessToken getPasswordUserToken(@Header("Authorization") String authClient
            , @Field(USER_PARAM) String username
            , @Field(PSWD_PARAM) String password
            , @Field(GRANT_TYPE_PARAM) String grantType);

    @FormUrlEncoded
    @POST(OauthConstant.TOKEN_PATH)
    OauthToken.AccessToken getRefreshUserToken(@Header("Authorization") String authClient
            , @Field(REFRESH_TK_PARAM) String refreshToken
            , @Field(GRANT_TYPE_PARAM) String grantType);

    @GET("/open/not_found")
    Response getNotFoundMsg();
}
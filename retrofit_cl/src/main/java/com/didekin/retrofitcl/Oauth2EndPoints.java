package com.didekin.retrofitcl;

import com.didekin.security.OauthEndPointsIf;
import com.didekin.security.OauthToken;
import com.didekin.security.SecurityConstant;
import retrofit.client.Response;
import retrofit.http.*;

import static com.didekin.security.SecurityConstant.*;

/**
 * User: pedro@didekin
 * Date: 04/09/15
 * Time: 13:40
 */
public interface Oauth2EndPoints{

    @FormUrlEncoded
    @POST(TOKEN_PATH)
    OauthToken.AccessToken getPasswordUserToken(@Header("Authorization") String authClient
            , @Field(USER_PARAM) String username
            , @Field(PSWD_PARAM) String password
            , @Field(GRANT_TYPE_PARAM) String grantType);

    @FormUrlEncoded
    @POST(TOKEN_PATH)
    OauthToken.AccessToken getRefreshUserToken(@Header("Authorization") String authClient
            , @Field(SecurityConstant.REFRESH_TK_PARAM) String refreshToken
            , @Field(GRANT_TYPE_PARAM) String grantType);

    //    .............  TESTS ...........

    @GET("/open/hello")
    OauthEndPointsIf.BodyText getHello();

    @GET(USER_READ + "/hello")
    OauthEndPointsIf.BodyText getHelloUserRead(@Header("Authorization") String accessToken);

    @GET("/open/not_found")
    Response getNotFoundMsg();
}
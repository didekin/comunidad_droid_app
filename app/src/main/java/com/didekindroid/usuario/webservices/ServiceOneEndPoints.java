package com.didekindroid.usuario.webservices;

import com.didekindroid.usuario.dominio.AccessToken;
import com.didekindroid.usuario.dominio.Comunidad;
import com.didekindroid.usuario.dominio.Usuario;
import com.didekindroid.usuario.dominio.UsuarioComunidad;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.List;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 14:13
 */
public interface ServiceOneEndPoints {

    //    ......... PATHS ........

    String OPEN = "/open";
    String SIGNUP = "/signup";
    String TOKEN_PATH = "/oauth/token";

    String COMUNIDAD_READ = "/comunidad/read";
    String COMUNIDAD_WRITE = "/comunidad/write";

    String COMUNIDAD_DELETE = COMUNIDAD_WRITE + "/delete";
    String COMUNIDAD_SEARCH = OPEN + "/comunidad_search";

    String USER_READ = "/users/read";
    String USER_WRITE = "/users/write";

    String COMUS_BY_USER = USER_READ + "/comus_by_user";
    String REG_COMU_USERCOMU = USER_WRITE + "/reg_comu_usercomu";
    String REG_USERCOMU = USER_WRITE + "/reg_usercomu";
    String USERCOMUS_BY_COMU = USER_READ + "/usercomus_by_comu";
    String USERCOMUS_BY_USER = USER_READ + "/usercomus_by_user";
    String USER_DELETE = USER_WRITE + "/delete";

    //    ........... PARAMETERS ..........
    String USER_PARAM = "username";
    String PSWD_PARAM = "password";
    String REFRESH_TK_PARAM = "refresh_token";
    String GRANT_TYPE_PARAM = "grant_type";

    //    ...... SECURITY CONSTANSTS ......
    String PASSWORD_GRANT = "password";
    String REFRESH_TOKEN_GRANT = "refresh_token";
    String OAUTH_CLIENT_ID = "user";
    String OAUTH_CLIENT_SECRET = "";


    @DELETE(COMUNIDAD_DELETE + "/{comunidadId}")
    boolean deleteComunidad(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @DELETE(USER_DELETE)
    boolean deleteUser(@Header("Authorization") String accessToken);

    @GET(COMUS_BY_USER)
    List<Comunidad> getComusByUser(@Header("Authorization") String accessToken);

    @GET("/open/not_found")
    Response getNotFoundMsg();

    @GET(USER_READ)
    Usuario getUserData(@Header("Authorization") String accessToken);

    @GET(USERCOMUS_BY_USER)
    List<UsuarioComunidad> getUserComusByUser(@Header("Authorization") String accessToken);

    @POST(REG_USERCOMU)
    int regUserComu(@Header("Authorization") String accessToken, @Body UsuarioComunidad usuarioComunidad);

    @POST(REG_COMU_USERCOMU)
    Usuario regComuAndUserComu(@Header("Authorization") String accessToken,
                               @Body UsuarioComunidad usuarioCom);

    @POST(COMUNIDAD_SEARCH)
    List<Comunidad> searchComunidades(@Body Comunidad comunidad);

    @GET(USERCOMUS_BY_COMU + "/{comunidadId}")
    List<UsuarioComunidad> seeUserComuByComu(@Header("Authorization") String accessToken,
                                             @Path("comunidadId") long comunidadId);

    @POST(SIGNUP)
    Usuario signUp(@Body UsuarioComunidad usuarioCom);

//    .............. SECURITY ...........

    @FormUrlEncoded
    @POST(TOKEN_PATH)
    AccessToken getPasswordUserToken(@Header("Authorization") String authClient
            , @Field(USER_PARAM) String username
            , @Field(PSWD_PARAM) String password
            , @Field(GRANT_TYPE_PARAM) String grantType);

    @FormUrlEncoded
    @POST(TOKEN_PATH)
    AccessToken getRefreshUserToken(@Header("Authorization") String authClient
            , @Field(REFRESH_TK_PARAM) String refreshToken
            , @Field(GRANT_TYPE_PARAM) String grantType);

//    .............  TESTS ...........

    @GET("/open/hello")
    BodyText getHello();

    @GET(USER_READ + "/hello")
    BodyText getHelloUserRead(@Header("Authorization") String accessToken);

//  ....... UTILITIES ........

    class BodyText {

        private String text;

        public BodyText(String text)
        {
            this.text = text;
        }

        public String getText()
        {
            return text;
        }
    }
}
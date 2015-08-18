package com.didekindroid.usuario.webservices;

import com.didekindroid.usuario.comunidad.dominio.Comunidad;
import com.didekindroid.usuario.comunidad.dominio.Usuario;
import com.didekindroid.usuario.comunidad.dominio.UsuarioComunidad;
import com.didekindroid.usuario.login.dominio.AccessToken;
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
    String USER_READ = "/users/read";
    String USER_WRITE = "/users/write";
    String COMUNIDAD_WRITE = "/comunidad/write";
    String COMUNIDAD_READ = "/comunidad/read";
    String COMUNIDAD_DELETE = COMUNIDAD_WRITE + "/delete";
    String COMUNIDAD_SEARCH = OPEN + "/comus_search";

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

    @DELETE(USER_WRITE)
    boolean deleteUser(@Header("Authorization") String accessToken);

    @DELETE(COMUNIDAD_DELETE + "/{comunidadId}")
    boolean deleteComunidad(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET("/open/not_found")
    Response getNotFoundMsg();

    @GET(USER_READ)
    Usuario getUserData(@Header("Authorization") String accessToken);

    @GET(COMUNIDAD_READ)
    List<UsuarioComunidad> getUsuariosComunidad(@Header("Authorization") String accessToken);

    @POST(COMUNIDAD_WRITE)
    Usuario insertUserOldComunidadNew(@Header("Authorization") String accessToken,
                                      @Body UsuarioComunidad usuarioCom);

    @POST(COMUNIDAD_SEARCH)
    List<Comunidad> searchComunidades(@Body Comunidad comunidad);

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
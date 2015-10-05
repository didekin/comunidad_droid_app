package com.didekin.retrofitcl;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import retrofit.http.*;

import java.util.List;

import static com.didekin.security.SecurityConstant.*;
import static com.didekin.serviceone.controllers.ControllerConstant.*;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 14:13
 */
public interface ServiceOneEndPoints {


    @DELETE(ACCESS_TOKEN_DELETE + "/{oldTk}")
    boolean deleteAccessToken(@Header("Authorization") String accessToken, @Path("oldTk") String oldAccessToken);

    @DELETE(USER_DELETE)
    boolean deleteUser(@Header("Authorization") String accessToken);

    @DELETE(USERCOMU_DELETE + "/{comunidadId}")
    int deleteUserComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(COMUNIDAD_READ + "/{comunidadId}")
    Comunidad getComuData(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(COMUS_BY_USER)
    List<Comunidad> getComusByUser(@Header("Authorization") String accessToken);

    @GET(USERCOMUS_BY_USER)
    List<UsuarioComunidad> getUserComusByUser(@Header("Authorization") String accessToken);

    @GET(USER_READ)
    Usuario getUserData(@Header("Authorization") String accessToken);

    @GET(COMUNIDAD_OLDEST_USER + "/{comunidadId}")
    boolean isOldestUserComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @PUT(COMUNIDAD_WRITE)
    int modifyComuData(@Header("Authorization") String accessToken, @Body Comunidad comunidad);

    @PUT(USER_WRITE)
    int modifyUser(@Header("Authorization") String accessToken, @Body Usuario usuario);

    @PUT(USERCOMU_MODIFY)
    int modifyUserComu(@Header("Authorization") String accessToken, @Body UsuarioComunidad usuarioComunidad);

    @FormUrlEncoded
    @POST(PASSWORD_MODIFY)
    int passwordChange(@Header("Authorization") String accessToken, @Field(PSWD_PARAM) String password);

    @POST(REG_COMU_AND_USER_AND_USERCOMU)
    boolean regComuAndUserAndUserComu(@Body UsuarioComunidad usuarioCom);

    @POST(REG_COMU_USERCOMU)
    boolean regComuAndUserComu(@Header("Authorization") String accessToken,
                               @Body UsuarioComunidad usuarioCom);

    @POST(REG_USER_USERCOMU)
    boolean regUserAndUserComu(@Body UsuarioComunidad userCom);

    @POST(REG_USERCOMU)
    int regUserComu(@Header("Authorization") String accessToken, @Body UsuarioComunidad usuarioComunidad);

    @POST(COMUNIDAD_SEARCH)
    List<Comunidad> searchComunidades(@Body Comunidad comunidad);

    @GET(USERCOMUS_BY_COMU + "/{comunidadId}")
    List<UsuarioComunidad> seeUserComuByComu(@Header("Authorization") String accessToken,
                                             @Path("comunidadId") long comunidadId);
}
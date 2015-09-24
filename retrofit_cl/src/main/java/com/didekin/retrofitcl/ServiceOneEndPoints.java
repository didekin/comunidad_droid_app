package com.didekin.retrofitcl;

import com.didekin.serviceone.controllers.ServiceOneEndPointsIf;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import retrofit.http.*;

import java.util.List;

import static com.didekin.security.SecurityConstant.USER_READ;
import static com.didekin.serviceone.controllers.ControllerConstant.*;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 14:13
 */
public interface ServiceOneEndPoints{


    @DELETE(ACCESS_TOKEN_DELETE)
    int deleteAccessToken(@Header("Authorization") String accessToken);

    @DELETE(COMUNIDAD_DELETE + "/{comunidadId}")
    boolean deleteComunidad(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @DELETE(USER_DELETE)
    boolean deleteUser(@Header("Authorization") String accessToken);

    @GET(COMUS_BY_USER)
    List<Comunidad> getComusByUser(@Header("Authorization") String accessToken);

    @GET(USER_READ)
    Usuario getUserData(@Header("Authorization") String accessToken);

    @GET(USERCOMUS_BY_USER)
    List<UsuarioComunidad> getUserComusByUser(@Header("Authorization") String accessToken);

    @PUT(USER_MODIFY)
    int modifyUser(@Header("Authorization") String accessToken, @Body Usuario usuario);

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